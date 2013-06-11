package de.jtem.jrworkspace.plugin.simplecontroller.widget;

import java.awt.Image;
import java.util.List;

import javax.swing.Icon;

import de.jtem.jrworkspace.plugin.sidecontainer.SideContainerPerspective;
import de.jtem.jrworkspace.plugin.simplecontroller.SimpleController;

public class SPIPluginTest extends SideContainerPerspective {

	@Override
	public Icon getIcon() {
		return null;
	}
	@Override
	public List<Image> getIconList() {
		return null;
	}

	@Override
	public String getTitle() {
		return "SPI Plugin Test";
	}

	@Override
	public void setVisible(boolean visible) {
		
	}

	
	public static void main(String[] args) {
		SimpleController c = new SimpleController();
		c.setPropertiesResource(SPIPluginTest.class, null);
		c.startup();
	}
	
}
