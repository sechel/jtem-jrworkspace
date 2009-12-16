package de.jtem.jrworkspace.plugin.sidecontainer.template;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.SwingUtilities;

import de.jtem.jrworkspace.plugin.Controller;
import de.jtem.jrworkspace.plugin.Plugin;
import de.jtem.jrworkspace.plugin.PluginInfo;
import de.jtem.jrworkspace.plugin.flavor.HelpFlavor;
import de.jtem.jrworkspace.plugin.flavor.UIFlavor;
import de.jtem.jrworkspace.plugin.sidecontainer.SideContainerPerspective;
import de.jtem.jrworkspace.plugin.sidecontainer.image.ImageHook;
import de.jtem.jrworkspace.plugin.sidecontainer.widget.ShrinkPanel;
import de.jtem.jrworkspace.plugin.sidecontainer.widget.ShrinkPanel.HelpCalledListener;

/** Extend this class to get a shrink panel {@link Plugin} which will show up in the {@link SideContainerPerspective} 
 * returned by your implementation of {@link #getPerspectivePluginClass()}.
 * 
 * <h4>PluginInfo<h4>
 * It is strongly recommended that you override {@link #getPluginInfo()} to return a 
 * descriptive {@link PluginInfo}. This method is called in the constructor of this class and must not
 * return <code>null</code>.
 * 
 * <h4>Attach online help</h4> 
 * If a file "<code>clazz.getSimpleName()</code>.html" is found as a resource of <code>clazz</code>, then this is
 * attached as the help file of this plugin, where <code>clazz</code> is the top level 
 * enclosing class of the runtime class of this object. Note that in Eclipse you probably need to 
 * remove *.html from "Filtered resources".
 * 
 * <p>To turn this off override {@link #getHelpDocument()}, {@link #getHelpPath()}, and {@link #getHelpHandle()}.
 *
 */
public abstract class ShrinkPanelPlugin extends Plugin implements UIFlavor, HelpFlavor, HelpCalledListener {

	private SideContainerPerspective
		perspective = null;
	protected ShrinkPanel 
		shrinkPanel = null;
	private boolean
		showPanel = true;
	private JCheckBoxMenuItem
		menuItem = null;
	private int 
		initPosition = SHRINKER_LEFT;
	private Controller
		c = null;
	private static Icon
		defaultIcon = ImageHook.getIcon("menu.png"); 
	private HelpListener
		helpListener = null;
	
	private String helpDocument;
	private String helpPath;
	private Class<?> helpHandle;
	private boolean helpResourceChecked=false;	
	
	public static final int
		SHRINKER_LEFT = 1,
		SHRINKER_RIGHT = 2,
		SHRINKER_BOTTOM = 3,
		SHRINKER_TOP = 4,
		SHRINKER_DEFAULT = 5;


	public abstract Class<? extends SideContainerPerspective> getPerspectivePluginClass(); 

	public ShrinkPanelPlugin() {
		if (getPluginInfo() == null ) {
			throw new NullPointerException("PluginInfo must not be null.");
		}
		shrinkPanel = new ShrinkPanel(getPluginInfo().name);
		shrinkPanel.setShowHelpIcon(true);
		shrinkPanel.setHelpCalledListener(this);
		shrinkPanel.setIcon(getPluginInfo().icon);
		Icon smallIcon = null;
		if (getPluginInfo().icon != null) {
			smallIcon = ImageHook.scaleIcon(getPluginInfo().icon, 16, 16);
		}
		if (smallIcon == null) {
			smallIcon = defaultIcon;
		}
		menuItem = new JCheckBoxMenuItem(getPluginInfo().name, smallIcon);
		menuItem.addActionListener(new ShowPanelListener());
	}
	
	
	/**
	 * Set the initial position of this panel plug-in
	 * Possible values are SHRINKER_LEFT, SHRINKER_RIGHT, SHRINKER_BOTTOM, SHRINKER_TOP, SHRINKER_DEFAULT
	 * @param initPosition
	 */
	public void setInitialPosition(int initPosition) {
		this.initPosition = initPosition;
	}
	
	
	public void setShowPanel(boolean show) {
		showPanel = show;
		if (perspective == null) {
			return;
		}
		if (show) {
			switch (initPosition) {
			default:
			case SHRINKER_LEFT:
				perspective.getLeftSlot().addShrinkPanel(getShrinkPanel());
				break;
			case SHRINKER_RIGHT:
				perspective.getRightSlot().addShrinkPanel(getShrinkPanel());
				break;
			case SHRINKER_TOP:
				perspective.getUpperSlot().addShrinkPanel(getShrinkPanel());
				break;
			case SHRINKER_BOTTOM:
				perspective.getLowerSlot().addShrinkPanel(getShrinkPanel());
				break;
			}
		} else {
			if (getShrinkPanel().getParentSlot() != null) {
				initPosition = getPosition();
				if (getShrinkPanel().isFloating()) {
					getShrinkPanel().setFloating(false);
				}
				getShrinkPanel().getParentSlot().removeShrinkPanel(getShrinkPanel());
			}
		}
		if (menuItem != null) {
			menuItem.setSelected(show);
		}
	}
	
	
	private int getPosition() {
		SideContainerPerspective scp = c.getPlugin(getPerspectivePluginClass());
		if (getShrinkPanel().getParentSlot() == scp.getLeftSlot()) {
			return SHRINKER_LEFT;
		} else if (getShrinkPanel().getParentSlot() == scp.getRightSlot()) {
			return SHRINKER_RIGHT;
		} else if (getShrinkPanel().getParentSlot() == scp.getUpperSlot()) {
			return SHRINKER_TOP;
		} else if (getShrinkPanel().getParentSlot() == scp.getLowerSlot()) {
			return SHRINKER_BOTTOM;
		}
		return SHRINKER_DEFAULT;
	}
	
	
	public ShrinkPanel getShrinkPanel() {
		return shrinkPanel;
	}
	
	
	@Override
	public void install(Controller c) throws Exception {
		this.c = c;
		perspective = c.getPlugin(getPerspectivePluginClass());
		perspective.getPanelsMenu().add(menuItem);
		menuItem.setSelected(showPanel);
		
		setShowPanel(showPanel);
	}

