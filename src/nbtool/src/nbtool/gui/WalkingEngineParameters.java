package nbtool.gui;

import java.awt.event.*;
import java.awt.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.text.Document;
import javax.swing.text.Element;

import java.io.*;
import java.util.*;

import nbtool.data.*;
import nbtool.gui.utilitypanes.*;
import nbtool.util.*;
import nbtool.io.ControlIO;
import nbtool.io.ControlIO.ControlInstance;
import nbtool.util.NBConstants.*;
import nbtool.util.Logger;

//import messages.WalkEnginePreferences;
import messages.EngineParameters.WalkEnginePreferences;


public class WalkingEngineParameters {
	
	protected abstract class Parameter {
		protected int index;
		protected Parameter(int i) {this.index = i;}
		
		abstract JComponent getDisplay();
		abstract String getValue();
		abstract void setValue(String value);
	}
	
	protected class FreeParameter extends Parameter {
		
		JTextField display;
		
		FreeParameter(int i) {
			super(i);
			
			display = new JTextField(defaultValuesV4[i], 8);
			display.setToolTipText(parameterHints[i]);
			display.setEditable(true);
		}

		@Override
		JComponent getDisplay() {
			return display;
		}

		@Override
		String getValue() {
			return display.getText();
		}

		@Override
		void setValue(String value) {
			display.setText(value);
		}
	}
	
	protected final class FixedParameter extends FreeParameter {
		FixedParameter(int i) {
			super(i);
			this.display.setEditable(false);
		}
	}
	
	protected final class BoolParameter extends Parameter {
		JCheckBox display;
		BoolParameter(int i) {
			super(i);
			display = new JCheckBox();
			display.setText("");
			display.setToolTipText(parameterHints[i]);
			
			this.setValue(defaultValuesV4[i]);
		}

		@Override
		JComponent getDisplay() {
			return display;
		}

		@Override
		String getValue() {
			return String.valueOf(display.isSelected());
		}

		@Override
		void setValue(String value) {
			Boolean b = Boolean.parseBoolean(value);
			display.setSelected(b);
		}
	}
	
	protected class BoundedParameter extends Parameter {
		public static final int DENOMINATOR = 10000;
		JSlider display;
		int istart;
		int imin, imax;
		BoundedParameter(int i, float min, float max, float start) {
			super(i);
			
			istart = (int) (start * DENOMINATOR);
			imin = (int) (min * DENOMINATOR);
			imax = (int) (max * DENOMINATOR);
			
			display = new JSlider(JSlider.HORIZONTAL, imin, imax, istart);
			display.setToolTipText(parameterHints[i]);
			Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
			labelTable.put( new Integer( imin ), new JLabel("" + min) );
			labelTable.put( new Integer( imax ), new JLabel("" + max) );
			
			display.setLabelTable(labelTable);
			display.setPaintLabels(true);
		}
		
		@Override
		JComponent getDisplay() {
			return display;
		}
		
		@Override
		String getValue() {
			int sval = display.getValue();
			float fval = ((float) sval) / DENOMINATOR;
			return String.valueOf(fval);
		}
		
		@Override
		void setValue(String value) {
			/*
			float fval = Float.parseFloat(value);
			int ival = (int) (fval * DENOMINATOR);
			display. */
			
			Logger.warnf("BoundedParameter ignoring setValue() that may be outside bounds.");
			display.setValue(istart);
		}
	}

