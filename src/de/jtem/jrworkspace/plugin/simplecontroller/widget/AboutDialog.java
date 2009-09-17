package de.jtem.jrworkspace.plugin.simplecontroller.widget;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;

import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import de.jtem.jrworkspace.plugin.simplecontroller.image.ImageHook;

public class AboutDialog extends JDialog{

	private static final long 
		serialVersionUID = 1L;
	private static Image
		image = ImageHook.getImage("splash01.png");
	private String
		statusString = "";

	public AboutDialog(Frame parent, boolean close_on_click){
		super(parent);
		setSize(500, 300);
		
		MediaTracker mt = new MediaTracker(this);
		mt.addImage(image, 0);
		try{
			mt.waitForAll();
		} catch (InterruptedException e){}
		
		if (close_on_click){
			addMouseListener(new MouseAdapter(){
				@Override
				public void mouseClicked(MouseEvent arg0) {
					dispose();
				}
			});
			setModal(true);
		}
		
		getRootPane().setDoubleBuffered(true);
		setBackground(Color.WHITE);
		setUndecorated(true);
		setLocationByPlatform(true);
		setLocationRelativeTo(parent);
	}


	@Override
	public void paint(Graphics g) {
		g.drawImage(image, 0, 0, this);
		g.setColor(Color.BLACK);
		g.drawString(statusString, 5, 295);	
	}
	
	
	public static void setBannerImage(URI file) {
		try {
			image = ImageIO.read(file.toURL());
		} catch (IOException e) {
			System.out.println("Could not load banner image " + file.getPath() + "\n" + e.getMessage());
		}
	}
	
	public static void setBannerImage(Image image) {
		if (image != null) {
			AboutDialog.image = image;
		}
	}
	
	
	public void setStatus(String status) {
		this.statusString = status;
		if (SwingUtilities.isEventDispatchThread()) {
			paint(getGraphics());
		} else {
			repaint();
		}
	}
	
}
