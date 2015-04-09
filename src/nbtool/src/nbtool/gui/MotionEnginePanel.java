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


import nbtool.data.BotStats;
import nbtool.data.Log;
import nbtool.data.SessionMaster;
import nbtool.io.CommandIO;
import nbtool.util.N;
import nbtool.util.N.NListener;
import nbtool.util.NBConstants;
import nbtool.util.N.EVENT;
import nbtool.util.NBConstants.FlagPair;
import nbtool.util.NBConstants.MODE;
import nbtool.util.NBConstants.STATUS;
import nbtool.util.P;
import nbtool.util.U;

public class MotionEnginePanel extends JPanel implements ActionListener,
                                                         ChangeListener {

    private JPanel canvas;
    private JButton start;
    private JButton stop;
    private JSlider paramSlider;
    private JScrollPane sp;
    private JComboBox<String> modes;

    public MotionEnginePanel() {	
        super();
		setLayout(null);
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				useSize(e.getComponent().getSize());
			}
		});

        canvas = new JPanel();
        canvas.setLayout(null);

        //adding GUI action components.
        start = new JButton("start");
        start.addActionListener(this);
        canvas.add(start);

        stop = new JButton("stop");
        stop.addActionListener(this);
        canvas.add(stop);
        
        //orientation,min,max, and init val
        paramSlider = new JSlider(JSlider.HORIZONTAL,0,100, 50);
        paramSlider.addChangeListener(this);
        paramSlider.setMinorTickSpacing(1);
        paramSlider.setPaintTicks(true);
        paramSlider.setPaintLabels(true);
        Hashtable labeltable = new Hashtable();
        labeltable.put (new Integer (0), new JLabel("0"));
        labeltable.put (new Integer (100), new JLabel("100"));
        canvas.add(paramSlider);
        
        //In case we get a lot of components in the tab.
        sp = new JScrollPane();
		sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sp.setViewportView(canvas);
        add(sp); 
        
    }

    public void model_returnKeypress() {
		ActionEvent e = new ActionEvent(start, ActionEvent.ACTION_PERFORMED, "start from keypress");
		this.actionPerformed(e);
	}



    public void actionPerformed (ActionEvent e){
        if (e.getSource() == start){
            System.out.println("Start button was pressed.");
        }
        else if(e.getSource() == stop){
            System.out.println("Stop button was pressed");
        }
        else{}
    }

    public void stateChanged(ChangeEvent e){
        JSlider source = (JSlider) e.getSource();
        if(!source.getValueIsAdjusting()){
            int value = (int) source.getValue();
            if(value == 0 ){
                System.out.println("Slider is at zero");
            }
            else if(value == 100){
                System.out.println("Slider is at max");
            }
            else{
                System.out.println("Slider stopped at value:"+value);
            }
        }
    }
    
    
    private void useSize(Dimension s) {

		sp.setBounds(0, 0, s.width, s.height);
		
		//update component sizes...
		Dimension d1, d2, d3;
		int y = 0;
		int max_x = 260;
		
        //start button
		d1 = start.getPreferredSize();
        d2 = d1;
		start.setBounds(0, y, d1.width, d1.height);
        //This calculates the y value of the bottom part of the button
        //I think.
		y += (d1.height > d2.height ? d1.height : d2.height) + 3;

        //stop button
        d1 = start.getPreferredSize();
		stop.setBounds(0, y + 3, d1.width, d1.height);
		y += (d1.height > d2.height ? d1.height : d2.height) + 9;

        paramSlider.getPreferredSize();
        paramSlider.setBounds(0, y+3, 3 * d1.width, d1.height);
        //paramSlider.setBorder(BorderFactory.createEmptyBorder( 2*y, y+3, y-3,y));
        
		
        canvas.setPreferredSize(new Dimension(max_x, y));
    }
    



}