    /* List of arguments and their setup values as BH put them initially
     * Several might not be used, but adding all of them for now.
     *
     * If there is not a description of the value's purpose next to the parameter,
     * then the value is part of the last description.
     *
     * Default values are next to the param description, in parenthesis
     * if there are different values for v4 or v5 robots then it is also specified
     *
     * Descriptions taken from WalkingEngine.h , in the B-human walking engine code.
     * Don't blame me for their unhelpfulness
     */
	public String[] parameterNames = new String[] {
			
    "StandComPosition_x (50.f)"                         ,    /** The position of the center of mass relative to the right foot when standing */
    "StandComPos_y (262.0f)"                            ,
    "standBodyTilt (0.f)"                               ,    /** The tilt of the torso when standing */
    "standArmJointAngles_x (0.2f)"                      ,    /** The joint angles of the left arm when standing */
    "standArmJointAngles_y (0.f)"                       ,
    "standHardnessAnklePitch (85)"                      ,    /** The hardness of the ankle pitch joint for standing and walking */
    "standHardnessAnkleRoll (85)"                       ,    /** The hardness of the ankle roll joint for standing and walking */
    "walkRef_x (16.f)"                                  ,    /** The position of the pendulum pivot point in Q */
    "walkRef_y (50.f)"                                  ,
    "walkRefAtFullSpeed_x (9.5f)"                       ,    /** The position of the pendulum pivot point when walking forwards with maximum speed */
    "walkRefAtFullSpeed_y (40.f)"                       ,
    "walkRefXPlanningLimit_low (-2.f)"                  ,    /** The limit for shifting the pendulum pivot point towards the x-axis when planning the next step size */
    "walkRefXPlanningLimit_high (3.f)"                  ,
    "walkRefXLimit_low  (-30.f)"                        ,    /** The limit for shifting the pendulum pivot point towards the x-axis when balancing */
    "walkRefXLimit_high (30.f)"                         ,
    "walkRefYLimit_low  (-30.f)"                        ,    /** The limit for shifting the pendulum pivot point towards the y-axis when balancing */
    "walkRefYLimit_high (30.f)"                         ,
    "walkStepSizeXPlanningLimit_low (-50.f)"            ,    /** The minimum and maximum step size used to plan the next step size */
    "walkStepSizeXPlanningLimit_high (60.f)"            ,
    "walkStepSizeXLimit_low (-55.f)"                    ,    /** The minimum and maximum step size when balancing */
    "walkStepSizeXLimit_high (66.f)"                    ,
    "walkStepDuration (v5=535.f)(v4=525.f)"             , /** the duration of a full step cycle (two half steps) (NBites: index 21)*/
    "walkStepDurationAtFullSpeedX (v5=535.f)(v4=525.f)" , /** the duration of a full step cycle when walking forwards with maximum speed */
    "walkStepDurationAtFullSpeedY (180.f)"              , /** The duration of a full step cycle when walking sidewards with maximum speed */
    "walkHeight_x (262.f)"                              , /** The height of the 3d linear inverted pendulum plane (for the pendulum motion towards the x-axis and the pendulum motion towards the y-axis) */
    "walkHeight_y (262.f)"                              ,
    "walkArmRotationAtFullSpeedX (0.1f)"                , /** The maximum deflection for the arm swinging motion */
    "walkMovePhase_beginning (0.f)"                     , /** The beginning and length of the trajectory used to move the swinging foot to its new position */
    "walkMovePhase_length (1.f)"                        ,
    "walkLiftPhase_beginning (0.f)"                     , /** The beginning and length of the trajectory used to lift the swinging foot */
    "walkLiftPhase_length (1.f)"                        ,
    "walkLiftOffset_x"                                  , /** The height the swinging foot is lifted */
    "walkLiftOffset_y"                                  ,
    "walkLiftOffset_z(v5=22.f)(v4=17.f)"                ,
    "walkLiftOffsetAtFullSpeedX_x"                      , /** The height the swinging foot is lifted when walking in x direction*/
    "walkLiftOffsetAtFullSpeedX_y"                      ,
    "walkLiftOffsetAtFullSpeedX_z (v4=25.f)(v5=22.f)"   ,
    "walkLiftOffsetAtFullSpeedY_x (0.f)"                , /** The height the swinging foot is lifted when walking full speed in y-direction */
    "walkLiftOffsetAtFullSpeedY_y (20.f)"               ,
    "walkLiftOffsetAtFullSpeedY_z (25.f) "              ,
    "walkLiftRotation_x (-0.05f)"                       , /** The amount the swinging foot is rotated while getting lifted */
    "walkLiftRotation_y (-0.1f)"                        ,
    "walkLiftRotation_z (0.f)"                          ,
    "walkSupportRotation (0.f)"                         , /** A rotation added to the supporting foot to boost the com acceleration */
    "walkComLiftOffset_x (0.f)"                         , /** The height the center of mass is lifted within a single support phase */
    "walkComLiftOffset_y (0.f)"                         ,
    "walkComLiftOffset_z (2.3f)"                        ,
    "walkComBodyRotation (v5 = 0.07f)(v4 = 0.05f)"      , /** How much the torso is rotated to achieve the center of mass shift along the y-axis */
    "speedMax_rot (0.5f)"                               , /** The maximum walking speed (in "size of two steps") */
    "speedMax_x (120.f)"                                ,    
    "speedMax_y (50.f)"                                 ,    
    "speedMaxBackwards (80.f)"                          , /** The maximum walking speed for backwards walking (in "size of two steps") */
    "speedMaxChange_rot (0.1f)"                         , /** The maximum walking speed deceleration that is used to avoid overshooting of the walking target */
    "speedMaxChange_x (8.f)"                            ,
    "speedMaxChange_y (20.f)"                           ,
    "balance (true)"                                    , /**  Whether sensory feedback should be used or not */
    "balanceBodyRotation_x (0.8f)"                      , /** A  torso rotation p-control factor */
    "balanceBodyRotation_y (0.f)"                       ,
    "balanceCom_x (0.054f)"                             , /** A measured center of mass position adoption factor */
    "balanceCom_y (0.054f)"                             ,
    "balanceComVelocity_x (0.14f)"                      , /** A measured center of mass velocity adoption factor */
    "balanceComVelocity_y (0.14f)"                      ,
    "balanceRef_x (0.f)"                                , /** A pendulum pivot point p-control factor */
    "balanceRef_y (0.08f)"                              ,
    "balanceNextRef_x (0.2f)"                           , /** A pendulum pivot point of the upcoming single support phase p-control factor */
    "balanceNextRef_y (0.f)"                            ,
    "balanceStepSize_x (0.1f)"                          , /** A step size i-control factor */
    "balanceStepSize_y (-0.04f)"                        ,
    "observerMeasurementDelay (40.f)"                   , /** The delay between setting a joint angle and the ability of measuring the result */
    "CANT CHANGE_observerMeasurementDeviation_x (2.f)"  , /** The measurement uncertainty of the computed "measured" center of mass position */
    "CANT CHANGE_observerMeasurementDeviation_y (2.f)"  ,
    "CANT CHANGE_observerProcessDeviation_x (0.1f)"     , /** The noise of the filtering process that estimates the position of the center of mass */
    "CANT CHANGE_observerProcessDeviation_y (0.1f)"     ,
    "CANT CHANGE_observerProcessDeviation_z (3.f)"      ,
    "CANT CHANGE_observerProcessDeviation_w (3.f)"      ,
    "odometryScale_rot (1.f)"                           , /** A scaling factor for computed odometry data */
    "odometryScale_x   (1.f)"                           ,
    "odometryScale_y   (1.f)"                           ,

    /* Parameters to calculate the correction of the torso's angular velocity. */
    "gyroStateGain (0.01f)"                             , /** Control weight (P) of the torso's angular velocity error. */
    "gyroDerivativeGain (0.0001f)"                      , /** Control weight (D) of the approximated rate of change of the angular velocity error. */
    "gyroSmoothing (0.5f)"                              , /** Smoothing (between 0 and 1!) to calculate the moving average of the y-axis gyro measurements. */
    "minRotationToReduceStepSize (1.3f)"                , /** I have no idea what im doing! Colin pls fix this! (Actual B-Human comment)**/
	};
	
