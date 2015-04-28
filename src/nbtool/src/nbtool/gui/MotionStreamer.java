/* Motion streamer is the controller class
 * for the motion engine panel in the nbtool.
 * This class received the input from the GUI, formats it
 * and sends it to the receiving class in C++.
 * This class only sends messages to the c++ class, it
 * performs no other calls/actions.
 * ISSUES:
 * Currently it is not in the right directory, but I need
 * to figure out where is the appropiate place to put it,
 * and what package should it go in.
 * Also need to figure out where to put the C++ thing in.
 */

import java.lang.*
import java.io.*

import nbtool.data.RobotStats;
import nbtool.data.Log;
import nbtool.data.SessionMaster;
import nbtool.io.CommandIO;
import nbtool.util.N;
import nbtool.util.N.NListener;
import nbtool.util.NBConstants;
import nbtool.util.N.EVENT;
import nbtool.util.NBConstants.FlagPair;
import nbtool.util.NBConstants.MODE;
import nbtool.util.NBConstants.STATUS;
import nbtool.util.P;
import nbtool.util.U;


/*Class will take values from tool and call commands to the robot
 *
 */
public class MotionStreamer{

    public MotionStreamer(MotionEnginePanel MotionPanel){
        MotionEnginePanel MotionPanel = new MotionEnginePanel();
    }

    public void walkMotion(int xInput, int yInput, int Hinput){
        // will need to send a protobuf to the robot with the walk params
        // Actually will need to override the behaviors thing and send 
        // input to the motion module.
        //convert to sExpression
        //Eventually will chagne the method to only work with walk motion stuff

        String walkMsg = "XValue=" + xInput.toString() + " ";
        walkMsg.append("YValue=" + yinput.toString() + " ");
        walkMsg.append("HValue=" + hInput.toString());


        System.out.println("Testing whether tool connects with Streamer class")
        System.out.println(walkMsg);
    }

    
}
