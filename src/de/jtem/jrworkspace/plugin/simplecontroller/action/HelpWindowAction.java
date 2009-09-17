package de.jtem.jrworkspace.plugin.simplecontroller.action;

import static de.jtem.jrworkspace.plugin.simplecontroller.image.ImageHook.getIcon;
import static java.awt.event.KeyEvent.VK_F1;
import static javax.swing.KeyStroke.getKeyStroke;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import de.jtem.jrworkspace.plugin.simplecontroller.help.HelpWindow;

public class HelpWindowAction extends AbstractAction {

	private static final long 
		serialVersionUID = 1L;
	private HelpWindow
		helpWindow = null;

	public HelpWindowAction(HelpWindow win) {
		putValue(NAME, "Help Center");
		putValue(LONG_DESCRIPTION, "jRWorkspace Plugin Help Pages");
		putValue(SMALL_ICON, getIcon("helpred.png"));
		putValue(ACCELERATOR_KEY, getKeyStroke(VK_F1, 0));
		
		helpWindow = win;
		helpWindow.setSize(800, 500);
		helpWindow.setMinimumSize(new Dimension(400, 400));
	}
	
	
	public void actionPerformed(ActionEvent e) {
		if (helpWindow.isShowing()) {
			helpWindow.toFront();
			return;
		}
		helpWindow.setLocationByPlatform(true);
		helpWindow.setLocationRelativeTo(helpWindow.getParent());
		helpWindow.updateData();
		helpWindow.setVisible(true);
	}

}
