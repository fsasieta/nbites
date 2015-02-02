package behavior_sim;

import java.awt.Color;
import java.awt.Graphics2D;

public class Player
{
    private int x, y, radius;
    private float h;

    public Player()
    {
        x = 0;
        y = 0;
        h = 0.0f;
        radius = 10;
    }

    public Player(int xCoord, int yCoord)
    {
        x = xCoord;
        y = yCoord;
        h = 0.0f;
        radius = 10;
    }

    public Player(int xCoord, int yCoord, float heading)
    {
        x = xCoord;
        y = yCoord;
        h = heading;
        radius = 10;
    }

    public Player(Location location)
    {
        x = location.x;
        y = location.y;
        h = 0.0f;
        radius = 10;
    }

    public void move(int xCoord, int yCoord)
    {
        x += xCoord;
        y += yCoord;
    }

    public void move(int xCoord, int yCoord, float heading)
    {
        x += xCoord;
        y += yCoord;
        h += heading;
    }

    public void moveTo(int xCoord, int yCoord)
    {
        x = xCoord;
        y = yCoord;
    }

    public void moveTo(int xCoord, int yCoord, float heading)
    {
        x = xCoord;
        y = yCoord;
        h = heading;
    }

    public void turn(float heading) { h += heading; }
    public void turnTo(float heading) { h = heading; }

    public int getX() { return x; }
    public int getY() { return y; }
    public float getH() { return h; }

    public Location getLocation() { return new Location(x, y); }

    public void drawPlayer(Graphics2D g2)
    {
        g2.setColor(Color.blue);
        g2.fillOval(x-radius, y-radius, 2*radius, 2*radius);
    }
}