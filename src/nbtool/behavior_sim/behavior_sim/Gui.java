package behavior_sim;

/*
    Here is where all of the gui and dispalys happen. 
*/

import javax.swing.*;

import java.awt.Container;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.util.ArrayList;

public class Gui extends JFrame
{
    private static final int frameWidth = 1200;
    private static final int frameHeight = 800;

    private static World fieldPanel;
    private static JTabbedPane tabs;

    public static void createAndShowGUI() {
        // this frame holds everything else
        JFrame mainFrame = new Gui();
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container cP = mainFrame.getContentPane();

        // the field/world state
        fieldPanel = new World();

        tabs = new JTabbedPane();
        tabs.addTab("Controls", controlPanel());
        tabs.addTab("Ball Info", ballInfo());

        // layout non-sense. simply keeps things where they should be
        SpringLayout layout = new SpringLayout();
        cP.setLayout(layout);
        layout.putConstraint(SpringLayout.WEST, tabs, 10, SpringLayout.EAST, fieldPanel);
        layout.putConstraint(SpringLayout.NORTH, fieldPanel, 10, SpringLayout.NORTH, cP);
        layout.putConstraint(SpringLayout.WEST, fieldPanel, 10, SpringLayout.WEST, cP);
        layout.putConstraint(SpringLayout.NORTH, tabs, 10, SpringLayout.NORTH, cP);

        // add the field and controls to the frame
        cP.add(fieldPanel);
        cP.add(tabs);
        
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
        setField.addActionListener(new ActionListener(){ 
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
                int index = 0;
                for (int i = 0; i < team.length; i++)
                {
                    if (team[i])
                    {
                        tabs.addTab("Player " + String.valueOf(i), playerInfo(index));
                        index++;
                    }
                }
            }
        });
        controls.add(setField);

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
        bLoc.setText("Set for kickoff");

        ballInfo.add(position);
        ballInfo.add(bLoc);

        return ballInfo;
    }

    // player inforrmation panel
    public static JPanel playerInfo(int num)
    {
        JPanel playerInfo = new JPanel();
        playerInfo.setLayout(new BoxLayout(playerInfo, BoxLayout.Y_AXIS));

        JLabel position = new JLabel("Player Location (x, y)");

        JTextField pLoc = new JTextField();
        fieldPanel.p.get(num).registerListener(pLoc); // gets updates from the ball
        pLoc.setText("Set for kick off");

        playerInfo.add(position);
        playerInfo.add(pLoc);

        return playerInfo;
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