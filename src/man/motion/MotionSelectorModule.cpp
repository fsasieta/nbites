/* Part of the motion selector module used 
 * in the tool for streaming parameters
 *
 * .h file in this directory as well
 */

#include "MotionSelectorModule.h"

using std::cout;
using std::endl;

namespace man{
namespace motion{

//Constructor
//We don't latch or retrieve a message in the constructor because we are not
//necessarily used when the class is constructed.
MotionSelectorModule::MotionSelectorModule()
    : selectorOutput_(base()),
      //savedOutPortal(base()), //We dont need to initialize pointers
      streamerIn(base()){

    clearedPortal = false;

}

//Destructor
MotionSelectorModule::~MotionSelectorModule()
{
}

/********************
 * Wiring functions *
 ********************/

/* Doesn't actually wire portal to anything.
 * It is used for clarity in Man.cpp
 * Motion streamer wires to this first.
 * saves the outportal entered to be able to change to it later.
 * unless called by motion streamer this has no purpose.
 *
 */

/*
void MotionSelectorModule::deadEndWire(portals::OutPortal<messages::MotionCommand> *incomingOutput,
                 bool asynchronous){

    //save the dead end wire to use later.
    saveOutPortal(&incomingOutput, asynchronous);
    //saveAsynchronousValue(asynchronous);

    streamerIn = incomingOutput;
    streamerAsynch =  asynchronous;
}
*/

/* Wire the input InPortal with the corresponding OutPortal
 * Through the selector Module. Used in Man.cpp
 * TODO: to generalize, will need to implement arrays of
 * selector input/output portals. (Or map?)
 */
void MotionSelectorModule::wireModulesThroughSelector(portals::InPortal<messages::MotionCommand> *OutgoingInput,
                 portals::OutPortal<messages::MotionCommand> *IncomingOutput,
                 bool asynchronous){

    selectorInput_.wireTo(IncomingOutput, asynchronous);

    //Even though it makes conceptual sense, the code below is wrong
    //selectorOutput.wireTo(&selectorInput_, asynchronous);
    
    selectorInput_.latch();

    selectorOutput_.setMessage(&selectorInput_.message());

    OutgoingInput->wireTo(&selectorOutput_, asynchronous);

    cout << "Successfully wired module through the selector" << endl;
}

/* Use the selector portals to change the wiring.
 */
void MotionSelectorModule::changeWiring(portals::OutPortal<messages::MotionCommand> *newIncomingOutPortal,
                  bool asynchronous){

    //first need to save the current portal we are using.
    saveOutPortal(&selectorInput_, asynchronous);
    //then we wire to new portal.
    selectorInput_.wireTo(newIncomingOutPortal, asynchronous);
}

/* Use the selector portals to change the wiring.
 * specifically for the motion streamer module
 */
/*
void MotionSelectorModule::changeWiringStreamer(){

    bool asynchronous  = true;
    //first need to save the current portal we are using.
    saveOutPortal(&selectorInput_, asynchronous);
    //then we wire to new portal.
    selectorInput_.wireTo(&streamerIn, streamerAsynch);
}
*/


/* Wires the inportal to its previous state
 */
void MotionSelectorModule::revertToPreviousWiring(){

    //Wrong because outportal doesnt get asynchValue back.
    //selectorInput_.wireTo(&savedOutPortal, savedOutPortal.asynchronousValue());

    selectorInput_.wireTo(savedOutPortal, savedAsynchronousValue);
    clearSavedPortal();
        
}

void MotionSelectorModule::saveOutPortal(portals::InPortal<messages::MotionCommand> *portal, bool asynchronous){
    savedOutPortal = portal->wiredTo();
    savedAsynchronousValue = asynchronous;
}

void MotionSelectorModule::clearSavedPortal(){

    savedOutPortal = NULL;
    clearedPortal = true;
    savedAsynchronousValue = false;
}

//Necessary for compilation, since we inherit from the module class.
//Not used however, since the diagram thread shouldn't initiate us.
void MotionSelectorModule::run_(){
    //cout << "Running Motion SelectorModule" << endl;
}

}//motion
}//man
