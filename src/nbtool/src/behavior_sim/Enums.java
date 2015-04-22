package behavior_sim;

public class Enums
{
    public static enum KickOffPositions
    {
        EDHL (FieldConstants.EVEN_DEFENDER_HOME_L),
        ODHL (FieldConstants.ODD_DEFENDER_HOME_L),
        ECKL (FieldConstants.EVEN_CHASER_KICKOFF_L),
        OCHL (FieldConstants.ODD_CHASER_HOME_L),
        EDHR (FieldConstants.EVEN_DEFENDER_HOME_L),
        ODHR (FieldConstants.ODD_DEFENDER_HOME_L),
        ECHR (FieldConstants.EVEN_CHASER_HOME_L),
        OCHR (FieldConstants.ODD_CHASER_HOME_L);
        
        public Location loc;

        KickOffPositions(final Location loc) { this.loc = loc; }
    }

    public static enum PostLocations
    {
        LB (new Location(FieldConstants.MY_GOALBOX_LEFT_X, 
                            FieldConstants.MY_GOALBOX_BOTTOM_Y)),
        LT (new Location(FieldConstants.MY_GOALBOX_LEFT_X, 
                            FieldConstants.MY_GOALBOX_TOP_Y)),
        RB (new Location(FieldConstants.OPP_GOALBOX_RIGHT_X, 
                            FieldConstants.OPP_GOALBOX_BOTTOM_Y)),
        RT (new Location(FieldConstants.OPP_GOALBOX_RIGHT_X, 
                            FieldConstants.OPP_GOALBOX_TOP_Y));
        
        public Location loc;

        PostLocations(final Location loc) { this.loc = loc; }
    }

    public static enum States
    {
        STATE_INITIAL(0),
        STATE_READY(1),
        STATE_SET(2),
        STATE_PLAYING(3),
        STATE_FINISHED(4);

        public int state;

        States(final int state) { this.state = state; }
    }

    public static enum Teams
    {
        MY_TEAM(0),
        OPP_TEAM(1);

        public int team;

        Teams(final int team) { this.team = team; }

        public static int swap(int otherTeam) 
        {
            if (otherTeam == 0) return 1;
            else return 0;
        }
    }

    public static enum States2
    {
        NORMAL(0),
        PENALTYSHOOT(1),
        OVERTIME(2);

        public int state;

        States2(final int state) { this.state = state; }
    }
}