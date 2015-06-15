package nbtool.gui;

import java.awt.event.*;
import java.awt.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.text.Document;
import javax.swing.text.Element;

import java.io.IOException;

import nbtool.data.*;
import nbtool.gui.utilitypanes.*;

public class WalkingEngineParameters {
	
	
    /* List of arguments and their setup values as BH put them initially
     * Several might not be used, but adding all of them for now.
     *
     */
	private String[] listOfParams = new String[] {

        "standComPosition (50.f, 262.0f)"  ,    /** The position of the center of mass relative to the right foot when standing */
        "standBodyTilt (0.f)"              ,    /** The tilt of the torso when standing */
        "standArmJointAngles (0.2f, 0.f)"  ,    /** The joint angles of the left arm when standing */
        "standHardnessAnklePitch (85)"     ,    /** The hardness of the ankle pitch joint for standing and walking */
        "standHardnessAnkleRoll (85)"      ,    /** The hardness of the ankle roll joint for standing and walking */
        "walkRef (16.f, 50.f)"             ,    /** The position of the pendulum pivot point in Q */
        "walkRefAtFullSpeedX (9.5f, 40.f)" ,    /** The position of the pendulum pivot point when walking forwards with maximum speed */
        "walkRefXPlanningLimit (-2.f, 3.f)",    /** The limit for shifting the pendulum pivot point towards the x-axis when
                                                    planning the next step size */
        "walkRefXLimit (-30.f, 30.f)"      ,    /** The limit for shifting the pendulum pivot point towards the x-axis when balancing */
        "walkRefYLimit (-30.f, 30.f)"      ,    /** The limit for shifting the pendulum pivot point towards the y-axis when balancing */
        "walkStepSizeXPlanningLimit (-50.f, 60.f)", /** The minimum and maximum step size used to plan the next step size */
        "walkStepSizeXLimit (-55.f, 66.f)"        , /** The minimum and maximum step size when balancing */

        "walkStepDuration (v5) (535.f)"             , /** the duration of a full step cycle (two half steps) */
        "walkStepDurationAtFullSpeedX (v5) (535.f)" , /** the duration of a full step cycle when walking forwards with maximum speed */
        "walkStepDuration (v4) (525.f)"             , /** the duration of a full step cycle (two half steps) */
        "walkStepDurationAtFullSpeedX (v4) (525.f)" , /** the duration of a full step cycle when walking forwards with maximum speed */

        "walkStepDurationAtFullSpeedY (180.f)", /** The duration of a full step cycle when walking sidewards with maximum speed */
        "walkHeight (262.f, 262.f))"          , /** the height of the 3d linear inverted pendulum plane
                                                    (for the pendulum motion towards the x-axis and the
                                                    pendulum motion towards the y-axis) */
        "walkArmRotationAtFullSpeedX (0.1f)"  , /** The maximum deflection for the arm swinging motion */
        "walkMovePhase (0.f, 1.f)"            , /** The beginning and length of the trajectory used to
                                                                   move the swinging foot to its new position */
        "walkLiftPhase (0.f, 1.f)"            , /** The beginning and length of the trajectory used to lift
                                                                      the swinging foot */
        "walkLiftOffset (v5) (0.f, 5.f, 22.f)"             , /** The height the swinging foot is lifted */
        "walkLiftOffsetAtFullSpeedX (v5) (0.f, 5.f, 22.f)" , /** The height the swinging foot is lifted */
        "walkLiftOffset (v4) (0.f, 5.f, 17.f)"             , /** The height the swinging foot is lifted */
        "walkLiftOffsetAtFullSpeedX (v4) (0.f, 5.f, 25.f)" , /** The height the swinging foot is lifted when
                                                                      walking full speed in x-direction */
        "walkLiftOffsetAtFullSpeedY (0.f, 20.f, 25.f)", /** The height the swinging foot is lifted 
                                                                    when walking full speed in y-direction */
        "walkLiftRotation (-0.05f, -0.1f, 0.f)"       , /** The amount the swinging foot is rotated while getting lifted */
        "walkSupportRotation (0.f)"                   , /** A rotation added to the supporting foot to boost the com acceleration */
        "walkComLiftOffset (0.f, 0.f, 2.3f)"          , /** The height the center of mass is lifted within a single support phase */

        "walkComBodyRotation (v5) (0.07f)"     ,  /** How much the torso is rotated to achieve the center of
                                                            mass shift along the y-axis */
        "walkComBodyRotation (v4) (0.05f)"     ,  /** How much the torso is rotated to achieve the center of
                                                            mass shift along the y-axis */
        "speedMax (0.5f, (120.f, 50.f))"     ,  /** The maximum walking speed (in "size of two steps") */
        "speedMaxBackwards (80.f)"             ,  /** The maximum walking speed for backwards
                                                               walking (in "size of two steps") */
        "speedMaxChange (0.1f, (8.f, 20.f)))" ,  /** The maximum walking speed deceleration that
                                                                     is used to avoid overshooting of the walking target */
         
        "balance (true)"                      ,  /**  Whether sensory feedback should be used or not */
        "balanceBodyRotation (0.8f, 0.f))"    ,  /** A  torso rotation p-control factor */
        "balanceCom (0.054f, 0.054f)"        ,  /** A measured center of mass position adoption factor */
        "balanceComVelocity (0.14f, 0.14f) " ,  /** A measured center of mass velocity adoption factor */
        "balanceRef (0.f, 0.08f)"            ,  /** A pendulum pivot point p-control factor */
        "balanceNextRef (0.2f, 0.f)"         ,  /** A pendulum pivot point of the upcoming single
                                                               support phase p-control factor */
        "balanceStepSize (0.1f, -0.04f)"     ,  /** A step size i-control factor */

        "observerMeasurementDelay (40.f)"     ,  /** The delay between setting a joint angle and
                                                        the ability of measuring the result */
        "observerMeasurementDeviation (2.f, 2.f)"         , /** The measurement uncertainty of the computed
                                                                      "measured" center of mass position */
        "observerProcessDeviation (0.1f, 0.1f, 3.f, 3.f)" , /** The noise of the filtering process that
                                                                      estimates the position of the center of mass */
         
        "odometryScale (1.f, (1.f, 1.f))",     /** A scaling factor for computed odometry data */
         
          /* Parameters to calculate the correction of the torso's angular velocity. */
        "gyroStateGain (0.01f)"         ,   /** Control weight (P) of the torso's angular velocity error. */
        "gyroDerivativeGain (0.0001f)"  ,   /** Control weight (D) of the approximated rate of change of the angular velocity error. */
        "gyroSmoothing (0.5f)"          ,   /** Smoothing (between 0 and 1!) to calculate the moving average of the y-axis gyro
                                                measurements. */
        
        "minRotationToReduceStepSize (1.3f)" ,   /** I have no idea what im doing! Colin pls fix this! (Actual B-Human comment)**/
	};

    public int getListOfParamsLength(){
        return listOfParams.length;
    }

}