	public String[] parameterHints = {
			"/** The position of the center of mass relative to the right foot when standing */",
			"/** The position of the center of mass relative to the right foot when standing */",
			"/** The tilt of the torso when standing */",
			"/** The joint angles of the left arm when standing */",
			"/** The joint angles of the left arm when standing */",
			"/** The hardness of the ankle pitch joint for standing and walking */",
			"/** The hardness of the ankle roll joint for standing and walking */",
			"/** The position of the pendulum pivot point in Q */",
			"/** The position of the pendulum pivot point in Q */",
			"/** The position of the pendulum pivot point when walking forwards with maximum speed */",
			"/** The position of the pendulum pivot point when walking forwards with maximum speed */",
			"/** The limit for shifting the pendulum pivot point towards the x-axis when planning the next step size */",
			"/** The limit for shifting the pendulum pivot point towards the x-axis when planning the next step size */",
			"/** The limit for shifting the pendulum pivot point towards the x-axis when balancing */",
			"/** The limit for shifting the pendulum pivot point towards the x-axis when balancing */",
			"/** The limit for shifting the pendulum pivot point towards the y-axis when balancing */",
			"/** The limit for shifting the pendulum pivot point towards the y-axis when balancing */",
			"/** The minimum and maximum step size used to plan the next step size */",
			"/** The minimum and maximum step size used to plan the next step size */",
			"/** The minimum and maximum step size when balancing */",
			"/** The minimum and maximum step size when balancing */",
			"/** the duration of a full step cycle (two half steps) */",
			"/** the duration of a full step cycle when walking forwards with maximum speed */",
			"/** The duration of a full step cycle when walking sidewards with maximum speed */",
			"/** The height of the 3d linear inverted pendulum pla(for the pendulum motion towards the x-axis and the pendulum motion towards the y-axis) */",
			"/** The height of the 3d linear inverted pendulum pla(for the pendulum motion towards the x-axis and the pendulum motion towards the y-axis) */",
			"/** The maximum deflection for the arm swinging motion */",
			"/** The beginning and length of the trajectory used to move the swinging foot to its new position */",
			"/** The beginning and length of the trajectory used to move the swinging foot to its new position */",
			"/** The beginning and length of the trajectory used to lift the swinging foot */",
			"/** The beginning and length of the trajectory used to lift the swinging foot */",
			"/** The height the swinging foot is lifted */",
			"/** The height the swinging foot is lifted */",
			"/** The height the swinging foot is lifted */",
			"/** The height the swinging foot is lifted when walking in x direction*/",
			"/** The height the swinging foot is lifted when walking in x direction*/",
			"/** The height the swinging foot is lifted when walking in x direction*/",
			"/** The height the swinging foot is lifted when walking full speed in y-direction */",
			"/** The height the swinging foot is lifted when walking full speed in y-direction */",
			"/** The height the swinging foot is lifted when walking full speed in y-direction */",
			"/** The amount the swinging foot is rotated while getting lifted */",
			"/** The amount the swinging foot is rotated while getting lifted */",
			"/** The amount the swinging foot is rotated while getting lifted */",
			"/** A rotation added to the supporting foot to boost the com acceleration */",
			"/** The height the center of mass is lifted within a single support phase */",
			"/** The height the center of mass is lifted within a single support phase */",
			"/** The height the center of mass is lifted within a single support phase */",
			"/** How much the torso is rotated to achieve the center of mass shift along the y-axis */",
			"/** The maximum walking speed (in \"size of two steps\") */",
			"/** The maximum walking speed (in \"size of two steps\") */",
			"/** The maximum walking speed (in \"size of two steps\") */",
			"/** The maximum walking speed for backwards walking (in \"size of two steps\") */",
			"/** The maximum walking speed deceleration that is used to avoid overshooting of the walking target */",
			"/** The maximum walking speed deceleration that is used to avoid overshooting of the walking target */",
			"/** The maximum walking speed deceleration that is used to avoid overshooting of the walking target */",
			"/**  Whether sensory feedback should be used or not */",
			"/** A  torso rotation p-control factor */",
			"/** A  torso rotation p-control factor */",
			"/** A measured center of mass position adoption factor */",
			"/** A measured center of mass position adoption factor */",
			"/** A measured center of mass velocity adoption factor */",
			"/** A measured center of mass velocity adoption factor */",
			"/** A pendulum pivot point p-control factor */",
			"/** A pendulum pivot point p-control factor */",
			"/** A pendulum pivot point of the upcoming single support phase p-control factor */",
			"/** A pendulum pivot point of the upcoming single support phase p-control factor */",
			"/** A step size i-control factor */",
			"/** A step size i-control factor */",
			"/** The delay between setting a joint angle and the ability of measuring the result */",
			"/** The measurement uncertainty of the computed \"measured\" center of mass position */",
			"/** The measurement uncertainty of the computed \"measured\" center of mass position */",
			"/** The noise of the filtering process that estimates the position of the center of mass */",
			"/** The noise of the filtering process that estimates the position of the center of mass */",
			"/** The noise of the filtering process that estimates the position of the center of mass */",
			"/** The noise of the filtering process that estimates the position of the center of mass */",
			"/** A scaling factor for computed odometry data */",
			"/** A scaling factor for computed odometry data */",
			"/** A scaling factor for computed odometry data */",
			"/** Control weight (P) of the torso's angular velocity error. */",
			"/** Control weight (D) of the approximated rate of change of the angular velocity error. */",
			"/** Smoothing (between 0 and 1!) to calculate the moving average of the y-axis gyro measurements. */",
			"/** I have no idea what im doing! Colin pls fix this! (Actual B-Human comment)**/"
	};

