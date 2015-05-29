/* Motion streamer class that sends input from the tool 
 * to the robot.
 *
 * It provides the functions that will run on the robot
 * and will  be called from the motion module
 *
 * 
 */

/* Order of arguments of the motion module commands:
 *
 * Odometry walk: x input, y input, degrees, gain.
 * Destination walk: x input, y input, degrees, gain, ????, kickname.
 */
#include "MotionStreamer.h"
#include "MotionCommand.h"

namespace man{
namespace motion{

MotionStreamerModule::MotionStreamerModule():
    portals::Module()    
    {
    streaming = false;
    makeMap();
    }

    void start(){
        streaming = true;
    }

    void stop(){
        streaming = false;
    }


    /* Main function. it parses throught the log given to determine the appropiate
     * function to call.
     *
     * Logs are in format: (commandName = name) (number of args) (args ...)
     *
     * If not, this function will not parse them right.
     * The arguments must also be in the same order.
     */
    void streamerTool(String argument){
        //Parsing the arguments. Need to figure out what are we dealing with.
        if(!argument.find("commandName").equals(string::npos)){

            motionCommandName = extractValue(argument, "commandName");
            String numberOfArguments = extractValue(argument, "numberOfArguments");
            numberOfArguments = atoi(numberOfArguments.c_str());
            
            for(i = 0; i < numberOfArguments; i++){
                
                String temp = "argument";
                String argumentNumber =  temp + i;
                argumentValue = extractValue(argument, argumentNumber);
                arguments.push_back(argumentValue);
                
            }
        }
        else{
            incorrectLog();
        }

        //Once argument vector is filled, we can then call the function we want
        switch (motionFunctionsMap[motionCommandName]){
            case DESTINATION_WALK:{
                break;
            }
            case ODOMETRY_WALK:{
                executeOdometryWalk(arguments);
                break;
            }
            default:{
                break;
            }
        };
    }

    enum motionFunction = {DESTINATION_WALK,
                           ODOMETRY_WALK
                          };

    void incorrectLog(){
        cout<<"Incorrect log, exiting"<<endl;
        system.exit(0);
    }
        
    /* Figure out command name from the s expression 
     * only one command/value per (), so this should work.
     */
    String extractValue(String input, String desiredValue){

        int desiredValueIndex = input.find(desiredValue); 
        int endOfValueIndex= argument.find_first_of(")", desiredValueIndex);
        String valueNoTrim = argument.substr(cmdIndex + 1, endOfValueIndex);
        String value = valueNoTrim.trim();

        return value;

    }

    //This function executes the Odometry walk command.
    int executeOdometryWalk(&vector<String> argumentList){

        //We assume that we get the parameters in order.
        int xCm, yCm, hDeg, gain;
        xCm = atoi(argumentList[0].c_str());
        yCm = atoi(argumentList[1].c_str());
        hDeg = atoi(argumentList[2].c_str());
        gain = atoi(argumentList[3].c_str());
        
        //make the message.
        //This is odometry walk, so the robot should stand at destination.
        //Probably should change the name of walkThere, leaving it for now as is.
        motionCommand = portals::Message<messages::MotionCommand> motionCommand(0); 
        motionCommand.set_rel_x(xCm);
        motionCommand.set_rel_y(yCm);
        motionCommand.set_rel_h(hDeg);
        motionCommand.set_gain(gain);

        streamerOutput.setMessage(motionCommand);
    }

    /* Only used to test network connectivity */ 
    void toolTest(String data){
        cout<<"Could access C++ code through the network. YAY!\n"
            <<"Should continue working on the C++ side now only"
            <<", this is the int data: "<< data <<endl; 
    }

    void makeMap(){
        motionFunctionsMap["DESTINATION_WALK"] = DESTINATION_WALK;
        motionFunctionsMap["ODOMETRY_WALK"] = ODOMETRY_WALK;
    }

}//namespace motion
}//namespace man
