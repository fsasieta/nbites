#include "BHWalkProvider.h"
#include "Profiler.h"
#include "Common.h"
#include "NaoPaths.h"

#include <cassert>
#include <string>
#include <iostream>
#include <sstream>
#include <stdio.h>

#include <sys/ioctl.h>
#include <sys/mman.h>
#include <sys/stat.h>

// BH
#include "bhuman.h"

namespace man
{
namespace motion
{

using namespace boost;
using nblog::SExpr;

const float BHWalkProvider::INITIAL_BODY_POSE_ANGLES[] =
{
        1.74f, 0.174f,1.31f,-.35f,
        0.0f, 0.0f, -0.36f, 0.9f, -0.54f, 0.0f,
        0.0f, 0.0f, -0.36f, 0.9f, -0.54f, 0.0f,
        1.74f,-0.174f,-1.31f,.35f
};

/**
 * Since the NBites use a different order for the joints, we use this
 * array to convert between Kinematics::JointName and BHuman's JointDataBH::Joint
 *
 * So the JointDataBH::Joint values in this array are arranged in the order
 * the NBites need them
 */
static const JointDataBH::Joint nb_joint_order[] = {
        JointDataBH::HeadYaw,
        JointDataBH::HeadPitch,
        JointDataBH::LShoulderPitch,
        JointDataBH::LShoulderRoll,
        JointDataBH::LElbowYaw,
        JointDataBH::LElbowRoll,
        JointDataBH::LHipYawPitch,
        JointDataBH::LHipRoll,
        JointDataBH::LHipPitch,
        JointDataBH::LKneePitch,
        JointDataBH::LAnklePitch,
        JointDataBH::LAnkleRoll,
        JointDataBH::RHipYawPitch,
        JointDataBH::RHipRoll,
        JointDataBH::RHipPitch,
        JointDataBH::RKneePitch,
        JointDataBH::RAnklePitch,
        JointDataBH::RAnkleRoll,
        JointDataBH::RShoulderPitch,
        JointDataBH::RShoulderRoll,
        JointDataBH::RElbowYaw,
        JointDataBH::RElbowRoll
};

BHWalkProvider::BHWalkProvider()
    : MotionProvider(WALK_PROVIDER), requestedToStop(false), tryingToWalk(false),file_mod_time()
{
	// Setup Walk Engine Configuation Parameters
	ModuleBase::config_path = common::paths::NAO_CONFIG_DIR;

    // Setup the blackboard (used by bhuman to pass data around modules)
	blackboard = new Blackboard;

    // Setup the walk engine
	walkingEngine = new WalkingEngine;
    hardReset();

    //Initializing the mod time of the walk engine file.
    //file_mod_time = 0;
}

BHWalkProvider::~BHWalkProvider()
{
    delete blackboard;
    delete walkingEngine;
}

void BHWalkProvider::requestStopFirstInstance()
{
    requestedToStop = true;
}

bool hasLargerMagnitude(float x, float y) {
    if (y > 0.0f)
        return x > y;
    if (y < 0.0f)
        return x < y;
    return true; // Considers values of 0.0f as always smaller in magnitude than anything
}

// Return true if p1 has "passed" p2 (has components values that either have a magnitude
// larger than the corresponding magnitude p2 within the same sign)
bool hasPassed(const Pose2DBH& p1, const Pose2DBH& p2) {

    return (hasLargerMagnitude(p1.rotation, p2.rotation) &&
            hasLargerMagnitude(p1.translation.x, p2.translation.x) &&
            hasLargerMagnitude(p1.translation.y, p2.translation.y));
}

/**
 * This method converts the NBites sensor and joint input to
 * input suitable for the (BH) B-Human walk, and similarly the output
 * and then interprets the walk engine output
 *
 * Main differences:
 * The BH joint data is in a different order and is transformed by sign and
 * offset to make calculation easier
 */
void BHWalkProvider::calculateNextJointsAndStiffnesses(
    std::vector<float>&            sensorAngles,
    std::vector<float>&            sensorCurrents,
    const messages::InertialState& sensorInertials,
    const messages::FSR&           sensorFSRs
    ) 
{

    PROF_ENTER(P_WALK);

    // If our calibration became bad (as decided by the walkingEngine,
    // reset. We will wait until we're recalibrated to walk.
    // NOTE currentMotionType and requestMotionType are of type MotionType enum 
    // defined in WalkingEngine.h
    if (walkingEngine->currentMotionType == 0 && tryingToWalk &&
        walkingEngine->instability.getAverageFloat() > 20.f && calibrated())
    {
        std::cout << "We are stuck! Recalibrating." << std::endl;
        walkingEngine->inertiaSensorCalibrator->reset();
        walkingEngine->instability.init();
    }

    assert(JointDataBH::numOfJoints == Kinematics::NUM_JOINTS);

    if (standby) {
        tryingToWalk = false;

        MotionRequestBH motionRequest;
        motionRequest.motion = MotionRequestBH::specialAction;

        // TODO why are we sitting and what does standby mean?
        // Special action requests keep bhwalk from recalibrating
        motionRequest.specialActionRequest.specialAction = SpecialActionRequest::sitDown;
        walkingEngine->theMotionRequestBH = motionRequest;

        // TODO anything that's not in the walk is marked as unstable
        walkingEngine->theMotionInfoBH = MotionInfoBH();
        walkingEngine->theMotionInfoBH.isMotionStable = false;

    } else {
    // TODO VERY UGLY -- refactor this please
    if (requestedToStop || !isActive()) {
        tryingToWalk = false;

        MotionRequestBH motionRequest;

        // TODO why are we sitting?
        motionRequest.motion = MotionRequestBH::specialAction;
        motionRequest.specialActionRequest.specialAction = SpecialActionRequest::sitDown;

        walkingEngine->theMotionRequestBH = motionRequest;

        currentCommand = MotionCommand::ptr();

    } else {
        // If we're not calibrated, wait until we are calibrated to walk
        if (!calibrated())
        {
            MotionRequestBH motionRequest;
            motionRequest.motion = MotionRequestBH::stand;

            walkingEngine->theMotionRequestBH = motionRequest;
        } else if (currentCommand.get() && currentCommand->getType() == MotionConstants::STEP) {
            tryingToWalk = true;

            StepCommand::ptr command = boost::shared_static_cast<StepCommand>(currentCommand);

            Pose2DBH deltaOdometry = walkingEngine->theOdometryDataBH - startOdometry;
            Pose2DBH absoluteTarget(command->theta_rads, command->x_mms, command->y_mms);

            Pose2DBH relativeTarget = absoluteTarget - (deltaOdometry + walkingEngine->upcomingOdometryOffset);

            if (!hasPassed(deltaOdometry + walkingEngine->upcomingOdometryOffset, absoluteTarget)) {
                MotionRequestBH motionRequest;
                motionRequest.motion = MotionRequestBH::walk;

                motionRequest.walkRequest.mode = WalkRequest::targetMode;

                motionRequest.walkRequest.speed.rotation = command->gain;
                motionRequest.walkRequest.speed.translation.x = command->gain;
                motionRequest.walkRequest.speed.translation.y = command->gain;

                motionRequest.walkRequest.target = relativeTarget;

                walkingEngine->theMotionRequestBH = motionRequest;

            } else {
                tryingToWalk = false;

                MotionRequestBH motionRequest;
                motionRequest.motion = MotionRequestBH::stand;

                walkingEngine->theMotionRequestBH = motionRequest;
            }

        } else {
        if (currentCommand.get() && currentCommand->getType() == MotionConstants::WALK) {
            tryingToWalk = true;

            WalkCommand::ptr command = boost::shared_static_cast<WalkCommand>(currentCommand);

            MotionRequestBH motionRequest;
            motionRequest.motion = MotionRequestBH::walk;

            motionRequest.walkRequest.mode = WalkRequest::percentageSpeedMode;

            motionRequest.walkRequest.speed.rotation = command->theta_percent;
            motionRequest.walkRequest.speed.translation.x = command->x_percent;
            motionRequest.walkRequest.speed.translation.y = command->y_percent;

            walkingEngine->theMotionRequestBH = motionRequest;
        } else {
        if (currentCommand.get() && currentCommand->getType() == MotionConstants::DESTINATION) {
            tryingToWalk = true;

            DestinationCommand::ptr command = boost::shared_static_cast<DestinationCommand>(currentCommand);

            MotionRequestBH motionRequest;
            motionRequest.motion = MotionRequestBH::walk;

            motionRequest.walkRequest.mode = WalkRequest::targetMode;

            motionRequest.walkRequest.speed.rotation = command->gain;
            motionRequest.walkRequest.speed.translation.x = command->gain;
            motionRequest.walkRequest.speed.translation.y = command->gain;

            motionRequest.walkRequest.target.rotation = command->theta_rads;
            motionRequest.walkRequest.target.translation.x = command->x_mm;
            motionRequest.walkRequest.target.translation.y = command->y_mm;

            // Let's do some motion kicking!
            if (command->motionKick && !justMotionKicked) {
                if (command->kickType == 0) {
                    motionRequest.walkRequest.kickType = WalkRequest::sidewardsLeft;
                }
                else if (command->kickType == 1) {
                    motionRequest.walkRequest.kickType = WalkRequest::sidewardsRight;
                }
                else if (command->kickType == 2) {
                    motionRequest.walkRequest.kickType = WalkRequest::left;
                }
                else if (command->kickType == 3){
                    motionRequest.walkRequest.kickType = WalkRequest::right;
                }
                else if (command->kickType == 4){
                    motionRequest.walkRequest.kickType = WalkRequest::diagonalLeft;
                }
                else {
                    motionRequest.walkRequest.kickType = WalkRequest::diagonalRight;
                }
            }

            walkingEngine->theMotionRequestBH = motionRequest;
        }
        //TODO make special command for stand
        if (!currentCommand.get()) {
            tryingToWalk = false;

            MotionRequestBH motionRequest;
            motionRequest.motion = MotionRequestBH::stand;

            walkingEngine->theMotionRequestBH = motionRequest;
        }
        }
        }
    }
    }

    // We do not copy temperatures because they are unused
    JointDataBH& bh_joint_data = walkingEngine->theJointDataBH;

    for (int i = 0; i < JointDataBH::numOfJoints; i++)
    {
        bh_joint_data.angles[nb_joint_order[i]] = sensorAngles[i];
    }

    SensorDataBH& bh_sensors = walkingEngine->theSensorDataBH;

    for (int i = 0; i < JointDataBH::numOfJoints; i++)
    {
        bh_sensors.currents[nb_joint_order[i]] = sensorCurrents[i];
    }

    bh_sensors.data[SensorDataBH::gyroX] = sensorInertials.gyr_x();
    bh_sensors.data[SensorDataBH::gyroY] = sensorInertials.gyr_y();
    bh_sensors.data[SensorDataBH::gyroZ] = sensorInertials.gyr_z(); // NOTE not currently used by BH

    bh_sensors.data[SensorDataBH::accX] = sensorInertials.acc_x();
    bh_sensors.data[SensorDataBH::accY] = sensorInertials.acc_y();
    bh_sensors.data[SensorDataBH::accZ] = sensorInertials.acc_z();

    bh_sensors.data[SensorDataBH::angleX] = sensorInertials.angle_x();
    bh_sensors.data[SensorDataBH::angleY] = sensorInertials.angle_y();
    bh_sensors.data[SensorDataBH::angleZ] = sensorInertials.angle_z();

    bh_sensors.data[SensorDataBH::fsrLFL] = sensorFSRs.lfl();
    bh_sensors.data[SensorDataBH::fsrLFR] = sensorFSRs.lfr();
    bh_sensors.data[SensorDataBH::fsrLBL] = sensorFSRs.lrl();
    bh_sensors.data[SensorDataBH::fsrLBR] = sensorFSRs.lrr();

    bh_sensors.data[SensorDataBH::fsrRFL] = sensorFSRs.rfl();
    bh_sensors.data[SensorDataBH::fsrLFR] = sensorFSRs.lfr();
    bh_sensors.data[SensorDataBH::fsrLBL] = sensorFSRs.lrl();
    bh_sensors.data[SensorDataBH::fsrLBR] = sensorFSRs.lrr();

    WalkingEngineOutputBH output;
    walkingEngine->update(output);

    // Update justMotionKicked so that we do not motion kick multiple times in a row
    if (output.executedWalk.kickType != WalkRequest::none) { // if we succesfully motion kicked
        justMotionKicked = true;
    }
    else if (walkingEngine->theMotionRequestBH.walkRequest.mode != WalkRequest::targetMode || // else if we are no longer attempting to motion kick
             !boost::shared_static_cast<DestinationCommand>(currentCommand)->motionKick) {
        justMotionKicked = false;
    }

    // Ignore the first chain since it's the head
    for (unsigned i = 1; i < Kinematics::NUM_CHAINS; i++) {
        std::vector<float> chain_angles;
        std::vector<float> chain_hardness;
        for (unsigned j = Kinematics::chain_first_joint[i];
                     j <= Kinematics::chain_last_joint[i]; j++) {
            chain_angles.push_back(output.angles[nb_joint_order[j]] * walkingEngine->theJointCalibrationBH.joints[nb_joint_order[j]].sign - walkingEngine->theJointCalibrationBH.joints[nb_joint_order[j]].offset);
            if (output.jointHardness.hardness[nb_joint_order[j]] == 0) {
                chain_hardness.push_back(MotionConstants::NO_STIFFNESS);
            } else {
                chain_hardness.push_back(output.jointHardness.hardness[nb_joint_order[j]] / 100.f);
            }

        }
        this->setNextChainJoints((Kinematics::ChainID) i, chain_angles);
        this->setNextChainStiffnesses((Kinematics::ChainID) i, chain_hardness);
    }

    // We only really leave when we do a sweet move, so request a special action
    if (walkingEngine->theMotionSelectionBH.targetMotion == MotionRequestBH::specialAction
            && requestedToStop) {

        inactive();
        requestedToStop = false;
        // Reset odometry - this allows the walk to not "freak out" when we come back
        // from other providers
        walkingEngine->theOdometryDataBH = OdometryDataBH();
    }

    PROF_EXIT(P_WALK);
}

bool BHWalkProvider::isStanding() const {
    return walkingEngine->theMotionRequestBH.motion == MotionRequestBH::stand;
}

bool BHWalkProvider::isWalkActive() const {
    return !(isStanding() && walkingEngine->theWalkingEngineOutputBH.isLeavingPossible) && isActive();
}

void BHWalkProvider::stand() {
    currentCommand = MotionCommand::ptr();
    active();
}

void BHWalkProvider::getOdometryUpdate(portals::OutPortal<messages::RobotLocation>& out) const
{
    portals::Message<messages::RobotLocation> odometryData(0);
    odometryData.get()->set_x(walkingEngine->theOdometryDataBH.translation.x
                              * MM_TO_CM);
    odometryData.get()->set_y(walkingEngine->theOdometryDataBH.translation.y
                              * MM_TO_CM);
    odometryData.get()->set_h(walkingEngine->theOdometryDataBH.rotation);

    out.setMessage(odometryData);
}

void BHWalkProvider::hardReset() {
    inactive();

    // Reset odometry
    walkingEngine->theOdometryDataBH = OdometryDataBH();

    MotionRequestBH motionRequest;
    motionRequest.motion = MotionRequestBH::specialAction;

    // TODO is this the same?
    motionRequest.specialActionRequest.specialAction = SpecialActionRequest::sitDown;
    currentCommand = MotionCommand::ptr();

    walkingEngine->inertiaSensorCalibrator->reset();

    requestedToStop = false;
}

void BHWalkProvider::resetOdometry() {
    walkingEngine->theOdometryDataBH = OdometryDataBH();
}

void BHWalkProvider::setCommand(const WalkCommand::ptr command) {

    if (command->theta_percent == 0 && command->x_percent == 0 && command->y_percent == 0) {
        this->stand();
        return;
    }

    currentCommand = command;

    active();
}

void BHWalkProvider::setCommand(const StepCommand::ptr command) {
    MotionRequestBH motionRequest;
    motionRequest.motion = MotionRequestBH::walk;
    walkingEngine->theMotionRequestBH = motionRequest;

    startOdometry = walkingEngine->theOdometryDataBH;
    currentCommand = command;

    active();
}

void BHWalkProvider::setCommand(const DestinationCommand::ptr command) {

    currentCommand = command;

    active();
}

bool BHWalkProvider::calibrated() const {
    return walkingEngine->theInertiaSensorDataBH.calibrated;
}

bool BHWalkProvider::upright() const {
    return walkingEngine->theFallDownStateBH.state == FallDownStateBH::upright;
}

float BHWalkProvider::leftHandSpeed() const {
    return walkingEngine->leftHandSpeed;
}

float BHWalkProvider::rightHandSpeed() const {
    return walkingEngine->rightHandSpeed;
}

//Parses throught the file of parameters, returns the  value specified in the .txt file
//and sets it to the engine param instance that is in the
//
void BHWalkProvider::updateWalkingEngineParameters(){

    std::string fileString;
    std::string filename;

    //Taking into account different NAOQI versions
    #ifdef NAOQI_2
    filename = "/home/nao/nbites/Config/V5WalkEngineParameters.txt";
    #else
    filename = "/home/nao/nbites/Config/V4WalkEngineParameters.txt";
    #endif

    //Reading file to a string
    std::ifstream file(filename);
    std::stringstream ssfile;
    ssfile << file.rdbuf();
    fileString = ssfile.str();

    //std::cout << "This is the current string read from the file\n" << std::endl; //fileString << std::endl;
    //printf(fileString.c_str());

    ssize_t i = 0;
    SExpr params = *SExpr::read(fileString, i);
    
    //std::cout << "Printing parameters in robot .txt file\n" << params.print() << std::endl; //<-- OK

    // regardless of request, we only update the parameters if they are different from the old ones
    // (If the TIMESTAMP of the file is different)
    if(updatedFile(filename)){

        std::cout << "[INFO] Parameters updating NOW!!" << std::endl;

        walkingEngine->standComPosition.y             = (float) params.find("vectorStandComPos_y")->get(1)->valueAsDouble();
        walkingEngine->standComPosition.z             = (float) params.find("vectorStandComPos_z")->get(1)->valueAsDouble();
        walkingEngine->standBodyTilt                  = (float) params.find("standBodyTilt")->get(1)->valueAsDouble();
        walkingEngine->standArmJointAngles.x          = (float) params.find("vectorStandArmJointAngle_x")->get(1)->valueAsDouble();
        walkingEngine->standArmJointAngles.y          = (float) params.find("vectorStandArmJointAngle_y")->get(1)->valueAsDouble();
        walkingEngine->standHardnessAnklePitch        = (int)   params.find("standHardnessAnklePitch")->get(1)->valueAsInt();
        walkingEngine->standHardnessAnkleRoll         = (int)   params.find("standHardnessAnkleRoll")->get(1)->valueAsInt();
        walkingEngine->walkRef.x                      = (float) params.find("vectorWalkRef_x")->get(1)->valueAsDouble();
        walkingEngine->walkRef.y                      = (float) params.find("vectorWalkRef_y")->get(1)->valueAsDouble();
        walkingEngine->walkRefAtFullSpeedX.x          = (float) params.find("vectorWalkRefAtFullSpeed_x")->get(1)->valueAsDouble();
        walkingEngine->walkRefAtFullSpeedX.y          = (float) params.find("vectorWalkRefAtFullSpeed_y")->get(1)->valueAsDouble();
        walkingEngine->walkRefXPlanningLimit.min      = (float) params.find("rangeWalkRefPlanningLimit_low")->get(1)->valueAsDouble();
        walkingEngine->walkRefXPlanningLimit.max      = (float) params.find("rangeWalkRefPlanningLimit_high")->get(1)->valueAsDouble();
        walkingEngine->walkRefXLimit.min              = (float) params.find("rangeWalkRefXLimit_low")->get(1)->valueAsDouble();
        walkingEngine->walkRefXLimit.max              = (float) params.find("rangeWalkRefXLimit_high")->get(1)->valueAsDouble();
        walkingEngine->walkRefYLimit.min              = (float) params.find("rangeWalkRefYLimit_low")->get(1)->valueAsDouble();
        walkingEngine->walkRefYLimit.max              = (float) params.find("rangeWalkRefYLimit_high")->get(1)->valueAsDouble();
        walkingEngine->walkStepSizeXPlanningLimit.min = (float) params.find("rangeWalkStepSizeXPlanningLimit_low")->get(1)->valueAsDouble();
        walkingEngine->walkStepSizeXPlanningLimit.max = (float) params.find("rangeWalkStepSizeXPlanningLimit_high")->get(1)->valueAsDouble();
        walkingEngine->walkStepSizeXLimit.min         = (float) params.find("rangeWalkStepSizeXLimit_low")->get(1)->valueAsDouble();
        walkingEngine->walkStepSizeXLimit.max         = (float) params.find("rangeWalkStepSizeXLimit_high")->get(1)->valueAsDouble();
        walkingEngine->walkStepDuration               = (float) params.find("walkStepDuration")->get(1)->valueAsDouble();
        walkingEngine->walkStepDurationAtFullSpeedX   = (float) params.find("walkStepDurationAtFullSpeedX")->get(1)->valueAsDouble();
        walkingEngine->walkStepDurationAtFullSpeedY   = (float) params.find("walkStepDurationAtFullSpeedY")->get(1)->valueAsDouble();
        walkingEngine->walkHeight.x                   = (float) params.find("vectorWalkHeight_x")->get(1)->valueAsDouble();
        walkingEngine->walkHeight.y                   = (float) params.find("vectorWalkHeight_y")->get(1)->valueAsDouble();
        walkingEngine->walkArmRotationAtFullSpeedX    = (float) params.find("walkArmRotationAtFullSpeedX")->get(1)->valueAsDouble();
        walkingEngine->walkMovePhase.start            = (float) params.find("walkMovePhaseBeginning")->get(1)->valueAsDouble();
        walkingEngine->walkMovePhase.duration         = (float) params.find("walkMovePhaseLength")->get(1)->valueAsDouble();
        walkingEngine->walkLiftPhase.start            = (float) params.find("walkLiftPhaseBeginning")->get(1)->valueAsDouble();
        walkingEngine->walkLiftPhase.duration         = (float) params.find("walkLiftPhaseLength")->get(1)->valueAsDouble();
        walkingEngine->walkLiftOffset.x               = (float) params.find("vectorWalkLiftOffSet_x")->get(1)->valueAsDouble();
        walkingEngine->walkLiftOffset.y               = (float) params.find("vectorWalkLiftOffSet_y")->get(1)->valueAsDouble();
        walkingEngine->walkLiftOffset.z               = (float) params.find("vectorWalkLiftOffSet_z")->get(1)->valueAsDouble();
        walkingEngine->walkLiftOffsetAtFullSpeedX.x   = (float) params.find("vectorWalkLiftOffSetAtFullSpeedX_x")->get(1)->valueAsDouble();
        walkingEngine->walkLiftOffsetAtFullSpeedX.y   = (float) params.find("vectorWalkLiftOffSetAtFullSpeedX_y")->get(1)->valueAsDouble();
        walkingEngine->walkLiftOffsetAtFullSpeedX.z   = (float) params.find("vectorWalkLiftOffSetAtFullSpeedX_z")->get(1)->valueAsDouble();
        walkingEngine->walkLiftOffsetAtFullSpeedY.x   = (float) params.find("vectorWalkLiftOffSetAtFullSpeedY_x")->get(1)->valueAsDouble();
        walkingEngine->walkLiftOffsetAtFullSpeedY.y   = (float) params.find("vectorWalkLiftOffSetAtFullSpeedY_y")->get(1)->valueAsDouble();
        walkingEngine->walkLiftOffsetAtFullSpeedY.z   = (float) params.find("vectorWalkLiftOffSetAtFullSpeedY_z")->get(1)->valueAsDouble();
        walkingEngine->walkLiftRotation.x             = (float) params.find("vectorWalkLiftRotation_x")->get(1)->valueAsDouble();
        walkingEngine->walkLiftRotation.y             = (float) params.find("vectorWalkLiftRotation_y")->get(1)->valueAsDouble();
        walkingEngine->walkLiftRotation.z             = (float) params.find("vectorWalkLiftRotation_z")->get(1)->valueAsDouble();
        walkingEngine->walkSupportRotation            = (float) params.find("walkSupportRotation")->get(1)->valueAsDouble();
        walkingEngine->walkComLiftOffset.x            = (float) params.find("walkComLiftOffSet_x")->get(1)->valueAsDouble();
        walkingEngine->walkComLiftOffset.y            = (float) params.find("walkComLiftOffSet_y")->get(1)->valueAsDouble();
        walkingEngine->walkComLiftOffset.z            = (float) params.find("walkComLiftOffSet_z")->get(1)->valueAsDouble();
        walkingEngine->walkComBodyRotation            = (float) params.find("walkComBodyRotation")->get(1)->valueAsDouble();
        walkingEngine->speedMax.rotation              = (float) params.find("speedMax_rot")->get(1)->valueAsDouble();
        walkingEngine->speedMax.translation.x         = (float) params.find("speedMax_Vector_x")->get(1)->valueAsDouble();
        walkingEngine->speedMax.translation.y         = (float) params.find("speedMax_Vector_y")->get(1)->valueAsDouble();
        walkingEngine->speedMaxBackwards              = (float) params.find("speedMaxBackwards")->get(1)->valueAsDouble();
        walkingEngine->speedMaxChange.rotation        = (float) params.find("speedMaxChange_rot")->get(1)->valueAsDouble();
        walkingEngine->speedMaxChange.translation.x   = (float) params.find("speedMaxChange_Vector_x")->get(1)->valueAsDouble();
        walkingEngine->speedMaxChange.translation.y   = (float) params.find("speedMaxChange_Vector_y")->get(1)->valueAsDouble();
        walkingEngine->balance                        = (params.find("balance")->get(1)->value() == "true" || params.find("balance")->get(1)->value() == "1") ? true : false;
        walkingEngine->balanceBodyRotation.x          = (float) params.find("vectorBalanceBodyRotation_x")->get(1)->valueAsDouble();
        walkingEngine->balanceBodyRotation.y          = (float) params.find("vectorBalanceBodyRotation_y")->get(1)->valueAsDouble();
        walkingEngine->balanceCom.x                   = (float) params.find("vectorBalanceCom_x")->get(1)->valueAsDouble();
        walkingEngine->balanceCom.y                   = (float) params.find("vectorBalanceCom_y")->get(1)->valueAsDouble();
        walkingEngine->balanceComVelocity.x           = (float) params.find("vectorBalanceComVelocity_x")->get(1)->valueAsDouble();
        walkingEngine->balanceComVelocity.y           = (float) params.find("vectorBalanceComVelocity_y")->get(1)->valueAsDouble();
        walkingEngine->balanceRef.x                   = (float) params.find("vectorBalanceRef_x")->get(1)->valueAsDouble();
        walkingEngine->balanceRef.y                   = (float) params.find("vectorBalanceRef_y")->get(1)->valueAsDouble();
        walkingEngine->balanceNextRef.x               = (float) params.find("vectorBalanceNextRef_x")->get(1)->valueAsDouble();
        walkingEngine->balanceNextRef.y               = (float) params.find("vectorBalanceNextRef_y")->get(1)->valueAsDouble();
        walkingEngine->balanceStepSize.x              = (float) params.find("vectorBalanceStepSize_x")->get(1)->valueAsDouble();
        walkingEngine->balanceStepSize.y              = (float) params.find("vectorBalanceStepSize_y")->get(1)->valueAsDouble();
        walkingEngine->observerMeasurementDelay       = (float) params.find("observerMeasurementDelay")->get(1)->valueAsDouble();

        //walkingEngine->observerMeasurementDeviation[0] = (float) params.find("vectorObserverMeasurementDeviation.x")->get(1)->valueAsDouble();
        //walkingEngine->observerMeasurementDeviation[1] = (float) params.find("vectorObserverMeasurementDeviation.y")->get(1)->valueAsDouble();
        //walkingEngine->observerProcessDeviation[0]    = (float) params.find("vectorObserverProcessDeviation.x")->get(1)->valueAsDouble();
        //walkingEngine->observerProcessDeviation[1]    = (float) params.find("vectorObserverProcessDeviation.y")->get(1)->valueAsDouble();
        //walkingEngine->observerProcessDeviation[2]    = (float) params.find("vectorObserverProcessDeviation.z")->get(1)->valueAsDouble();
        //walkingEngine->observerProcessDeviation[3]    = (float) params.find("vectorObserverProcessDeviation.w")->get(1)->valueAsDouble();

        walkingEngine->odometryScale.rotation         = (float) params.find("odometryScale_rot")->get(1)->valueAsDouble();
        walkingEngine->odometryScale.translation.x    = (float) params.find("odometryScale_Vector_x")->get(1)->valueAsDouble();
        walkingEngine->odometryScale.translation.y    = (float) params.find("odometryScale_Vector_y")->get(1)->valueAsDouble();
        walkingEngine->gyroStateGain                  = (float) params.find("gyroStateGain")->get(1)->valueAsDouble();
        walkingEngine->gyroDerivativeGain             = (float) params.find("gyroDerivativeGain")->get(1)->valueAsDouble();
        walkingEngine->gyroSmoothing                  = (float) params.find("gyroSmoothing")->get(1)->valueAsDouble();
        walkingEngine->minRotationToReduceStepSize    = (float) params.find("minRotationToReduceStepSize")->get(1)->valueAsDouble();
        //82, in case you're wondering. 6 of them get me a segfault when trying to change them, no idea why.

        std::cout << "[INFO] Parameters finished updating" << std::endl;
    }
}

bool BHWalkProvider::updatedFile(std::string filename){

    struct stat file_stats;
    //static time_t file_mod_time = 0;
    //if(stat(filename.c_str(), &file_stats) == -1){
    //    std::cout << "Error on stat function in BHWalkProvider::updatedFile" << std::endl;
    //}
    int error = stat(filename.c_str(), &file_stats);

    int timeDiff = (file_stats.st_mtime - file_mod_time);
    std::cout << "[INFO] file tim diff: " << timeDiff << std::endl;
    std::cout << "[INFO] Printing file_mod_time: " << file_mod_time << std::endl;
    if(timeDiff > 0.0) {
        file_mod_time = file_stats.st_mtime;
        std::cout << "[INFO] New Mod. Time " << file_mod_time << std::endl;
        std::cout << "[INFO] Updating parameters used." << std::endl;
        return true;
    } else {
        std::cout << "[ERR] File has not been modified"<<std::endl;
    }
    return false;
}

void BHWalkProvider::printCurrentEngineParams(){

    std::cout << "[DEBUG] This are the current parameters in the walk engine,\n\t order is the same as proto file and tool\n" <<
    walkingEngine->standComPosition.y               << "\n" << 
    walkingEngine->standComPosition.z               << "\n" << 
    walkingEngine->standBodyTilt                    << "\n" << 
    walkingEngine->standArmJointAngles.x            << "\n" << 
    walkingEngine->standArmJointAngles.y            << "\n" << 
    walkingEngine->standHardnessAnklePitch          << "\n" << 
    walkingEngine->standHardnessAnkleRoll           << "\n" << 
    walkingEngine->walkRef.x                        << "\n" << 
    walkingEngine->walkRef.y                        << "\n" << 
    walkingEngine->walkRefAtFullSpeedX.x            << "\n" << 
    walkingEngine->walkRefAtFullSpeedX.y            << "\n" << 
    walkingEngine->walkRefXPlanningLimit.min        << "\n" << 
    walkingEngine->walkRefXPlanningLimit.max        << "\n" << 
    walkingEngine->walkRefXLimit.min                << "\n" << 
    walkingEngine->walkRefXLimit.max                << "\n" << 
    walkingEngine->walkRefYLimit.min                << "\n" << 
    walkingEngine->walkRefYLimit.max                << "\n" << 
    walkingEngine->walkStepSizeXPlanningLimit.min   << "\n" << 
    walkingEngine->walkStepSizeXPlanningLimit.max   << "\n" << 
    walkingEngine->walkStepSizeXLimit.min           << "\n" << 
    walkingEngine->walkStepSizeXLimit.max           << "\n" << 
    walkingEngine->walkStepDuration                 << "\n" << 
    walkingEngine->walkStepDurationAtFullSpeedX     << "\n" << 
    walkingEngine->walkStepDurationAtFullSpeedY     << "\n" << 
    walkingEngine->walkHeight.x                     << "\n" << 
    walkingEngine->walkHeight.y                     << "\n" << 
    walkingEngine->walkArmRotationAtFullSpeedX      << "\n" << 
    walkingEngine->walkMovePhase.start              << "\n" << 
    walkingEngine->walkMovePhase.duration           << "\n" << 
    walkingEngine->walkLiftPhase.start              << "\n" << 
    walkingEngine->walkLiftPhase.duration           << "\n" << 
    walkingEngine->walkLiftOffset.x                 << "\n" << 
    walkingEngine->walkLiftOffset.y                 << "\n" << 
    walkingEngine->walkLiftOffset.z                 << "\n" << 
    walkingEngine->walkLiftOffsetAtFullSpeedX.x     << "\n" << 
    walkingEngine->walkLiftOffsetAtFullSpeedX.y     << "\n" << 
    walkingEngine->walkLiftOffsetAtFullSpeedX.z     << "\n" << 
    walkingEngine->walkLiftOffsetAtFullSpeedY.x     << "\n" << 
    walkingEngine->walkLiftOffsetAtFullSpeedY.y     << "\n" << 
    walkingEngine->walkLiftOffsetAtFullSpeedY.z     << "\n" << 
    walkingEngine->walkLiftRotation.x               << "\n" << 
    walkingEngine->walkLiftRotation.y               << "\n" << 
    walkingEngine->walkLiftRotation.z               << "\n" << 
    walkingEngine->walkSupportRotation              << "\n" << 
    walkingEngine->walkComLiftOffset.x              << "\n" << 
    walkingEngine->walkComLiftOffset.y              << "\n" << 
    walkingEngine->walkComLiftOffset.z              << "\n" << 
    walkingEngine->walkComBodyRotation              << "\n" << 
    walkingEngine->speedMax.rotation                << "\n" << 
    walkingEngine->speedMax.translation.x           << "\n" << 
    walkingEngine->speedMax.translation.y           << "\n" << 
    walkingEngine->speedMaxBackwards                << "\n" << 
    walkingEngine->speedMaxChange.rotation          << "\n" << 
    walkingEngine->speedMaxChange.translation.x     << "\n" << 
    walkingEngine->speedMaxChange.translation.y     << "\n" << 
    walkingEngine->balance                          << "\n" << 
    walkingEngine->balanceBodyRotation.x            << "\n" << 
    walkingEngine->balanceBodyRotation.y            << "\n" << 
    walkingEngine->balanceCom.x                     << "\n" << 
    walkingEngine->balanceCom.y                     << "\n" << 
    walkingEngine->balanceComVelocity.x             << "\n" << 
    walkingEngine->balanceComVelocity.y             << "\n" << 
    walkingEngine->balanceRef.x                     << "\n" << 
    walkingEngine->balanceRef.y                     << "\n" << 
    walkingEngine->balanceNextRef.x                 << "\n" << 
    walkingEngine->balanceNextRef.y                 << "\n" << 
    walkingEngine->balanceStepSize.x                << "\n" << 
    walkingEngine->balanceStepSize.y                << "\n" << 
    walkingEngine->observerMeasurementDelay         << "\n" << 
    walkingEngine->observerMeasurementDeviation.x   << "\n" << 
    walkingEngine->observerMeasurementDeviation.y   << "\n" << 
    walkingEngine->observerProcessDeviation[0]      << "\n" << 
    walkingEngine->observerProcessDeviation[1]      << "\n" << 
    walkingEngine->observerProcessDeviation[2]      << "\n" << 
    walkingEngine->observerProcessDeviation[3]      << "\n" << 
    walkingEngine->odometryScale.rotation           << "\n" << 
    walkingEngine->odometryScale.translation.x      << "\n" << 
    walkingEngine->odometryScale.translation.y      << "\n" << 
    walkingEngine->gyroStateGain                    << "\n" << 
    walkingEngine->gyroDerivativeGain               << "\n" << 
    walkingEngine->gyroSmoothing                    << "\n" << 
    walkingEngine->minRotationToReduceStepSize      << "\n" << 
    "End of param printing" << std::endl;
}


} //motion                                                                                 
} //man
