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

import java.util.ArrayList;

public class World extends JPanel implements MouseMotionListener, MouseListener 
{
    private int numPlayers = 0;     // number of players on the field
    private final int numPosts = 4; // number of goal posts

    private int playerMove;   // tracks which player should move on mouse drag
    private boolean ballMove;       // whether or not the ball should be moved on mouse drag

    public FieldConstants field;    // the field
    public Ball ball;              // the ball

    public ArrayList<Player> p;    // all of the players
    private ArrayList<Location> startPos;    // the start postitions of the players
    private Post[] posts;           // the goal posts

    public World()
    {
        // Mouse listeners
        addMouseMotionListener(this);
        addMouseListener(this);

        field = new FieldConstants();
        ball = new Ball(field.MIDFIELD_X, field.MIDFIELD_Y);

        p = new ArrayList<Player>();
        // the start positions from field constants
        startPos =  new ArrayList<Location>();
        startPos.add(field.EVEN_DEFENDER_KICKOFF_L);
        startPos.add(field.ODD_DEFENDER_KICKOFF_L);
        startPos.add(field.EVEN_CHASER_KICKOFF_L);
        startPos.add(field.ODD_CHASER_KICKOFF_L);
        startPos.add(field.EVEN_DEFENDER_KICKOFF_R);
        startPos.add(field.ODD_DEFENDER_KICKOFF_R);
        startPos.add(field.EVEN_CHASER_KICKOFF_R);
        startPos.add(field.ODD_CHASER_KICKOFF_R);

        // the posts postitions from field constants
        posts = new Post[numPosts];
        posts[0] = new Post(field.MY_GOALBOX_LEFT_X, 
                            field.MY_GOALBOX_BOTTOM_Y);
        posts[1] = new Post(field.MY_GOALBOX_LEFT_X, 
                            field.MY_GOALBOX_TOP_Y);
        posts[2] = new Post(field.OPP_GOALBOX_RIGHT_X, 
                            field.OPP_GOALBOX_BOTTOM_Y);
        posts[3] = new Post(field.OPP_GOALBOX_RIGHT_X, 
                            field.OPP_GOALBOX_TOP_Y);

        ballMove = false;
        playerMove = -1;

        // for the GUI
        setPreferredSize(new Dimension((int)field.FIELD_WIDTH,
                                        (int)field.FIELD_HEIGHT));
    }

    // draws the state of the field
    public void drawWorld(Graphics2D g2)
    {
        field.drawField(g2);        // draw the field
        ball.draw(g2);              // draw the ball
        for (int i = 0; i < numPlayers; i ++) p.get(i).draw(g2);    // draw the players
        for (int i = 0; i < numPosts; i++) posts[i].draw(g2);       // draw the posts

        // Check if there was a goal. Done here because every field update
        // calls this function
        goal();
    }

    // paints the world to the World JPanel
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        drawWorld(g2);
    }

    // The given array determines which players will be on the field.
    // It is important that the team array indexed the same as the startPos array
    public void buildTeam(boolean[] team)
    {
        reset();  // clear the field
        // match the team array to start positions
        for (int i = 0; i < team.length; i++)
        {
            if(team[i])
            {
                p.add(new Player(startPos.get(i)));
                numPlayers++;
            }
        }
        repaint();
    }

    // clear the players and move the ball to center
    public void reset()
    {
        ball.moveTo(field.MIDFIELD_X, field.MIDFIELD_Y);
        p.clear();
        numPlayers = 0;
        repaint();
    }

    // if there was a goal, return to initial positions
    public void goal()
    {
        if ((ball.getX() <= field.MY_GOALBOX_LEFT_X &&
            ball.getY() >= field.MY_GOALBOX_BOTTOM_Y &&
            ball.getY() <= field.MY_GOALBOX_TOP_Y) ||
            (ball.getX() >= field.OPP_GOALBOX_RIGHT_X &&
            ball.getY() >= field.OPP_GOALBOX_BOTTOM_Y &&
            ball.getY() <= field.OPP_GOALBOX_TOP_Y))
        {
            // take drag control of objects away
            playerMove = -1;
            ballMove = false;

            // reset the field
            ball.goHome();
            for (int i = 0; i < p.size(); i++) p.get(i).goHome();
        }          
    }

    // find out if the ball or any player has been pressed
    @Override
    public void mousePressed(MouseEvent e)
    {
        if (ball.contains(e.getX(), e.getY())) ballMove = true;
        for (int i = 0; i < numPlayers; i++)
        {
            if (p.get(i) != null && p.get(i).contains(e.getX(), e.getY()))
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
        
        if (playerMove != -1) p.get(playerMove).moveTo(e.getX(), e.getY());

        // check if the ball is colliding with an object. if so, move it
        // on the outward normal to that object

        for (int i = 0; i < numPlayers; i++)
        {
            if (p.get(i).contains(ball.getLocation()))
            {
                float xDist = ball.getX() - p.get(i).getX();
                float yDist = ball.getY() - p.get(i).getY();
                ball.move(xDist, yDist);
            }
        }
        for (int i = 0; i < numPosts; i++)
        {
            if (posts[i].contains(ball.getLocation()))
            {
                float xDist = ball.getX() - posts[i].getX();
                float yDist = ball.getY() - posts[i].getY();
                ball.move(xDist, yDist);
                break;
            }
        }
        repaint();
    }

    // create a new player on double clicks
    @Override
    public void mouseClicked(MouseEvent e)
    {
        if (e.getClickCount() == 2)
        {
            numPlayers++;
            p.add(new Player(e.getX(), e.getY()));
            repaint();
        }
    } 

    @Override
    public void mouseMoved(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e){}
    @Override
    public void mouseEntered(MouseEvent e){}   
}
