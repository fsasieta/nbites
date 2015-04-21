package behavior_sim;

/*
    This is where the state of the world is held. The field and all objects/players are
    created here. The world can be updated via mouse commands.
*/

import javax.swing.JPanel;

import java.awt.Dimension;

import java.awt.Graphics;
import java.awt.Graphics2D;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;

public class World extends JPanel implements MouseMotionListener, MouseListener 
{
    public int time;   // track how much time is left
    public int half;   // which half is it
    public int state;   // game state
    public int kickOffTeam;     // who is kicking off

    private int playerMove;   // tracks which player should move on mouse drag
    private boolean ballMove;       // whether or not the ball should be moved on mouse drag

    public Ball ball;              // the ball
    public Player[] players;    // all of the players
    private Location[] leftKickoff;    // left team kickoff positions
    private Location[] rightKickoff;    // right team kickoff positions
    private Post[] posts;           // the goal posts
    private Kick kick;

    private BehaviorInterpreter bIntp;  // the interpreter

    public World()
    {
        // Mouse listeners
        addMouseMotionListener(this);
        addMouseListener(this);

        ball = new Ball(FieldConstants.MIDFIELD_X, FieldConstants.MIDFIELD_Y);
        players = new Player[FieldConstants.NUM_PLAYERS];

        // the start positions for the left team kickoff
        leftKickoff =  new Location[2*FieldConstants.TEAM_SIZE];
        for (int i = 0; i < leftKickoff.length; i++)
        {
            leftKickoff[i] = Enums.KickOffPositions.values()[i].loc;
        }

        // start positions for the right team kickoff
        rightKickoff = new Location[2*FieldConstants.TEAM_SIZE];
        // copy leftKickoff to rightKickoff
        System.arraycopy(leftKickoff, 0, rightKickoff, 0, 2*FieldConstants.TEAM_SIZE);
        // switch who takes the kickoff
        rightKickoff[2] = FieldConstants.EVEN_CHASER_HOME_L;
        rightKickoff[6] = FieldConstants.EVEN_CHASER_KICKOFF_R;

        // the posts postitions from field constants
        posts = new Post[FieldConstants.NUM_POSTS];
        for (int i = 0; i < posts.length; i++)
        {
                posts[i] = new Post(Enums.PostLocations.values()[i].loc);
        }

        ballMove = false;
        playerMove = -1;
        kick = new Kick();

        time = FieldConstants.TIME_PER_HALF;
        half = 1;
        state = Enums.States.valueOf("STATE_PLAYING").state;    // todo
        kickOffTeam = Enums.Teams.valueOf("MY_TEAM").team;

        bIntp = new BehaviorInterpreter();

        // for the GUI
        setPreferredSize(new Dimension((int)FieldConstants.FIELD_WIDTH,
                                        (int)FieldConstants.FIELD_HEIGHT));
    }

    // draws the state of the field
    public void drawWorld(Graphics2D g2)
    {
        FieldConstants.drawField(g2);        // draw the field
        ball.draw(g2);              // draw the ball
        for (int i = 0; i < FieldConstants.NUM_PLAYERS; i ++) if (players[i] != null) players[i].draw(g2);  // draw the players
        for (int i = 0; i < FieldConstants.NUM_POSTS; i++) posts[i].draw(g2);       // draw the posts

        // Check if there was a goal. Done here because every field update
        // calls this function
        goal();
        kick();
    }

