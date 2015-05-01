from ..headTracker import HeadMoves
from .. import SweetMoves
from ..util import *

@superState('gameControllerResponder')
def gameInitial(player):
    if player.firstFrame():
        player.gainsOn()
        player.brain.nav.stand()
        player.runfallController = False
    return player.stay()

@superState('gameControllerResponder')
def gameReady(player):
    if player.firstFrame():
        player.brain.nav.stand()
    return player.stay()

@superState('gameControllerResponder')
def gameSet(player):
    return player.stay()

@superState('gameControllerResponder')
def gamePlaying(player):
    return player.goNow('kick')

@superState('gameControllerResponder')
def gamePenalized(player):
    return player.stay()

@superState('gameControllerResponder')
def kick(player):
    if player.firstFrame():
        #Change the input to change the kick to be done when
        #the home button is pressed.
        player.executeMove(SweetMoves.LEFT_STRAIGHT_KICK)

    return player.stay()

@superState('gameControllerResponder')
def fallen(player):
    return player.stay()
