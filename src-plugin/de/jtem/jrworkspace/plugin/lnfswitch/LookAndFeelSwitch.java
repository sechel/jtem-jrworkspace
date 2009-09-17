package de.jtem.jrworkspace.plugin.lnfswitch;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import de.jtem.jrworkspace.plugin.Controller;
import de.jtem.jrworkspace.plugin.Plugin;
import de.jtem.jrworkspace.plugin.PluginInfo;
import de.jtem.jrworkspace.plugin.PluginNameComparator;
import de.jtem.jrworkspace.plugin.flavor.FrontendFlavor;
import de.jtem.jrworkspace.plugin.flavor.PreferencesFlavor;
import de.jtem.jrworkspace.plugin.lnfswitch.image.ImageHook;

public class LookAndFeelSwitch extends Plugin implements PreferencesFlavor, FrontendFlavor, ActionListener {

	private OptionsPanel
		optionsPanel = null;
	private int
		activeIndex = -1;
	private List<LookAndFeelPlugin>
		lookAndFeelList = new LinkedList<LookAndFeelPlugin>();
	private FrontendListener
		frontendListener = null;
	private Controller
		controller = null;

	private JComboBox
		lnfComboBox = new JComboBox();
	private JPanel
		lnfOptionsPanel = new JPanel();
	
	public LookAndFeelSwitch() {
		lnfComboBox.addActionListener(this);
	}
	
	
	public void addLookAndFeel(LookAndFeelPlugin lnf) {
		lookAndFeelList.add(lnf);
		updateFrontend();
	}
	
	
	public void removeLookAndFeel(LookAndFeelPlugin lnf) {
		lookAndFeelList.remove(lnf);
		updateFrontend();
	}
	
	
	public void updateLookAndFeel() {
		Object item = lnfComboBox.getSelectedItem();
		if (item instanceof LookAndFeelPlugin) {
			LookAndFeelPlugin lnf = (LookAndFeelPlugin)item;
			activeIndex = lnfComboBox.getSelectedIndex();
			SwitchWorkspaceThread switcher = new SwitchWorkspaceThread(lnf);
			SwingUtilities.invokeLater(switcher);
		}
	}
	
	
	private void updateFrontend() {
		lnfComboBox.removeActionListener(this);
		Collections.sort(lookAndFeelList, new PluginNameComparator());
		lnfComboBox.removeAllItems();
		for (LookAndFeelPlugin lnf : lookAndFeelList) {
			lnfComboBox.addItem(lnf);
		}
		lnfOptionsPanel.removeAll();
		if (activeIndex < lookAndFeelList.size()) {
			lnfComboBox.setSelectedIndex(activeIndex);
			Object lnfObject = lnfComboBox.getSelectedItem();
			if (lnfObject != null) {
				LookAndFeelPlugin lnf = (LookAndFeelPlugin)lnfObject;
				if (lnf.getOptionPanel() != null) {
					lnfOptionsPanel.add(lnf.getOptionPanel());
				}
			}
		}
		lnfOptionsPanel.doLayout();
		lnfComboBox.addActionListener(this);
	}
	
	
	public void setFrontendListener(FrontendListener l) {
		this.frontendListener = l;
	}
	
	public OptionsPanel getOptionsPanel() {
		if (optionsPanel == null) {
			optionsPanel = new OptionsPanel();
		}
		return optionsPanel;
	}
	
	@Override
	public void install(Controller c) throws Exception {
		this.controller = c;
	}
	
	
	@Override
	public void uninstall(Controller c) throws Exception {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			controller.storeProperty(controller.getClass(), "lookAndFeelClass", UIManager.getSystemLookAndFeelClassName());
			frontendListener.updateFrontendUI();
		} catch (Exception e1) {
			System.err.println("Could not uninstell LookAndFeelSwitch");
		}
	}

	
	
	@Override
	public void storeStates(Controller c) throws Exception {
		super.storeStates(c);
		c.storeProperty(getClass(), "activeLookAndFeel", activeIndex);
	}
	
	
	@Override
	public void restoreStates(Controller c) throws Exception {
		super.restoreStates(c);
		activeIndex = c.getProperty(getClass(), "activeLookAndFeel", 0);
	}
	
	public int getNumSubPages() {
		return 0;
	}

	public String getMainName() {
		return "Look And Feel";
	}

	public JPanel getMainPage() {
		return getOptionsPanel();
	}

	public JPanel getSubPage(int i) {
		return null;
	}

	public String getSubPageName(int i) {
		return null;
	}

	@Override
	public PluginInfo getPluginInfo() {
		PluginInfo info = new PluginInfo();
		info.name = "Look And Feel Switch";
		info.vendorName = "Stefan Sechelmann";
		info.icon = ImageHook.getIcon("pictures.png");
		return info;
	}

	
	public void actionPerformed(ActionEvent e) {
		updateLookAndFeel();
		updateFrontend();
	}
	

	private class SwitchWorkspaceThread extends Thread {
		
		private LookAndFeelPlugin 
			lnf = null;
		
		SwitchWorkspaceThread(LookAndFeelPlugin lnf) {
			this.lnf = lnf;
		}
		
		@Override
		public void run() {
			String lnfClassName = lnf.getLnFClassName();
			controller.storeProperty(controller.getClass(), "lookAndFeelClass", lnfClassName);
			try {
				frontendListener.installLookAndFeel(lnfClassName);
				frontendListener.updateFrontendUI();
			} catch (Exception e1) {
				System.err.println("Could not apply the Look And Feel");
			}
		}
		
	}
	
	
	private class OptionsPanel extends JPanel {

		private static final long 
			serialVersionUID = 1L;
		
		public OptionsPanel() {
			makeLayout();
		}

		private void makeLayout() {
			setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.insets = new Insets(2,2,2,2);
			c.fill = GridBagConstraints.BOTH;
			c.anchor = GridBagConstraints.CENTER;
			c.gridwidth = GridBagConstraints.REMAINDER;
			c.weightx = 0.0;
			add(lnfComboBox, c);
			lnfOptionsPanel.setBorder(BorderFactory.createTitledBorder("Options"));
			c.weightx = 1.0;
			lnfOptionsPanel.setLayout(new GridLayout());
			add(lnfOptionsPanel, c);
		}

	}

	public Icon getSubPageIcon(int i) {
		return null;
	}

	public Icon getMainIcon() {
		return getPluginInfo().icon;
	}
	
	
}
