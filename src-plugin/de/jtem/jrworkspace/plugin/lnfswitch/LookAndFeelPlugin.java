package de.jtem.jrworkspace.plugin.lnfswitch;

import javax.swing.JPanel;

import de.jtem.jrworkspace.plugin.Controller;
import de.jtem.jrworkspace.plugin.Plugin;
import de.jtem.jrworkspace.plugin.PluginInfo;
import de.jtem.jrworkspace.plugin.lnfswitch.image.ImageHook;

public abstract class LookAndFeelPlugin extends Plugin {

	protected LookAndFeelSwitch
		lookAndFeelSwitch = null;
	
	public abstract String getLnFName();
	
	public abstract String getLnFClassName();
	
	public abstract boolean isSupported();
	
	public JPanel getOptionPanel() {
		return null;
	}
	
	@Override
	public PluginInfo getPluginInfo() {
		PluginInfo info = new PluginInfo();
		info.name = getClass().getName();
		info.icon = ImageHook.getIcon("pictures.png");
		return info;
	}

	@Override 
	public void install(Controller c) throws Exception {
		super.install(c);
		lookAndFeelSwitch = c.getPlugin(LookAndFeelSwitch.class);
		if (isSupported()) {
			lookAndFeelSwitch.addLookAndFeel(this);
		}
	}
	
	@Override
	public void uninstall(Controller c) throws Exception {
		super.uninstall(c);
		lookAndFeelSwitch.removeLookAndFeel(this);
	}
	
	@Override
	public String toString() {
		return getLnFName();
	}
	
}
