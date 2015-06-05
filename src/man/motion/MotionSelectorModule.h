/* This is the selector class for the motion module
 * for now the idea for this class is to manage the
 * wiring between the behaviors and the streaming 
 * tool, so we can wire and dewire modules on the fly
 *
 * Class is mainly used by motionStreamer to switch
 * wiring on the fly when using the tool.
 *
 * Will want to generalize this further, but this will do for now.
 * (Stuff in parenthesis is what I started doing towards that.)
 * Currently only handles switching between two connections.
 *
 * @author Franco Sasieta, fsasieta@bowdoin.edu
 * Created 05/29/2015
 * Last modified 06/01/2015
 */

#ifndef MOTION_SELECTOR_MODULE_H
#define MOTION_SELECTOR_MODULE_H


#include "RoboGrams.h"
#include "PMotion.pb.h"

namespace man{
namespace motion{

class MotionSelectorModule : public portals::Module{

public:
    
    MotionSelectorModule();
    
    //Do we need a destructor?
    //virtual ~MotionSelectorModule();
    
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
     * unless called by motion streamer this has no purpose.
     */
    void deadEndWire(portals::OutPortal<messages::MotionCommand> *incomingOutput,
                     bool asynchronous);
    
    /* Wire the input InPortal with the corresponding OutPortal
     * Through the selector Module. Used in Man.cpp
     * TODO: to generalize, will need to implement arrays of
     * selector input/output portals.
     * ADDED: & and * to handle references
     */
    void wireModulesThroughSelector(portals::InPortal<messages::MotionCommand> &OutgoingInput,
                                    portals::OutPortal<messages::MotionCommand> *IncomingOutput,
                                    bool asynchronous);
    
    /* Use the selector portals to change the wiring.
     */
    void changeWiring(portals::OutPortal<messages::MotionCommand> newIncomingOutPortal,
                      bool asynchronous);
    
    /* Use the selector portals to change the wiring.
     * specifically for the motion streamer module
     */
    void changeWiringStreamer();
    
    /* Wires the inportal to its previous state
     */
    void revertToPreviousWiring();
    
    
private:
    
    //need to implement this because otherwise we get an error.
    void run_();

    //save the OutPortal we are currently connected to;
    void saveOutPortal(portals::InPortal<messages::MotionCommand> portal, bool asynchronous){
        savedOutPortal = portal.wiredTo();
        savedAsynchronousValue = asynchronous;
    }
    
    //Previously saved portal.
    void clearSavedPortal(){
        savedOutPortal = NULL;
        savedAsynchronousValue = false;
    }
    
    /** Out Portals **/
    portals::OutPortal<messages::MotionCommand> *selectorOutput;
    portals::InPortal<messages::MotionCommand> *selectorInput_;
    portals::OutPortal<messages::MotionCommand> *savedOutPortal; //portal saved
    portals::OutPortal<messages::MotionCommand> *streamerIn;//specifically for the streamer
    
    bool savedAsynchronousValue;
    bool streamerAsynch;
    
};//class bracket

}//namespace motion
}//namespace man


#endif
