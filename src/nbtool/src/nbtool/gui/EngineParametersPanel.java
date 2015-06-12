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
	
	//static final long serialVersionUID = 1L;

    private JScrollPane sp;
	private JPanel canvas;
    //private JPanel notScrollable;
    //private JScrollPane canvas;
	
	//private JButton startStreaming;
	private JButton saveParamsV4, saveParamsV5;
    private JButton useV4, useV5;
    
    private JTextField[] paramFields;
	
	public Integer[] v4Params;
	public Integer[] v5Params;
    private static int paramNumber =  49; //size of parameter string.

    private WalkingEngineParameters engineStreamer;
    private JTextField test;


	public EngineParametersPanel() {
		/*
		 * GUI below. 
		 */
		super();
		setLayout(null);
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				useSize(e.getComponent().getSize());
			}
		});
		
        //notScrollable = new JPanel();
		canvas = new JPanel();
        //canvas.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); 
        //canvas.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		canvas.setLayout(null);

        engineStreamer = new WalkingEngineParameters();

        useV4 = new JButton("Use v4 parameters");
        useV4.addActionListener(this);
        //useV4.setPreferredSize(new Dimension(80,25));
        canvas.add(useV4);

        useV5 = new JButton("Use v5 parameters");
        useV5.addActionListener(this);
        //useV5.setPreferredSize(new Dimension(80,25));
        canvas.add(useV5);

        saveParamsV4 = new JButton("Save v4 params");
        saveParamsV5 = new JButton("Save v5 params");
        saveParamsV4.addActionListener(this);
        saveParamsV5.addActionListener(this);
        //saveParamsV4.setPreferredSize(new Dimension(80, 25));
        //saveParamsV5.setPreferredSize(new Dimension(80, 25));
        canvas.add(saveParamsV4);
        canvas.add(saveParamsV5);

        //In case we get a lot of components in the tab.
        sp = new JScrollPane();
		sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sp.setViewportView(canvas);
        add(sp);

        paramFields = new JTextField[paramNumber];
        for(int i = 0; i < paramNumber; i++){
            paramFields[i] = new JTextField(6);
            //System.out.println("in initializer array");
            paramFields[i].setEditable(true);
            //paramFields[i].setBounds( 5, 50 + i*25, useV4.getPreferredSize().width, useV4.getPreferredSize().height);
            paramFields[i].addActionListener(this);
            canvas.add(paramFields[i]);
        }

        //JComponent labelsAndFields = getTwoColumnLayout(engineStreamer.listOfParams, paramFields);
        //JComponent optionsPanel = new JPanel(new BorderLayout(10,10));
        //canvas.add(optionsPanel);
    }

    /*
    public void model_returnKeypress() {
        ActionEvent e = new ActionEvent(
                startStop, Action.Event.ACTION_PERFORMED, "start from keypress"
                );
        this.actionPerformed(e);
    }
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
        else{
            //Nothing should happen since an unknown button was pressed
        }
	}

    private void useSize(Dimension s) {

    sp.setBounds(0, 0, s.width, s.height);
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
    
/* everything else is a comment

    protected JComponent getTwoColumnLayout(JLabel[] labels, JComponent[] fields) {
		if(labels.length != fields.length) {
			String s = "Inconsistent # of labels and fields";
			throw new IllegalArgumentException(s);
		}
		
		JComponent panel = new JPanel();
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		GroupLayout.Group yLabelGroup = layout.createParallelGroup(GroupLayout.Alignment.TRAILING);
		hGroup.addGroup(yLabelGroup);
		
		GroupLayout.Group yFieldGroup = layout.createParallelGroup();
		hGroup.addGroup(yFieldGroup);
		layout.setHorizontalGroup(hGroup);
		
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
		layout.setVerticalGroup(vGroup);
		
		int p = GroupLayout.PREFERRED_SIZE;
		
		for(JLabel label : labels) {
			yLabelGroup.addComponent(label);
		}
		for(Component field : fields) {
			yFieldGroup.addComponent(field,p,p,p);
			for(int i=0; i<labels.length; i++) {
				vGroup.addGroup(layout.createParallelGroup().
						addComponent(labels[i]).
						addComponent(fields[i],p,p,p));
			}
		}
		return panel;
	}
	
	protected JComponent getTwoColumnLayout(String[] labelStrings, JComponent[] fields) {
		JLabel[] labels = new JLabel[labelStrings.length];
		for(int i=0; i<labels.length; i++) {
			labels[i] = new JLabel(labelStrings[i]);
		}
		return getTwoColumnLayout(labels,fields);
	}


}
