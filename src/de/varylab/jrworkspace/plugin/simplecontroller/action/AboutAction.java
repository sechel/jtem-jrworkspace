package de.varylab.jrworkspace.plugin.simplecontroller.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import de.varylab.jrworkspace.plugin.simplecontroller.image.ImageHook;
import de.varylab.jrworkspace.plugin.simplecontroller.widget.AboutDialog;

public class AboutAction extends AbstractAction {

	private static final long 
		serialVersionUID = 1L;
	private AboutDialog
		dialog = null;
	
	public AboutAction(AboutDialog dialog) {
		this.dialog = dialog;
		putValue(NAME, "About");
		putValue(LONG_DESCRIPTION, "About jRWorkspace");
		putValue(SMALL_ICON, ImageHook.getIcon("exclam.png"));
	}
	
	
	public void actionPerformed(ActionEvent e) {
		if (dialog.isShowing()) {
			dialog.toFront();
			return;
		}
		dialog.setLocationByPlatform(true);
		dialog.setLocationRelativeTo(dialog.getParent());
		dialog.setVisible(true);
	}

}
