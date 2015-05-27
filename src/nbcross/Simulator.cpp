#include "Simulator.h"
#include <typeinfo>

Simulator::Simulator(int playerNum)
: behaviors(0, playerNum%5)
{
    pNum = playerNum;
}

void Simulator::run(std::vector<Log*> pbufs)
{
    behaviors.reset();

    sendMessages(pbufs);

    behaviors.run();

    getMotionCommandsAndComm();
}

void Simulator::setWorldModels(portals::Message<messages::WorldModel> wm[])
{
    if (pNum < 5)
        for (int i(0); i < 5; i++) { behaviors.worldModelIn[i].setMessage(wm[i]); }
    else
        for (int i(5); i < 10; i++) { behaviors.worldModelIn[i].setMessage(wm[i]); }
}

void Simulator::sendMessages(std::vector<Log*> pbufs)
{
    // // The data strings from the arguments
    // const std::string pBufString0((const char*)pbufs[0].data(), pbufs[0].data().size());
    // const std::string pBufString1((const char*)pbufs[1].data(), pbufs[1].data().size());
    // const std::string pBufString2((const char*)pbufs[2].data(), pbufs[2].data().size());
    // const std::string pBufString3((const char*)pbufs[3].data(), pbufs[3].data().size());
    // const std::string pBufString4((const char*)pbufs[4].data(), pbufs[4].data().size());
    // const std::string pBufString5((const char*)pbufs[5].data(), pbufs[5].data().size());
    // const std::string pBufString6((const char*)pbufs[6].data(), pbufs[6].data().size());
    // const std::string pBufString7((const char*)pbufs[7].data(), pbufs[7].data().size());
    // const std::string pBufString8((const char*)pbufs[8].data(), pbufs[8].data().size());
    // const std::string pBufString9((const char*)pbufs[9].data(), pbufs[9].data().size());
    // const std::string pBufString10((const char*)pbufs[10].data(), pbufs[10].data().size());
    // const std::string pBufString11((const char*)pbufs[11].data(), pbufs[11].data().size());
    // const std::string pBufString12((const char*)pbufs[12].data(), pbufs[12].data().size());
    // const std::string pBufString13((const char*)pbufs[13].data(), pbufs[13].data().size());

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
    localizationPB.ParseFromString(pbufs[0]->data());
    filteredBallPB.ParseFromString(pbufs[1]->data());
    gameStatePB.ParseFromString(pbufs[2]->data());
    visionFieldPB.ParseFromString(pbufs[3]->data());
    visionRobotPB.ParseFromString(pbufs[4]->data());
    visionObstaclePB.ParseFromString(pbufs[5]->data());
    fallStatusPB.ParseFromString(pbufs[6]->data());
    motionStatusPB.ParseFromString(pbufs[7]->data());
    odometryPB.ParseFromString(pbufs[8]->data());
    jointsPB.ParseFromString(pbufs[9]->data());
    stiffStatusPB.ParseFromString(pbufs[10]->data());
    obstaclePB.ParseFromString(pbufs[11]->data());
    sharedBallPB.ParseFromString(pbufs[12]->data());
    sharedFlipPB.ParseFromString(pbufs[13]->data());


    // TODO TRYING TO FIX MESSAGE OVERFLOWS

    // std::cout << "WAIT FOR OVERFLOW" << std::endl;

    localizationMg = portals::Message<messages::RobotLocation>(&localizationPB);
    behaviors.localizationIn.setMessage(localizationMg);

    filteredBallMg = portals::Message<messages::FilteredBall>(&filteredBallPB);
    behaviors.filteredBallIn.setMessage(filteredBallMg);

    gameStateMg = portals::Message<messages::GameState>(&gameStatePB);
    behaviors.gameStateIn.setMessage(gameStateMg);

    visionFieldMg = portals::Message<messages::VisionField>(&visionFieldPB);
    behaviors.visionFieldIn.setMessage(visionFieldMg);

    visionRobotMg = portals::Message<messages::VisionRobot>(&visionRobotPB);
    behaviors.visionRobotIn.setMessage(visionRobotMg);

    visionObstacleMg = portals::Message<messages::VisionObstacle>(&visionObstaclePB);
    behaviors.visionObstacleIn.setMessage(visionObstacleMg);

    fallStatusMg = portals::Message<messages::FallStatus>(&fallStatusPB);
    behaviors.fallStatusIn.setMessage(fallStatusMg);

    motionStatusMg = portals::Message<messages::MotionStatus>(&motionStatusPB);
    behaviors.motionStatusIn.setMessage(motionStatusMg);

    localizationMg = portals::Message<messages::RobotLocation>(&odometryPB);
    behaviors.odometryIn.setMessage(localizationMg);

    // std::cout << "DID WE OVERFLOW?" << std::endl;

    jointsMg = portals::Message<messages::JointAngles>(&jointsPB);
    behaviors.jointsIn.setMessage(jointsMg);

    stiffStatusMg = portals::Message<messages::StiffStatus>(&stiffStatusPB);
    behaviors.stiffStatusIn.setMessage(stiffStatusMg);

    obstacleMg = portals::Message<messages::FieldObstacles>(&obstaclePB);
    behaviors.obstacleIn.setMessage(obstacleMg);

    sharedBallMg = portals::Message<messages::SharedBall>(&sharedBallPB);
    behaviors.sharedBallIn.setMessage(sharedBallMg);

    localizationMg = portals::Message<messages::RobotLocation>(&sharedFlipPB);
    behaviors.sharedFlipIn.setMessage(localizationMg);
}

void Simulator::getMotionCommandsAndComm()
{
    hmc = *behaviors.headMotionCommandOut.getMessage(true).get();

    std::string hmcData;
    hmc.SerializeToString(&hmcData);

    headMotion.setData(hmcData);

    bmc = *behaviors.bodyMotionCommandOut.getMessage(true).get();

    std::string bmcData;
    hmc.SerializeToString(&bmcData);

    bodyMotion.setData(bmcData);

    messages::WorldModel wm = *behaviors.myWorldModelOut.getMessage(true).get();
    myWM = portals::Message<messages::WorldModel>(&wm);
}