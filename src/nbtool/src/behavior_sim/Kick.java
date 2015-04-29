package behavior_sim;

public class Kick
{
    public float dist;
    public Location moveVect;
    public int kicker;
    public int time;


    public Kick()
    {
        dist = -1;
        kicker = -1;
        time = -1;
        moveVect = new Location(-1, -1);
    }

    public Kick(float h, int dir, float d, int k, int t, int speed)
    {
        dist = d;
        kicker = k;
        time = t;

        switch(dir)
        {
            case -1:
                h -= Math.PI/2;
                if (h > Math.PI) h = h - (float)Math.PI*2;
                break;

            case 1:
                h += Math.PI/2;
                if (h < -Math.PI) h = (float)Math.PI*2 - h;
                break;

            case -2:
                h -= Math.PI/4;
                if (h > Math.PI) h = h - (float)Math.PI*2;
                break;

            case 2:
                h += Math.PI/4;
                if (h < -Math.PI) h = (float)Math.PI*2 - h;
                break;
        }

        float xDist = (float)Math.cos(-h);
        float yDist = (float)Math.sin(-h);
        float mag = (float)Math.pow(xDist*xDist + yDist*yDist, 0.5);

        moveVect = new Location(speed*xDist/mag, speed*yDist/mag);
    }
}