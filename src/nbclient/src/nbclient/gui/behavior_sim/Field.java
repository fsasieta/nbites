package behavior_sim;

public class Field
{
    private float width, height;

    public Field()
    {
       width = 0.0f;
        height = 0.0f;   
    }

    public Field(float w, float h)
    {
        width = w;
        height = h;
    }

    public float getHeight() { return height; }
    public float getWidth() { return width; }

    public Location getDimentions() { return new Location(width, height); }

    public Location getCenter() { return new Location(width/2, height/2); }
}