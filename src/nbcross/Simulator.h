#pragma once

#include "Common.h"
#include "RoboGrams.h"
#include "../man/log/logio.h"

#include "behaviors/BehaviorsModule.h"

#include "GameState.pb.h"
#include "BallModel.pb.h"
#include "LedCommand.pb.h"
#include "WorldModel.pb.h"
#include "PMotion.pb.h"
#include "MotionStatus.pb.h"
#include "VisionField.pb.h"
#include "VisionRobot.pb.h"
#include "ButtonState.pb.h"
#include "FallStatus.pb.h"
#include "RobotLocation.pb.h"
#include "StiffnessControl.pb.h"
#include "Obstacle.pb.h"

class Simulator
{
public:
    Simulator();
    void run(std::vector<logio::log_t> pbufs);

private:
    portals::Message<messages::RobotLocation> localizationMg;
    portals::Message<messages::FilteredBall> filteredBallMg;
    portals::Message<messages::GameState> gameStateMg;
    portals::Message<messages::VisionField> visionFieldMg;
    portals::Message<messages::VisionRobot> visionRobotMg;
    portals::Message<messages::VisionObstacle> visionObstacleMg;
    portals::Message<messages::FallStatus> fallStatusMg;
    portals::Message<messages::MotionStatus> motionStatusMg;
    portals::Message<messages::RobotLocation> odometryMg;
    portals::Message<messages::JointAngles> jointsMg;
    portals::Message<messages::StiffStatus> stiffStatusMg;
    portals::Message<messages::FieldObstacles> obstacleMg;
    portals::Message<messages::SharedBall> sharedBallMg;
    portals::Message<messages::RobotLocation> sharedFlipMg;
    portals::Message<messages::WorldModel> model;

    man::behaviors::BehaviorsModule behaviors;
};
