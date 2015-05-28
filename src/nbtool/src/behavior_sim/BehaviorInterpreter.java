package behavior_sim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import nbtool.data.Log;
import nbtool.data.SExpr;
import nbtool.io.CommonIO.IOInstance;
import nbtool.io.CrossIO;
import nbtool.io.CrossIO.CrossCall;
import nbtool.io.CrossIO.CrossFunc;
import nbtool.io.CrossIO.CrossInstance;
import nbtool.util.Center;
import nbtool.util.Logger;

import com.google.protobuf.InvalidProtocolBufferException;

import messages.*;

    // TODO eventually all of the protobufs need to be set
    // in a realistic way, not just giving perfect data

public class BehaviorInterpreter implements nbtool.io.CommonIO.IOFirstResponder
{
    private boolean runSim;    // should the sim keep running?

    private LinkedList<Integer> pQ;     // player index who's behavior's are being run

    private Ball ball;
    private Player[] players;
    private World world;

    private Thread callThread;

    private int runSpeed;

    private long tBM;   // time to build messages
    private long tFC;    // time func call
    private long tGUI;  // time to update GUI
    private long tTot; // total run time


    public BehaviorInterpreter() 
    { 
    	Center.startCenter();
    	CrossIO.startCrossServer();
        Logger.level = Logger.WARN;

        runSim = false;     // shouldn't be running yet

        pQ = new LinkedList();
    }
    
    @Override
	public void ioFinished(IOInstance instance) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ioReceived(IOInstance inst, int ret, Log... out) {
		// System.out.println("Func Call Time: " + (System.nanoTime() - tFC)/1000000.);
        tGUI = System.nanoTime();

        if (out.length == 0) return;

        int pIndex = pQ.remove();

        PMotion.MotionCommand bmc = PMotion.MotionCommand.newBuilder().build();
        try
        { 
            bmc = PMotion.MotionCommand.parseFrom(out[0].bytes);
            
            float normalizer = 1;

            switch(bmc.getType())
            {
                case SCRIPTED_MOVE:
                    int dir = 0;
                    switch((int)bmc.getScript().getH())
                    {
                        case 90:
                            dir = -1;
                            break;
                        case 45:
                            dir = -2;
                            break;
                        case -45:
                            dir = 2;
                            break;
                        case -90:
                            dir = 1;
                            break;
                    }
                    world.kick(pIndex, players[pIndex].h, dir, bmc.getScript().getDist());
                    break;
                case DESTINATION_WALK:
                    PMotion.DestinationWalk dest = bmc.getDest();

                    int kick = dest.getKick().getKickType();
                    if (kick != 0)
                    {
                        float heading = players[pIndex].h;

                        switch(kick)
                        {
                            case 1:
                                world.kick(pIndex, heading, -1, FieldConstants.M_SIDE_DIST);
                                break;
                            case 2:
                                world.kick(pIndex, heading, 1, FieldConstants.M_SIDE_DIST);
                                break;
                            case 3:
                                world.kick(pIndex, heading, 0, FieldConstants.M_STRAIGHT_DIST);
                                break;
                            case 4:
                                world.kick(pIndex, heading, 0, FieldConstants.M_STRAIGHT_DIST);
                                break;
                            case 5:
                                world.kick(pIndex, heading, -2, FieldConstants.M_CHIP_DIST);
                                break;
                            case 6:
                                world.kick(pIndex, heading, 2, FieldConstants.M_CHIP_DIST);
                                break;
                        }
                    }

                    else
                    {
                        float relX = dest.getRelX();
                        float relY = dest.getRelY();
                        float relH = dest.getRelH();

                        if (relH != 0) relH = relH/(float)Math.abs(relH)*FieldConstants.DEST_SPEED_H;

                        normalizer = (float)Math.pow(relX*relX + relY*relY, 0.5);
                        if (normalizer == 0) normalizer = 1;
                        
                        players[pIndex].moveRel(relX*dest.getGain()*FieldConstants.WALK_SPEED/normalizer, 
                                                relY*dest.getGain()*FieldConstants.WALK_SPEED/normalizer,
                                                relH*dest.getGain()*FieldConstants.WALK_SPEED);
                    }

                    world.repaint();
                    break;

                case WALK_COMMAND:
                    players[pIndex].moveRel(FieldConstants.WALK_SPEED*bmc.getSpeed().getXPercent(), 
                                            FieldConstants.WALK_SPEED*bmc.getSpeed().getYPercent(), 
                                            FieldConstants.WALK_SPEED_H*bmc.getSpeed().getHPercent());
                    
                    world.repaint();
                    break;

                case ODOMETRY_WALK:
                    normalizer = (bmc.getOdometryDest().getRelX() + 
                        bmc.getOdometryDest().getRelY());
                    
                    players[pIndex].moveRel(bmc.getOdometryDest().getRelX()/normalizer, 
                                            -bmc.getOdometryDest().getRelY()/normalizer,
                                            -bmc.getOdometryDest().getRelH()*FieldConstants.DEST_SPEED_H);

                    world.repaint();
                    break;
            } 
        }
        catch(InvalidProtocolBufferException ipbe){
            System.out.println("Received Invalid Protobuf");
        }

        // System.out.println("GUI time: " + (System.nanoTime() - tGUI)/1000000.);
        // System.out.println("Total time: " + (System.nanoTime() - tTot)/1000000.);
		
	}

