package behavior_sim;

public class Ball
{
    private float x, y;
    
    public Ball()
    {
        x = 0.0f;
        y = 0.0f;
    }
    
    public Ball(float xCoord, float yCoord)
    {
        x = xCoord;
        y = yCoord;
    }

    public Ball(Location location)
    {
        x = location.x;
        y = location.y;
    }   

    public void move(float xCoord, float yCoord)
    {
        x = xCoord;
        y = yCoord;
    }

    public void move(Location location)
    {
        x = location.x;
        y = location.y;
    }

    public float getX() { return x; }
    public float getY() { return y; }
    
    public Location getLocation() { return new Location(x , y); }
}