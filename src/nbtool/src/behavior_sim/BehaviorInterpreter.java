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
    private World world;

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
        this.world = world;
        ball = world.ball;
        players = world.players;

        this.sendMessagesInSeparateThread();
    }

    // stops the function requests to C++ and results in killing the thread
    public void end() { runSim = false; }

    // prepares and send the world state info as Protobufs to C++
    private void sendMessages()
    {
        // each player calls its own BehaviorsModule
        for (int i = 0; i < players.length; i++)
        {
            if (players[i] != null)
            {
                Player player = new Player(players[i]); // make a copy so we can edit it
                Ball b = new Ball(ball);
                if (player.team() == Enums.Teams.valueOf("MY_TEAM").team) // left team needs to be flipped on y
                {
                    player.moveTo(player.getX(), player.flipY());
                    b.moveTo(b.getX(), b.flipY());
                }
                else
                {
                    player.moveTo(player.flipX(), player.getY());
                    b.moveTo(b.flipX(), b.getY());
                }

                ArrayList<Log> protobufs = new ArrayList<Log>();
                protobufs.add(new Log("type=RobotLocation", this.setLocalization(player).toByteArray()));
                protobufs.add(new Log("type=FilteredBall", this.setFilteredBall(player, b).toByteArray()));
                protobufs.add(new Log("type=GameState", this.setGameState(player).toByteArray()));
                protobufs.add(new Log("type=VisionField", this.setVisionField().toByteArray()));
                protobufs.add(new Log("type=VisionRobot", this.setVisionRobot().toByteArray()));
                protobufs.add(new Log("type=VisionObstacle", this.setVisionObstacle().toByteArray()));
                protobufs.add(new Log("type=FallStatus", this.setFallStatus().toByteArray()));
                protobufs.add(new Log("type=MotionStatus", this.setMotionStatus().toByteArray()));
                protobufs.add(new Log("type=RobotLocation", this.setOdometry().toByteArray()));
                protobufs.add(new Log("type=Joints", this.setJoints().toByteArray()));
                protobufs.add(new Log("type=StiffStatus", this.setStiffStatus().toByteArray()));
                protobufs.add(new Log("type=Obstacle", this.setObstacle().toByteArray()));
                protobufs.add(new Log("type=SharedBall", this.setSharedBall(b).toByteArray()));
                protobufs.add(new Log("type=RobotLocation", this.setSharedFlip().toByteArray()));

                // create the function call
                CppFuncCall funcCall = new CppFuncCall();
                funcCall.index = CppIO.current.indexOfFunc("Behaviors");
                funcCall.name = "Behaviors";
                funcCall.args = protobufs;
                funcCall.listener = this;        

                CppIO.current.tryAddCall(funcCall);     // execute the function call
            }

            if (!runSim) break; // stop making function calls
        }
    }

    // make a function request to C++ in a separate thread. Allows other proccesses (like the GUI)
    // to keep running while we wait to make function requests
    private void sendMessagesInSeparateThread()
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
                sendMessages();
                return; 
            }
        };
        callThread.start();
    }

    @Override
    public void returned(int ret, Log... out) 
    {
    }

    private RobotLocationOuterClass.RobotLocation setLocalization(Player p)
    {
        return RobotLocationOuterClass.RobotLocation.newBuilder()
                            .setX(p.getX())
                            .setY(p.getY())
                            .setH(p.getH())
                            .setUncert(0.0f)    // TODO how is this calculated
                            .build();
    }

    private BallModel.FilteredBall setFilteredBall(Player p, Ball b)
    {
        // DONE
        return BallModel.FilteredBall.newBuilder()
                            .setVis(this.setVisionBall())
                            .setDistance(p.distanceTo(b.getLocation()))
                            .setBearing(p.bearingTo(b.getLocation()))
                            .setRelX(b.getX() - p.getX())
                            .setRelY(b.getY() - p.getY())
                            .setX(b.getX())
                            .setY(b.getY())
                            .build();
    }

    // TODO determine when ball is being seen
    private BallModel.VisionBall setVisionBall()
    {
        // DONE
        return BallModel.VisionBall.newBuilder()
                            .setOn(true)
                            .setFramesOn(50)
                            .setFramesOff(0)
                            .build();
    }

    // TODO set team
    private GameStateOuterClass.GameState setGameState(Player p)
    {
        int kickOffTeam;
        if (p.team() == Enums.Teams.valueOf("MY_TEAM").team) 
            kickOffTeam = world.kickOffTeam;
        else
            kickOffTeam = Enums.Teams.swap(world.kickOffTeam);

        return GameStateOuterClass.GameState.newBuilder()
                                .setState(world.state)
                                .setFirstHalf(world.half == 1)
                                .setKickOffTeam(kickOffTeam)
                                .setSecondaryState(Enums.States2.valueOf("NORMAL").state)
                                .setSecsRemaining(world.time)
                                .setHaveRemoteGc(false)
                                .addTeam(0, this.setTeamInfo(0))
                                .addTeam(1, this.setTeamInfo(1))
                                .build();
    }
    private GameStateOuterClass.TeamInfo setTeamInfo(int team)
    {
        GameStateOuterClass.TeamInfo teamInfo = GameStateOuterClass.TeamInfo.newBuilder()
                                                .setTeamNumber(team)    // not important
                                                .setTeamColor(team)
                                                .setScore(0)    // todo
                                                .setGoalColor(Enums.Teams.swap(team))
                                                .addPlayer(0,this.setRobotInfo())
                                                .addPlayer(1,this.setRobotInfo())
                                                .addPlayer(2,this.setRobotInfo())
                                                .addPlayer(3,this.setRobotInfo())
                                                .build();

        return teamInfo;
    }

    private GameStateOuterClass.RobotInfo setRobotInfo()
    {
        return GameStateOuterClass.RobotInfo.newBuilder()
                                            .setPenalty(0)
                                            .setSecsLeft(0)
                                            .build();
    }

    private VisionFieldOuterClass.VisionField setVisionField()
    {
        return VisionFieldOuterClass.VisionField.newBuilder().build();
    }

    // TODO populate
    private VisionRobotOuterClass.VisionRobot setVisionRobot()
    {
        return VisionRobotOuterClass.VisionRobot.newBuilder().build();
    }

    private VisionRobotOuterClass.VisionObstacle setVisionObstacle()
    {
        return VisionRobotOuterClass.VisionObstacle.newBuilder().build();
    }

    private FallStatusOuterClass.FallStatus setFallStatus()
    {
        return FallStatusOuterClass.FallStatus.newBuilder()
                            .setFalling(false)
                            .setFallen(false)
                            .setOnFront(false)
                            .build();
    }

    private MotionStatusOuterClass.MotionStatus setMotionStatus()
    {
        return MotionStatusOuterClass.MotionStatus.newBuilder()
                                        .setStanding(true)
                                        .setBodyIsActive(true)
                                        .setWalkIsActive(true)
                                        .setHeadIsActive(true)
                                        .setCalibrated(true)
                                        .setUpright(true)
                                        .build();
    }

    private RobotLocationOuterClass.RobotLocation setOdometry()
    {
        return RobotLocationOuterClass.RobotLocation.newBuilder().build();
    }

    private PMotion.JointAngles setJoints()
    {
        return PMotion.JointAngles.newBuilder().build();
    }

    private StiffnessControlOuterClass.StiffStatus setStiffStatus()
    {
        return StiffnessControlOuterClass.StiffStatus.newBuilder()
                                                    .setOn(true)
                                                    .build();
    }

    // TODO populate
    private ObstacleOuterClass.FieldObstacles setObstacle()
    {
        return ObstacleOuterClass.FieldObstacles.newBuilder().build();
    }
    
    //TODO populate
    private ObstacleOuterClass.FieldObstacles.Obstacle setObstacles()
    {
        return ObstacleOuterClass.FieldObstacles.Obstacle.newBuilder().build();
    }

        // TODO determine how this actually get calculated
    private BallModel.SharedBall setSharedBall(Ball b)
    {
        // DONE
        return BallModel.SharedBall.newBuilder()
                            .setX(b.getX())
                            .setY(b.getY())
                            .setBallOn(true)
                            .setReliability(4)  // # players seeing the ball
                            .build();
    }

    private RobotLocationOuterClass.RobotLocation setSharedFlip()
    {
        return RobotLocationOuterClass.RobotLocation.newBuilder()
                                        .setTimestamp(0)    // means it never flips
                                        .build();
    }
}