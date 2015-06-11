/* Motion streamer is the controller class
 * for the motion engine panel in the nbtool.
 * This class received the input from the GUI, formats it
 * and sends it to the receiving class in C++. 
 *
 * This class only sends messages to the c++ class, it
 * performs no other calls/actions.
 * It doesn't actually send stuff to the c++ class directly, it uses 
 * Phil Koch's networking system
 *
 * ISSUES diary:
 *  -   Currently it is not in the right directory, but I need
 * to figure out where is the appropiate place to put it,
 * and what package should it go in. <-- currently just put it 
 *                                  in the gui pkg for simplicity
 *  -   UPDATE: made a dir in motion.Correct place?
 *  -   Also need to figure out where to put the C++ thing in.
 * UPDATE: c++ class should be in motion package maybe?
 *          started it there. <-- wrong, no packages in c++
 *          but the class is in the motion namespace
 *
 * TODO: Figure out a system to do scripted moves in.
 *       Since scripted moves are a sequence of joints angles, not a single
 *       thing will do.
 *       Although now that I think of it, it may be better to do a Single thing first
 *
 * @author: Franco Sasieta
 * Last Modified: 06/08/2015
 */


package nbtool.gui;

import java.lang.*;
import java.io.*;
import java.util.*;
import java.util.Arrays;
//import java.lang.System;

import nbtool.data.RobotStats;
import nbtool.data.Log;
import nbtool.data.SessionMaster;
import nbtool.io.ControlIO;
import nbtool.io.ControlIO.ControlInstance;
import nbtool.util.Logger;

/*
import nbtool.util.N;
import nbtool.util.N.NListener;
import nbtool.util.NBConstants;
import nbtool.util.N.EVENT;
import nbtool.util.NBConstants.FlagPair;
import nbtool.util.NBConstants.MODE;
import nbtool.util.NBConstants.STATUS;
import nbtool.util.P;
import nbtool.util.U;
*/

import messages.PMotion;

/*Class will take values from tool and call commands to the robot
 *
 */
public class MotionStreamer{

    //public static String[] commandArray = new String[32];
    public static ArrayList<String> commandArray = new ArrayList<String>();
    public static boolean streaming = false;

    public MotionStreamer(MotionEnginePanel motionPanel){
        MotionEnginePanel MotionPanel = motionPanel;
    }

    /* Simple walk method. Uses percentages
     */
    public void walkCommand(float xPercent, float yPercent, float hPercent){

        commandArray.clear();
        commandArray.add("XValue=" + xPercent);
        commandArray.add("YValue=" + yPercent);
        commandArray.add("HValue=" + hPercent);
        commandArray.add("timeStamp=" + System.currentTimeMillis());

        System.out.println(Arrays.toString(commandArray.toArray()));
        String walkMsg = Arrays.toString(commandArray.toArray());

        if(streaming){ sendDataOverNetwork(walkMsg); }
    }

    /* The x, y and h values are __relative__ to where the robot's values are right now
     * For more info look at MotionModule.h and PMotion.proto files
     */
    public void odometryWalk(float xInput, float yInput, float hInput, float gain){
        
        commandArray.clear();
        commandArray.add("XValue=" + xInput);
        commandArray.add("YValue=" + yInput);
        commandArray.add("HValue=" + hInput);
        commandArray.add("Gain=" + gain);
        commandArray.add("timeStamp=" + System.currentTimeMillis());

        System.out.println(Arrays.toString(commandArray.toArray()));
        String walkMsg = Arrays.toString(commandArray.toArray());

        if(streaming){ sendDataOverNetwork(walkMsg); }
    }

    /* Very similar to associated protobuff in PMOtion.proto.
     * Actually uses that protobuff indirectly. 
     */
    public void destinationWalk(float x, float y, 
                                float h, float gain, 
                                boolean performKick, int kickType){

        commandArray.clear();
        commandArray.add("XValue=" + x);
        commandArray.add("YValue=" + y);
        commandArray.add("HValue=" + h);
        commandArray.add("Gain=" + gain);
        commandArray.add("performkick=" + performKick);
        commandArray.add("kickType=" + kickType);
        commandArray.add("timeStamp=" + System.currentTimeMillis());

        System.out.println(Arrays.toString(commandArray.toArray()));
        String walkMsg = Arrays.toString(commandArray.toArray());

        if(streaming){ sendDataOverNetwork(walkMsg); }
    }

    /* Body joint command. Sets the robot's joints to an appropiate angle specified.
     * Also checks that the angle specified is not past the maximum required by aldebaran
     * TO DO!!!!!! right now the head command seems more important.
     */
    //public void Scripted
    
    
    /* Head Command. must make sure I only need one head command.
     * type: 0= set head command, 1= scripted head command.
     *  POS_HEAD_COMMAND = 0; // AKA set_head_command
     *  SCRIPTED_HEAD_COMMAND = 1;
     *  We use one huge parameter filled function to mimick the protobuff.
     *  TODO: add the scripted joint command to this function
     */
    public void headCommand(String type, 
                            float headYaw, 
                            float headPitch, 
                            float maxYawSpeed, 
                            float maxPitchSpeed){

        commandArray.clear();
        //Only handle position commands for now.
        if(type == "POS_HEAD_COMMAND"){

            commandArray.add("type=" + type);
            commandArray.add("headYaw=" + headYaw);
            commandArray.add("headPitch=" + headPitch);
            commandArray.add("maxYawSpeed=" + maxYawSpeed);
            commandArray.add("maxPitchSpeed=" + maxPitchSpeed);
            commandArray.add("timeStamp=" + System.currentTimeMillis());
        }

        System.out.println(Arrays.toString(commandArray.toArray()));
        String walkMsg = Arrays.toString(commandArray.toArray());

        if(streaming){ sendDataOverNetwork(walkMsg); }
    }

    public void streamingStatus(String status){
        //String msg;
        if(status == "start"){

            streaming = true;
            //msg = "StartStreaming";
            //sendDataOverNetwork(msg);
        }
        else{

            streaming = false;
            //msg = "StopStreaming";
            //sendDataOverNetwork(msg);
        }
    }

    private void sendDataOverNetwork(String packet){
        ControlInstance inst = ControlIO.getByIndex(0);

        
        if (inst == null) {
			Logger.log(Logger.WARN, "MotionEnginePanel clicked while no ControlInstance available");
            
			return;
		}

        boolean success = inst.tryAddCmnd(ControlIO.createCmndSendMotionCommands(packet));
	    Logger.logf(Logger.INFO, "MotionStreamer: CommandIO.createCmdSendMotionCommands(%s) returned %B\n", packet, success);
    }
}
