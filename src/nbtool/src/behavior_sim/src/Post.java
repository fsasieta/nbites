package nbtool.behavior_sim.src;

// a very basic SimObject

import java.awt.Color;

public class Post extends SimObject
{
    public Post() { super(); }
    public Post(float xCoord, float yCoord) { super(xCoord, yCoord); }
    public Post(Location location) { super(location); }

    protected void setRadiusAndColor()
    {
        color = new Color(230, 200, 25);   // dark yellow
        radius = 5.0f;
    }
}