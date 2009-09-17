package de.jtem.jrworkspace.plugin.jrdesktop;

import java.awt.Component;
import java.awt.Dimension;

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

	public void setVisible(boolean visible) {
		
	}

	@Override
	public void install(Controller c) throws Exception {
	}

	@Override
	public void uninstall(Controller c) throws Exception {
	}

	public Icon getIcon() {
		return getPluginInfo().icon;
	}

	public String getTitle() {
		return getPluginInfo().name;
	}
	
}
