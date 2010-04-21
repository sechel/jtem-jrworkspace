package de.jtem.jrworkspace.plugin.widget;

import javax.swing.Icon;

import de.jtem.jrworkspace.plugin.sidecontainer.SideContainerPerspective;
import de.jtem.jrworkspace.plugin.simplecontroller.SimpleController;

public class SPIPluginTest extends SideContainerPerspective {

	public Icon getIcon() {
		return null;
	}

	public String getTitle() {
		return "SPI Plugin Test";
	}

	public void setVisible(boolean visible) {
		
	}

	
	public static void main(String[] args) {
		SimpleController c = new SimpleController();
		c.setPropertiesResource(SPIPluginTest.class, null);
		c.startup();
	}
	
}
