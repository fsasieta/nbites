/* This is the selector class for the motion module
 * for now the idea for this class is to manage the
 * wiring between the behaviors and the streaming 
 * tool, so we can wire and dewire modules on the fly
 *
 * Class is mainly used by motionStreamer to switch
 * wiring on the fly when using the tool.
 *
 * Will want to generalize this further, but this will do for now.
 * Currently only handles switching between two connections.
 *
 * @author Franco Sasieta, fsasieta@bowdoin.edu
 * Created 05/29/2015
 * Last modified 06/01/2015
 */

#include "RoboGrams.h"

namespace man{
namespace motion{

class MotionSelectorModule public: portals::Module{
public:

MotionSelectorModule():
    selectorOutput(base())
    {}
~MotionSelectorModule();

//actually make a map of portals saved.
//map<portals::InPortal, portals::OutPortal *> previousWire;
//map<portals::OutPortal, bool> previousWireAsynchValues;

//map<portals::InPortal, portals::OutPortal> deadEndWires;
//map<portals::OutPortal, portals::InPortal> deadEndWiresInverse;

/********************
 * Wiring functions *
 ********************/

/* Doesn't actually wire portal to anything.
 * It is used for clarity in Man.cpp
 * Motion streamer wires to this first.
 * saves the outportal entered to be able to change to it later.
 *
 * TODO: expand this from a single variable to a map of dead end variables 
 *       to make this more general.
 *
 */
void deadEndWire(portals::OutPortal<message::MotionCommand> incomingOutput,
                 bool asynchronous){

    //portals::InPortal<messages::MotionCommand>  deadEndInPortal_;
    //deadEndInPortal_.wireTo(incomingOutput, asynchronous);

    //save the dead end wire to use later.
    saveOutPortal(incomingOutput, asynchronous);
    //saveAsynchronousValue(asynchronous);

    streamerIn = incomingOutput;
    streamerAsynch =  asynchronous;
}

/* Wire the input InPortal with the corresponding OutPortal
 * Through the selector Module. Used in Man.cpp
 * TODO: to generalize, will need to implement arrays of
 * selector input/output portals.
 */
void wireModulesThroughSelector(portals::InPortal<messages::MotionCommand> OutgoingInput,
                 portals::OutPortal<messages::MotionCommand> IncomingOutput,
                 bool asynchronous){

    selectorInput.wireTo(&IncomingOutput, asynchronous);

    selectorOutput.wireTo(&selectorInput, asynchronous);

    OutgoingInput.wireTo(&selectorOutput.asynchronous);
}

/* Use the selector portals to change the wiring.
 * Now that I think of this, you dont need to save the outPortal,
 * since you'd only be using the selectors, right?
 */
void changeWiring(portals::OutPortal<messages::MotionCommand> newIncomingOutPortal,
                  bool asynchronous){

    //first need to save the current portal we are using.
    saveOutPortal(selectorInput);
    //then we wire to new portal.
    selectorInput.wireTo(newIncomingOutPortal, asynchronous);
    
}

void revertToPreviousWiring(portals::InPortal<messages::MotionCommand> InputPortal,
                            bool asynchronous){

    InputPortal.wireTo(&savedPortal, asynchronous);

    clearSavedPortal();
        
}


private:
/** Out Portals **/
portals::OutPortal<messages::MotionCommand> *selectorOutput;
portals::InPortal<messages::MotionCommand> *selectorInput_;

//middlemen connect the portals to each other
//portals::OutPortal<messages::MotionCommand> middlemanOut;
//portals::InPortal<messages::MotionCommand> middlemanIn;

//portal saved:
portals::OutPortal<messages::MotionCommand> *savedPortal;

//specifically for the streamer
portals::OutPortal<messages::MotionCommand> *streamerIn;

bool savedAsynchronousValue = false;

//save the OutPortal we are currently connected to;
void saveOutPortal(portals::InPortal<messages::MotionCommand> portal){

    savedOutPortal = portal.wiredTo();
    savedAsynchronousValue = portal.asynchronousValue();
        
    //make sure no other key is here first.
    previousWire.erase( savedOutPortal );
    previousAsynchValue.erase( savedAsynchronousValue );

    previousWire.insert( pair<portals::OutPortal, portals::InPortal>(savedOutPortal, portal) );
    previousAsynchValue( pair<OutPortal, bool>(savedOutPortal, savedAsynchronousValue) )

}

//Previously saved portal.
void clearSavedPortal(){
    savedPortal = NULL;
    savedAsynchronousValue = false;

}


}//class bracket

}//namespace motion
}//namespace man
