/**
This file is part of a jTEM project.
All jTEM projects are licensed under the FreeBSD license 
or 2-clause BSD license (see http://www.opensource.org/licenses/bsd-license.php). 

Copyright (c) 2002-2009, Technische Universität Berlin, jTEM
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, 
are permitted provided that the following conditions are met:

-	Redistributions of source code must retain the above copyright notice, 
	this list of conditions and the following disclaimer.

-	Redistributions in binary form must reproduce the above copyright notice, 
	this list of conditions and the following disclaimer in the documentation 
	and/or other materials provided with the distribution.
 
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS 
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, 
OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT 
OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING 
IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY 
OF SUCH DAMAGE.
**/

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
