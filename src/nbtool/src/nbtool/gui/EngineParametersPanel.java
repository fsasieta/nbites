package nbtool.gui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import nbtool.gui.WalkingEngineParameters.Parameter;


/* GUI for the Walking engine parameters streamer.
 * Shows buttons and basic labels, processing is done elsewhere
 */
public class EngineParametersPanel extends JPanel implements ActionListener {

    private JScrollPane spCenter, spLeft;
    //private JPanel labelCanvas;
	private JPanel canvas, buttonCanvas, backEndCanvas;
	
	private JButton saveParamsV4, saveParamsV5, useV4, useV5, setParams;
    private JLabel currentValuesTopLabel;
    
    private JLabel[] fieldLabels, currentValuesLabels;
    //private JSlider[] sliderFields;
	
    private static int paramNumber; //size of parameter string.
    private static String[] paramList, defaultValuesV4, defaultValuesV5, currentValues;

    private WalkingEngineParameters engineStreamer;
    private Parameter[] parameters;

	public EngineParametersPanel() {

		/*
		 * GUI below. 
		 */
		super();
	
        //Class that contains the networking and other logic stuff.
		engineStreamer = new WalkingEngineParameters();
		parameters = engineStreamer.parameters;

        /* I use two layout. The border layout to have everything centered, and then the
         * box layout in the canvas panel to have everything in list type.
         */
        setLayout(new BorderLayout());
        
		canvas = new JPanel();
        buttonCanvas = new JPanel();
        backEndCanvas = new JPanel(new BorderLayout());
        //labelCanvas = new JPanel();
        //labelCanvas.setLayout(new BoxLayout(labelCanvas, BoxLayout.Y_AXIS));

        // there are more than 80 parameters, so we need to be able to scroll stuff
        spCenter = new JScrollPane(backEndCanvas,
                             JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                             JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        //Notice we are adding them to the WalkingEngineParameterPanel, not the 
        //canvas panel just created
        add(spCenter, BorderLayout.CENTER);
        add(buttonCanvas, BorderLayout.NORTH);

        //The backend canvas contains the middle components
        backEndCanvas.add(canvas, BorderLayout.WEST);
        //backEndCanvas.add(labelCanvas, BorderLayout.EAST);

        canvas.setLayout(new GridLayout(0, 3));

        //Adding Buttons
        useV4 = new JButton("Default V4 params");
        useV5 = new JButton("Default V5 params");
        saveParamsV4 = new JButton("Locally Save v4 params");
        saveParamsV5 = new JButton("Locally Save v5 params");
        setParams = new JButton("Set and Save parameters on robot");
        setParams.addActionListener(this);
        useV4.addActionListener(this);
        useV5.addActionListener(this);
        saveParamsV4.addActionListener(this);
        saveParamsV5.addActionListener(this);
        buttonCanvas.add(useV4);
        buttonCanvas.add(useV5);
        buttonCanvas.add(saveParamsV4);
        buttonCanvas.add(saveParamsV5);

        //We add it to the main one so it is visible even when we scroll
        add(setParams, BorderLayout.SOUTH);

        //Top JLabel
        currentValuesTopLabel = new JLabel("Current Value");
        buttonCanvas.add(currentValuesTopLabel, BorderLayout.EAST);

        //Initializing needed arrays
        //Number of params
        paramNumber = engineStreamer.getListOfParamsLength();

        fieldLabels = new JLabel[paramNumber]; 
        currentValuesLabels = new JLabel[paramNumber];
        //sliderFields = new JSlider[paramNumber];

        paramList = engineStreamer.getListOfParams();

        defaultValuesV4 = engineStreamer.getDefaultValuesV4();
        defaultValuesV5 = engineStreamer.getDefaultValuesV5();

        currentValues = new String[defaultValuesV4.length];

        //Setting the arrays equal to each other will only redirect pointers, and we dont want that.
        System.arraycopy(defaultValuesV4, 0, currentValues, 0, defaultValuesV4.length);

        //Adding the labels and the text fields into the grid layout.
        //We always initialize with the default values for v4.
        //Up to the person using the tool to actively change the values for V5.
        //labelCanvas.add(Box.createRigidArea(new Dimension(0,18)));
        for(int i = 0; i < paramNumber; i++){ 
            fieldLabels[i] = new JLabel("(" + i + ")" + " " + paramList[i]);
            fieldLabels[i].setToolTipText("Default value is in parenthesis");
            canvas.add(fieldLabels[i]);

            //paramFields[i] = new JTextField(defaultValuesV4[i], 8);
            //paramFields[i] = Parameters[i].getDisplay(defaultValuesV4[i]);
            //Dimension opt = parameters[i].getDisplay().getPreferredSize();
            //parameters[i].getDisplay().setMaximumSize(new Dimension(opt.width, fieldLabels[i].getHeight()));
            canvas.add(parameters[i].getDisplay());

            //if( i != 21 || i != 22 || i != 55){
            //    sliderFields[i] = new JSlider(JSlider.HORIZONTAL, Float.parseFloat(defaultValuesV4[i]) - 200, Float.parseFloat(defaultValuesV4[i]) + 200); 
            //}

            currentValuesLabels[i] = new JLabel(currentValues[i]);
            //currentValuesLabels[i].setAlignmentX(Component.RIGHT_ALIGNMENT);
            //labelCanvas.add(currentValuesLabels[i], BorderLayout.EAST);
           // labelCanvas.add(Box.createRigidArea(new Dimension(0,50)));
            canvas.add(currentValuesLabels[i]);

        }
    }

    /* Action code handling 
     */
    @Override
	public void actionPerformed(ActionEvent e) {

		if(e.getSource() == useV4) {
			//make v4 parameters visible.
            System.out.println("Default V4 button pressed");
            for(int i = 0; i < parameters.length; i++){
            	parameters[i].setValue(defaultValuesV4[i]);
                //paramFields[i].setText(defaultValuesV4[i]);
            }
            System.arraycopy(defaultValuesV4, 0, currentValues, 0, defaultValuesV4.length);
		}
        else if(e.getSource() == useV5) {
			//make v5 parameters visible.
            System.out.println("Default V5 button pressed");
            for(int i = 0; i < parameters.length; i++){
            	parameters[i].setValue(defaultValuesV5[i]);
                //paramFields[i].setText(defaultValuesV5[i]);
            }
            System.arraycopy(defaultValuesV5, 0, currentValues, 0, defaultValuesV5.length);
		}
        else if(e.getSource() == saveParamsV4) {
            //save parameters of v4 robot
            System.out.println("Save V4 parameters button pressed.");
            for(int i = 0; i < parameters.length; i++){
                //currentValues[i] = paramFields[i].getText();
            	currentValues[i] = "" + parameters[i].getValue();
            }
            engineStreamer.writeDataToFile(currentValues, 4);

		}
        else if(e.getSource() == saveParamsV5) {
            //save parameters of V5 robot 
            System.out.println("Save V5 parameters button pressed");
            for(int i = 0; i < parameters.length; i++){
                //currentValues[i] = paramFields[i].getText();
            	currentValues[i] = "" + parameters[i].getValue();
            }
            engineStreamer.writeDataToFile(currentValues, 5);

        }
        else if(e.getSource() == setParams){
              
            //This grabs all values from the textfields array and sends them to the next class to format
            //into a protobuf and send them.
            System.out.println("Set and save on robot params button pressed");

            //TODO: Check for boundary conditions in some of the parameters!!

            for(int i = 0; i < parameters.length; i++){
                //currentValues[i] = paramFields[i].getText();
            	currentValues[i] = "" + parameters[i].getValue();
                currentValuesLabels[i].setText(currentValues[i]);
            }
            
            //System.out.println("Array that was acquired by set params button");
            //System.out.println(Arrays.toString(currentValues));
            System.out.println("Sending data over network");
            engineStreamer.sendDataOverNetwork(currentValues);
        }
	}
}
