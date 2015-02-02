package behavior_sim;

import javax.swing.*;
import java.awt.*;
import java.util.Scanner;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class World extends JPanel implements MouseMotionListener 
{
    private static final int frameWidth = 1200;
    private static final int frameHeight = 900;
    private int numPlayers = 5;

    private FieldConstants field;
    private Ball ball;
    private Player[] p;

    public World()
    {
        addMouseMotionListener(this);

        field = new FieldConstants();
        ball = new Ball(new Location((int)field.MIDFIELD_X, (int)field.MIDFIELD_Y));

        p = new Player[numPlayers];
        p[0] = null;
        p[1] = new Player(field.EVEN_DEFENDER_KICKOFF);
        p[2] = new Player(field.ODD_DEFENDER_KICKOFF);
        p[3] = new Player(field.EVEN_CHASER_KICKOFF);
        p[4] = new Player(field.ODD_CHASER_KICKOFF);
    }

    public void drawWorld(Graphics2D g2)
    {
        field.drawField(g2);
        ball.drawBall(g2);
        for (int i = 0; i < numPlayers; i ++) 
            if (p[i] != null) p[i].drawPlayer(g2); 
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        drawWorld(g2);
    }

    @Override
    public void mouseDragged(MouseEvent e) 
    {
        ball.move(5,0);
        System.out.println(ball.getX());
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        
    }

    public static void createAndShowGUI() {
        JFrame mainFrame = new JFrame("Field View 1");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JComponent contentPane = new World();
        contentPane.setOpaque(true);

        mainFrame.getContentPane().add(contentPane);
        
        JScrollPane scrolling = new JScrollPane(contentPane);
        scrolling.getVerticalScrollBar().setUnitIncrement(12);
        scrolling.getHorizontalScrollBar().setUnitIncrement(12);
        mainFrame.getContentPane().add(scrolling);
        
        mainFrame.pack();
        mainFrame.setResizable(true);
        mainFrame.setSize(frameWidth, frameHeight);
        mainFrame.setVisible(true);
    }

    public static void main(String[] args) 
    {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
