/* Class to stream motion commands directly to the robot,
 * using the motion module commands. In short, it sends
 * commands to the motion module, which then executes them.
 * The class gets manual input from the Java tool.
 *
 * Therefore this class takes no inportals
 *
 * @author: Franco Sasieta
 * @email: fsasieta@bowdoin.edu
 * Last modified: 05/28/2016
 *
 */

#include "RoboGrams.h"
#include "MotionModule.h"
#include "motionSelectorModule.h"
#include <fstream>

// Motion commands
#include "BodyJointCommand.h"
#include "WalkCommand.h"
#include "DestinationCommand.h"
#include "StepCommand.h"
#include "FreezeCommand.h"
#include "UnfreezeCommand.h"

// Messages
#include "InertialState.pb.h"
#include "RobotLocation.pb.h"
#include "PMotion.pb.h"
#include "MotionStatus.pb.h"
#include "StiffnessControl.pb.h"
#include "FallStatus.pb.h"
#include "HandSpeeds.pb.h"

#include <vector>


namespace man{
namespace motion{
class MotionStreamerModule: public portals:: Module{

public:
    
    //Constructor
    MotionStreamerModule();
    //Not sure what the virtual method is actually for
    virtual ~MotionStreamerModule();

    /*Start streaming parameters
     */
    void start();

    /*Stop streaming parameters
     */
    void stop();

    /* Test Function that sends messages to the motion module
     */
    int executeOdometryWalk(&vector<String> argumentList);

    //test function only. used for network connectivity.
    void test(String data);


    //For now we just do one outportal, since we need to make this work first.
    //There are only outputs outportals in this module.
    /** Out Portals **/
    portals::OutPortal<messages::MotionCommand> streamerOutput;

private:

    String motionCommandName;
    int numberOfArguments;
    bool streaming;

    vector<String> arguments
    map<string, motionFunction> motionFunctionsMap;

    messages::MotionCommand motionCommand;

    String extractValue(String input, String desiredValue);

};//class 

}//namespace motion
}//namespace man
