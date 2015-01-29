package behavior_sim;

public class World
{
    public static void main(String[] args)
    {
        try
        {
            World world = new World();
            world.run();
        }
        catch (Exception e)
        {
            e.printStackTrace ();
        }
    }

    public void run () throws Exception
    {
        Constants consts = new Constants();

        Field field = new Field(consts.FIELD_WHITE_WIDTH,
                                consts.FIELD_WHITE_HEIGHT);

        Ball ball = new Ball(field.getCenter());

        Player p2 = new Player(consts.EVEN_DEFENDER_KICKOFF),
               p3 = new Player(consts.ODD_DEFENDER_KICKOFF),
               p4 = new Player(consts.EVEN_CHASER_KICKOFF),
               p5 = new Player(consts.ODD_CHASER_KICKOFF);

        System.out.println("The ball coordinates are (" + ball.getX() + ", " + 
                            ball.getY() + ")");
        System.out.println("Player 2 is at (" + p2.getX()  + ", " +
                            p2.getY() + ")");
        System.out.println("Player 3 is at (" + p3.getX()  + ", " +
                            p3.getY() + ")");
        System.out.println("Player 4 is at (" + p4.getX()  + ", " +
                            p4.getY() + ")");
        System.out.println("Player 5 is at (" + p5.getX()  + ", " +
                            p5.getY() + ")");
    }    
}