	@Override
	public boolean ioMayRespondOnCenterThread(IOInstance inst) {
		return false;
	}

    public void run()
    {
        runSim = true;  // now it can run
        this.sendMessagesInSeparateThread();        
    }

    public void initAndRun(World world)
    {
        runSim = true;  // now it can run

        // need to send information about these to the BehaviorsModule
        this.world = world;
        ball = world.ball;
        players = world.players;

        byte[] bArray = {(byte)100};
        Log clearLog = Log.logWithType("int", bArray);
        clearLog.tree().append(SExpr.newKeyValue("pNum", "100"));
        
        //Phil's dumb names.
        CrossInstance ci = CrossIO.instanceByIndex(0);
        CrossFunc cf = ci.functionWithName("InitSim");
        CrossCall clearSim = new CrossCall(this, cf, clearLog);
        ci.tryAddCall(clearSim);
        
        int numPlayers = 0;

        for (Player p : players) 
        { 
            if (p != null) 
            {
                numPlayers++;

                byte[] byteArray = {(byte)p.num};
                Log log = Log.logWithType("int", byteArray);
                log.tree().append(SExpr.newKeyValue("pNum", String.valueOf(p.num)));
                
                CrossCall is = new CrossCall(this, cf, log);
                ci.tryAddCall(is);
            }
        }

        runSpeed = FieldConstants.RUN_SPEED * numPlayers;

        this.sendMessagesInSeparateThread();
    }

    // stops the function requests to C++ and results in killing the thread
    public void end() { runSim = false; }

