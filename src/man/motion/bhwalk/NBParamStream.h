/* Class that gives out the latest parameters to the B-Human walking engine
 *
 * @author Franco Sasieta
 */

//TODO: before compiling, we need to figure out how to linke the SExpr file.

#include <iostream> 
#include "SExpr.h"

#define V4FILEPATH "home/nao/nbites/Config/V4WalkEngineParameters"
#define V5FILEPATH "home/nao/nbites/Config/V5WalkEngineParameters"



class WalkingEngineParamTranscriber
{
public:
    WalkingEngineParamTranscriber();
    ~WalkingEngineParamTranscriber();


    void readFromFile(string filepath);
    bool updatedParams;

    time_t file_mod_time;
    int first_time;

    struct engineParameters{
        float vectorStandComPos_y;
        float vectorStandComPos_z;
        float standBodyTilt;
        float vectorStandArmJointAngle_x;
        float vectorStandArmJointAngle_y;
        int   standHardnessAnklePitch;
        int   standHardnessAnkleRoll;
        float vectorWalkRef_x;
        float vectorWalkRef_y;
        float vectorWalkRefAtFullSpeed_x;
        float vectorWalkRefAtFullSpeed_y;
        float rangeWalkRefPlanningLimit_low;
        float rangeWalkRefPlanningLimit_high;
        float rangeWalkRefXLimit_low;
        float rangeWalkRefXLimit_high;
        float rangeWalkRefYLimit_low;
        float rangeWalkRefYLimit_high;
        float rangeWalkStepSizeXPlanningLimi
        float rangeWalkStepSizeXPlanningLimi
        float rangeWalkStepSizeXLimit_low;
        float rangeWalkStepSizeXLimit_high;
        float walkStepDuration;
        float walkStepDurationAtFullSpeedX;
        float walkStepDurationAtFullSpeedY;
        float vectorWalkHeight_x;
        float vectorWalkHeight_y;
        float walkArmRotationAtFullSpeedX;
        float walkMovePhaseBeginning;
        float walkMovePhaseLength;
        float walkLiftPhaseBeginning;
        float walkLiftPhaseLength;
        float vectorWalkLiftOffSet_x;
        float vectorWalkLiftOffSet_y;
        float vectorWalkLiftOffSet_z;
        float vectorWalkLiftOffSetAtFullSpee
        float vectorWalkLiftOffSetAtFullSpee
        float vectorWalkLiftOffSetAtFullSpee
        float vectorWalkLiftOffSetAtFullSpee
        float vectorWalkLiftOffSetAtFullSpee
        float vectorWalkLiftOffSetAtFullSpee
        float vectorWalkLiftRotation_x;
        float vectorWalkLiftRotation_y;
        float vectorWalkLiftRotation_z;
        float walkSupportRotation;
        float walkComLiftOffSet_x;
        float walkComLiftOffSet_y;
        float walkComLiftOffSet_z;
        float walkComBodyRotation;
        float speedMax_rot;
        float speedMax_Vector_x;
        float speedMax_Vector_y;
        float speedMaxBackwards;
        float speedMaxChange_rot;
        float speedMaxChange_Vector_x;
        float speedMaxChange_Vector_y;
        bool  balance;
        float vectorBalanceBodyRotation_x;
        float vectorBalanceBodyRotation_y;
        float vectorBalanceCom_x;
        float vectorBalanceCom_y;
        float vectorBalanceComVelocity_x;
        float vectorBalanceComVelocity_y;
        float vectorBalanceRef_x;
        float vectorBalanceRef_y;
        float vectorBalanceNextRef_x;
        float vectorBalanceNextRef_y;
        float vectorBalanceStepSize_x;
        float vectorBalanceStepSize_y;
        float observerMeasurementDelay;
        float vectorObserverMeasurementDevia
        float vectorObserverMeasurementDevia
        float vectorObserverProcessDeviation
        float vectorObserverProcessDeviation
        float vectorObserverProcessDeviation
        float vectorObserverProcessDeviation
        float odometryScale_rot;
        float odometryScale_Vector_x;
        float odometryScale_Vector_y;
        float gyroStateGain;
        float gyroDerivativeGain;
        float gyroSmoothinge;
        float minRotationToReduceStepSize;


    }
};










