#include "Simulator.h"

Simulator::Simulator()
: behaviors(0,4)
{

}

void Simulator::run(std::vector<logio::log_t> pbufs)
{
    behaviors.reset();

    // The data strings from the arguments
    const std::string pBufString0((const char*)pbufs[0].data, pbufs[0].dlen);
    const std::string pBufString1((const char*)pbufs[1].data, pbufs[1].dlen);
    const std::string pBufString2((const char*)pbufs[2].data, pbufs[2].dlen);
    const std::string pBufString3((const char*)pbufs[3].data, pbufs[3].dlen);
    const std::string pBufString4((const char*)pbufs[4].data, pbufs[4].dlen);
    const std::string pBufString5((const char*)pbufs[5].data, pbufs[5].dlen);
    const std::string pBufString6((const char*)pbufs[6].data, pbufs[6].dlen);
    const std::string pBufString7((const char*)pbufs[7].data, pbufs[7].dlen);
    const std::string pBufString8((const char*)pbufs[8].data, pbufs[8].dlen);
    const std::string pBufString9((const char*)pbufs[9].data, pbufs[9].dlen);
    const std::string pBufString10((const char*)pbufs[10].data, pbufs[10].dlen);
    const std::string pBufString11((const char*)pbufs[11].data, pbufs[11].dlen);
    const std::string pBufString12((const char*)pbufs[12].data, pbufs[12].dlen);
    const std::string pBufString13((const char*)pbufs[13].data, pbufs[13].dlen);

    messages::RobotLocation localizationPB;
    messages::FilteredBall filteredBallPB;
    messages::GameState gameStatePB;
    messages::VisionField visionFieldPB;
    messages::VisionRobot visionRobotPB;
    messages::VisionObstacle visionObstaclePB;
    messages::FallStatus fallStatusPB;
    messages::MotionStatus motionStatusPB;
    messages::RobotLocation odometryPB;
    messages::JointAngles jointsPB;
    messages::StiffStatus stiffStatusPB;
    messages::FieldObstacles obstaclePB;
    messages::SharedBall sharedBallPB;
    messages::RobotLocation sharedFlipPB;

    // Parse the Protobufs
    localizationPB.ParseFromString(pBufString0);
    filteredBallPB.ParseFromString(pBufString1);
    gameStatePB.ParseFromString(pBufString2);
    visionFieldPB.ParseFromString(pBufString3);
    visionRobotPB.ParseFromString(pBufString4);
    visionObstaclePB.ParseFromString(pBufString5);
    fallStatusPB.ParseFromString(pBufString6);
    motionStatusPB.ParseFromString(pBufString7);
    odometryPB.ParseFromString(pBufString8);
    jointsPB.ParseFromString(pBufString9);
    stiffStatusPB.ParseFromString(pBufString10);
    obstaclePB.ParseFromString(pBufString11);
    sharedBallPB.ParseFromString(pBufString12);
    sharedFlipPB.ParseFromString(pBufString13);

    localizationMg = portals::Message<messages::RobotLocation>(&localizationPB);
    filteredBallMg = portals::Message<messages::FilteredBall>(&filteredBallPB);
    gameStateMg = portals::Message<messages::GameState>(&gameStatePB);
    visionFieldMg = portals::Message<messages::VisionField>(&visionFieldPB);
    visionRobotMg = portals::Message<messages::VisionRobot>(&visionRobotPB);
    visionObstacleMg = portals::Message<messages::VisionObstacle>(&visionObstaclePB);
    fallStatusMg = portals::Message<messages::FallStatus>(&fallStatusPB);
    motionStatusMg = portals::Message<messages::MotionStatus>(&motionStatusPB);
    odometryMg = portals::Message<messages::RobotLocation>(&odometryPB);
    jointsMg = portals::Message<messages::JointAngles>(&jointsPB);
    stiffStatusMg = portals::Message<messages::StiffStatus>(&stiffStatusPB);
    obstacleMg = portals::Message<messages::FieldObstacles>(&obstaclePB);
    sharedBallMg = portals::Message<messages::SharedBall>(&sharedBallPB);
    sharedFlipMg = portals::Message<messages::RobotLocation>(&sharedFlipPB);
    model = portals::Message<messages::WorldModel>(0);

    behaviors.localizationIn.setMessage(localizationMg);
    behaviors.filteredBallIn.setMessage(filteredBallMg);
    behaviors.gameStateIn.setMessage(gameStateMg);
    behaviors.visionFieldIn.setMessage(visionFieldMg);
    behaviors.visionRobotIn.setMessage(visionRobotMg);
    behaviors.visionObstacleIn.setMessage(visionObstacleMg);
    behaviors.fallStatusIn.setMessage(fallStatusMg);
    behaviors.motionStatusIn.setMessage(motionStatusMg);
    behaviors.odometryIn.setMessage(odometryMg);
    behaviors.jointsIn.setMessage(jointsMg);
    behaviors.stiffStatusIn.setMessage(stiffStatusMg);
    behaviors.obstacleIn.setMessage(obstacleMg);
    behaviors.sharedBallIn.setMessage(sharedBallMg);
    behaviors.sharedFlipIn.setMessage(sharedFlipMg);

    //TODO
    for (int i = 0; i < 5; i++) { behaviors.worldModelIn[i].setMessage(model); }

    std::cout << "Should RUn" << std::endl;

    behaviors.run();
    
    portals::InPortal<messages::MotionCommand> motionCommand;
    motionCommand.setMessage(behaviors.bodyMotionCommandOut.getMessage(true).get());
    
    std::cout << motionCommand.message().dest().gain() << std::endl;

    // std::cout << motionCommand.message().speed().x_percent() <<
    // ", " << motionCommand.message().speed().y_percent() << ", " <<
    // motionCommand.message().speed().h_percent() << std::endl;

    // portals::InPortal<messages::HeadMotionCommand> headMotionCommand;
    // headMotionCommand.setMessage(behaviors.headMotionCommandOut.getMessage(true).get());

    // float temp = headMotionCommand.message().scripted_command().command_size();
    // std::cout << temp << std::endl;
    // messages::HeadJointCommand hjc = headMotionCommand.message().scripted_command().command(0);

    // std::cout << hjc.angles().head_yaw() << std::endl;


    // TODO
    // portals::InPortal<messages::VisionBall> visionBallIn;
}