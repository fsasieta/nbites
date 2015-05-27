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

MotionStreamerModule::MotionStreamerModule():
    portals::Module()    
    {
    streaming = false;
    }

    void start(){
        streaming = true;
    }
    void stop(){
        streaming = false;
    }

    int main (int argc, char *argv[]){

        //parameters are somewhat randomish, the first 4 make sense,
        //DestinationWalk test = new DestinationWalk(100, 100, 2, .5);
        //sendMotionCommand(test);

        executeWalk(100, 100, 0, .5);

    }

    int executeWalk(int xCm, int yCm, int hDeg, int gain){
        //Get the parameters from somewhere else

        //make the message.
        //This is odometry walk, so the robot should stand at destination.
        //Probably should change the name of walkThere, leaving it for now as is.
        walkThere.set_rel_x(xCm);
        walkThere.set_rel_y(yCm);
        walkThere.set_rel_h(hDeg);
        walkThere.set_gain(gain);

        streamerWalk_.setMessage(walkThere)
    }

    void test(){
        cout<<"Could access C++ code through the network. YAY!\n"
            <<"Should continue working on the c++ side now only"<<endl; 
    }



}//namespace motion
}//namespace man
