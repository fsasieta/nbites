package behavior_sim;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.BasicStroke;

public class FieldConstants
{
    // FOR SIM
    public static final int NUM_POSTS = 4; // number of goal posts
    public static final int TEAM_SIZE = 4; // max players per team <no goalies>
    public static final int NUM_PLAYERS = 2*TEAM_SIZE;     // max number of players on the field
    public static final int NUM_PLAYERS_PER_TEAM = 5;       // with goalies
    public static final int TIME_PER_HALF = 600;   // 10 minute half

    public static final float FIELD_WHITE_WIDTH = 900.f;
    public static final float FIELD_WHITE_HEIGHT = 600.f;
    public static final float GREEN_PAD_X = 69.5f;
    public static final float GREEN_PAD_Y = 69.5f;

    public static final float LINE_WIDTH = 5.0f;

    public static final float FIELD_GREEN_WIDTH = FIELD_WHITE_WIDTH + 2.0f * GREEN_PAD_X;
    public static final float FIELD_GREEN_HEIGHT = FIELD_WHITE_HEIGHT + 2.0f * GREEN_PAD_Y;
    public static final float FIELD_WIDTH = FIELD_GREEN_WIDTH;
    public static final float FIELD_HEIGHT = FIELD_GREEN_HEIGHT;
        
    public static final float CENTER_FIELD_X = FIELD_GREEN_WIDTH * .5f;
    public static final float CENTER_FIELD_Y = FIELD_GREEN_HEIGHT * .5f;
    public static final float FIELD_GREEN_LEFT_SIDELINE_X = 0;
    public static final float FIELD_GREEN_RIGHT_SIDELINE_X = FIELD_GREEN_WIDTH;
    public static final float FIELD_GREEN_BOTTOM_SIDELINE_Y = 0;
    public static final float FIELD_GREEN_TOP_SIDELINE_Y = FIELD_GREEN_HEIGHT;
    
    public static final float FIELD_WHITE_BOTTOM_SIDELINE_Y = GREEN_PAD_Y;
    public static final float FIELD_WHITE_TOP_SIDELINE_Y = (FIELD_WHITE_HEIGHT +
                                                      GREEN_PAD_Y);
    public static final float FIELD_WHITE_LEFT_SIDELINE_X = GREEN_PAD_X;
    public static final float FIELD_WHITE_RIGHT_SIDELINE_X = (FIELD_WHITE_WIDTH +
                                                        GREEN_PAD_X);
    
    public static final float MIDFIELD_X = FIELD_GREEN_WIDTH * .5f;
    public static final float MIDFIELD_Y = FIELD_GREEN_HEIGHT * .5f;
        
    // Other Field object dimensions
    public static final float GOAL_POST_CM_HEIGHT = 90.0f; // 80cm to the bottom
                                                        // of the crossbar.
    public static final float GOAL_POST_CM_WIDTH = 10.0f;
    public static final float CROSSBAR_CM_WIDTH = 150.f;
    public static final float CROSSBAR_CM_HEIGHT = 10.0f;
    public static final float GOAL_DEPTH = 50.0f;
    public static final float GOAL_POST_RADIUS = GOAL_POST_CM_WIDTH / 2.0f;
    
    // GOAL CONSTANTS
    public static final float LANDMARK_MY_GOAL_BOTTOM_POST_X =
        FIELD_WHITE_LEFT_SIDELINE_X + GOAL_POST_RADIUS;
    public static final float LANDMARK_MY_GOAL_TOP_POST_X =
        FIELD_WHITE_LEFT_SIDELINE_X + GOAL_POST_RADIUS;
    public static final float LANDMARK_OPP_GOAL_BOTTOM_POST_X =
        FIELD_WHITE_RIGHT_SIDELINE_X - GOAL_POST_RADIUS;
    public static final float LANDMARK_OPP_GOAL_TOP_POST_X =
    FIELD_WHITE_RIGHT_SIDELINE_X - GOAL_POST_RADIUS;

    public static final float LANDMARK_MY_GOAL_BOTTOM_POST_Y =
        CENTER_FIELD_Y - CROSSBAR_CM_WIDTH / 2.0f;
    public static final float LANDMARK_MY_GOAL_TOP_POST_Y =
        CENTER_FIELD_Y + CROSSBAR_CM_WIDTH / 2.0f;
    public static final float LANDMARK_OPP_GOAL_BOTTOM_POST_Y =
        CENTER_FIELD_Y - CROSSBAR_CM_WIDTH / 2.0f;
    public static final float LANDMARK_OPP_GOAL_TOP_POST_Y =
        CENTER_FIELD_Y + CROSSBAR_CM_WIDTH / 2.0f;
    
    public static final float CENTER_CIRCLE_RADIUS = 75.0f; // Not scaled

    public static final float GOALBOX_DEPTH = 60.0f;
    public static final float GOALBOX_WIDTH = 220.0f;


