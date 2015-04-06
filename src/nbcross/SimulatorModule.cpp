#include "SimulatorModule.h"

SimulatorModule::SimulatorModule(messages::RobotLocation localizationPB,
                                messages::FilteredBall filteredBallPB,
                                messages::GameState gameStatePB,
                                messages::VisionField visionFieldPB,
                                messages::VisionRobot visionRobotPB,
                                messages::VisionObstacle visionObstaclePB,
                                messages::FallStatus fallStatusPB,
                                messages::MotionStatus motionStatusPB,
                                messages::RobotLocation odometryPB,
                                messages::JointAngles jointsPB,
                                messages::StiffStatus stiffStatusPB,
                                messages::FieldObstacles obstaclePB,
                                messages::SharedBall sharedBallPB,
                                messages::RobotLocation sharedFlipPB)
    : portals::Module(),
    localizationOut(base()),
    // filteredBallOut(base()),
    gameStateOut(base()),
    visionFieldOut(base()),
    visionRobotOut(base()),
    visionObstacleOut(base()),
    fallStatusOut(base()),
    motionStatusOut(base()),
    odometryOut(base()),
    jointsOut(base()),
    stiffStatusOut(base()),
    obstacleOut(base()),
    sharedBallOut(base()),
    sharedFlipOut(base()),

    behaviors(0, 3) // TODO
{
    localizationPt = &localizationPB;
    filteredBallPt = &filteredBallPB;
    gameStatePt = &gameStatePB;
    visionFieldPt = &visionFieldPB;
    visionRobotPt = &visionRobotPB;
    visionObstaclePt = &visionObstaclePB;
    fallStatusPt = &fallStatusPB;
    motionStatusPt = &motionStatusPB;
    odometryPt = &odometryPB;
    jointsPt = &jointsPB;
    stiffStatusPt = &stiffStatusPB;
    obstaclePt = &obstaclePB;
    sharedBallPt = &sharedBallPB;
    sharedFlipPt = &sharedFlipPB;
}

SimulatorModule::~SimulatorModule()
{
}

void SimulatorModule::run_ ()

{
    std::cout << "RUNNING SIM" << std::endl;
    sendMessages();
    behaviors.run();
}


void SimulatorModule::sendMessages()
{
    // // The messages
    const portals::Message<messages::RobotLocation>& localizationMg(localizationPt);
    // const portals::Message<messages::FilteredBall>& filteredBallMg(filteredBallPt);
    const portals::Message<messages::GameState>& gameStateMg(gameStatePt);
    const portals::Message<messages::VisionField>& visionFieldMg(visionFieldPt);
    const portals::Message<messages::VisionRobot>& visionRobotMg(visionRobotPt);
    const portals::Message<messages::VisionObstacle>& visionObstacleMg(visionObstaclePt);
    const portals::Message<messages::FallStatus>& fallStatusMg(fallStatusPt);
    const portals::Message<messages::MotionStatus>& motionStatusMg(motionStatusPt);
    const portals::Message<messages::RobotLocation>& odometryMg(odometryPt);
    const portals::Message<messages::JointAngles>& jointsMg(jointsPt);
    const portals::Message<messages::StiffStatus>& stiffStatusMg(stiffStatusPt);
    const portals::Message<messages::FieldObstacles>& obstacleMg(obstaclePt);
    const portals::Message<messages::SharedBall>& sharedBallMg(sharedBallPt);
    const portals::Message<messages::RobotLocation>& sharedFlipMg(sharedFlipPt);

    // Set the OutPortal messages to the Protobuf messages
    localizationOut.setMessage(localizationMg);
    // filteredBallOut.setMessage(filteredBallMg);
    gameStateOut.setMessage(gameStateMg);
    visionFieldOut.setMessage(visionFieldMg);
    visionRobotOut.setMessage(visionRobotMg);
    visionObstacleOut.setMessage(visionObstacleMg);
    fallStatusOut.setMessage(fallStatusMg);
    motionStatusOut.setMessage(motionStatusMg);
    odometryOut.setMessage(odometryMg);
    jointsOut.setMessage(jointsMg);
    stiffStatusOut.setMessage(stiffStatusMg);
    obstacleOut.setMessage(obstacleMg);
    sharedBallOut.setMessage(sharedBallMg);
    sharedFlipOut.setMessage(sharedFlipMg);

    behaviors.reset();
    reset();

    // Wire the OutPortals to the Behaviors Module
    behaviors.localizationIn.wireTo(&localizationOut);
    // behaviors.filteredBallIn.wireTo(&filteredBallOut);
    behaviors.gameStateIn.wireTo(&gameStateOut);
    behaviors.visionFieldIn.wireTo(&visionFieldOut);
    behaviors.visionRobotIn.wireTo(&visionRobotOut);
    behaviors.visionObstacleIn.wireTo(&visionObstacleOut);
    behaviors.fallStatusIn.wireTo(&fallStatusOut);
    behaviors.motionStatusIn.wireTo(&motionStatusOut);
    behaviors.odometryIn.wireTo(&odometryOut);
    behaviors.jointsIn.wireTo(&jointsOut);
    behaviors.stiffStatusIn.wireTo(&stiffStatusOut);
    behaviors.obstacleIn.wireTo(&obstacleOut);
    behaviors.sharedBallIn.wireTo(&sharedBallOut);
    behaviors.sharedFlipIn.wireTo(&sharedFlipOut);
}