package de.varylab.jrworkspace.plugin.sidecontainer.widget;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

public class TestBed {

	
	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		JFrame f = new JFrame("ShrinkPanel Testbed");
		f.setLayout(new BorderLayout());
		f.setSize(800, 600);
		
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BorderLayout());
		
		
		ShrinkPanel sp1 = new ShrinkPanel("Test Panel 1", true);
		ShrinkPanel sp2 = new ShrinkPanel("Test Panel 2");
		ShrinkPanel sp3 = new ShrinkPanel("Test Panel 3");
		ShrinkPanel sp4 = new ShrinkPanel("Test Panel 4");
		
		sp1.add(new JLabel("Test Label 1"));
		sp2.add(new JLabel("Test Label 2"));
		sp3.add(new JLabel("Test Label 3"));
		sp4.add(new JLabel("Test Label 4"));
		
		ShrinkSlotVertical spc1 = new ShrinkSlotVertical(200);
		ShrinkSlotVertical spc2 = new ShrinkSlotVertical(200);
		ShrinkSlotHorizontal sps1 = new ShrinkSlotHorizontal(10);
		ShrinkSlotHorizontal sps2 = new ShrinkSlotHorizontal(10);
//		JPanel spc1 = new JPanel();
//		JPanel spc2 = new JPanel();
//		JPanel sps1 = new JPanel();
//		JPanel sps2 = new JPanel();
		spc1.setBorder(BorderFactory.createEtchedBorder());
		spc2.setBorder(BorderFactory.createEtchedBorder());
		sps1.setBorder(BorderFactory.createEtchedBorder());
		sps2.setBorder(BorderFactory.createEtchedBorder());
		
		spc1.addShrinkPanel(sp1);
		spc1.addShrinkPanel(sp2);
		spc1.addShrinkPanel(sp3);
		spc2.addShrinkPanel(sp4);
		
		f.add(spc1, BorderLayout.WEST);
		f.add(spc2, BorderLayout.EAST);
		centerPanel.add(sps1, BorderLayout.NORTH);
		centerPanel.add(sps2, BorderLayout.SOUTH);
		f.add(centerPanel, BorderLayout.CENTER);
		
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}
	
	
}
