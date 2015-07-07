#pragma once

#include "Common.h"
#include "RoboGrams.h"
#include "../share/logshare/Log.h"

#include "behaviors/BehaviorsModule.h"

#include "GameState.pb.h"
#include "BallModel.pb.h"
#include "LedCommand.pb.h"
#include "WorldModel.pb.h"
#include "PMotion.pb.h"
#include "MotionStatus.pb.h"
#include "Vision.pb.h"
#include "ButtonState.pb.h"
#include "FallStatus.pb.h"
#include "RobotLocation.pb.h"
#include "StiffnessControl.pb.h"
#include "Obstacle.pb.h"

using nblog::Log;
using nblog::SExpr;

class Simulator
{
public:
    Simulator(int pNum);
    void run(std::vector<Log*> pbufs);
    void setWorldModels(portals::Message<messages::WorldModel> wm[]);

public:
    Log bodyMotion;
    Log headMotion;
    portals::Message<messages::WorldModel> myWM;

    int pNum;

private:
    void sendMessages(std::vector<Log*> pbufs);
    void getMotionCommandsAndComm();

private:
    portals::Message<messages::RobotLocation> localizationMg;
    portals::Message<messages::FilteredBall> filteredBallMg;
    portals::Message<messages::GameState> gameStateMg;
    portals::Message<messages::FallStatus> fallStatusMg;
    portals::Message<messages::MotionStatus> motionStatusMg;
    portals::Message<messages::RobotLocation> odometryMg;
    portals::Message<messages::JointAngles> jointsMg;
    portals::Message<messages::StiffStatus> stiffStatusMg;
    portals::Message<messages::FieldObstacles> obstacleMg;
    portals::Message<messages::FieldLines> linesMg;
    portals::Message<messages::Corners> cornersMg;
    portals::Message<messages::SharedBall> sharedBallMg;
    portals::Message<messages::RobotLocation> sharedFlipMg;
    portals::Message<messages::Toggle> sitDownMg;
    portals::Message<messages::WorldModel> model;

    messages::HeadMotionCommand hmc;
    messages::MotionCommand bmc;

    man::behaviors::BehaviorsModule behaviors;
};