	@Override
	public void restoreStates(Controller c) throws Exception {
		getShrinkPanel().setShrinked(c.getProperty(getClass(), "shrinked", getShrinkPanel().isShrinked()));
		showPanel = c.getProperty(getClass(), "showPanel", showPanel);
		initPosition = c.getProperty(getClass(), "initPosition", initPosition);
		getShrinkPanel().setPreferredPosition(c.getProperty(getClass(), "preferredPosition", 0));
		super.restoreStates(c);
	}

	@Override
	public void storeStates(Controller c) throws Exception {
		c.storeProperty(getClass(), "shrinked", getShrinkPanel().isShrinked());
		c.storeProperty(getClass(), "showPanel", showPanel);
		c.storeProperty(getClass(), "initPosition", getPosition());
		c.storeProperty(getClass(), "preferredPosition", getShrinkPanel().getPreferredPosition());
		super.storeStates(c);
	}

	@Override
	public void uninstall(Controller c) throws Exception {
		SideContainerPerspective scp = c.getPlugin(getPerspectivePluginClass());
		scp.getPanelsMenu().remove(menuItem);
		if (getShrinkPanel().isFloating()) {
			getShrinkPanel().setFloating(false);
		}
		if (getShrinkPanel().getParentSlot() != null) {
			getShrinkPanel().getParentSlot().removeShrinkPanel(getShrinkPanel());
		}
	}
	
	
	
	private class ShowPanelListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			getShrinkPanel().setShrinked(false);
			setShowPanel(menuItem.isSelected());
		}
		
	}
	
	public void mainUIChanged(String uiClass) {
		SwingUtilities.updateComponentTreeUI(getShrinkPanel());
	}
	
	
	public void helpCalled() {
		if (helpListener != null) {
			helpListener.showHelpPage(this);
		}
	}
	
	public String getHelpTitle() {
		return getPluginInfo().name;
	}
	
	public Icon getHelpIcon() {
		return getPluginInfo().icon;
	}
	
	public String getHelpDocument() {
		checkHelpResource();
		return helpDocument==null ? "default.html" : helpDocument;
	}

	public String getHelpPath() {
		checkHelpResource();
		return helpPath==null ? "help" : helpPath;
	}

	public Class<?> getHelpHandle() {
		checkHelpResource();
		return helpHandle==null ? ShrinkPanelPlugin.class : helpHandle;
	}
	
	
	private void checkHelpResource() {
		if (helpResourceChecked) return;
		Class<?> clazz = getClass();
		while (null != clazz.getEnclosingClass()) {
			clazz = clazz.getEnclosingClass();
		}
		String filename = clazz.getSimpleName()+".html";
		if (null!=clazz.getResource(filename)) {
			helpDocument=filename;
			helpPath="";
			helpHandle=clazz;
		}
		helpResourceChecked=true;
	}
	
	
	public String getHelpStyleSheet() {
		return null;
	}
	
	public void setHelpListener(HelpListener l) {
		helpListener = l;
	}
	
	public static Icon getDefaultIcon() {
		return defaultIcon;
	}
	
}
