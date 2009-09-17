package de.jtem.jrworkspace.plugin.lnfswitch.plugin;

import de.jtem.jrworkspace.plugin.lnfswitch.LookAndFeelPlugin;

public class SystemLookAndFeel extends LookAndFeelPlugin {

	@Override
	public String getLnFClassName() {
		return "system_lnf_classname";
	}

	@Override
	public String getLnFName() {
		return "System Look and Feel";
	}
	
	@Override
	public boolean isSupported() {
		return true;
	}

}
