package de.jtem.jrworkspace.plugin.simplecontroller.action;

import static de.jtem.jrworkspace.plugin.simplecontroller.image.ImageHook.getIcon;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import de.jtem.jrworkspace.plugin.simplecontroller.preferences.PreferencesWindow;

public class PreferencesWindowAction extends AbstractAction {

	private static final long 
		serialVersionUID = 1L;
	private PreferencesWindow
		preferencesWindow = null;

	public PreferencesWindowAction(PreferencesWindow win) {
		putValue(NAME, "Preferences");
		putValue(LONG_DESCRIPTION, "Configure jRWorkspace Preferences");
		putValue(SMALL_ICON, getIcon("prefs.png"));
		
		preferencesWindow = win;
		preferencesWindow.setSize(600, 500);
		preferencesWindow.setMinimumSize(new Dimension(400, 400));
	}
	
	
	public void actionPerformed(ActionEvent e) {
		if (preferencesWindow.isShowing()) {
			preferencesWindow.toFront();
			return;
		}
		preferencesWindow.setLocationByPlatform(true);
		preferencesWindow.setLocationRelativeTo(preferencesWindow.getParent());
		preferencesWindow.updateData();
		preferencesWindow.setVisible(true);
	}

}
