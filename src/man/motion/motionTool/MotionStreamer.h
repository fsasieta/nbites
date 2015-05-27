/* Class to stream motion commands directly to the robot,
 * using the motion module commands. In short, it sends
 * commands to the motion module, which then executes them.
 * The class gets manual input from the Java tool.
 *
 * Therefore this class takes no inportals
 *
 * @author: Franco Sasieta
 * @email: fsasieta@bowdoin.edu
 * Last modified: 05/21/2016
 */


#include "Robograms.h"


#include "MotionModule.h"
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
    int executeWalk();



    //For now we just do one outportal, since we need to make this work first.
    /** Out Portals **/
    portals::Outportal<messages::MotionCommand> streamerWalkInput_;

private:
    
    bool streaming;

    //functions/variables I will not provide
    messages::MotionCommand walkThere;

};

}
}