    // prepares and send the world state info as Protobufs to C++
    private void sendMessages()
    {
        tTot = System.nanoTime();
        tBM = System.nanoTime();
        // each player calls its own BehaviorsModule
        for (int i = 0; i < players.length; i++)
        {
            if (players[i] != null)
            {   
                pQ.add(i);

                Player player = new Player(players[i]); // make a copy so we can edit it
                Ball b = new Ball(ball);

                if (player.team) b.moveTo(b.x, b.flipY());
                else b.moveTo(b.flipX(), b.y);

                ArrayList<Log> protobufs = new ArrayList<Log>();
                protobufs.add(Log.logWithType("RobotLocation", this.setLocalization(player).toByteArray()));
                protobufs.add(Log.logWithType("FilteredBall", this.setFilteredBall(player, b).toByteArray()));
                protobufs.add(Log.logWithType("GameState", this.setGameState(player).toByteArray()));
                protobufs.add(Log.logWithType("VisionField", this.setVisionField().toByteArray()));
                protobufs.add(Log.logWithType("VisionRobot", this.setVisionRobot().toByteArray()));
                protobufs.add(Log.logWithType("VisionObstacle", this.setVisionObstacle().toByteArray()));
                protobufs.add(Log.logWithType("FallStatus", this.setFallStatus().toByteArray()));
                protobufs.add(Log.logWithType("MotionStatus", this.setMotionStatus().toByteArray()));
                protobufs.add(Log.logWithType("RobotLocation", this.setOdometry().toByteArray()));
                protobufs.add(Log.logWithType("Joints", this.setJoints().toByteArray()));
                protobufs.add(Log.logWithType("StiffStatus", this.setStiffStatus().toByteArray()));
                protobufs.add(Log.logWithType("Obstacle", this.setObstacle().toByteArray()));
                protobufs.add(Log.logWithType("SharedBall", this.setSharedBall(b).toByteArray()));
                protobufs.add(Log.logWithType("RobotLocation", this.setSharedFlip().toByteArray()));
                
                CrossInstance ci = CrossIO.instanceByIndex(0);
                CrossFunc cf = ci.functionWithName("Behaviors");
                CrossCall funcCall = new CrossCall(this, cf, protobufs.toArray(new Log[0]));      

                // System.out.println("Build Time: " + (System.nanoTime() - tBM)/1000000.);
                tFC = System.nanoTime();
                ci.tryAddCall(funcCall);     // execute the function call
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
                    // runSim = false;
                    // return;
                    // time delay before next call
                    try { callThread.sleep(runSpeed); }
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

    private RobotLocationOuterClass.RobotLocation setLocalization(Player p)
    {
        return RobotLocationOuterClass.RobotLocation.newBuilder()
                            .setX(p.x)
                            .setY(p.y)
                            .setH(p.h)
                            .setUncert(0.0f)    // TODO how is this calculated
                            .build();
    }

    private BallModel.FilteredBall setFilteredBall(Player p, Ball b)
    {
        float bearing = p.bearingTo(b.getLocation());
        
        Location relBall = new Location(b.x - p.x, b.y - p.y);
        relBall.rotate(-p.h);

        // DONE
        return BallModel.FilteredBall.newBuilder()
                            .setVis(this.setVisionBall(bearing))
                            .setDistance(p.distanceTo(b.getLocation()))
                            .setBearing(bearing)
                            .setBearingDeg((float)Math.toDegrees(bearing))
                            .setRelX(relBall.x)
                            .setRelY(relBall.y)
                            .setX(b.x)
                            .setY(b.y)
                            .build();
    }

    // TODO determine when ball is being seen
    private BallModel.VisionBall setVisionBall(float bearing)
    {
        int framesOn = 0;
        int framesOff = 0;

        boolean on = Math.abs(Math.toDegrees(bearing)) < 31.;

        if (on) framesOn = 50;
        else framesOff = 50;
        // DONE
        return BallModel.VisionBall.newBuilder()
                            .setOn(on)
                            .setFramesOn(framesOn)
                            .setFramesOff(framesOff)
                            .build();
    }

    private GameStateOuterClass.GameState setGameState(Player p)
    {
        int kickOffTeam;
        if (p.team) 
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
                                                .addPlayer(4,this.setRobotInfo())
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
                                        .setBodyIsActive(false)
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
                            .setX(b.x)
                            .setY(b.y)
                            .setBallOn(true)
                            .setReliability(4)  // TODO # players seeing the ball
                            .build();
    }

    private RobotLocationOuterClass.RobotLocation setSharedFlip()
    {
        return RobotLocationOuterClass.RobotLocation.newBuilder()
                                        .setTimestamp(0)    // means it never flips
                                        .build();
    }
}