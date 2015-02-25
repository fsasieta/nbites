package behavior_sim;

// A Sim object that also tracks the heading, which is specific to players

import java.awt.Color;

public class Player extends SimObject
{
    private float h;

    public Player() { super(); }
    
    public Player(float xCoord, float yCoord)
    {
        super(xCoord, yCoord);
        h = 0.0f;
    }

    public Player(float xCoord, float yCoord, float heading)
    {
        super(xCoord, yCoord);
        h = heading;
    }

    public Player(Location location)
    {
        super(location);
        h = 0.0f;
    }

    public void setRadiusAndColor()
    {
        radius = 7.5f;
        color = Color.blue;
    }

    public void move(float xCoord, float yCoord, float heading)
    {
        super.move(xCoord, yCoord);
        h += heading;
        notifyListener();
    }

    public void moveTo(float xCoord, float yCoord, float heading)
    {
        super.moveTo(xCoord, yCoord);
        h = heading;
        notifyListener();
    }

    // heading specific functions
    public void turn(float heading) { h += heading; }
    public void turnTo(float heading) { h = heading; }
    public float getH() { return h; }
}