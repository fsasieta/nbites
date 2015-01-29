package behavior_sim;

public class Player
{
    private float x, y, h;

    public Player()
    {
        x = 0.0f;
        y = 0.0f;
        h = 0.0f;
    }

    public Player(float xCoord, float yCoord)
    {
        x = xCoord;
        y = yCoord;
        h = 0.0f;
    }

    public Player(float xCoord, float yCoord, float heading)
    {
        x = xCoord;
        y = yCoord;
        h = heading;
    }

    public Player(Location location)
    {
        x = location.x;
        y = location.y;
        h = 0.0f;
    }

    public void move(float xCoord, float yCoord)
    {
        x = xCoord;
        y = yCoord;
    }

    public void move(float xCoord, float yCoord, float heading)
    {
        x = xCoord;
        y = yCoord;
        h = heading;
    }

    public void turn(float heading)
    {
        h = heading;
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getH() { return h; }

    public Location getLocation() { return new Location(x, y); }
}