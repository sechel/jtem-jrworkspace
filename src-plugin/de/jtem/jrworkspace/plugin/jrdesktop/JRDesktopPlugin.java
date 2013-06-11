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

package de.jtem.jrworkspace.plugin.jrdesktop;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

import de.jtem.jrworkspace.plugin.Controller;
import de.jtem.jrworkspace.plugin.Plugin;
import de.jtem.jrworkspace.plugin.PluginInfo;
import de.jtem.jrworkspace.plugin.flavor.PerspectiveFlavor;
import de.jtem.jrworkspace.plugin.jrdesktop.image.ImageHook;

public class JRDesktopPlugin extends Plugin implements PerspectiveFlavor {

	private JDesktopPane
		desktop = new JDesktopPane();
	
	
	public JRDesktopPlugin() {
		desktop.setPreferredSize(new Dimension(500, 400));
	}
	
	@Override
	public PluginInfo getPluginInfo() {
		PluginInfo info = new PluginInfo();
		info.name = "jRDesktop";
		info.vendorName = "Stefan Sechelmann";
		info.icon = ImageHook.getIcon("arrow_right_blue_round.png");
		return info;
	}

	
	@Override
	public Component getCenterComponent() {
		return desktop;
	}
	
	public void addDesktopPlugin(JRDesktopFrame p) {
		JInternalFrame frame = p.getInternalFrame();
		frame.setVisible(true);
		desktop.add(frame);
		try {
	        frame.setSelected(true);
	    } catch (java.beans.PropertyVetoException e) {}
	}
	
	
	public void removeDesktopPlugin(JRDesktopFrame p) {
		JInternalFrame frame = p.getInternalFrame();
		frame.setVisible(false);
		desktop.remove(frame);
	}

	@Override
	public void setVisible(boolean visible) {
		
	}

	@Override
	public void install(Controller c) throws Exception {
	}

	@Override
	public void uninstall(Controller c) throws Exception {
	}

	@Override
	public Icon getIcon() {
		return getPluginInfo().icon;
	}
	
	@Override
	public List<Image> getIconList() {
		return null;
	}

	@Override
	public String getTitle() {
		return getPluginInfo().name;
	}
	
}
