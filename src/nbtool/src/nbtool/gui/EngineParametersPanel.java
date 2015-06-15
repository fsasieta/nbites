package nbtool.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import nbtool.data.*;
import nbtool.util.*;
import nbtool.io.ControlIO;
import nbtool.io.ControlIO.ControlInstance;
import nbtool.util.NBConstants.*;
import nbtool.util.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

/* GUI for the Walking engine parameters streamer.
 * Shows buttons and basic labels, processing is done elsewhere
 */
public class EngineParametersPanel extends JPanel implements ActionListener {
	

    private JScrollPane spCenter, spLeft;
	private JPanel canvas, labelCanvas;
	
	private JButton saveParamsV4, saveParamsV5, useV4, useV5;
    private BoxLayout layout;
    
    private JTextField[] paramFields;
    private JLabel[] fieldLabels;
	
    private static int paramNumber =  49; //size of parameter string.

    private WalkingEngineParameters engineStreamer;
    private JTextField test;

	public EngineParametersPanel() {

		/*
		 * GUI below. 
		 */
		super();

        //Class that contains the networking and other logic stuff.
		engineStreamer = new WalkingEngineParameters();

        /* I use two layout. The border layout to have everything centered, and then the
         * box layout in the canvas panel to have everything in list type.
         */
        setLayout(new BorderLayout());
        
		canvas = new JPanel();

        // there are more than 50 parameters, so we need to be able to scroll stuff
        sp = new JScrollPane(canvas,
                             JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                             JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        add(sp, BorderLayout.CENTER);
        canvas.setLayout(new BoxLayout(canvas, BoxLayout.Y_AXIS));
        
        //Button initialization
        useV4 = new JButton("Use v4 params");
        useV5 = new JButton("Use v5 params");
        saveParamsV4 = new JButton("Save v4 params");
        saveParamsV5 = new JButton("Save v5 params");
        useV4.addActionListener(this);
        useV5.addActionListener(this);
        saveParamsV4.addActionListener(this);
        saveParamsV5.addActionListener(this);

        /* Layout bullshit*/
        useV4.setAlignmentX(Component.LEFT_ALIGNMENT);
        useV5.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveParamsV4.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveParamsV5.setAlignmentX(Component.LEFT_ALIGNMENT);

        canvas.add(useV4);
        canvas.add(useV5);
        canvas.add(saveParamsV4);
        canvas.add(saveParamsV5);

        paramFields = new JTextField[engineStreamer.getListOfParamsLength()];
        
        for(int i = 0; i < paramNumber; i++){
            paramFields[i] = new JTextField(6);
            paramFields[i].setEditable(true);
            paramFields[i].addActionListener(this);
            paramFields[i].setAlignmentX(Component.LEFT_ALIGNMENT);
            canvas.add(paramFields[i]);
        }


        fieldLabels = new JLabels[engineStreamer.getListOfParamsLength()];

        setParams = new JButton("Set parameters");
        setParams.addActionListener(this);
        canvas.add(setParams);
    }

    /* Action code handling 
     */
    @Override
	public void actionPerformed(ActionEvent e) {

		if(e.getSource() == useV4) {
			//make v4 parameters visible.
            System.out.println("use v4 button pressed");
		}
        else if(e.getSource() == useV5) {
			//make v5 parameters visible.
            System.out.println("use v5 button pressed");
		}
        else if(e.getSource() == saveParamsV4) {
            //save parameters of v4 robot
            System.out.println("save v4 parameters button pressed.");
		}
        else if(e.getSource() == saveParamsV5) {
            //save parameters of V5 robot 
            System.out.println("Save v5 parameters button pressed");
        }
        else if(e.getSource() == setParams){
            //Nothing should happen since an unknown button was pressed
        }
	}



/*

    private void useSize(Dimension s) {

    //sp.setBounds(0, 0, s.width, s.height);
    
    //update component sizes...
    Dimension d1, d2, d3, d4;
    int y = 0;
    int max_x = 260;

    //Buttons used: useV4,useV5, saveParamsV4, saveParamsV5;

    d1 = useV4.getPreferredSize();
    d2 = d1;
    d4 = useV5.getPreferredSize();

    useV4.setBounds(0, y, d1.width + 6, d1.height);
    useV5.setBounds(d1.width + 7, y, d1.width + 5, d1.height);

    //calculate bottom y value
    y += (d1.height > d2.height ? d1.height : d2.height) + 3;

    saveParamsV4.setBounds(0, y, d1.width + 5, d1.height);
    saveParamsV5.setBounds(d1.width + 6, y, d1.width + 5, d2.height);
    
    y += (d1.height > d2.height ? d1.height : d2.height) + 3;

    //test.setBounds(0 ,y , d1.width, d1.height);

    
    for(int i = 0; i < paramNumber; i++){
        //paramFields[i] = new JTextField(String.valueOf(0));
        paramFields[i].setBounds(d1.width , y + i*25, useV4.getPreferredSize().width, useV4.getPreferredSize().height);
    }
    
    }
    */
}
