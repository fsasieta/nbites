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
 * UPDATE: made a dir in motion.Correct place?
 * Also need to figure out where to put the C++ thing in.
 * UPDATE: c++ class should be in motion package maybe?
 *          started it there
 */


package nbtool.gui;

import java.lang.*;
import java.io.*;

import nbtool.data.RobotStats;
import nbtool.data.Log;
import nbtool.data.SessionMaster;
import nbtool.io.CommandIO;
import nbtool.io.CommandIO.ControlInstance;
import nbtool.util.N;
import nbtool.util.N.NListener;
import nbtool.util.NBConstants;
import nbtool.util.N.EVENT;
import nbtool.util.NBConstants.FlagPair;
import nbtool.util.NBConstants.MODE;
import nbtool.util.NBConstants.STATUS;
import nbtool.util.P;
import nbtool.util.U;

import messages.PMotion;


/*Class will take values from tool and call commands to the robot
 *
 */
public class MotionStreamer{

    //public native void walkMotionCommand(String s);

    public MotionStreamer(MotionEnginePanel motionPanel){
        MotionEnginePanel MotionPanel = motionPanel;
    }

    public void walkMotion(float xInput, float yInput, float hInput, float gain){
        
        //Need to convert this to s-expr somehow.
        //Eventually will change the method to only work with walk motion stuff
        String walkMsg = "XValue=" + xInput + " ";
        walkMsg += ("YValue=" + yInput + " ");
        walkMsg += ("HValue=" + hInput);
        walkMsg += ("Gain=" + gain)

        System.out.println("Testing whether tool connects with Streamer class");
        System.out.println(walkMsg);
    }


    //private void sendDataOverNetwork(String packet){
    //    ControlIO inst = ControlIO.getByIndex(0);

    //}
}
