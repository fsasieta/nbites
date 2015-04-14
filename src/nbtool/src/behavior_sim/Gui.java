package behavior_sim;

/*
    Here is where all of the gui and dispalys happen. 
*/

import javax.swing.*;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.util.ArrayList;

public class Gui extends JFrame
{
    private static final int frameWidth = 1200;
    private static final int frameHeight = 700;

    private static World fieldPanel;
    private static JTabbedPane tabs;

    private static Timer timer;

    public static void createAndShowGUI() {
        // this frame holds everything else
        JFrame mainFrame = new Gui();
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container cP = mainFrame.getContentPane();
        
        // make the field/world state a scrollable panel
        fieldPanel = new World();
        JScrollPane scrollField = new JScrollPane(fieldPanel, 
                                            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
                                            JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        // Control and info tabs
        tabs = new JTabbedPane();
        tabs.addTab("Controls", controlPanel());
        tabs.addTab("Ball Info", ballInfo());

        // Game timer
        final JLabel time = new JLabel("10:00");
        time.setFont(new Font(Font.DIALOG, Font.BOLD, 20));
        timer = makeTimer(time);

        // layout non-sense. simply keeps things where they should be
        SpringLayout layout = new SpringLayout();
        cP.setLayout(layout);
        layout.putConstraint(SpringLayout.WEST, tabs, 10, SpringLayout.EAST, scrollField);
        layout.putConstraint(SpringLayout.NORTH, scrollField, 10, SpringLayout.NORTH, cP);
        layout.putConstraint(SpringLayout.WEST, scrollField, 10, SpringLayout.WEST, cP);
        layout.putConstraint(SpringLayout.NORTH, time, 5, SpringLayout.SOUTH, scrollField);
        layout.putConstraint(SpringLayout.NORTH, tabs, 10, SpringLayout.NORTH, cP);
        layout.putConstraint(SpringLayout.EAST, cP, 5, SpringLayout.EAST, tabs);
        layout.putConstraint(SpringLayout.SOUTH, cP, 5, SpringLayout.SOUTH, time);
        layout.putConstraint(SpringLayout.WEST, time, 500, SpringLayout.WEST, cP);

        // add the field and controls to the frame
        cP.add(scrollField);
        cP.add(tabs);
        cP.add(time);
        
        mainFrame.pack();
        mainFrame.setResizable(true);
        mainFrame.setSize(frameWidth, frameHeight);
        mainFrame.setVisible(true);
    }

    // add all of the user interface controls
    public static JPanel controlPanel()
    {
        JPanel controls = new JPanel();
        controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));
        // left team
        final JLabel team1 = new JLabel("Left Team");
        controls.add(team1);

        // these check boxes decide what players will be on the field for the left team
        final ArrayList<JCheckBox> cb1 = new ArrayList<JCheckBox>();
        cb1.add(new JCheckBox("Even Defender"));
        cb1.add(new JCheckBox("Odd Defender"));
        cb1.add(new JCheckBox("Even Chaser"));
        cb1.add(new JCheckBox("Odd Chaser"));

        for (int i = 0; i < cb1.size(); i++) controls.add(cb1.get(i));

        // right team
        final JLabel team2 = new JLabel("Right Team");
        controls.add(team2);

        // these check boxes decide what players will be on the field for the right team
        final ArrayList<JCheckBox> cb2 = new ArrayList<JCheckBox>();
        cb2.add(new JCheckBox("Even Defender"));
        cb2.add(new JCheckBox("Odd Defender"));
        cb2.add(new JCheckBox("Even Chaser"));
        cb2.add(new JCheckBox("Odd Chaser"));

        for (int i = 0; i < cb2.size(); i++) controls.add(cb2.get(i));

        // this button sends the information to the World about what players
        // should be on the field. Reset set field by unchecking everything
        final JButton setField = new JButton("Set Field");
        setField.addActionListener(new ActionListener() { 
        @Override
        public void actionPerformed(ActionEvent e)
            {
                boolean[] team = new boolean[cb1.size() + cb2.size()];

                // reset the info tabs
                tabs.removeAll();
                tabs.addTab("Controls", controlPanel());
                tabs.addTab("Ball Info", ballInfo());

                for (int i = 0; i < cb1.size(); i++)
                {
                    if (cb1.get(i).isSelected()) team[i] = true;
                    if (cb2.get(i).isSelected()) team[i + cb1.size()] = true;
                    else if (!cb1.get(i).isSelected())
                    {
                        team[i] = false;
                        team[i + cb1.size()] = false;
                    }
                }
                
                fieldPanel.buildTeam(team);

                // make info tabs for the players on the field
                for (int i = 0; i < team.length; i++)
                {
                    if (team[i])
                    {
                        tabs.addTab("Player " + String.valueOf(i), 
                                    playerInfo(i));
                    }
                }
            }
        } );

        // starts the simulation
        final JButton startSim = new JButton("Start Sim");
        startSim.addActionListener(new ActionListener() { 
            public void actionPerformed(ActionEvent e) { 
                fieldPanel.startSim(); 
                timer.start();
            } 
        } );

        // ends the simulation
        final JButton endSim = new JButton("End Sim");
        endSim.addActionListener(new ActionListener() { 
            public void actionPerformed(ActionEvent e) { 
                fieldPanel.endSim();
                timer.stop(); 
            } 
        } );
        
        // ends the simulation
        final JButton resumeSim = new JButton("Resume Sim");
        resumeSim.addActionListener(new ActionListener() { 
            public void actionPerformed(ActionEvent e) { 
                fieldPanel.resumeSim();
                timer.start(); 
            } 
        } );

        controls.add(setField);
        controls.add(startSim);
        controls.add(endSim);
        controls.add(resumeSim);

        return controls; 
    }

    // Ball information panel
    public static JPanel ballInfo()
    {
        JPanel ballInfo = new JPanel();
        ballInfo.setLayout(new BoxLayout(ballInfo, BoxLayout.Y_AXIS));

        JLabel position = new JLabel("Ball Location (x, y)");

        JTextField bLoc = new JTextField();
        fieldPanel.ball.registerListener(bLoc); // gets updates from the ball
        //bLoc.setText("Set for kickoff");

        ballInfo.add(position);
        ballInfo.add(bLoc);

        return ballInfo;
    }

    // player inforrmation panel
    public static JPanel playerInfo(int num)
    {
        JPanel playerInfo = new JPanel();
        playerInfo.setLayout(new BoxLayout(playerInfo, BoxLayout.Y_AXIS));

        JLabel gCoords = new JLabel("GUI Coords");
        JTextField gLoc = new JTextField();
        fieldPanel.players[num].registerListener(gLoc);
        gLoc.setText("Set for kick off");

        JLabel rCoords = new JLabel("Player's Reference Frame Coords");
        JTextField rLoc = new JTextField();
        fieldPanel.players[num].registerListener(rLoc);
        rLoc.setText("Set for kick off");

        JButton flip = new JButton("Flip");

        playerInfo.add(gCoords);
        playerInfo.add(gLoc);
        playerInfo.add(rCoords);
        playerInfo.add(rLoc);
        playerInfo.add(flip);

        return playerInfo;
    }

    public static Timer makeTimer(final JLabel time)
    {
        return new Timer(1000, new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                fieldPanel.time--; 
                int minutes = fieldPanel.time/60;
                int seconds = fieldPanel.time % 60;
                time.setText(minutes + ":" + String.format("%02d", seconds));       
            }
        });
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