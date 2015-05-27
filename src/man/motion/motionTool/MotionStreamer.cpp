/* Motion streamer class that sends input from the tool 
 * to the robot.
 * It connects with the "MotionStreamer" in the gui package, which 
 * itself takes input from the MotionEnginePanel .
 *
 * This class uses motionModule commands to send to the robot.
 *
 * Should it inherit from the motion module then?
 * I think thats a possibility.
 *
 * Pros: you inherit stuff
 * Cons: I'm not sure what that implies though,
 * since we need to send stuff over to the motion module.
 * 
 */


#include "MotionStreamer.h"
// used to call a destination walk function in the motion module
#include "DestinationCommand.h"


namespace man{
namespace motion{

    MotionStreamerModule::~MotionStreamerModule(){}

    void start(){
        streaming = true;
    }
    void stop(){
        streaming = false;
    }

    int main (int argc, char*argv[]){

        //Testing purposes only
        //Should work?
        //parameters are somewhat randomish, the first 4 make sense,
        //total gamble on the kick ones.
        //DestinationWalk test = new DestinationWalk(100, 100, 2, .5, 0, 0);
        //sendMotionCommand(test);

        
        //Need to the the wiring of an in/out portal.
        //main code








    }

}//namespace motion
}//namespace man

