package behavior_sim;

/*
    Here is where all of the gui and dispalys happen. 
*/

import javax.swing.*;

import java.awt.Dimension;
import java.awt.Container;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.util.ArrayList;

public class Gui extends JFrame
{
    private static final int frameWidth = 1200;
    private static final int frameHeight = 800;

    private static World fieldPanel;

    public static void createAndShowGUI() {
        // this frame holds everything else
        JFrame mainFrame = new Gui();
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container cP = mainFrame.getContentPane();

        // the field/world state
        fieldPanel = new World();
        fieldPanel.setPreferredSize(new Dimension((int)fieldPanel.field.FIELD_WIDTH,
                                                (int)fieldPanel.field.FIELD_HEIGHT));

        // the controls, buttons, etc.
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        addControls(controlPanel);

        // Ball information panel
        JPanel ballInfo = new JPanel();
        ballInfo.setLayout(new BoxLayout(ballInfo, BoxLayout.Y_AXIS));
        JTextField bX = new JTextField();
        JTextField bY = new JTextField();
        ballInfo.add(bX);
        ballInfo.add(bY);
        bX.setText(String.valueOf(fieldPanel.ball.getX()));
        bY.setText(String.valueOf(fieldPanel.ball.getY()));

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Controls", controlPanel);
        tabs.addTab("Ball Info", ballInfo);

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
    public static void addControls(JPanel controls)
    {
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
        // should be on the field
        final JButton setField = new JButton("Set Field");
        setField.addActionListener(new ActionListener(){ 
        @Override
        public void actionPerformed(ActionEvent e)
            {
                boolean[] team = new boolean[cb1.size() + cb2.size()];

                for (int i = 0; i < cb1.size(); i++)
                {
                    if (cb1.get(i).isSelected()) 
                        team[i] = true;
                    if (cb2.get(i).isSelected())
                        team[i + cb1.size()] = true;
                    else if (!cb1.get(i).isSelected())
                    {
                        team[i] = false;
                        team[i + cb1.size()] = false;
                    }
                }
                fieldPanel.buildTeam(team);
            }
        });
        controls.add(setField);

        // tells the World to reset the state of the field
        final JButton reset = new JButton("Reset");
        reset.addActionListener(new ActionListener(){ 
        @Override
        public void actionPerformed(ActionEvent e)
            {
                fieldPanel.reset();
            }
        });
        controls.add(reset); 
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