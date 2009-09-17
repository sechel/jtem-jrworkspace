package de.jtem.jrworkspace.plugin.lnfswitch.plugin;

import de.jtem.jrworkspace.plugin.lnfswitch.LookAndFeelPlugin;

public class CrossPlatformLnF extends LookAndFeelPlugin {

	@Override
	public String getLnFName() {
		return "Cross Platform Look And Feel";
	}

	@Override
	public String getLnFClassName() {
		return "cross_platform_lnf_classname";
	}

	@Override
	public boolean isSupported() {
		return true;
	}
	
}