    // These are used by the vision system
    // The distance the goalie box extends out past each goal post
    public static final float GOALBOX_OVERAGE = (GOALBOX_WIDTH -
                                          (CROSSBAR_CM_WIDTH +
                                           2.0f * GOAL_POST_RADIUS)) / 2.0f;
    // The distance from any goal post to the goalie box corner nearest it
    public static final float POST_CORNER = (float)Math.sqrt(GOALBOX_DEPTH * GOALBOX_DEPTH +
                                           GOALBOX_OVERAGE * GOALBOX_OVERAGE);
    
    // Headings to goal from center
    public static final float OPP_GOAL_HEADING = 0.0f;
    public static final float MY_GOAL_HEADING = 180.0f;

    // my goal box constants relative to (0,0) on my team
    public static final float MY_GOALBOX_TOP_Y = MIDFIELD_Y + GOALBOX_WIDTH * .5f;
    public static final float MY_GOALBOX_BOTTOM_Y = MIDFIELD_Y - GOALBOX_WIDTH * .5f;
    // bottom as in closest to (0,0)
    public static final float MY_GOALBOX_LEFT_X = GREEN_PAD_X;
    public static final float MY_GOALBOX_RIGHT_X = GREEN_PAD_X + GOALBOX_DEPTH;
    
    // opp goal box constants relative to (0,0) on my team
    public static final float OPP_GOALBOX_BOTTOM_Y = MIDFIELD_Y - GOALBOX_WIDTH * .5f;
    public static final float OPP_GOALBOX_TOP_Y = MIDFIELD_Y + GOALBOX_WIDTH * .5f;
    public static final float OPP_GOALBOX_LEFT_X =
    FIELD_WHITE_RIGHT_SIDELINE_X - GOALBOX_DEPTH;
    public static final float OPP_GOALBOX_RIGHT_X = FIELD_WHITE_RIGHT_SIDELINE_X;

    public static final float LINE_CROSS_OFFSET = 130.0f;
        
    public static final float LANDMARK_MY_GOAL_CROSS_X = FIELD_WHITE_LEFT_SIDELINE_X +
        LINE_CROSS_OFFSET;
    public static final float LANDMARK_MY_GOAL_CROSS_Y = MIDFIELD_Y;
    public static final float LANDMARK_OPP_GOAL_CROSS_X = FIELD_WHITE_RIGHT_SIDELINE_X -
        LINE_CROSS_OFFSET;
    public static final float LANDMARK_OPP_GOAL_CROSS_Y = MIDFIELD_Y;

    public static final float CC_LINE_CROSS_X = MIDFIELD_X;
    public static final float CC_LINE_CROSS_Y = MIDFIELD_Y;

    public static final float TOP_CC_Y = CENTER_FIELD_Y + CENTER_CIRCLE_RADIUS;
    public static final float TOP_CC_X = CENTER_FIELD_X;
    public static final float BOTTOM_CC_Y = CENTER_FIELD_Y - CENTER_CIRCLE_RADIUS;
    public static final float BOTTOM_CC_X = CENTER_FIELD_X;

    public static final float LINE_CROSS_LENGTH = 10.0f; // length of each cross in cm

    // Useful constants for initial localization
    public static final float MY_CC_NEAREST_POINT_X = CENTER_FIELD_X -
    CENTER_CIRCLE_RADIUS;
    public static final float MY_CROSS_CIRCLE_MIDPOINT_X = LANDMARK_MY_GOAL_CROSS_X +
        ((MY_CC_NEAREST_POINT_X-LANDMARK_MY_GOAL_CROSS_X)/2.0f);
    public static final float MY_GOALBOX_MIDPOINT_X = FIELD_WHITE_LEFT_SIDELINE_X +
        (GOALBOX_DEPTH / 2);
    public static final float MY_GOALBOX_CROSS_MIDPOINT_X = FIELD_WHITE_LEFT_SIDELINE_X +
        GOALBOX_DEPTH + ((LINE_CROSS_OFFSET - GOALBOX_DEPTH) / 2);
    
    // Constants for heading
    //  Right is towards opponents' goal.
    //  Left is towards own goal.
    public static final float HEADING_RIGHT = 0.0f;
    public static final float HEADING_UP = 90.0f;
    public static final float HEADING_LEFT = 180.0f;
    public static final float HEADING_DOWN = -90.0f;

    // Simulator constants

    // time between run calls
    public static final int RUN_SPEED = 100;

    // kick distances
    public static final int M_STRAIGHT_DIST = 70;
    public static final int M_CHIP_DIST = 70;
    public static final int M_SIDE_DIST = 100;

    // walk speeds
    public static final int WC_SPEED = 2;
    public static final float WC_SPEED_H = .1f;
    public static final float DEST_SPEED_H = .05f;

    public static final int KICK_SPEED = 10;

