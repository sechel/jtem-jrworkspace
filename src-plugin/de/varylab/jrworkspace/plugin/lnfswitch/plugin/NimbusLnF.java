package de.varylab.jrworkspace.plugin.lnfswitch.plugin;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import de.varylab.jrworkspace.plugin.lnfswitch.LookAndFeelPlugin;

public class NimbusLnF extends LookAndFeelPlugin {

	@Override
	public String getLnFClassName() {
		String nimbusClassName = "";
		for (LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels()) {
			if ("Nimbus".equals(laf.getName())) {
				nimbusClassName = laf.getClassName();
			}
		}
		return nimbusClassName;
	}

	@Override
	public String getLnFName() {
		return "Nimbus Look And Feel (Java 1.6 only)";
	}

	
	@Override
	public boolean isSupported() {
		for (LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels()) {
			if ("Nimbus".equals(laf.getName())) {
				return true;
			}
		}
		return false;
	}
	
}
