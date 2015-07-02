import ChaseBallTransitions as transitions
from ..navigator import Navigator
#from objects import RelRobotLocation

#Only called when we are lost but localized
#i.e when the loc flag lost is set.
#This flag is only set if we return off the field data
#but we know where we are.

def returnToField(player)
    #Loc data is reliable, so we update and check it first check it first

    player.brain.updateLoc()
    loc = player.brain.loc
    field = player.brain.visionField
    
    # I use this to determine where to walk
    if player.brain.lost:

        #We dont want to walk anywhere if we are lost (for the moment)
        player.brain.nav.stand()

        #We spin a little and then pan to look for a landmark
        returnToField.speed = Navigator.SLOW_SPEED
        player.brain.nav.walk(0,0, returnToField.speed)
        print "Spinning at speed: ", returnToField.speed        
        if loc.h < 30:
            player.brain.nav.stand()
            player.brain.tracker.performWidePan()
            #Figure out if we see a landmark (goal post, ball, centercircle) 
            if loc.goal_post_r.on or loc.goal_post_r.on:
                ## We see the goalposts. Stop and loc on those

        if loc.h < -30:
            player.brain.nav.stand()
            player.brain.tracker.repeatWidePan() 

    else:
        #we are not outside the field anymore
        #Go on our business

    
