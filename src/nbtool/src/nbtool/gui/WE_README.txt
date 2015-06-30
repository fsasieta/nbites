
*************************************
* Walk Engine Parameter Tool README *
*************************************

This file is used to document any behavior learnt from the walk engine
The Northern-Bites (at the time of writing this) do not make their own 
walk engine system. Instead, we copy and port the code we need from the 
german team B-Human, and adapt that to our needs.

The walk engine has model the robot as an inverted pendulum, and this allows
the system to keep the robot from wobbling too wildly, and it help it keep it
balance while walking and being pushed (disable the balance boolean in the
tool to see what I mean). Additionally, the model does several computations
with default starting parameters, and this document aims to collect
information regarding those. B-Human categorizes 51 parameters, but in reality
there are 82, since several of those parameters have several components (i.e 
2D, 3D and 4D vectors and phase parameters, as well as rotation vectors). I
tried to document these in the name of the parameter, so parameters that are
related to each other have the same name at the beginnning and have their
specific characteric at the end of the name.

This index is organized from 0 to 82, but I grouped parameters that are related
to each other accordingly and separated the groups by spaces. I also 
put the description provided by B-Human directly below the parameters name, in 
case there is no information gathered by our team and clarity. One B-Human 
description is only included with the group parameter. Some B-Human
descriptions will be more helpful than others, as you will probably find
out soon enough. In addition, next to each parameter you'll find the value
that B-Human initially put, and if the default B-Human value differs from
model to model, whether the value is for a v4 or v5  will be specified).

It should be noted there are parameters that at the time of this writing
cannot be modified because their modification gives a segmentation fault,
which is of course not ideal. These Values are marked by "CANT CHANGE" at the
beginning of their names, and are included here for completeness.

*** Tool usage specifications ***

As of today the java files used for this part of the tool are:
WalkingEngineParameters.java and EngineParametersPanel.java.
In case a JSlider is needed (as opposed to a JTextField) the changes
need only be done in the former file (from FreeParameter to Bounded
Parameters).
There are several buttons in the interface. The nao will display the current
values in loaded at the command line, as long as "naolog" was used. Then,
whenever the robott's values are changed, the current values sent will be
displayed on the side, next to the input components. 
If you want to change the robot's values to their default, press the 
"Default Values v4" (or v5), and then press the set robot parameters. 
This will save the values on the robot. In case you want to make the 
robot save the values you currently have, you can save the values 
locally, which will override the starting values the robot will have in the
future (this works for any robot to which you install later). There are
different starting values for v4's and v5's

*** Version Information ***

The version of the walking engine used to make this tool (and corresponding
README) is the 2013 Josh Imhoff port of the engine.

