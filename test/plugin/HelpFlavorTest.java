package plugin;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.util.List;

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

	@Override
	public String getHelpTitle() {
		return "Test Help";
	}

	@Override
	public Icon getHelpIcon() {
		return null;
	}

	@Override
	public String getHelpPath() {
		return "help/";
	}

	@Override
	public Class<?> getHelpHandle() {
		return ShrinkPanelPlugin.class;
	}

	@Override
	public String getHelpDocument() {
		return "default.html";
	}

	@Override
	public String getHelpStyleSheet() {
		return null;
	}

	@Override
	public void setHelpListener(HelpListener l) {
		
	}

	@Override
	public String getTitle() {
		return "Help Test";
	}

	@Override
	public Icon getIcon() {
		return null;
	}
	@Override
	public List<Image> getIconList() {
		return null;
	}

	@Override
	public void setVisible(boolean visible) {
		
	}

	@Override
	public Component getCenterComponent() {
		JPanel panel = new JPanel();
		panel.setMinimumSize(new Dimension(800, 600));
		return panel;
	}

}
