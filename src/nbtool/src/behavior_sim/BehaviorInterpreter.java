package behavior_sim;

import java.util.ArrayList;
import java.util.Arrays;

import nbtool.data.Log;
import nbtool.io.CppIO;
import nbtool.io.CppIO.CppFuncCall;
import nbtool.io.CppIO.CppFuncListener;

import messages.*;

    // TODO eventually all of the protobufs need to be set
    // in a realistic way, not just giving perfect data

    // TODO, info needs to be passed differently for the right team (symmetric flip)

        // gameState, 
        // visionField, visionRobot, visionObstacle, fallStatus, 
        // motionStatus, odometry, joints, stiffStatus, obstacle, 
        // sharedFlip, worldModel

public class BehaviorInterpreter implements CppFuncListener
{
    private boolean runSim;    // should the sim keep running?

    private Ball ball;
    private Player[] players;

    private Thread callThread;

    public BehaviorInterpreter() 
    { 
        CppIO.ref();        // open an nbcross connection

        runSim = false;     // shouldn't be running yet
    }

    public void run(World world)
    {
        runSim = true;  // now it can run

        // need to send information about these to the BehaviorsModule
        ball = world.ball;
        players = world.players;

        this.sendMessagesInSeparateThread();
    }

    // stops the function requests to C++ and results in killing the thread
    public void end() { runSim = false; }

    // prepares and send the world state info as Protobufs to C++
    public void sendMessages()
    {
        // each player calls its own BehaviorsModule
        for (int i = 0; i < players.length; i++)
        {
            if (players[i] != null)
            {
                // set the Protubufs
                BallModel.FilteredBall bMod = this.setFilteredBall(players[i]);

                // create the function call
                CppFuncCall funcCall = new CppFuncCall();
                funcCall.index = CppIO.current.indexOfFunc("Behaviors");
                funcCall.name = "Behaviors";
                funcCall.args = new ArrayList<Log>(Arrays.asList(new Log("type=BallModel", bMod.toByteArray())));
                funcCall.listener = this;        

                CppIO.current.tryAddCall(funcCall);     // execute the function call
            }

            if (!runSim) break; // stop making function calls
        }
    }

    // make a function request to C++ in a separate thread. Allows other proccesses (like the GUI)
    // to keep running while we wait to make function requests
    public void sendMessagesInSeparateThread()
    {
        callThread = new Thread() {
            public void run() { 
                while (runSim) 
                {
                    sendMessages();
                    // time delay before next call
                    try { callThread.sleep(1000); }
                    catch (InterruptedException e) { 
                        System.out.println("Thread interrupted."); 
                    }
                }
                return; 
            }
        };
        callThread.start();
    }

    @Override
    public void returned(int ret, Log... out) 
    {
    }

    public BallModel.FilteredBall setFilteredBall(Player p)
    {
        float xDist = ball.getX() - p.getX();
        float yDist = ball.getY() - p.getY();
        float distance = (float)Math.sqrt(Math.pow(xDist, 2) +
                        Math.pow(yDist, 2));
        float bearing = p.getH() - (float)Math.asin(yDist/distance);

        // DONE
        return BallModel.FilteredBall.newBuilder()
                            .setVis(this.setVisionBall())
                            .setDistance(distance)
                            .setBearing(bearing)
                            .setRelX(xDist)
                            .setRelY(yDist)
                            .setX(ball.getX())
                            .setY(ball.getY())
                            .build();
    }

    // TODO determine when ball is being seen
    public BallModel.VisionBall setVisionBall()
    {
        // DONE
        return BallModel.VisionBall.newBuilder()
                            .setOn(true)
                            .setFramesOn(30)
                            .setFramesOff(0)
                            .build();
    }

    // TODO determine how this actually get calculated
    public BallModel.SharedBall setSharedBall()
    {
        // DONE
        return BallModel.SharedBall.newBuilder()
                            .setX(ball.getX())
                            .setY(ball.getY())
                            .setBallOn(true)
                            .setReliability(4)  // Todo # players seeing the ball
                            .build();
    }

    public RobotLocationOuterClass.RobotLocation setRobotLocation(Player p)
    {
        return RobotLocationOuterClass
                .RobotLocation.newBuilder()
                            .setX(p.getX())
                            .setY(p.getY())
                            .setH(p.getH())
                            .setUncert(0.0f)    // TODO how is this calculated
                            .build();
    }

    public GameStateOuterClass.GameState setGameState(Player p)
    {
        // TODO see GameController.py for constants, which we should enum
        return GameStateOuterClass.GameState.newBuilder()
                            .setState(3)        // TODO build into world and use consts 
                            .setFirstHalf(true) // TODO
                            .setKickOffTeam(0)  // the left team (TEAM_BLUE, PYNogginConstants)
                            .setSecondaryState(0)
                            .setSecsRemaining(600)   // TODO make clock
                            .setHaveRemoteGc(false)
                            .setTeam(0, this.setTeamInfo(p))    //??
                            .build();

    }
    // TODO not sure how to set this yet
    public GameStateOuterClass.TeamInfo setTeamInfo(Player p)
    {
        return null;
    }

    // todo not sure how to set this yet
    public GameStateOuterClass.RobotInfo setRobotInfo()
    {
        return null;
    }
}