package nbtool.gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.nio.file.FileSystem;
import java.util.Map.Entry;
import java.util.Hashtable;
//import java.util.event.ChangeListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JSlider;
import javax.swing.event.*;
import javax.swing.BorderFactory;


//import nbtool.data.BotStats;
import nbtool.data.Log;
import nbtool.data.SessionMaster;
import nbtool.io.ControlIO;

/*
import nbtool.util.N;
import nbtool.util.N.NListener;
import nbtool.util.NBConstants;
import nbtool.util.N.EVENT;
import nbtool.util.NBConstants.FlagPair;
import nbtool.util.NBConstants.MODE;
import nbtool.util.NBConstants.STATUS;
import nbtool.util.P;
import nbtool.util.U;
*/

/* TO DO:
 * Add a text box to input the place where I want the robot to go
 * i.e. if I want it to go to certain x,y position in the field,
 * then add a couple of text boxes indicating this.
 * Maybe some kind of box input will be better UX.
 * Need to figure out size of the field. (6 x 9 meters)
 *
 * This view acts as both a view and a controller.
 * Bad MVC, but it works (sort of)
 */
public class MotionEnginePanel extends JPanel implements ActionListener,
                                                         ChangeListener {

    private JPanel canvas;
    private JButton startStop;//, stop;
    private JLabel robPositionLab, walkParamLab;
    //Names of Slider based on robot parameters
    //Look in bhwalk/~/ WalkCommand.
    //h is for rot, not up. No haters please,
    //I didnt choose the letter
    private JSlider xSlider, ySlider, hSlider;
    private JScrollPane sp;
    private JComboBox<String> modes;
    private MotionStreamer dataStreamer;

    public MotionEnginePanel() {	
        super();
		setLayout(null);
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				useSize(e.getComponent().getSize());
			}
		});

        //Need to specify comm with the Motion Streamer
        dataStreamer = new MotionStreamer(this);


        canvas = new JPanel();
        canvas.setLayout(null);

        //adding GUI action components.
        startStop = new JButton("Start streaming");
        startStop.addActionListener(this);
        canvas.add(startStop);

        //stop = new JButton("Stop streaming");
        //stop.addActionListener(this);
        //canvas.add(stop);
        
        walkParamLab = new JLabel("Walking parameters");
        robPositionLab = new JLabel("Robot Position in the field.");
        canvas.add(robPositionLab);
        canvas.add(walkParamLab);
        
        //orientation,min,max, and init val
        //same deal for other sliders.
        xSlider = new JSlider(JSlider.HORIZONTAL,0,100, 50);
        xSlider.addChangeListener(this);
        xSlider.setMinorTickSpacing(1);
        xSlider.setPaintTicks(true);
        xSlider.setPaintLabels(true);
        Hashtable xlabeltable = new Hashtable();
        xlabeltable.put (new Integer (0), new JLabel("0"));
        xlabeltable.put (new Integer (100), new JLabel("100"));
        canvas.add(xSlider);
        

        ySlider = new JSlider(JSlider.HORIZONTAL,0,100, 50);
        ySlider.addChangeListener(this);
        ySlider.setMinorTickSpacing(1);
        ySlider.setPaintTicks(true);
        ySlider.setPaintLabels(true);
        Hashtable ylabeltable = new Hashtable();
        ylabeltable.put (new Integer (0), new JLabel("0"));
        ylabeltable.put (new Integer (100), new JLabel("100"));
        canvas.add(ySlider);
        
        hSlider = new JSlider(JSlider.HORIZONTAL,0,100, 50);
        hSlider.addChangeListener(this);
        hSlider.setMinorTickSpacing(1);
        hSlider.setPaintTicks(true);
        hSlider.setPaintLabels(true);
        Hashtable hlabeltable = new Hashtable();
        hlabeltable.put (new Integer (0), new JLabel("0"));
        hlabeltable.put (new Integer (100), new JLabel("100"));
        canvas.add(hSlider);
        

        //In case we get a lot of components in the tab.
        sp = new JScrollPane();
		sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sp.setViewportView(canvas);
        add(sp); 
        
    }

    public void model_returnKeypress() {
		ActionEvent e = new ActionEvent(
                startStop, ActionEvent.ACTION_PERFORMED, "start from keypress"
                );
		this.actionPerformed(e);
	}



    /* For some reason you have to press the button twice to make it go.
     * The first press is always stop streaming
     */
    public void actionPerformed (ActionEvent e){
        if (e.getSource() == startStop){
            if("Start Streaming".equals(e.getActionCommand())){
                System.out.println("Start button was pressed.");
                dataStreamer.streamingStatus("start");
                startStop.setText("Stop Streaming");
            }
            else{
                System.out.println("Stop button was pressed");
                dataStreamer.streamingStatus("stop");
                startStop.setText("Start Streaming");
            }
        }
    }

    /* Commands will only be sent if the the tool is
     * streaming. ie. if the "Stop streaming" button is shown
     */
    public void stateChanged(ChangeEvent e){
        JSlider source = (JSlider) e.getSource();
        if(!source.getValueIsAdjusting()){

            float valueX = (float) xSlider.getValue();
            float valueY = (float) ySlider.getValue();
            float valueH = (float) hSlider.getValue();
            //Need to add a slider for this.
            //float gainsValue = (float) gainsSlider.getValue();

            //Sends parameters to class that handles rest of motion movmt and msgs.
            //this gets upadated everytime this method is called
            //Declared in MotionStreamer.java. Works!!
            dataStreamer.odometryWalk(valueX, valueY, valueH, .5f); //hardcoded value for now.
        }
    }
    
    
    private void useSize(Dimension s) {

		sp.setBounds(0, 0, s.width, s.height);
		
		//update component sizes...
		Dimension d1, d2, d3;
		int y = 0;
		int max_x = 260;
		
        //start button
		d1 = startStop.getPreferredSize();
        d2 = d1;
		startStop.setBounds(0, y, d1.width + 5, d1.height);
        //This calculates the y value of the bottom part of the button
        //I think.
		y += (d1.height > d2.height ? d1.height : d2.height) + 3;

        //stop button
        //d1 = start.getPreferredSize();
		//stop.setBounds(0, y + 3, d1.width, d1.height);
		//y += (d1.height > d2.height ? d1.height : d2.height) + 3;

        //label for the walk motion sliders
        walkParamLab.setBounds(0, y + 3, d1.width * 3 , d1.height);
        y += (d1.height > d2.height ? d1.height : d2.height) + 3;

        //Sliders
        xSlider.getPreferredSize();
        xSlider.setBounds(0, y + 5, 3 * d1.width, d1.height);
        y += (d1.height > d2.height ? d1.height : d2.height) + 9;

        ySlider.getPreferredSize();
        ySlider.setBounds(0, y + 5, 3 * d1.width, d1.height);
        y += (d1.height > d2.height ? d1.height : d2.height) + 9;

        hSlider.getPreferredSize();
        hSlider.setBounds(0, y + 5, 3 * d1.width, d1.height);
        y += (d1.height > d2.height ? d1.height : d2.height) + 9;
        
        //Label for other stuff
        robPositionLab.setBounds(0, y + 3, d1.width *3, d1.height);

        //xSlider.setBorder(BorderFactory.createEmptyBorder( 2*y, y+3, y-3,y));
		
        canvas.setPreferredSize(new Dimension(max_x, y));
    }
}
