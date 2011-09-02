package de.jtem.jrworkspace.plugin.simplecontroller.widget;

import java.awt.Dimension;

import javax.swing.JFrame;

public abstract class SplashScreen extends JFrame {

	private static final long serialVersionUID = 1L;

	public SplashScreen() {
		setUndecorated(true);
		Dimension size = new Dimension(500, 300);
		setPreferredSize(size);
	}
	
	@Override
	public void setVisible(boolean b) {
		pack();
		setLocationRelativeTo(null);
		super.setVisible(b);
	}
	
	public abstract void setStatus(String status);
	public abstract void setProgress(double progress);
	
}
