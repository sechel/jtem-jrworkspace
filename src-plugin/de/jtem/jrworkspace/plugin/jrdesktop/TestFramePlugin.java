package de.jtem.jrworkspace.plugin.jrdesktop;

import java.awt.Container;

import javax.swing.JLabel;
import javax.swing.JPanel;

import de.jtem.jrworkspace.plugin.PluginInfo;
import de.jtem.jrworkspace.plugin.jrdesktop.image.ImageHook;

public class TestFramePlugin extends JRDesktopFrame {

	private JPanel
		content = new JPanel();
	
	public TestFramePlugin() {
		JLabel testLabel = new JLabel("Test Label");
		testLabel.setIcon(ImageHook.getIcon("world.png"));
		content.add(testLabel);
		getInternalFrame().setSize(300, 200);
	} 
	
	
	@Override
	protected Container getContent() {
		return content;
	}

	@Override
	public PluginInfo getPluginInfo() {
		PluginInfo info = new PluginInfo();
		info.name = "Test Frame";
		info.vendorName = "Stefan Sechelmann";
		info.icon = ImageHook.getIcon("arrow_right_blue_round.png");
		return info;
	}

}
