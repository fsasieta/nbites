/* Motion streamer class that sends input from the tool 
 * to the robot.
 *
 * It provides the functions that will run on the robot
 * and will  be called from the motion module
 */

/* Order of arguments of the motion module commands:
 * (Numbering starts goes from 0 to some specified n)
 *
 * Odometry walk: x input, y input, degrees, gain.
 * Destination walk: x input, y input, degrees, gain, ????, kickname.
 */

#include "MotionStreamer.h"
#include "MotionCommand.h"
#include "MotionSelector.h"

using std::cout;
using std::endl;


namespace man{
namespace motion{

//MotionStreamerModule::MotionStreamerModule(MotionSelectorModule *selectorInput)
MotionStreamerModule::MotionStreamerModule(MotionSelectorModule &selectorInput)
    :   streamerOutput_(base()),
        selector(selectorInput){
        streaming = false;
        makeMap();

        cout << "Building Streamer Module" << endl;
        control::streamedData  = "randomString";
        cout << "Changed value of streamedData to: " <<control::streamedData<< endl;
}

//MotionStreamerModule::~MotionStreamerModule(){}




/* When you start streaming, you need to change the wiring.
 * the selector allows you to do that.
 */
void MotionStreamerModule::startStreaming(){
    streaming = true;
    selector.changeWiring(&streamerOutput_, true);
}

/* Always remember to stop streaming in order to make behaviors
 * work on the robot again.
 */
void MotionStreamerModule::stopStreaming(){
    streaming = false;
    selector.revertToPreviousWiring();
}

/* Main function. it parses throught the log given to determine the appropiate
 * function to call.
 *
 * Logs are in format: (commandName = name) (number of args) (args ...)
 *
 * If not, this function will not parse them right.
 * The arguments must also be in the same order.
 */
void MotionStreamerModule::streamerTool(std::string argument){
    //Parsing the arguments. Need to figure out what are we dealing with.
    //I think I should be using asserts here, but we will leave this for now
    
    cout <<"String received: \n"<< argument << endl;

    cout <<"\nRemember, parsing not implemented yet"<< endl;

    motionCommandName = "ODOMETRY_WALK";
    /*
    if(!argument.find("commandName").equals(string::npos)){

        motionCommandName = extractValue(argument, "commandName");
        std::string numberOfArguments = extractValue(argument, "numberOfArguments");
        numberOfArguments = atoi(numberOfArguments.c_str());
        
        for(i = 0; i < numberOfArguments; i++){
            
            std::string temp = "argument";
            std::string argumentNumber =  temp + i;
            argumentValue = extractValue(argument, argumentNumber);
            arguments.push_back(argumentValue);
            
        }
    }
    else{
        incorrectLog();
    }
    */
    arguments[0] =  "200";
    arguments[1] =  "200";
    arguments[2] =  "0";
    arguments[3] =  "1";

    //Once argument vector is filled, we can then call the function we want
    switch (motionFunctionsMap[motionCommandName]){
        case DESTINATION_WALK:{
            cout <<"Destination Walk not implemented yet"<< endl;
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

int MotionStreamerModule::incorrectLog(){
    stopStreaming();
    cout <<"Incorrect log, exiting"<< endl;
    return 0;
}
    
/* Figure out command name from the s expression 
 * only one command/value per (), so this should work.
 */
std::string MotionStreamerModule::extractValue(std::string input, std::string desiredValue){

    int desiredValueIndex = input.find(desiredValue); 
    int endOfValueIndex= input.find_first_of(")", desiredValueIndex);
    std::string commandName = input.substr(desiredValueIndex + 1, endOfValueIndex);

    return commandName;
}

//This function executes the Odometry walk command.
void MotionStreamerModule::executeOdometryWalk(std::vector<std::string> &argumentList){


    cout << "Executing odometry walk command in motion streamer." << endl;

    //We assume that we get the parameters in order.
    float xCm, yCm, hDeg, gain;
    xCm = (float) atoi(argumentList[0].c_str());
    yCm = (float) atoi(argumentList[1].c_str());
    hDeg = (float) atoi(argumentList[2].c_str());
    gain = (float) atoi(argumentList[3].c_str());
    
        
    //building motionCommand msg from scratch;
    messages::MotionCommand motionProto;
    motionProto.set_type(messages::MotionCommand::ODOMETRY_WALK);

    //odometrywalk is a proto inside the motioncommand proto.
    //the mutable make this possible I think
    motionProto.mutable_odometry_dest()->set_rel_x(xCm);
    motionProto.mutable_odometry_dest()->set_rel_y(xCm);
    motionProto.mutable_odometry_dest()->set_rel_h(xCm);
    motionProto.mutable_odometry_dest()->set_gain(xCm);

    //messages::OdometryWalk odomWalk;
    //odomWalk.set_rel_x(xCm);
    //motionProto.mutable_odometry_dest(odomWalk);

    portals::Message<messages::MotionCommand> motionCommand(&motionProto); 

    cout << "Setting message for the motion module to execute" << endl;

    streamerOutput_.setMessage(motionCommand);
}

/* Only used to test network connectivity */ 
void MotionStreamerModule::toolTest(std::string data){
    cout <<"Could access C++ code through the network. YAY!\n"
         <<"Should continue working on the C++ side now only"
         <<", this is the string received: \n"<< data << endl; 
}

void MotionStreamerModule::makeMap(){
    motionFunctionsMap["DESTINATION_WALK"] = DESTINATION_WALK;
    motionFunctionsMap["ODOMETRY_WALK"] = ODOMETRY_WALK;
}

/* For now the implementation of run is empty,
 * since we don't plan to make the diagram thread run
 * this everytime.
 * Also, the diagram is not intended to run the tool.
 * Put here cause the implementation of run_() is required
 * by the base class.
 *
 * Spoke to soon, may be able to make it work this way.
 */
void MotionStreamerModule::run_(){

    if(control::newCommand){
        cout << "Motion Command received through streamer" << endl;

        startStreaming();
        streamerTool(control::streamedData);
        stopStreaming();
        
        cout << "Motion Command sent through streamer" << endl;
    }
    else{
        //Nothing happens
    }

}

}//namespace motion
}//namespace man

