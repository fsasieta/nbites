package behavior_sim;

// A Sim object that also tracks the heading, which is specific to players

import java.awt.Color;

public class Player extends SimObject
{
    private float h;
    private int num;

    public Player() { super(); }
    
    public Player(float xCoord, float yCoord)
    {
        super(xCoord, yCoord);
        h = 0.0f;
        num = 0;
    }

    public Player(float xCoord, float yCoord, float heading)
    {
        super(xCoord, yCoord);
        h = heading;
        num = 0;
    }

    public Player(Location location)
    {
        super(location);
        h = 0.0f;
        num = 0;
    }

    public Player(Location location, int n)
    {
        super(location);
        h = 0.0f;
        num = n;
    }

    public Player (Player player)
    {
        super(player);
        h = player.getH();
        num = player.getNum();
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
        h += relH;
        h %= (float)Math.PI;

        // 2D rotation
        float xDist = relX * (float)Math.cos(-h) - 
                        relY * (float)Math.sin(-h);

        float yDist = relY * (float)Math.cos(-h) + 
                        relX * (float)Math.sin(-h);

        super.move(xDist, yDist);
        this.notifyListeners();
    }

    // heading specific functions
    public void turn(float heading) { h += heading; }
    public void turnTo(float heading) { h = heading; }
    public float getH() { return h; }
    public float flipH() { return h + (float)Math.PI; }
    public float bearingTo(Location loc)
    {
        float yDist = loc.y - y;
        return h - (float)Math.asin(yDist/distanceTo(loc));
    }

    public int getNum() { return num; }
    public int team() { return num/FieldConstants.TEAM_SIZE;}

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