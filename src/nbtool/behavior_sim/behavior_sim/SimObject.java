package behavior_sim;
/*
    Abstract class that all field objects (ball, players, posts) inherit from.
    The objects can be moved, tell you if they contain a point and be drawn as
    a filled oval. In order to set an objects color and radius (for drawing)
    override setRadiusAndObject() in sub-classes. Home is the place it was initialized
    so that it can be easily sent back to its home.
*/
import java.awt.Color;
import java.awt.Graphics2D;

public abstract class SimObject
{
    protected float x, y, radius;
    protected Location home;
    protected Color color;

    public SimObject()
    {
        x = 0.0f;
        y = 0.0f;
        home = new Location(x,y);
        setRadiusAndColor();
    }
    
    public SimObject(float xCoord, float yCoord)
    {
        x = xCoord;
        y = yCoord;
        home = new Location(x,y);
        setRadiusAndColor();
    }

    public SimObject(Location location)
    {
        x = location.x;
        y = location.y;
        home = new Location(x,y);
        setRadiusAndColor();
    } 

    // Probably want to override this in sub-classes
    protected void setRadiusAndColor()
    {
        radius = 10.0f;
        color = Color.white;
    } 

    // move by given increments
    public void move(float xCoord, float yCoord)
    {
        x += xCoord;
        y += yCoord;
    }

    // move to the given coordinates
    public void moveTo(float xCoord, float yCoord)
    {
        x = xCoord;
        y = yCoord;
    }

    // move to the given location
    public void moveTo(Location location)
    {
        x = location.x;
        y = location.y;
    }

    // send the object to the place it was initialized
    public void goHome() { moveTo(home); }

    // getters
    public float getX() { return x; }
    public float getY() { return y; }
    public Location getLocation() { return new Location(x, y); }

    // does the object contain the coordinates?
    public boolean contains(float xCoord, float yCoord)
    {
        return (xCoord >= (x-radius) && xCoord <= (x+radius) &&
                yCoord >= (y-radius) && yCoord <= (y+radius));
    }

    // does the object contain the location?
    public boolean contains(Location loc)
    {
        float xCoord = loc.x;
        float yCoord = loc.y;
        return (xCoord >= (x-radius) && xCoord <= (x+radius) &&
                yCoord >= (y-radius) && yCoord <= (y+radius));
    }


    // draw the object
    public void draw(Graphics2D g2)
    {
        g2.setColor(color);
        g2.fillOval((int)(x-radius), (int)(y-radius), (int)(2*radius), (int)(2*radius));
    }
}