    /* Values for the v4 robot. v5 values are in side comment.
     * The two versions share most of the same parameter values
     * (As B-Human put them).
     */
    public String[] defaultValuesV4 = new String [] {
                                      "50.f"   ,   
                                      "262.0f" ,
                                      "0.f"    ,   
                                      "0.2f"   ,   
                                      "0.f"    ,
                                      "85"     ,   
                                      "85"     ,   
                                      "16.f"   ,   
                                      "50.f"   ,
                                      "9.5f"   ,   
                                      "40.f"   ,
                                      "-2.f"   ,   
                                      "3.f"    ,
                                      "-30.f"  ,   
                                      "30.f"   ,
                                      "-30.f"  ,   
                                      "30.f"   ,
                                      "-50.f"  ,   
                                      "60.f"   ,
                                      "-55.f"  ,   
                                      "66.f"   ,
                                      "525.f"  , //(v5 = 535.f) Walkstep duration (index 21)
                                      "525.f"  , //(v5 = 535.f) Walkstep duration at full speed.(index 22)
                                      "180.f"  ,   
                                      "262.f"  ,   
                                      "262.f"  ,   
                                      "0.1f"   ,   
                                      "0.f"    ,   
                                      "1.f"    ,   
                                      "0.f"    ,   
                                      "1.f"    ,   

                                      "0.f"    , //(v5 = (0.f, 5.f, 22.f) 
                                      "5.f"    , //These are in the same vector
                                      "17.f"   , //(v5 = 22.f) (index 33) 

                                      "0.f"    , //(v5 = (0.f, 5.f, 22.f)
                                      "5.f"    , //These are in the same vector
                                      "25.f"   , //(v5 = 22.f) (index 36)

                                      "0.f"    ,   
                                      "20.f"   ,   
                                      "25.f"   ,   
                                      "-0.05f" ,   
                                      "-0.1f"  ,   
                                      "0.f"    ,   
                                      "0.f"    ,   
                                      "0.f"    ,   
                                      "0.f"    ,   
                                      "2.3f"   ,   
                                      "0.05f"  , //(v5 = 0.07f) Single value (index 47)
                                      "0.5f"   , 
                                      "120.f"  , 
                                      "50.f"   , 
                                      "80.f"   , 
                                      "0.1f"   , 
                                      "8.f"    , 
                                      "20.f"   , 
                                      "true"   , //only boolean value. balance (index 55) used for testing
                                      "0.8f"   ,   
                                      "0.f"    ,   
                                      "0.054f" ,   
                                      "0.054f" ,   
                                      "0.14f"  ,   
                                      "0.14f"  ,   
                                      "0.f"    ,   
                                      "0.08f"  ,   
                                      "0.2f"   ,   
                                      "0.f"    ,   
                                      "0.1f"   ,   
                                      "-0.04f" ,   
                                      "40.f"   ,   
                                      "2.f"    ,   
                                      "2.f"    ,
                                      "0.1f"   ,   
                                      "0.1f"   ,   
                                      "3.f"    ,   
                                      "3.f"    ,   
                                      "1.f"    ,   
                                      "1.f"    ,   
                                      "1.f"    ,   
                                      "0.01f"  ,   
                                      "0.0001f",   
                                      "0.5f"   ,   
                                      "1.3f"   ,   
    };
    
