#pragma once

#include "Common.h"
#include "RoboGrams.h"

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

class SimulatorModule : public portals::Module
{
public:
    SimulatorModule(messages::RobotLocation localizationPB,
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
                    messages::RobotLocation sharedFlipPB);
    virtual ~SimulatorModule();

    // Runs the module
    virtual void run_();

private:
    void sendMessages();    

private:
    // Pointers to the Protobufs
    const messages::RobotLocation* localizationPt;
    const messages::FilteredBall* filteredBallPt;
    const messages::GameState* gameStatePt;
    const messages::VisionField* visionFieldPt;
    const messages::VisionRobot* visionRobotPt;
    const messages::VisionObstacle* visionObstaclePt;
    const messages::FallStatus* fallStatusPt;
    const messages::MotionStatus* motionStatusPt;
    const messages::RobotLocation* odometryPt;
    const messages::JointAngles* jointsPt;
    const messages::StiffStatus* stiffStatusPt;
    const messages::FieldObstacles* obstaclePt;
    const messages::SharedBall* sharedBallPt;
    const messages::RobotLocation* sharedFlipPt;

    // The OutPortals
    portals::OutPortal<messages::RobotLocation> localizationOut;
    // portals::OutPortal<messages::FilteredBall> filteredBallOut;
    portals::OutPortal<messages::GameState> gameStateOut;
    portals::OutPortal<messages::VisionField> visionFieldOut;
    portals::OutPortal<messages::VisionRobot> visionRobotOut;
    portals::OutPortal<messages::VisionObstacle> visionObstacleOut;
    portals::OutPortal<messages::FallStatus> fallStatusOut;
    portals::OutPortal<messages::MotionStatus> motionStatusOut;
    portals::OutPortal<messages::RobotLocation> odometryOut;
    portals::OutPortal<messages::JointAngles> jointsOut;
    portals::OutPortal<messages::StiffStatus> stiffStatusOut;
    portals::OutPortal<messages::FieldObstacles> obstacleOut;
    portals::OutPortal<messages::SharedBall> sharedBallOut;
    portals::OutPortal<messages::RobotLocation> sharedFlipOut;

    man::behaviors::BehaviorsModule behaviors;
};
