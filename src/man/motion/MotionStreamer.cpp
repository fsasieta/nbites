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

namespace man{
namespace motion{

//MotionStreamerModule::MotionStreamerModule(MotionSelectorModule *selectorInput)

MotionStreamerModule::MotionStreamerModule(MotionSelectorModule &selectorInput)
    :   streamerOutput_(base()),
        selector(selectorInput){
        streaming = false;
        makeMap();
}

//MotionStreamerModule::~MotionStreamerModule(){}


/* For now the implementation of run is empty,
 * since we don't plan to make the diagram thread run
 * this everytime.
 * Also, the diagram is not intended to run the tool.
 * Put here cause the implementation of run_() is required
 * by the base class.
 */
void MotionStreamerModule::run_(){
}

/* When you start streaming, you need to change the wiring.
 * the selector allows you to do that.
 */
void MotionStreamerModule::startStreaming(){
    streaming = true;
    selector.changeWiring(streamerOutput_, true);
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
    
    std::cout<<"String received: \n"<<argument<<std::endl;

    std::cout<<"\nRemember, parsing not implemented yet"<<std::endl;

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
            std::cout<<"DestinationWalk not implemented yet"<<std::endl;
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
    std::cout<<"Incorrect log, exiting"<<std::endl;
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

    //We assume that we get the parameters in order.
    float xCm, yCm, hDeg, gain;
    xCm = (float) atoi(argumentList[0].c_str());
    yCm = (float) atoi(argumentList[1].c_str());
    hDeg = (float) atoi(argumentList[2].c_str());
    gain = (float) atoi(argumentList[3].c_str());
    
    //make the message.
    //This is odometry walk, so the robot should stand at destination.
    //None of the commented stuff works! (as of right now)
    //portals::Message<messages::MotionCommand> motionCommand(0); 
    //portals::Message<messages::OdometryWalk> odoWalk(0);

    //messages::MotionCommand motionMsg = 
    //motionCommand.get()->set_type(messages::MotionCommand::ODOMETRY_WALK);
    //motionMsg.mutable_odometry_dest().set_rel_x(xCm);
    //motionMsg.mutable_odometry_dest().set_rel_y(yCm);
    //motionMsg.mutable_odometry_dest().set_rel_h(hDeg);
    //motionMsg.mutable_odometry_dest().set_gain(gain);
    //motionCommand.get()->odometry_dest

    //odoWalk.get()->set_rel_x(xCm);
    //odoWalk.get()->set_rel_y(yCm);
    //odoWalk.get()->set_rel_h(hDeg);
    //odoWalk.get()->set_gain(gain);
    
    //building motionCommand msg from scratch;
    messages::MotionCommand motionProto;
    motionProto.set_type(messages::MotionCommand::ODOMETRY_WALK);

    //literally no idea why this syntax works
    //odometrywalk is a proto inside the motioncommand proto.
    //the mutable make this possible I think
    motionProto.mutable_odometry_dest()->set_rel_x(xCm);
    motionProto.mutable_odometry_dest()->set_rel_y(xCm);
    motionProto.mutable_odometry_dest()->set_rel_h(xCm);
    motionProto.mutable_odometry_dest()->set_gain(xCm);

    //messages::OdometryWalk odomWalk;
    //odomWalk.set_rel_x(xCm);
    //odomWalk.set_rel_y(yCm);
    //odomWalk.set_rel_h(hDeg);
    //odomWalk.set_gain(gain);

    //motionProto.mutable_odometry_dest(odomWalk);

    portals::Message<messages::MotionCommand> motionCommand(&motionProto); 

    //motionCommand.get()->set_type(messages::MotionCommand::ODOMETRY_WALK);
    //motionCommand.get()->mutable_odometry_dest() = odomWalk;

    streamerOutput_.setMessage(motionCommand);
}

/* Only used to test network connectivity */ 
void MotionStreamerModule::toolTest(std::string data){
    std::cout<<"Could access C++ code through the network. YAY!\n"
        <<"Should continue working on the C++ side now only"
        <<", this is the string received: \n"<< data <<std::endl; 
}

void MotionStreamerModule::makeMap(){
    motionFunctionsMap["DESTINATION_WALK"] = DESTINATION_WALK;
    motionFunctionsMap["ODOMETRY_WALK"] = ODOMETRY_WALK;
}

}//namespace motion
}//namespace man

