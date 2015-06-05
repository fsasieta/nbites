/* Part of the motion selector module used 
 * in the tool for streaming parameters
 *
 * .h file in this directory as well
 */


namespace man{
namespace motion{

//constructor
MotionSelectorModule::MotionSelectorModule()
    : selectorOutput(base())
{}

//destructor
MotionSelectorModule::~MotionSelectorModule{}



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
void deadEndWire(portals::OutPortal<messages::MotionCommand> *incomingOutput,
                 bool asynchronous){

    //save the dead end wire to use later.
    saveOutPortal(&incomingOutput, asynchronous);
    //saveAsynchronousValue(asynchronous);

    streamerIn = incomingOutput;
    streamerAsynch =  asynchronous;
}

/* Wire the input InPortal with the corresponding OutPortal
 * Through the selector Module. Used in Man.cpp
 * TODO: to generalize, will need to implement arrays of
 * selector input/output portals.
 */
void wireModulesThroughSelector(portals::InPortal<messages::MotionCommand> &OutgoingInput,
                 portals::OutPortal<messages::MotionCommand> *IncomingOutput,
                 bool asynchronous){

    selectorInput.wireTo(&IncomingOutput, asynchronous);

    selectorOutput.wireTo(&selectorInput, asynchronous);

    OutgoingInput.wireTo(&selectorOutput.asynchronous);
}

/* Use the selector portals to change the wiring.
 */
void changeWiring(portals::OutPortal<messages::MotionCommand> &newIncomingOutPortal,
                  bool asynchronous){

    //first need to save the current portal we are using.
    saveOutPortal(selectorInput, asynchronous);
    //then we wire to new portal.
    selectorInput.wireTo(&newIncomingOutPortal, asynchronous);
}

/* Use the selector portals to change the wiring.
 * specifically for the motion streamer module
 */
void changeWiringStreamer(){

    //first need to save the current portal we are using.
    saveOutPortal(selectorInput, asynchronous);
    //then we wire to new portal.
    selectorInput.wireTo(&streamerIn, streamerAsynch);
}



/* Wires the inportal to its previous state
 */
void revertToPreviousWiring(){

    selectorInput.wireTo(&savedOutPortal, savedOutPortal->asynchronousValue());
    clearSavedPortal();
        
}

}//motion
}//man
