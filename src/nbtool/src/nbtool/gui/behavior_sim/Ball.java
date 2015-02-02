package behavior_sim;

import java.awt.Color;
import java.awt.Graphics2D;

public class Ball
{
    private int x, y;
    private int radius = 5;
    
    public Ball()
    {
        x = 0;
        y = 0;
    }
    
    public Ball(int xCoord, int yCoord)
    {
        x = xCoord;
        y = yCoord;
    }

    public Ball(Location location)
    {
        x = location.x;
        y = location.y;
    }   

    public void move(int xCoord, int yCoord)
    {
        x += xCoord;
        y += yCoord;
    }

    public void move(Location location)
    {
        x += location.x;
        y += location.y;
    }

    public void moveTo(int xCoord, int yCoord)
    {
        x = xCoord;
        y = yCoord;
    }

    public void moveTo(Location location)
    {
        x = location.x;
        y = location.y;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    
    public Location getLocation() { return new Location(x , y); }

    public void drawBall(Graphics2D g2)
    {
        g2.setColor(Color.red);
        g2.fillOval(x-radius, y-radius, 2*radius, 2*radius);
    }
}