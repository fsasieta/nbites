#include "nbfuncs.h"
#include <assert.h>
#include <vector>
#include <string>
#include <iostream>

#include "RoboGrams.h"
#include "Simulator.h"

using nblog::Log;
using nblog::SExpr;

std::vector<Simulator *> sims;
portals::Message<messages::WorldModel> comm[10];

int pIndex = 0;

int Behaviors_func() {
    // std::chrono::high_resolution_clock::time_point start = std::chrono::high_resolution_clock::now();

    assert(args.size() == 14);

    Simulator *sim = sims.at(pIndex);
    int pNum = sim->pNum;

    sim->setWorldModels(comm);
    sim->run(args);

    Log bodyMotion = Log(&sim->bodyMotion);
    rets.push_back(&bodyMotion);

    comm[pNum-1] = sim->myWM;

    pIndex = (pIndex + 1) % sims.size();

    // std::chrono::high_resolution_clock::time_point end = std::chrono::high_resolution_clock::now();

    // std::chrono::duration<double> timeSpan = std::chrono::duration_cast<std::chrono::duration<double>>(end-start);
    // std::cout << "Behaviors time: " << 1000*timeSpan.count() << std::endl;

    return 0;
}

int InitSim_func() {
    assert(args.size() == 1);

    // int pNum = static_cast<int>(args[0]->data().data());
    int pNum = 2;

    if (pNum == 100) {
        sims.clear();
        pIndex = 0;
    }

    else {
        Simulator *sim = new Simulator(pNum + 2);
        sims.push_back(sim);

        portals::Message<messages::WorldModel> defaultModel(0);
        for (int i = 0; i < 10; i++) { comm[i] = defaultModel; }
    }

    return 0;
}