#include "nbfuncs.h"
#include <assert.h>
#include <vector>
#include <string>
#include <iostream>

#include "RoboGrams.h"
#include "Simulator.h"

using nblog::Log;
using nblog::SExpr;

int pNum;

Simulator* sim;
portals::Message<messages::WorldModel> comm[10];

int Behaviors_func() {
    // std::chrono::high_resolution_clock::time_point start = std::chrono::high_resolution_clock::now();

    assert(args.size() == 14);

    sim->setWorldModels(comm);
    sim->run(args);

    Log * bodyMotion = new Log(&sim->bodyMotion);
    SExpr s("pNum", pNum);
    bodyMotion->tree().append(s);
    rets.push_back(bodyMotion);

    // comm[pNum-1] = sim->myWM;

    // std::chrono::high_resolution_clock::time_point end = std::chrono::high_resolution_clock::now();

    // std::chrono::duration<double> timeSpan = std::chrono::duration_cast<std::chrono::duration<double>>(end-start);
    // std::cout << "Behaviors time: " << 1000*timeSpan.count() << std::endl;

    return 0;
}

int InitSim_func() {
    assert(args.size() == 1);

    pNum = std::stoi(args[0]->tree().find("pNum")->get(1)->value());

    sim = new Simulator(pNum + 2);

    portals::Message<messages::WorldModel> defaultModel(0);
    for (int i = 0; i < 10; i++) { comm[i] = defaultModel; }

    return 0;
}