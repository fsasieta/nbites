//
//  nbfuncs.cpp
//  nbcross
//
//  Created by Philip Koch on 11/28/14.
//

#include "nbfuncs.h"
#include <assert.h>
#include <vector>
#include <stdlib.h>
#include <chrono>

std::vector<nbfunc_t> FUNCS;

std::vector<logio::log_t> args;
std::vector<logio::log_t> rets;

std::vector<Simulator *> sims;
portals::Message<messages::WorldModel> comm[10];

int pIndex = 0;

//Common arg types -- used to check arg types and for human readability.
const char sYUVImage[] = "YUVImage";
const char sParticleSwarm_pbuf[] = "ParticleSwarm";
const char sParticle_pbuf[] = "Particle";
const char sTest[] = "test";
const char sInt[] = "int";
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

// TODO MAKE A COMM ARRAY
int Behaviors_func() {
    // std::chrono::high_resolution_clock::time_point start = std::chrono::high_resolution_clock::now();

    assert(args.size() == 14);

    Simulator *sim = sims.at(pIndex);
    int pNum = sim->pNum;

    std::cout << pNum <<std::endl;
    sim->setWorldModels(comm);
    sim->run(args);

    logio::log_t bodyMotion = logio::copyLog(&sim->bodyMotion);
    rets.push_back(bodyMotion);

    comm[pNum-1] = sim->myWM;

    pIndex = (pIndex + 1) % sims.size();

    // std::chrono::high_resolution_clock::time_point end = std::chrono::high_resolution_clock::now();

    // std::chrono::duration<double> timeSpan = std::chrono::duration_cast<std::chrono::duration<double>>(end-start);
    // std::cout << "Behaviors time: " << 1000*timeSpan.count() << std::endl;

    return 0;
}

// TODO this should be changed to one call per player and be sent the playerNum
int InitSim_func() {
    assert(args.size() == 1);

    int pNum = static_cast<int>(*args[0].data);
    std::cout << pNum << std::endl;
    Simulator *sim = new Simulator(pNum + 2);
    sims.push_back(sim);

    for (int i = 0; i < 10; i++) { comm[i] = portals::Message<messages::WorldModel>(0); }

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
                        sRobotLocation_pbuf,
                    };
    Behaviors.func = Behaviors_func;
    FUNCS.push_back(Behaviors);

    //InitSim
    nbfunc_t InitSim;
    InitSim.name = "InitSim";
    InitSim.args = {sInt};
    InitSim.func = InitSim_func;
    FUNCS.push_back(InitSim);
}



