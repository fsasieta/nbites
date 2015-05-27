/* Class to stream motion commands directly to the robot,
 * using the motion module commands. In short, it sends
 * commands to the motion module, which then executes them.
 * The class gets manual input from the Java tool.
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



class MotionStreamerModule: public portals:: Module{

public:
    //Functions I will provide
    MotionStreamerModule();

    //Not sure what the cirtual method is actually for
    virtual ~MotionStreamerModule();

    /*Start streaming parameters
     */
    void start();

    /*Stop streaming parameters
     */
    void stop();

    //For now we just do one outportal, since we need to make this work first.
    portals::Outportal<messages::DestinationWalkCommand> walkThereCommand;

private:
    //functions/vaariables I will not provide
    //bool streaming; //given by module base class as "running" methoe
}