    // kick off positions
    public static final Location ODD_DEFENDER_HOME_L = new Location((MY_GOALBOX_RIGHT_X + 50),
                                                        MY_GOALBOX_BOTTOM_Y); 
    public static final Location EVEN_DEFENDER_HOME_L = new Location((MY_GOALBOX_RIGHT_X + 50),
                                                        MY_GOALBOX_TOP_Y);
    public static final Location ODD_CHASER_HOME_L = new Location((CENTER_FIELD_X - 45),
                                                        (OPP_GOALBOX_BOTTOM_Y - 100));
    public static final Location EVEN_CHASER_HOME_L = new Location((CENTER_FIELD_X - CENTER_CIRCLE_RADIUS - 20),
                                                        CENTER_FIELD_Y);
    public static final Location EVEN_CHASER_KICKOFF_L = new Location((CENTER_FIELD_X - 45),
                                                        CENTER_FIELD_Y);
    public static final Location ODD_DEFENDER_HOME_R = new Location(FIELD_WIDTH - (MY_GOALBOX_RIGHT_X + 50),
                                                        MY_GOALBOX_BOTTOM_Y); 
    public static final Location EVEN_DEFENDER_HOME_R = new Location(FIELD_WIDTH - (MY_GOALBOX_RIGHT_X + 50),
                                                        MY_GOALBOX_TOP_Y);
    public static final Location ODD_CHASER_HOME_R = new Location(FIELD_WIDTH - (CENTER_FIELD_X - 45),
                                                        (OPP_GOALBOX_BOTTOM_Y - 100));
    public static final Location EVEN_CHASER_HOME_R = new Location(FIELD_WIDTH - (CENTER_FIELD_X - CENTER_CIRCLE_RADIUS - 20),
                                                        CENTER_FIELD_Y);
    public static final Location EVEN_CHASER_KICKOFF_R = new Location(FIELD_WIDTH - (CENTER_FIELD_X - 45),
                                                        CENTER_FIELD_Y);

    public static void drawField(Graphics2D g2)
    {
        // green field
        g2.setColor(Color.green);
        g2.fillRect(0, 0, (int)FIELD_WIDTH, (int)FIELD_HEIGHT);

        // white lines
        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(LINE_WIDTH/2));
        
        // side lines
        g2.drawRect((int)FIELD_WHITE_LEFT_SIDELINE_X, 
                    (int)FIELD_WHITE_BOTTOM_SIDELINE_Y, 
                    (int)FIELD_WHITE_WIDTH, 
                    (int)FIELD_WHITE_HEIGHT);

        // mid-field
        g2.drawLine((int)MIDFIELD_X,
                    (int)FIELD_WHITE_BOTTOM_SIDELINE_Y,
                    (int)MIDFIELD_X,
                    (int)FIELD_WHITE_TOP_SIDELINE_Y);

        // center circle
        g2.drawOval((int)(MIDFIELD_X - CENTER_CIRCLE_RADIUS),
                    (int)(MIDFIELD_Y - CENTER_CIRCLE_RADIUS),
                    (int)CENTER_CIRCLE_RADIUS*2,
                    (int)CENTER_CIRCLE_RADIUS*2);

        // left goal
        g2.drawRect((int)MY_GOALBOX_LEFT_X, (int)MY_GOALBOX_BOTTOM_Y,
                    (int)GOALBOX_DEPTH, (int)GOALBOX_WIDTH);
        // right goal
        g2.drawRect((int)OPP_GOALBOX_LEFT_X, (int)OPP_GOALBOX_BOTTOM_Y,
                    (int)GOALBOX_DEPTH, (int)GOALBOX_WIDTH);

        // left field cross
        g2.drawLine((int)LANDMARK_MY_GOAL_CROSS_X,
                    (int)(LANDMARK_MY_GOAL_CROSS_Y - LINE_CROSS_LENGTH/2),
                    (int)LANDMARK_MY_GOAL_CROSS_X,
                    (int)(LANDMARK_MY_GOAL_CROSS_Y + LINE_CROSS_LENGTH/2));
        g2.drawLine((int)(LANDMARK_MY_GOAL_CROSS_X - LINE_CROSS_LENGTH/2),
                    (int)(LANDMARK_MY_GOAL_CROSS_Y),
                    (int)(LANDMARK_MY_GOAL_CROSS_X + LINE_CROSS_LENGTH/2),
                    (int)LANDMARK_MY_GOAL_CROSS_Y);   
        // right field cross
        g2.drawLine((int)LANDMARK_OPP_GOAL_CROSS_X,
                    (int)(LANDMARK_OPP_GOAL_CROSS_Y - LINE_CROSS_LENGTH/2),
                    (int)LANDMARK_OPP_GOAL_CROSS_X,
                    (int)(LANDMARK_OPP_GOAL_CROSS_Y + LINE_CROSS_LENGTH/2));
        g2.drawLine((int)(LANDMARK_OPP_GOAL_CROSS_X - LINE_CROSS_LENGTH/2),
                    (int)(LANDMARK_OPP_GOAL_CROSS_Y),
                    (int)(LANDMARK_OPP_GOAL_CROSS_X + LINE_CROSS_LENGTH/2),
                    (int)LANDMARK_OPP_GOAL_CROSS_Y);
    }
}