//
//  nbfuncs.cpp
//  nbcross
//
//  Created by Philip Koch on 11/28/14.
//

#include "nbfuncs.h"
#include <assert.h>
#include <vector>

#include "behaviors/BehaviorsModule.h"
#include "SimulatorModule.h"

std::vector<nbfunc_t> FUNCS;

std::vector<logio::log_t> args;
std::vector<logio::log_t> rets;

//Common arg types -- used to check arg types and for human readability.
const char sYUVImage[] = "YUVImage";
const char sParticleSwarm_pbuf[] = "ParticleSwarm";
const char sParticle_pbuf[] = "Particle";
const char sTest[] = "test";
const char sRobotLocation_pbuf[] = "RobotLocation";
const char sFilteredBall_pbuf[] = "FilteredBall";
const char sGameState_pbuf[] = "GameState";
const char sVisionField_pbuf[] = "VisionField";
const char sVisionRobot_pbuf[] = "VisionRobot";
const char sVisionObstacle_pbuf[] = "VisionObstacle";
const char sFallStatus_pbuf[] = "FallStatus";
const char sMotionStatus_pbuf[] = "MotionStatus";
const char sJoints_pbuf[] = "Joints";
const char sStiffStatus_pbuf[] = "StiffStatus";
const char sObstacle_pbuf[] = "Obstacle";
const char sSharedBall_pbuf[] = "SharedBall";

const char stext[] = "text";//No current sources for this data type.

int test_func() {
    assert(args.size() == 2);
    for (int i = 0; i < args.size(); ++i) {
        printf("test_func(): %s\n", args[i].desc);
    }
    
    return 0;
}

int arg_test_func() {
    printf("arg_test_func()\n");
    assert(args.size() == 2);
    for (int i = 0; i < 2; ++i) {
        printf("\t%s\n", args[i].desc);
        rets.push_back(logio::copyLog(&args[i]));
    }
    
    return 0;
}

int CrossBright_func() {
    assert(args.size() == 1);
    printf("CrossBright_func()\n");
    //work on a copy of the arg so we can safely push to rets.
    logio::log_t log = logio::copyLog(&args[0]);
    for (int i = 0; i < log.dlen; i += 2) {
        *(log.data + i) = 240;
    }
    
    printf("[%s] modified.\n", log.desc);
    rets.push_back(log);
    
    return 0;
}

int Behaviors_func() {
    const int ARG_SIZE = 14;

    assert(args.size() == ARG_SIZE);

    // The Protobufs
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

    // The data strings from the arguments
    const std::string pBufString0((const char*)args[0].data, args[0].dlen);
    const std::string pBufString1((const char*)args[1].data, args[1].dlen);
    const std::string pBufString2((const char*)args[2].data, args[2].dlen);
    const std::string pBufString3((const char*)args[3].data, args[3].dlen);
    const std::string pBufString4((const char*)args[4].data, args[4].dlen);
    const std::string pBufString5((const char*)args[5].data, args[5].dlen);
    const std::string pBufString6((const char*)args[6].data, args[6].dlen);
    const std::string pBufString7((const char*)args[7].data, args[7].dlen);
    const std::string pBufString8((const char*)args[8].data, args[8].dlen);
    const std::string pBufString9((const char*)args[9].data, args[9].dlen);
    const std::string pBufString10((const char*)args[10].data, args[10].dlen);
    const std::string pBufString11((const char*)args[11].data, args[11].dlen);
    const std::string pBufString12((const char*)args[12].data, args[12].dlen);
    const std::string pBufString13((const char*)args[13].data, args[13].dlen);

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

    SimulatorModule simulator(localizationPB, filteredBallPB, gameStatePB, 
                                        visionFieldPB, visionRobotPB, visionObstaclePB, 
                                        fallStatusPB, motionStatusPB, odometryPB, jointsPB, 
                                        stiffStatusPB, obstaclePB, sharedBallPB, sharedFlipPB);
    simulator.run();

    // const std::string testData((const char*)log.data, log.dlen);
    // messages::FilteredBall filtBall;
    //  if (filtBall.ParseFromString(testData))
    //  {
    //      std::cout << "Ball x: " << filtBall.x() << 
    //                  " Ball y: " << filtBall.y() << 
    //                  " Ball Dist: " << filtBall.distance();
    //  } 

    return 0;
}

void register_funcs() {
    
    /*test func 1*/
    nbfunc_t test;
    test.name = (const char *) "simple test";
    test.args = {sTest, sTest};
    test.func = test_func;
    FUNCS.push_back(test);
    
    /*test func 2*/
    nbfunc_t arg_test;
    arg_test.name = (char *) "arg test";
    arg_test.args = {sYUVImage, sYUVImage};
    arg_test.func = arg_test_func;
    FUNCS.push_back(arg_test);
    
    //CrossBright
    nbfunc_t CrossBright;
    CrossBright.name = "CrossBright";
    CrossBright.args = {sYUVImage};
    CrossBright.func = CrossBright_func;
    FUNCS.push_back(CrossBright);

    //Behaviors
    nbfunc_t Behaviors;
    Behaviors.name = "Behaviors";
    Behaviors.args = {
                        sRobotLocation_pbuf,
                        sFilteredBall_pbuf,
                        sGameState_pbuf,
                        sVisionField_pbuf,
                        sVisionRobot_pbuf,
                        sVisionObstacle_pbuf,
                        sFallStatus_pbuf,
                        sMotionStatus_pbuf,
                        sRobotLocation_pbuf,
                        sJoints_pbuf,
                        sStiffStatus_pbuf,
                        sObstacle_pbuf,
                        sSharedBall_pbuf,
                        sRobotLocation_pbuf
                    };
    Behaviors.func = Behaviors_func;
    FUNCS.push_back(Behaviors);
}