    // paints the world to the World JPanel
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        this.drawWorld(g2);
    }

    // The given array determines which players will be on the field.
    // It is important that the team array indexed the same as the leftKickoff array
    public void buildTeam(boolean[] team)
    {
        this.reset();  // clear the field
        // match the team array to start positions
        for (int i = 0; i < team.length; i++)
        {
            if(team[i]) 
                {
                    if (i/FieldConstants.TEAM_SIZE == 0)
                        players[i] = new Player(leftKickoff[i], i, 0.0f);
                    else
                        players[i] = new Player(leftKickoff[i], i, (float)Math.PI);
                }
        }
        this.repaint();
    }

    // clear the players and move the ball to center
    public void reset()
    {
        time = FieldConstants.TIME_PER_HALF;
        ball.goHome();
        players = new Player[FieldConstants.NUM_PLAYERS];
        this.repaint();
    }

    // if there was a goal, return to initial positions
    public void goal()
    {
        if (ball.x <= FieldConstants.MY_GOALBOX_LEFT_X &&
            ball.y >= FieldConstants.MY_GOALBOX_BOTTOM_Y &&
            ball.y <= FieldConstants.MY_GOALBOX_TOP_Y)
        {
            kickOffTeam = Enums.Teams.valueOf("MY_TEAM").team;

            // take drag control of objects away
            playerMove = -1;
            ballMove = false;

            // reset the field
            ball.goHome();
            for (int i = 0; i < FieldConstants.NUM_PLAYERS; i++) 
                if (players[i] != null) players[i].moveTo(leftKickoff[i]);
        }
        else if (ball.x >= FieldConstants.OPP_GOALBOX_RIGHT_X &&
                ball.y >= FieldConstants.OPP_GOALBOX_BOTTOM_Y &&
                ball.y <= FieldConstants.OPP_GOALBOX_TOP_Y)
        {
            kickOffTeam = Enums.Teams.valueOf("OPP_TEAM").team; 

            // take drag control of objects away
            playerMove = -1;
            ballMove = false;

            // reset the field
            ball.goHome();
            for (int i = 0; i < FieldConstants.NUM_PLAYERS; i++) 
                if (players[i] != null) players[i].moveTo(rightKickoff[i]);
        }          
    }

    public void kick()
    {
        if (kick.dist <= 0) return;

        ball.move(kick.moveVect.x, kick.moveVect.y);

        kick.dist -= 2;
    }

    public void kick(int pIndex, float h, int dir, float dist)
    {
        if (kick.kicker == pIndex && (kick.time - time) < 6) return;

        kick = new Kick(h, dir, dist, pIndex, time);
    }

    // run the interpreter
    public void startSim() { bIntp.initAndRun(this); }
    //ends the interpreter
    public void endSim() { bIntp.end(); }
    // resume paused sim
    public void resumeSim() { bIntp.run(); }


    // find out if the ball or any player has been pressed
    @Override
    public void mousePressed(MouseEvent e)
    {
        if (ball.contains(e.getX(), e.getY())) ballMove = true;
        for (int i = 0; i < FieldConstants.NUM_PLAYERS; i++)
        {
            if (players[i] != null && players[i].contains(e.getX(), e.getY()))
            {
                playerMove = i;
                break;
            }
        }
    }

    // reset movement variables
    @Override
    public void mouseReleased(MouseEvent e)
    {
        ballMove = false;
        playerMove = -1;
    }

    // move the appropriate objects that are being dragged
    @Override
    public void mouseDragged(MouseEvent e) 
    {
        if (ballMove) ball.moveTo(e.getX(), e.getY());
        
        if (playerMove != -1) players[playerMove].moveTo(e.getX(), e.getY());

        // check if the ball is colliding with an object. if so, move it
        // on the outward normal to that object

        // for (int i = 0; i < FieldConstants.NUM_PLAYERS; i++)
        // {
        //     if (players[i] != null && players[i].contains(ball.getLocation()))
        //     {
        //         float xDist = ball.x - players[i].x;
        //         float yDist = ball.y - players[i].y;
        //         ball.move(xDist, yDist);
        //     }
        // }
        // for (int i = 0; i < FieldConstants.NUM_POSTS; i++)
        // {
        //     if (posts[i].contains(ball.getLocation()))
        //     {
        //         float xDist = ball.x - posts[i].x;
        //         float yDist = ball.y - posts[i].y;
        //         ball.move(xDist, yDist);
        //         break;
        //     }
        // }
        this.repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {} 
    @Override
    public void mouseMoved(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}   
}