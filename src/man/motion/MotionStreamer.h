/* Class to stream motion commands directly to the robot,
 * using the motion module commands. In short, it sends
 * commands to the motion module, which then executes them.
 * The class gets manual input from the Java tool.
 *
 * Therefore this class takes __no__ inportals
 *
 * @author: Franco Sasieta
 * @email: fsasieta@bowdoin.edu
 * Last modified: 05/28/2016
 */

#ifndef MOTION_STREAMER_H
#define MOTION_STREAMER_H

#include "RoboGrams.h"
#include "MotionModule.h"
#include "MotionSelectorModule.h"
#include "control.h"

//Standard lib 
#include <iostream>
#include <ostream>
#include <vector>
#include <string>
#include <map>

// Motion commands
//#include "BodyJointCommand.h"
//#include "WalkCommand.h"
//#include "DestinationCommand.h"
//#include "StepCommand.h"
//#include "FreezeCommand.h"
//#include "UnfreezeCommand.h"

// Messages
#include "InertialState.pb.h"
#include "RobotLocation.pb.h"
#include "PMotion.pb.h"
#include "MotionStatus.pb.h"
#include "StiffnessControl.pb.h"
#include "FallStatus.pb.h"
#include "HandSpeeds.pb.h"


namespace man{
namespace motion{
class MotionStreamerModule : public portals:: Module{
public:
    
    //Constructor
    //MotionStreamerModule(MotionSelectorModule *selectorInput);
    //^ compiles somewhat, this is a new try
    MotionStreamerModule(MotionSelectorModule &selectorInput);
    

    //extern *this;


    //Not sure what the virtual keyword is actually for.
    //~MotionStreamerModule();
    
    /*Start streaming parameters
     */
    void startStreaming();
    
    /*Stop streaming parameters
     */
    void stopStreaming();
    
    /* For now starts everything
     * It will contain the main logic of the tool
     */
    void streamerTool(std::string argument);
    
    /* Test Function that sends messages to the motion module
     */
    void executeOdometryWalk(std::vector<std::string> &argumentList );
    
    /* Test function only. used for network connectivity.
     */
    void toolTest(std::string data);

    /* This needs to be called; before anything else.
     */
    void setSelector(MotionSelectorModule selectorInput);
     
    //There are only outputs outportals in this module.
    /** Out Portals **/
    portals::OutPortal<messages::MotionCommand> streamerOutput_;
    
private:
    
    enum motionFunction : int {DESTINATION_WALK,
                               ODOMETRY_WALK
                              };
    
    std::string motionCommandName;
    int numberOfArguments;
    bool streaming;
    bool selectorInstance;

    std::string previousDataReceived;

    //Pointer battle to figure out how to do this.
    //MotionSelectorModule * selector;
    MotionSelectorModule &selector;


    std::vector<std::string> arguments;
    std::map<std::string, enum motionFunction> motionFunctionsMap;
    //messages::MotionCommand motionCommand;
    
    void run_();
    std::string extractValue(std::string input, std::string desiredValue);
    void makeMap();
    int incorrectLog();

};//class 
}//namespace motion
}//namespace man

#endif //MOTION_STREAMER_H
