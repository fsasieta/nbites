package behavior_sim;

import nbclient.gui.utilitypanes.UtilityParent;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Simulator extends UtilityParent implements ChangeListener {

    public Simulator() {
        setLayout(null);
        Dimension s = slider.getPreferredSize();
        slider.setBounds(0, 0, s.width, s.height);
        
        slider.addChangeListener(this);
        add(slider);
        setSize(800, 800);
    }

    public Object getCurrentValue() {
        return new Integer(42);
    }
    
    public void paint(Graphics g){
        super.paint(g);
    }
    
    private JSlider slider = new JSlider(0, 255, 128);
    private JLabel yl, ul, vl;

    public void stateChanged(ChangeEvent e) {
        this.repaint();
    }
}
