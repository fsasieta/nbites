package behavior_sim;

// A Sim object that also tracks the heading, which is specific to players

import java.awt.Color;
import java.awt.Graphics2D;

public class Player extends SimObject
{
    public float h;
    public int num;
    public boolean team;

    public Player() { super(); }
    
    public Player(float xCoord, float yCoord)
    {
        super(xCoord, yCoord);
        h = 0.0f;
        num = 0;
        team = true;
    }

    public Player(float xCoord, float yCoord, float heading)
    {
        super(xCoord, yCoord);
        h = heading;
        num = 0;
        team = true;
    }

    public Player(Location location)
    {
        super(location);
        h = 0.0f;
        num = 0;
        team = true;
    }

    public Player(Location location, int n, float heading)
    {
        super(location);
        h = heading;
        num = n;
        team = num/FieldConstants.TEAM_SIZE == 0;
    }

    public Player (Player player)
    {
        super(player);
        h = player.h;
        num = player.num;
        team = player.team;
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
        this.notifyListeners();
    }

    public void moveTo(float xCoord, float yCoord, float heading)
    {
        super.moveTo(xCoord, yCoord);
        h = heading;
        this.notifyListeners();
    }

    public void moveRel(float relX, float relY, float relH)
    {
        relX = (float)Math.abs(relX);
        h += relH;

        if (h < -Math.PI) h = (float)Math.PI*2 - h;
        else if (h > Math.PI) h = h - (float)Math.PI*2;

        // Location moveVect = new Location(relX, relY);
        // moveVect.rotate(h);

        // float xDist = moveVect.x;
        // float yDist = moveVect.y;
        float xDist = relX * (float)Math.cos(h) - 
                        relY * (float)Math.sin(h);

        float yDist = relY * (float)Math.cos(h) + 
                        relX * (float)Math.sin(h);

        super.move(xDist, yDist);
        this.notifyListeners();
    }

    // heading specific functions
    public void turn(float heading) { h += heading; }
    public void turnTo(float heading) { h = heading; }
    public float getH() { return h; }
    public float flipH() 
    { 
        if (h <= 0) h += 2*(-(float)Math.PI/2 - h);
        else h += 2*((float)Math.PI/2 - h);
        return h;
    }
    public float bearingTo(Location loc)
    {
        float xDist = loc.x - x;
        float yDist = loc.y - y;
        float bearing = (float)Math.atan(yDist/xDist) - h;

        if (xDist < 0)
        {
            if (yDist > 0) bearing += Math.PI;
            else bearing -= Math.PI;
        }

        System.out.println("Heading: " + h);
        System.out.println("Bearing: " + bearing);
        return bearing;
    }

    public int getNum() { return num; }
    public int team() { return num/FieldConstants.TEAM_SIZE;}

    public void draw(Graphics2D g2)
    {
        super.draw(g2);

        g2.setColor(Color.black);

        int xDist = (int)(20 * Math.cos(h) + x);
        int yDist = (int)(20 * Math.sin(h) + y);     

        g2.drawLine((int)x, (int)y, xDist, yDist); 
    }

    protected void notifyListeners()
    {
        if (listeners.isEmpty()) return;
        super.notifyListeners();
        if (num / FieldConstants.TEAM_SIZE >= 1)
        {
            listeners.get(1).setText((String.valueOf((int)this.flipX()) + 
                                            ", " + String.valueOf((int)y))); 
        }
        else
        {
            listeners.get(1).setText((String.valueOf((int)x) + 
                                            ", " + String.valueOf((int)flipY())));
        }
    }
}