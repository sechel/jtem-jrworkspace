package de.varylab.jrworkspace.plugin.jrdesktop;

import java.awt.Container;

import javax.swing.JInternalFrame;

import de.varylab.jrworkspace.plugin.Controller;
import de.varylab.jrworkspace.plugin.Plugin;

public abstract class JRDesktopFrame extends Plugin {

	private JInternalFrame
		frame = null;
	
	
	@Override
	public void install(Controller c) throws Exception {
		JRDesktopPlugin jrdesktop = c.getPlugin(JRDesktopPlugin.class);
		jrdesktop.addDesktopPlugin(this);
	}

	@Override
	public void uninstall(Controller c) throws Exception {
		JRDesktopPlugin jrdesktop = c.getPlugin(JRDesktopPlugin.class);
		jrdesktop.removeDesktopPlugin(this);
	}
	
	
	protected abstract Container getContent();

	
	protected JInternalFrame getInternalFrame() {
		if (frame == null) {
			frame = new JInternalFrame(getPluginInfo().name, true, true, false, true);
			Container content = getContent();
			frame.setSize(content.getSize());
			frame.setContentPane(content);
		}
		return frame;
	}
	
	
}
