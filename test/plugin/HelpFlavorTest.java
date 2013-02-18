package plugin;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.JPanel;

import de.jtem.jrworkspace.plugin.Plugin;
import de.jtem.jrworkspace.plugin.flavor.HelpFlavor;
import de.jtem.jrworkspace.plugin.flavor.PerspectiveFlavor;
import de.jtem.jrworkspace.plugin.sidecontainer.template.ShrinkPanelPlugin;
import de.jtem.jrworkspace.plugin.simplecontroller.SimpleController;

public class HelpFlavorTest extends Plugin implements HelpFlavor, PerspectiveFlavor {

	public static void main(String[] args) {
		SimpleController c = new SimpleController();
		c.registerPlugin(HelpFlavorTest.class);
		c.startup();
	}

	public String getHelpTitle() {
		return "Test Help";
	}

	public Icon getHelpIcon() {
		return null;
	}

	public String getHelpPath() {
		return "help/";
	}

	public Class<?> getHelpHandle() {
		return ShrinkPanelPlugin.class;
	}

	public String getHelpDocument() {
		return "default.html";
	}

	public String getHelpStyleSheet() {
		return null;
	}

	public void setHelpListener(HelpListener l) {
		
	}

	public String getTitle() {
		return "Help Test";
	}

	public Icon getIcon() {
		return null;
	}

	public void setVisible(boolean visible) {
		
	}

	public Component getCenterComponent() {
		JPanel panel = new JPanel();
		panel.setMinimumSize(new Dimension(800, 600));
		return panel;
	}

}
