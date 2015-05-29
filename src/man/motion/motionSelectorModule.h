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
 * Last modified 05/29/2015
 */

#include "RoboGrams.h"

namespace man{
namespace motion{

class MotionSelectorModule public: portals::Module{
public:

MotionSelectorModule();
~MotionSelectorModule();

/** Out Portals **/
portals::OutPortal<messages::MotionCommand> selectorOutput;
portals::InPortal<messages::MotionCommand> selectorInput;

//portal saved:
portals::OutPortal<messages:: MotionCommand>* savedPortal;
bool savedAsynchronousValue = false;

//actually make a map of portals saved.
map<portals::InPortal, portals::OutPortal>

/********************
 * Wiring functions *
 ********************/

/* Wire the input InPortal with the corresponding OutPortal
 * Throught the selector Module
 * May not have goetten any pointer stuff right.
 * ...
 * Probably did not get any pointer stuff right.
 * I'm not sure this is needed though, but maybe it will be
 * needed later for generality
 */
void wireModules(portals::InPortal<messages::MotionCommand> Input,
                 portals::OutPortal<messages::MotionCommand> Output,
                 bool asynchronous){

    selectorInput.wireTo(Output, asynchronous);
    selectoOutput.wireTo(Input, asynchronous);
}

// Input the InPortal that will be changed and the OutPortal that it will
// be changed to.
void changeWiring(portals::InPortal<messages::MotionCommand> inputPortal,
                  portals::OutPortal<messages::MotionCommand> newOutPortal,
                  bool asynchronous){

    //first need to save the current portal we are using.
    saveOutPortal(selectorInput);

    //then we wire to new portal.
    inputPortal.wireTo(newOutPortal, asynchronous);
}

void revertToOriginalWiring(portals::InPortal<messages::MotionCommand> InputPortal,
                            portals::OutPortal<messages::MotionCommand> OutputPortal,
                            bool asynchronous){

    InputPortal.wireTo(*savedPortal, asynchronous);

    clearSavedPortal();
        
}


private:
//save the OutPortal we are currently connected to;
void saveOutPortal(portals::InPortal<messages::MotionCommand> portal, bool asyncValue){

    savedOutPortal = portal.wiredTo();
    savedAsynchronousValue = portal.asynchronousValue();

}
void clearSavedPortal(){
    savedPortal = NULL;
    savedAsynchronousValue = false;
}


}//class bracket

}//namespace motion
}//namespace man
