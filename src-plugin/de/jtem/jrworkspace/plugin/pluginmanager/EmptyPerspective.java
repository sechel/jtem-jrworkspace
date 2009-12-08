package de.jtem.jrworkspace.plugin.pluginmanager;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.JPanel;

import de.jtem.jrworkspace.plugin.Plugin;
import de.jtem.jrworkspace.plugin.flavor.PerspectiveFlavor;

public class EmptyPerspective extends Plugin implements PerspectiveFlavor {

	private JPanel
		panel = new JPanel();
	
	public EmptyPerspective() {
		panel.setMinimumSize(new Dimension(800, 600));
	}
	
	public Component getCenterComponent() {
		return panel;
	}

	public Icon getIcon() {
		return null;
	}

	public String getTitle() {
		return "Test Perspective";
	}

	public void setVisible(boolean visible) {

	}

}