    //Variables used.
    public String[] defaultValuesV5 = buildV5ParamList();

    private String[] buildV5ParamList(){
    	String[] dv5;
    	dv5 = new String[defaultValuesV4.length];

        System.arraycopy(defaultValuesV4, 0, dv5, 0, defaultValuesV4.length);        

        //Actual default values for V5 robot for specific parameters.
        dv5[21] = "535.f"; //WalkStepDuration
        dv5[22] = "535.f"; //WalkStepDurationAtFullSpeedX
        dv5[33] = "22.f";
        dv5[36] = "22.f";
        dv5[47] = "0.07f";

        //test
        //defaultValuesV5[55] = "false";
        return dv5;
    }

    public int getListOfParamsLength(){
        return parameterNames.length;
    }
    public String[] getListOfParams(){
        return parameterNames;
    }
    public String[] getDefaultValuesV4(){
        return defaultValuesV4;
    }
    public String[] getDefaultValuesV5(){
        return defaultValuesV5;
    }

    /* Called when the Set Param button is pressed. Doesnt check if the 
     * input for balance is "true" or "false" 
     * */
    public void sendDataOverNetwork(String[] packet){

        //System.out.println("Printing whole array");
        //System.out.println(Arrays.toString(packet));
        //for(i=0
        //System.out.println("list of v4 params length: " + listOfV4Params.length);
        //System.out.println("Length of array: " + packet.length + "| Last element: " + packet[81] + "|First element:" + packet[0]);

        WalkEnginePreferences walkingEngineParams = WalkEnginePreferences.newBuilder()
                              .setVectorStandComPosY(Float.parseFloat(packet[0]))
                              .setVectorStandComPosZ(Float.parseFloat(packet[1]))

                              .setStandBodyTilt(Float.parseFloat(packet[2]))

                              .setVectorStandArmJointAngleX(Float.parseFloat(packet[3]))
                              .setVectorStandArmJointAngleY(Float.parseFloat(packet[4]))

                              .setStandHardnessAnklePitch(Long.parseLong(packet[5]))    //These are long!
                              .setStandHardnessAnkleRoll(Long.parseLong(packet[6]))     //These are long!

                              .setVectorWalkRefX(Float.parseFloat(packet[7]))
                              .setVectorWalkRefY(Float.parseFloat(packet[8]))

                              .setVectorWalkRefAtFullSpeedX(Float.parseFloat(packet[9]))
                              .setVectorWalkRefAtFullSpeedY(Float.parseFloat(packet[10]))

                              .setRangeWalkRefPlanningLimitLow(Float.parseFloat(packet[11]))
                              .setRangeWalkRefPlanningLimitHigh(Float.parseFloat(packet[12]))

                              .setRangeWalkRefXLimitLow(Float.parseFloat(packet[13]))
                              .setRangeWalkRefXLimitHigh(Float.parseFloat(packet[14]))

                              .setRangeWalkRefYLimitLow(Float.parseFloat(packet[15]))
                              .setRangeWalkRefYLimitHigh(Float.parseFloat(packet[16]))

                              .setRangeWalkStepSizeXPlanningLimitLow(Float.parseFloat(packet[17]))
                              .setRangeWalkStepSizeXPlanningLimitHigh(Float.parseFloat(packet[18]))

                              .setRangeWalkStepSizeXLimitLow(Float.parseFloat(packet[19]))
                              .setRangeWalkStepSizeXLimitHigh(Float.parseFloat(packet[20]))

                              .setWalkStepDuration(Float.parseFloat(packet[21])) //This index must be 21
                              .setWalkStepDurationAtFullSpeedX(Float.parseFloat(packet[22]))//This index must be 22
                              .setWalkStepDurationAtFullSpeedY(Float.parseFloat(packet[23]))

                              .setVectorWalkHeightX(Float.parseFloat(packet[24]))
                              .setVectorWalkHeightY(Float.parseFloat(packet[25]))

                              .setWalkArmRotationAtFullSpeedX(Float.parseFloat(packet[26]))

                              .setWalkMovePhaseBeginning(Float.parseFloat(packet[27]))
                              .setWalkMovePhaseLength(Float.parseFloat(packet[28]))

                              .setWalkLiftPhaseBeginning(Float.parseFloat(packet[29]))
                              .setWalkLiftPhaseLength(Float.parseFloat(packet[30]))

                              .setVectorWalkLiftOffSetX(Float.parseFloat(packet[31]))
                              .setVectorWalkLiftOffSetY(Float.parseFloat(packet[32]))
                              .setVectorWalkLiftOffSetZ(Float.parseFloat(packet[33]))

                              .setVectorWalkLiftOffSetAtFullSpeedXX(Float.parseFloat(packet[34]))
                              .setVectorWalkLiftOffSetAtFullSpeedXY(Float.parseFloat(packet[35]))
                              .setVectorWalkLiftOffSetAtFullSpeedXZ(Float.parseFloat(packet[36]))

                              .setVectorWalkLiftOffSetAtFullSpeedYX(Float.parseFloat(packet[37]))
                              .setVectorWalkLiftOffSetAtFullSpeedYY(Float.parseFloat(packet[38]))
                              .setVectorWalkLiftOffSetAtFullSpeedYZ(Float.parseFloat(packet[39]))

                              .setVectorWalkLiftRotationX(Float.parseFloat(packet[40]))
                              .setVectorWalkLiftRotationY(Float.parseFloat(packet[41]))
                              .setVectorWalkLiftRotationZ(Float.parseFloat(packet[42]))

                              .setWalkSupportRotation(Float.parseFloat(packet[43]))

                              .setWalkComLiftOffSetX(Float.parseFloat(packet[44]))
                              .setWalkComLiftOffSetY(Float.parseFloat(packet[45]))
                              .setWalkComLiftOffSetZ(Float.parseFloat(packet[46]))

                              .setWalkComBodyRotation(Float.parseFloat(packet[47]))

                              .setSpeedMaxRot(Float.parseFloat(packet[48]))
                              .setSpeedMaxVectorX(Float.parseFloat(packet[49]))
                              .setSpeedMaxVectorY(Float.parseFloat(packet[50]))

                              .setSpeedMaxBackwards(Float.parseFloat(packet[51]))

                              .setSpeedMaxChangeRot(Float.parseFloat(packet[52]))
                              .setSpeedMaxChangeVectorX(Float.parseFloat(packet[53]))
                              .setSpeedMaxChangeVectorY(Float.parseFloat(packet[54]))

                              .setBalance(Boolean.parseBoolean(packet[55])) //Boolean Conversion! TODO!! This index must be 55

                              .setVectorBalanceBodyRotationX(Float.parseFloat(packet[56]))
                              .setVectorBalanceBodyRotationY(Float.parseFloat(packet[57]))

                              .setVectorBalanceComX(Float.parseFloat(packet[58]))
                              .setVectorBalanceComY(Float.parseFloat(packet[59]))

                              .setVectorBalanceComVelocityX(Float.parseFloat(packet[60]))
                              .setVectorBalanceComVelocityY(Float.parseFloat(packet[61]))

                              .setVectorBalanceRefX(Float.parseFloat(packet[62]))
                              .setVectorBalanceRefY(Float.parseFloat(packet[63]))

                              .setVectorBalanceNextRefX(Float.parseFloat(packet[64]))
                              .setVectorBalanceNextRefY(Float.parseFloat(packet[65]))

                              .setVectorBalanceStepSizeX(Float.parseFloat(packet[66]))
                              .setVectorBalanceStepSizeY(Float.parseFloat(packet[67]))

                              .setObserverMeasurementDelay(Float.parseFloat(packet[68]))

                              .setVectorObserverMeasurementDeviationX(Float.parseFloat(packet[69]))
                              .setVectorObserverMeasurementDeviationY(Float.parseFloat(packet[70]))

                              .setVectorObserverProcessDeviationX(Float.parseFloat(packet[71]))
                              .setVectorObserverProcessDeviationY(Float.parseFloat(packet[72]))
                              .setVectorObserverProcessDeviationZ(Float.parseFloat(packet[73]))
                              .setVectorObserverProcessDeviationW(Float.parseFloat(packet[74]))

                              .setOdometryScaleRot(Float.parseFloat(packet[75]))
                              .setOdometryScaleVectorX(Float.parseFloat(packet[76]))
                              .setOdometryScaleVectorY(Float.parseFloat(packet[77]))

                              .setGyroStateGain(Float.parseFloat(packet[78]))
                              .setGyroDerivativeGain(Float.parseFloat(packet[79]))
                              .setGyroSmoothing(Float.parseFloat(packet[80]))

                              .setMinRotationToReduceStepSize(Float.parseFloat(packet[81]))

                              .build(); //incredibly everything here is treated as in the same line
        								/*That's called daisy chaining Franco!  Google thinks its cool.*/
        
        ControlInstance inst = ControlIO.getByIndex(0);
        if (inst == null) {
			Logger.log(Logger.WARN, "EngineParameterPanel clicked while no ControlInstance available");
            //does not return anything
			return;
		}

        boolean success = inst.tryAddCmnd(ControlIO.createCmndSetWalkingEngineParameters(walkingEngineParams));

        //TODO this actually just means the command was accepted by the ControlInstance.
        if(success){ System.out.println("Successfully sent a walking engine parameters command through the tool");}

	    Logger.logf(Logger.INFO, "EngineParameterPanel: CommandIO.createCmdSendMotionCommands(%s) returned %B\n", packet, success);
    }


