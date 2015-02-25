package behavior_sim;

import java.util.ArrayList;
import java.util.Arrays;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import nbtool.data.Log;
import nbtool.io.CppIO;
import nbtool.io.CppIO.CppFuncCall;
import nbtool.io.CppIO.CppFuncListener;

public class BehaviorInterpreter implements CppFuncListener
{
    private World world;

    public BehaviorInterpreter(World w)
    {
        CppIO.ref();

        world = w;

        BallModel.FilteredBall bMod = setBallModel();

        // wait until connection has been established
        try { Thread.sleep(1000); } 
        catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        CppFuncCall funcCall = new CppFuncCall();
        funcCall.index = CppIO.current.indexOfFunc("Behaviors");
        funcCall.name = "Behaviors";
        funcCall.args = new ArrayList<Log>(Arrays.asList(new Log("type=BallModel", bMod.toByteArray())));
        funcCall.listener = this;        

        CppIO.current.tryAddCall(funcCall);
    }

    @Override
    public void returned(int ret, Log... out) 
    {
        
    }

    public BallModel.FilteredBall setBallModel()
    {
        Ball ball = world.ball;
        return BallModel.FilteredBall.newBuilder()
                            .setX(ball.getX())
                            .setY(ball.getY())
                            .build();
    }
}