@author Franco Sasieta 
@email fsasieta@bowdoin.edu || franco.sasieta@gmail.com
@date last modified: 07/30/15


 (0),   "StandComPosition_x (50.f)"                             
 (1),   "StandComPos_y (262.0f)"                            
        /** The position of the center of mass relative to the right foot when standing */

 (2),   "standBodyTilt (0.f)"                                   
        /** The tilt of the torso when standing */

 (3),   "standArmJointAngles_x (0.2f)"                          
 (4),   "standArmJointAngles_y (0.f)"                       
        /** The joint angles of the left arm when standing */

 (5),   "standHardnessAnklePitch (85)"                          
        /** The hardness of the ankle pitch joint for standing and walking */

 (6),   "standHardnessAnkleRoll (85)"                           
        /** The hardness of the ankle roll joint for standing and walking */

 (7),   "walkRef_x (16.f)"                                      
 (8),   "walkRef_y (50.f)"                                  
        /** The position of the pendulum pivot point in Q */

 (9),   "walkRefAtFullSpeed_x (9.5f)"                           
 (10)   "walkRefAtFullSpeed_y (40.f)"                       
        /** The position of the pendulum pivot point when walking forwards with maximum speed */

 (11)   "walkRefXPlanningLimit_low (-2.f)"                      
 (12)   "walkRefXPlanningLimit_high (3.f)"                  
        /** The limit for shifting the pendulum pivot point towards the x-axis when planning the next step size */

 (13)   "walkRefXLimit_low  (-30.f)"                            
 (14)   "walkRefXLimit_high (30.f)"                         
        /** The limit for shifting the pendulum pivot point towards the x-axis when balancing */

 (15)   "walkRefYLimit_low  (-30.f)"                            
 (16)   "walkRefYLimit_high (30.f)"                         
        /** The limit for shifting the pendulum pivot point towards the y-axis when balancing */

 (17)   "walkStepSizeXPlanningLimit_low (-50.f)"                
 (18)   "walkStepSizeXPlanningLimit_high (60.f)"            
        /** The minimum and maximum step size used to plan the next step size */

 (19)   "walkStepSizeXLimit_low (-55.f)"                     
 (20)   "walkStepSizeXLimit_high (66.f)"                    
        /** The minimum and maximum step size when balancing */

 (21)   "walkStepDuration (v5=535.f)(v4=525.f)"              
        /** the duration of a full step cycle (two half steps) */

 (22)   "walkStepDurationAtFullSpeedX (v5=535.f)(v4=525.f)"  
        /** the duration of a full step cycle when walking forwards with maximum speed */

 (23)   "walkStepDurationAtFullSpeedY (180.f)"               
        /** The duration of a full step cycle when walking sidewards with maximum speed */

 (24)   "walkHeight_x (262.f)"                               
 (25)   "walkHeight_y (262.f)"                              
        /** The height of the 3d linear inverted pendulum plane (for the pendulum motion towards the x-axis and the pendulum motion towards the y-axis) */

 (26)   "walkArmRotationAtFullSpeedX (0.1f)"                 
        /** The maximum deflection for the arm swinging motion */

 (27)   "walkMovePhase_beginning (0.f)"                      
 (28)   "walkMovePhase_length (1.f)"                        
        /** The beginning and length of the trajectory used to move the swinging foot to its new position */

 (29)   "walkLiftPhase_beginning (0.f)"                      
 (30)   "walkLiftPhase_length (1.f)"                        
        /** The beginning and length of the trajectory used to lift the swinging foot */

 (31)   "walkLiftOffset_x"                                   
 (32)   "walkLiftOffset_y"                                  
 (33)   "walkLiftOffset_z(v5=22.f)(v4=17.f)"                
        /** The height the swinging foot is lifted */

 (34)   "walkLiftOffsetAtFullSpeedX_x"                       
 (35)   "walkLiftOffsetAtFullSpeedX_y"                      
 (36)   "walkLiftOffsetAtFullSpeedX_z (v4=25.f)(v5=22.f)"   
        /** The height the swinging foot is lifted when walking in x direction*/

 (37)   "walkLiftOffsetAtFullSpeedY_x (0.f)"                 
 (38)   "walkLiftOffsetAtFullSpeedY_y (20.f)"               
 (39)   "walkLiftOffsetAtFullSpeedY_z (25.f) "              
        /** The height the swinging foot is lifted when walking full speed in y-direction */

 (40)   "walkLiftRotation_x (-0.05f)"                        
 (41)   "walkLiftRotation_y (-0.1f)"                        
 (42)   "walkLiftRotation_z (0.f)"                          
        /** The amount the swinging foot is rotated while getting lifted */

 (43)   "walkSupportRotation (0.f)"                          
        /** A rotation added to the supporting foot to boost the com acceleration */

 (44)   "walkComLiftOffset_x (0.f)"                          
 (45)   "walkComLiftOffset_y (0.f)"                         
 (46)   "walkComLiftOffset_z (2.3f)"                        
        /** The height the center of mass is lifted within a single support phase */

 (47)   "walkComBodyRotation (v5 = 0.07f)(v4 = 0.05f)"       
        /** How much the torso is rotated to achieve the center of mass shift along the y-axis */

 (48)   "speedMax_rot (0.5f)"                                
 (49)   "speedMax_x (120.f)"                                    
 (50)   "speedMax_y (50.f)"                                     
        /** The maximum walking speed (in "size of two steps") */

 (51)   "speedMaxBackwards (80.f)"                           
        /** The maximum walking speed for backwards walking (in "size of two steps") */

 (52)   "speedMaxChange_rot (0.1f)"                          
 (53)   "speedMaxChange_x (8.f)"                            
 (54)   "speedMaxChange_y (20.f)"                           
        /** The maximum walking speed deceleration that is used to avoid overshooting of the walking target */

 (55)   "balance (true)"                                     
        /**  Whether sensory feedback should be used or not */
        NBITES: Should never be turned off (false). This value dramatically
        changes the behavior of the engine and makes the whole system
        unstable. It's pretty obvious when testing.

 (56)   "balanceBodyRotation_x (0.8f)"                       
 (57)   "balanceBodyRotation_y (0.f)"                       
        /** A  torso rotation p-control factor */

 (58)   "balanceCom_x (0.054f)"                              
 (59)   "balanceCom_y (0.054f)"                             
        /** A measured center of mass position adoption factor */

 (60)   "balanceComVelocity_x (0.14f)"                       
 (61)   "balanceComVelocity_y (0.14f)"                      
        /** A measured center of mass velocity adoption factor */

 (62)   "balanceRef_x (0.f)"                                 
 (63)   "balanceRef_y (0.08f)"                              
        /** A pendulum pivot point p-control factor */
        NBITES: p-control stands for "proportional control", if wikipedia is
        right. This seems to be part of the control theory system used in the model.

 (64)   "balanceNextRef_x (0.2f)"                            
 (65)   "balanceNextRef_y (0.f)"                            
        /** A pendulum pivot point of the upcoming single support phase p-control factor */

 (66)   "balanceStepSize_x (0.1f)"                           
 (67)   "balanceStepSize_y (-0.04f)"                        
        /** A step size i-control factor */

 (68)   "observerMeasurementDelay (40.f)"                    
        /** The delay between setting a joint angle and the ability of measuring the result */

 (69)   "CANT CHANGE_observerMeasurementDeviation_x (2.f)"   
 (70)   "CANT CHANGE_observerMeasurementDeviation_y (2.f)"  
        /** The measurement uncertainty of the computed "measured" center of mass position */

 (71)   "CANT CHANGE_observerProcessDeviation_x (0.1f)"      
 (72)   "CANT CHANGE_observerProcessDeviation_y (0.1f)"     
 (73)   "CANT CHANGE_observerProcessDeviation_z (3.f)"      
 (74)   "CANT CHANGE_observerProcessDeviation_w (3.f)"      
        /** The noise of the filtering process that estimates the position of the center of mass */

 (76)   "odometryScale_rot (1.f)"                            
 (76)   "odometryScale_x   (1.f)"                           
 (77)   "odometryScale_y   (1.f)"                           
        /** A scaling factor for computed odometry data */    


        /** Parameters to calculate the correction of the torso's angular velocity. */
        /**
 (78)   "gyroStateGain (0.01f)"                              
        /** Control weight (P) of the torso's angular velocity error. */

 (79)   "gyroDerivativeGain (0.0001f)"                       
        /** Control weight (D) of the approximated rate of change of the angular velocity error. */

 (80)   "gyroSmoothing (0.5f)"                               
        /** Smoothing (between 0 and 1!) to calculate the moving average of the y-axis gyro measurements. */

 (81)   "minRotationToReduceStepSize (1.3f)"                 
        /** I have no idea what im doing! Colin pls fix this! (Actual B-Human comment)**/