    void writeDataToFile(String[] params, int naoVersion){

        SExpr  sexParams = buildSExprFromArray(params);
        String paramString = sexParams.serialize();
        File engineParams;

		if(naoVersion == 4) {
			engineParams = new File("../../src/man/config/V4WalkEngineParameters.txt");
		} else {
			engineParams = new File("../../src/man/config/V5WalkEngineParameters.txt");
		}
		
		try {
			BufferedWriter paramsOut = new BufferedWriter(new FileWriter(engineParams));
			paramsOut.write(paramString);
			paramsOut.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
        System.out.println("Parameters Saved");
    }

    private SExpr buildSExprFromArray(String[] Params) {
		SExpr s;
		s = SExpr.list(
                SExpr.newKeyValue("vectorStandComPos_y",Params[0]),
                SExpr.newKeyValue("vectorStandComPos_z",Params[1]),
                SExpr.newKeyValue("standBodyTilt",Params[2]),
                SExpr.newKeyValue("vectorStandArmJointAngle_x",Params[3]),
                SExpr.newKeyValue("vectorStandArmJointAngle_y",Params[4]),
                SExpr.newKeyValue("standHardnessAnklePitch",Params[5]),
                SExpr.newKeyValue("standHardnessAnkleRoll",Params[6]),
                SExpr.newKeyValue("vectorWalkRef_x",Params[7]),
                SExpr.newKeyValue("vectorWalkRef_y",Params[8]),
                SExpr.newKeyValue("vectorWalkRefAtFullSpeed_x",Params[9]),
                SExpr.newKeyValue("vectorWalkRefAtFullSpeed_y",Params[10]),
                SExpr.newKeyValue("rangeWalkRefPlanningLimit_low",Params[11]),
                SExpr.newKeyValue("rangeWalkRefPlanningLimit_high",Params[12]),
                SExpr.newKeyValue("rangeWalkRefXLimit_low",Params[13]),
                SExpr.newKeyValue("rangeWalkRefXLimit_high",Params[14]),
                SExpr.newKeyValue("rangeWalkRefYLimit_low",Params[15]),
                SExpr.newKeyValue("rangeWalkRefYLimit_high",Params[16]),
                SExpr.newKeyValue("rangeWalkStepSizeXPlanningLimit_low",Params[17]),
                SExpr.newKeyValue("rangeWalkStepSizeXPlanningLimit_high",Params[18]),
                SExpr.newKeyValue("rangeWalkStepSizeXLimit_low",Params[19]),
                SExpr.newKeyValue("rangeWalkStepSizeXLimit_high",Params[20]),
                SExpr.newKeyValue("walkStepDuration",Params[21]),
                SExpr.newKeyValue("walkStepDurationAtFullSpeedX",Params[22]),
                SExpr.newKeyValue("walkStepDurationAtFullSpeedY",Params[23]),
                SExpr.newKeyValue("vectorWalkHeight_x",Params[24]),
                SExpr.newKeyValue("vectorWalkHeight_y",Params[25]),
                SExpr.newKeyValue("walkArmRotationAtFullSpeedX",Params[26]),
                SExpr.newKeyValue("walkMovePhaseBeginning",Params[27]),
                SExpr.newKeyValue("walkMovePhaseLength",Params[28]),
                SExpr.newKeyValue("walkLiftPhaseBeginning",Params[29]),
                SExpr.newKeyValue("walkLiftPhaseLength",Params[30]),
                SExpr.newKeyValue("vectorWalkLiftOffSet_x",Params[31]),
                SExpr.newKeyValue("vectorWalkLiftOffSet_y",Params[32]),
                SExpr.newKeyValue("vectorWalkLiftOffSet_z",Params[33]),
                SExpr.newKeyValue("vectorWalkLiftOffSetAtFullSpeedX_x",Params[34]),
                SExpr.newKeyValue("vectorWalkLiftOffSetAtFullSpeedX_y",Params[35]),
                SExpr.newKeyValue("vectorWalkLiftOffSetAtFullSpeedX_z",Params[36]),
                SExpr.newKeyValue("vectorWalkLiftOffSetAtFullSpeedY_x",Params[37]),
                SExpr.newKeyValue("vectorWalkLiftOffSetAtFullSpeedY_y",Params[38]),
                SExpr.newKeyValue("vectorWalkLiftOffSetAtFullSpeedY_z",Params[39]),
                SExpr.newKeyValue("vectorWalkLiftRotation_x",Params[40]),
                SExpr.newKeyValue("vectorWalkLiftRotation_y",Params[41]),
                SExpr.newKeyValue("vectorWalkLiftRotation_z",Params[42]),
                SExpr.newKeyValue("walkSupportRotation",Params[43]),
                SExpr.newKeyValue("walkComLiftOffSet_x",Params[44]),
                SExpr.newKeyValue("walkComLiftOffSet_y",Params[45]),
                SExpr.newKeyValue("walkComLiftOffSet_z",Params[46]),
                SExpr.newKeyValue("walkComBodyRotation",Params[47]),
                SExpr.newKeyValue("speedMax_rot",Params[48]),
                SExpr.newKeyValue("speedMax_Vector_x",Params[49]),
                SExpr.newKeyValue("speedMax_Vector_y",Params[50]),
                SExpr.newKeyValue("speedMaxBackwards",Params[51]),
                SExpr.newKeyValue("speedMaxChange_rot",Params[52]),
                SExpr.newKeyValue("speedMaxChange_Vector_x",Params[53]),
                SExpr.newKeyValue("speedMaxChange_Vector_y",Params[54]),
                SExpr.newKeyValue("balance",Params[55]),
                SExpr.newKeyValue("vectorBalanceBodyRotation_x",Params[56]),
                SExpr.newKeyValue("vectorBalanceBodyRotation_y",Params[57]),
                SExpr.newKeyValue("vectorBalanceCom_x",Params[58]),
                SExpr.newKeyValue("vectorBalanceCom_y",Params[59]),
                SExpr.newKeyValue("vectorBalanceComVelocity_x",Params[60]),
                SExpr.newKeyValue("vectorBalanceComVelocity_y",Params[61]),
                SExpr.newKeyValue("vectorBalanceRef_x",Params[62]),
                SExpr.newKeyValue("vectorBalanceRef_y",Params[63]),
                SExpr.newKeyValue("vectorBalanceNextRef_x",Params[64]),
                SExpr.newKeyValue("vectorBalanceNextRef_y",Params[65]),
                SExpr.newKeyValue("vectorBalanceStepSize_x",Params[66]),
                SExpr.newKeyValue("vectorBalanceStepSize_y",Params[67]),
                SExpr.newKeyValue("observerMeasurementDelay",Params[68]),
                SExpr.newKeyValue("vectorObserverMeasurementDeviation_x",Params[69]),
                SExpr.newKeyValue("vectorObserverMeasurementDeviation_y",Params[70]),
                SExpr.newKeyValue("vectorObserverProcessDeviation_x",Params[71]),
                SExpr.newKeyValue("vectorObserverProcessDeviation_y",Params[72]),
                SExpr.newKeyValue("vectorObserverProcessDeviation_z",Params[73]),
                SExpr.newKeyValue("vectorObserverProcessDeviation_w",Params[74]),
                SExpr.newKeyValue("odometryScale_rot",Params[75]),
                SExpr.newKeyValue("odometryScale_Vector_x",Params[76]),
                SExpr.newKeyValue("odometryScale_Vector_y",Params[77]),
                SExpr.newKeyValue("gyroStateGain",Params[78]),
                SExpr.newKeyValue("gyroDerivativeGain",Params[79]),
                SExpr.newKeyValue("gyroSmoothing",Params[80]),
                SExpr.newKeyValue("minRotationToReduceStepSize",Params[81])
                );
		return s;                                                                     
	}                          

    protected Parameter[] parameters = {
			new FreeParameter(0),
			new FreeParameter(1),
			new FreeParameter(2),
			new FreeParameter(3),
			new FreeParameter(4),
			new FreeParameter(5),
			new FreeParameter(6),
			new FreeParameter(7),
			new FreeParameter(8),
			new FreeParameter(9),
			new FreeParameter(10),
			new FreeParameter(11),
			new FreeParameter(12),
			new FreeParameter(13),
			new FreeParameter(14),
			new FreeParameter(15),
			new FreeParameter(16),
			new FreeParameter(17),
			new FreeParameter(18),
			new FreeParameter(19),
			new FreeParameter(20),
			new FreeParameter(21),
			new FreeParameter(22),
			new FreeParameter(23),
			new FreeParameter(24),
			new FreeParameter(25),
			new FreeParameter(26),
			new FreeParameter(27),
			new FreeParameter(28),
			new FreeParameter(29),
			new FreeParameter(30),
			new FreeParameter(31),
			new FreeParameter(32),
			new FreeParameter(33),
			new FreeParameter(34),
			new FreeParameter(35),
			new FreeParameter(36),
			new FreeParameter(37),
			new FreeParameter(38),
			new FreeParameter(39),
			new FreeParameter(40),
			new FreeParameter(41),
			new FreeParameter(42),
			new FreeParameter(43),
			new FreeParameter(44),
			new FreeParameter(45),
			new FreeParameter(46),
			new FreeParameter(47),
			new FreeParameter(48),
			new FreeParameter(49),
			new FreeParameter(50),
			new FreeParameter(51),
			new FreeParameter(52),
			new FreeParameter(53),
			new FreeParameter(54),
			new BoolParameter(55),
			new FreeParameter(56),
			new FreeParameter(57),
			new FreeParameter(58),
			new FreeParameter(59),
			new FreeParameter(60),
			new FreeParameter(61),
			new FreeParameter(62),
			new FreeParameter(63),
			new FreeParameter(64),
			new FreeParameter(65),
			new FreeParameter(66),
			new FreeParameter(67),
			new FreeParameter(68),
			new FixedParameter(69),
			new FixedParameter(70),
			new FixedParameter(71),
			new FixedParameter(72),
			new FixedParameter(73),
			new FixedParameter(74),
			new BoundedParameter(75, 0f, 1f, 1f),
			new FreeParameter(76),
			new FreeParameter(77),
			new FreeParameter(78),
			new FreeParameter(79),
			new FreeParameter(80),
			new FreeParameter(81),
	};
}
