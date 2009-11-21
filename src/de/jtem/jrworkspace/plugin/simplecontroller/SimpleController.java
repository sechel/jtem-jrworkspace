package de.jtem.jrworkspace.plugin.simplecontroller;

import static de.jtem.jrworkspace.plugin.simplecontroller.SimpleController.Status.Started;
import static de.jtem.jrworkspace.plugin.simplecontroller.SimpleController.Status.Starting;
import static de.jtem.jrworkspace.plugin.simplecontroller.image.ImageHook.setIconImage;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;
import static java.awt.BorderLayout.SOUTH;
import static java.awt.FlowLayout.LEADING;
import static java.lang.Runtime.getRuntime;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import static javax.swing.SwingUtilities.invokeAndWait;
import static javax.swing.SwingUtilities.invokeLater;
import static javax.swing.SwingUtilities.isEventDispatchThread;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GridLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;

import de.jtem.jrworkspace.plugin.Controller;
import de.jtem.jrworkspace.plugin.Plugin;
import de.jtem.jrworkspace.plugin.PluginNameComparator;
import de.jtem.jrworkspace.plugin.flavor.FrontendFlavor;
import de.jtem.jrworkspace.plugin.flavor.HelpFlavor;
import de.jtem.jrworkspace.plugin.flavor.MenuFlavor;
import de.jtem.jrworkspace.plugin.flavor.PerspectiveFlavor;
import de.jtem.jrworkspace.plugin.flavor.PreferencesFlavor;
import de.jtem.jrworkspace.plugin.flavor.PropertiesFlavor;
import de.jtem.jrworkspace.plugin.flavor.StatusFlavor;
import de.jtem.jrworkspace.plugin.flavor.ToolBarFlavor;
import de.jtem.jrworkspace.plugin.flavor.UIFlavor;
import de.jtem.jrworkspace.plugin.flavor.FrontendFlavor.FrontendListener;
import de.jtem.jrworkspace.plugin.flavor.HelpFlavor.HelpListener;
import de.jtem.jrworkspace.plugin.flavor.PropertiesFlavor.PropertiesListener;
import de.jtem.jrworkspace.plugin.flavor.StatusFlavor.StatusChangedListener;
import de.jtem.jrworkspace.plugin.simplecontroller.action.AboutAction;
import de.jtem.jrworkspace.plugin.simplecontroller.action.HelpWindowAction;
import de.jtem.jrworkspace.plugin.simplecontroller.action.PreferencesWindowAction;
import de.jtem.jrworkspace.plugin.simplecontroller.help.HelpWindow;
import de.jtem.jrworkspace.plugin.simplecontroller.image.ImageHook;
import de.jtem.jrworkspace.plugin.simplecontroller.preferences.PreferencesWindow;
import de.jtem.jrworkspace.plugin.simplecontroller.widget.AboutDialog;
import de.jtem.jrworkspace.plugin.simplecontroller.widget.WrappingLayout;


/**
 * A simple implementation of the {@link Controller} interface. 
 * It creates a JFrame if a plug-in  with {@link PerspectiveFlavor}
 * is registered.
 * First call the registerPlugin method to insert a plug-in. Then call
 * a startup method to initialize the application.
 * @author Stefan Sechelmann
 *
 */
public class SimpleController implements Controller {

	protected Set<Plugin>
		plugins = new TreeSet<Plugin>(new PluginNameComparator());
	protected Stack<Plugin>
		dependencyStack = new Stack<Plugin>();
	protected Set<Class<? extends Plugin>>
		installed = new HashSet<Class<? extends Plugin>>();
	protected HashMap<String, Object>
		properties = new HashMap<String, Object>();
	private boolean
		propertiesAreSafe = false;
	protected PerspectiveFlavor 
		perspective = null;
	protected FlavorListener
		flavorListener = new FlavorListener();
	protected JFrame
		mainWindow = null;
	protected Frame
		fullScreenFrame = null;
	protected HelpWindow
		helpWindow = null;
	protected HelpWindowAction	
		helpWindowAction = null;
	protected PreferencesWindow
		preferencesWindow = null;
	protected AboutDialog
		aboutDialog = null;
	protected JPanel 
		centerPanel = new JPanel(),
		toolBarPanel = new JPanel();
	protected JLabel
		statusLabel = new JLabel("Ready");
	protected boolean
		hasMainWindow = false,
		hasMenuBar = false,
		hasToolBar = false,
		hasStatusBar = false,
		hasHelpMenu = false,
		hasPreferencesMenu = false,
		localStartup = false,
		manageLookAndFeel = true;
	protected Status
		status = Status.PreStartup;

	protected static File
		defaultPropFile = null;
	protected ShutdownHook
		shutdownHook = new ShutdownHook();
	protected XStream 
		propertyxStream = new XStream(new PureJavaReflectionProvider());
	protected File 
		propFile = null;
	protected InputStream
		propInputStream = null;
	
	public static enum Status {
		PreStartup,
		Starting,
		Started
	}
	
	
	// get the default properties file
	static {
		try {
			String userHome = System.getProperty("user.home");
			File rcDir = new File(userHome + "/.jrworkspace");
			if (!rcDir.exists()) {
				rcDir.mkdirs();
			}
			defaultPropFile = new File(userHome + "/.jrworkspace/default_simple.xml");
		} catch (SecurityException se) {}
	}
	
	
	/**
	 * Construct a SimpleController which uses the properties file
	 * System.getProperty("user.home") + "/.jrworkspace/default_simple.xml
	 * which will be written on shutdown.
	 */
	public SimpleController() {
		setPropertiesFile(defaultPropFile);
		getRuntime().addShutdownHook(shutdownHook);
	}
	
	/**
	 * Construct a SimpleController and uses the given properties file
	 * @param propertiesFile the properties file
	 */
	public SimpleController(File propertiesFile) {
		setPropertiesFile(propertiesFile);
		getRuntime().addShutdownHook(shutdownHook);
	}
	
	/**
	 * Uses the given stream as property source. Use this constructor 
	 * for webstart applications
	 * @param propsInput The stream to read the properties
	 */
	public SimpleController(InputStream propsInput) {
		setPropertiesInputStream(propsInput);
		getRuntime().addShutdownHook(shutdownHook);
	}
	
	/**
	 * Registers a plug-in with this SimpleController
	 * @param p the plug-in to register
	 */
	public void registerPlugin(Plugin p) {
		plugins.add(p);
	}
	
	/**
	 * Installs all registered plug-ins and opens the main window if there
	 * is a plug-in implementing {@link PerspectiveFlavor}
	 */
	public void startup() {
		status = Status.Starting;
		loadProperties();
		Runnable r = new Runnable() {
			public void run() {
				initializeComponents();
				for (Plugin p : new LinkedList<Plugin>(plugins)) {
					activatePlugin(p);
				}
				status = Status.Started;
				if (hasToolBar || hasMenuBar) {
					if (perspective == null) {
						System.err.println("SimpleController: No main perspective flavor found. Exit.");
						System.exit(-1);
					}
				}
				if (mainWindow != null) {
					if (!localStartup) {
						mainWindow.setSize(perspective.getCenterComponent().getPreferredSize());
						mainWindow.setVisible(true);
					}
					if (manageLookAndFeel) {
						try {
							String defaultLnF = "cross_platform_lnf_classname";
							String lnfClass = getProperty(SimpleController.class, "lookAndFeelClass", defaultLnF);
							flavorListener.installLookAndFeel(lnfClass);
							flavorListener.updateFrontendUI();
						} catch (Exception e) {
							System.err.println("Could not load look and feel.");
						}
					}
				}
			}
		};
		try {
			if (isEventDispatchThread()) {
				invokeLater(r);
			} else {
				invokeAndWait(r);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Starts this SimpleController but does not open the main window
	 * instead it returns the content panel of this window that can be used 
	 * outside of the controller.
	 * @return The content pane of the main window if there is one 
	 * an empty Container else
	 */
	public JRootPane startupLocal() {
		localStartup = true;
		startup();
		if (mainWindow != null) {
			return mainWindow.getRootPane();
		} else {
			return new JRootPane();
		}
	}
	
	
	
	protected void initializeComponents() {
		for (Plugin p : plugins) {
			if (p instanceof PerspectiveFlavor) { 
				perspective = (PerspectiveFlavor)p;
				hasMainWindow = true;
			}
			if (p instanceof HelpFlavor) {
				hasHelpMenu = true;
				hasMenuBar = true;
				hasMainWindow = true;
			}
			if (p instanceof PreferencesFlavor) {
				hasPreferencesMenu = true;
				hasMenuBar = true;
				hasMainWindow = true;
			}
			if (p instanceof ToolBarFlavor) {
				hasToolBar = true;
				hasMainWindow = true;
			}
			if (p instanceof MenuFlavor) {
				hasMenuBar = true;
				hasMainWindow = true;
			}
			if (p instanceof StatusFlavor) {
				hasStatusBar = true;
				hasMainWindow = true;
			}
		}
		if (hasMainWindow && mainWindow == null) {
			mainWindow = new JFrame();
			mainWindow.setDefaultCloseOperation(EXIT_ON_CLOSE);
			mainWindow.setLayout(new BorderLayout());
			mainWindow.add(centerPanel, CENTER);
		}
		if (hasHelpMenu && helpWindow == null) {
			helpWindow = new HelpWindow(mainWindow);
			helpWindow.setLocationByPlatform(true);
			helpWindowAction = new HelpWindowAction(helpWindow);
		}
		if (hasPreferencesMenu && preferencesWindow == null) {
			preferencesWindow = new PreferencesWindow(mainWindow);
			preferencesWindow.setLocationByPlatform(true);
		}
		if (hasToolBar && mainWindow != null) {
			toolBarPanel.setLayout(new WrappingLayout(LEADING, 0, 0));
			mainWindow.remove(toolBarPanel);
			mainWindow.add(toolBarPanel, NORTH);
		}
		if (hasStatusBar && mainWindow != null) {
			mainWindow.remove(statusLabel);
			mainWindow.add(statusLabel, SOUTH);				
		}
	}

	
	protected void activatePlugin(Plugin p) {
		if (isActive(p)) {
			return;
		} else {
			installed.add(p.getClass());
		}
		dependencyStack.push(p);
		try {
			p.restoreStates(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (p instanceof FrontendFlavor) {
			((FrontendFlavor)p).setFrontendListener(flavorListener);
		}
		if (p instanceof StatusFlavor) {
			((StatusFlavor)p).setStatusListener(flavorListener);
		}
		if (p instanceof HelpFlavor) {
			((HelpFlavor)p).setHelpListener(flavorListener);
		}
		if (p instanceof PropertiesFlavor) {
			((PropertiesFlavor)p).setPropertiesListener(flavorListener);
		}
		try {
			p.install(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		dependencyStack.pop();
		if (p instanceof PerspectiveFlavor) { 
			updateMainWindow();
		}
		if (p instanceof HelpFlavor) {
			HelpFlavor hf = (HelpFlavor)p;
			helpWindow.addHelpPlugin(hf);
			updateMenuBarInternal();
		}
		if (p instanceof PreferencesFlavor) {
			preferencesWindow.addPreferencesPlugin((PreferencesFlavor)p);
			updateMenuBarInternal();
		}
		if (p instanceof ToolBarFlavor) {
			updateToolBar();
		}
		if (p instanceof MenuFlavor) {
			updateMenuBarInternal();
		}
	}

	
	public <T extends Plugin> T getPlugin(Class<T> clazz) {
		for (Plugin p : new LinkedList<Plugin>(plugins)) {
			if (p.getClass().equals(clazz)) {
				if (status == Starting && !isActive(p)) {
					try {
						activatePlugin(p);
					} catch (Exception e) {
						System.err.println("could not install dependent plug-in: " + p);
					}
				}
				return clazz.cast(p);
			}
		}
		if (status == Starting) {
			Plugin p = null;
			try {
				p = clazz.newInstance();
			} catch (Exception e) {
				System.err.println("could not instantiate dependent plug-in: " + clazz.getSimpleName() + ": " + e.getClass().getSimpleName());
				return null;
			}
			registerPlugin(p);
			initializeComponents();
			try {
				activatePlugin(p);
			} catch (Exception e) {
				System.err.println("could not install dependent plug-in: " + p + ": " + e.getClass().getSimpleName());
			}
			return clazz.cast(p);
		}
		throw new RuntimeException("SimpleController: plug-in " + clazz.getSimpleName() + " not found.");
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> getPlugins(Class<T> clazz) {
		List<Class<T>> classList = new LinkedList<Class<T>>();
		for (Plugin p : plugins) {
			if (clazz.isAssignableFrom(p.getClass())) {
				classList.add(clazz.getClass().cast(p.getClass()));
			}
		}	
		List<T> plugins = new LinkedList<T>();
		for (Class<T> c : classList) {
			plugins.add((T)getPlugin((Class<? extends Plugin>)c));
		}
		return plugins;
	}

	
	@SuppressWarnings("unchecked")
	public <T> T getProperty(Class<?> context, String key, T defaultValue) {
		T value = (T)properties.get(context.getName() + ":" + key);
		if (value == null) {
			value = defaultValue;
			storeProperty(context, key, value);
		}
		return value;
	}

	public Object storeProperty(Class<?> context, String key, Object property) {
		return properties.put(context.getName() + ":" + key, property);
	}
	
	
	@SuppressWarnings("unchecked")
	public <T> T deleteProperty(Class<?> context, String key) {
		return (T)properties.remove(context.getName() + ":" + key);
	}
	
	
	public boolean isActive(Plugin p) {
		return installed.contains(p.getClass());
	}
	
	
	protected void updateMainWindow() {
		if (!hasMainWindow) {
			return;
		}
		centerPanel.removeAll();
		centerPanel.setLayout(new GridLayout());
		if (perspective.getCenterComponent() == null) {
			return;
		}
		centerPanel.add(perspective.getCenterComponent());
		mainWindow.setTitle(perspective.getTitle());
		mainWindow.setMinimumSize(mainWindow.getRootPane().getMinimumSize());
		mainWindow.setIconImage(ImageHook.renderIcon(perspective.getIcon()));
	}
	
	
	protected void updateMenuBarInternal() {
		if (!hasMenuBar) {
			return;
		}
		List<MenuFlavor> menuList = new LinkedList<MenuFlavor>();
		for (Plugin mp : plugins) {
			if (mp instanceof MenuFlavor) {
				MenuFlavor mf = (MenuFlavor)mp;
				if (mf.getPerspective().equals(perspective.getClass())) {
					menuList.add(mf);
				}
			}
		}
		Collections.sort(menuList, new MenuFlavorComparator());
		JMenuBar mBar = new JMenuBar();
		for (MenuFlavor mf : menuList) {
			for (JMenu m : mf.getMenus()) {
				mBar.add(m);
			}
		}
		JMenu helpMenu = new JMenu("Help");
		helpMenu.setMnemonic('H');
		if (hasPreferencesMenu) {
			helpMenu.add(new PreferencesWindowAction(preferencesWindow));
		}
		if (hasHelpMenu) {
			helpMenu.add(helpWindowAction);
		}
		if (aboutDialog == null) {
			aboutDialog = new AboutDialog(mainWindow, true);
		}
		helpMenu.add(new JPopupMenu.Separator());
		helpMenu.add(new AboutAction(aboutDialog));
		mBar.add(helpMenu);
		mainWindow.setJMenuBar(mBar);
	}
	
	
	protected void updateToolBar() {
		if (!hasToolBar) {
			return;
		}
		toolBarPanel.removeAll();
		List<ToolBarFlavor> toolList = new LinkedList<ToolBarFlavor>();
		for (Plugin p : plugins) {
			if (isActive(p) && p instanceof ToolBarFlavor) {
				ToolBarFlavor tbf = (ToolBarFlavor)p;
				if (tbf.getPerspective().equals(perspective.getClass())) {
					toolList.add(tbf);
				}
			}
		}
		Collections.sort(toolList, new ToolBarFlavorComparator());
		for (ToolBarFlavor tbf : toolList) {
			toolBarPanel.add(tbf.getToolBarComponent());
		}
		mainWindow.doLayout();
	}
	

	
	/**
	 * The methods in this class are intended to be invoked
	 * when the respective controller is started. The behavior
	 * is undefined if the controller is not started.
	 * 
	 * @author Stefan Sechelmann
	 */ 
	protected class FlavorListener implements StatusChangedListener, FrontendListener, HelpListener, PropertiesListener {

		/**
		 * Notifies this controller that the status 
		 * string should be changed
		 * @param status the new status string
		 */
		public void statusChanged(String status) {
			if (statusLabel != null) {
				statusLabel.setText(status);
				statusLabel.repaint();
			}
			System.out.println("\t" + status);			
		}

		/**
		 * Completely rebuilds the gui of this controller
		 */
		public void updateFrontend() {
			if (mainWindow != null) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						updateMainWindow();
						updateMenuBar();
						mainWindow.doLayout();	
						if (preferencesWindow != null) {
							preferencesWindow.doLayout();
						}
						if (helpWindow != null) {
							helpWindow.doLayout();
						}
					}
				});
			}
		}
		
		
		/**
		 * Updates the content gui of this controller
		 */
		public void updateContent() {
			if (mainWindow != null) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						updateMainWindow();
						mainWindow.doLayout();
						if (fullScreenFrame != null) {
							fullScreenFrame.doLayout();
						}
					}
				});
			}
		}

		/**
		 * Updates the menu bar of this controller
		 */
		public void updateMenuBar() {
			if (mainWindow != null) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						SimpleController.this.updateMenuBarInternal();
						mainWindow.doLayout();
						if (fullScreenFrame != null) {
							fullScreenFrame.doLayout();
						}
					}
				});
			}
		}
		
		/**
		 * Updates the frontends UI
		 */
		public void updateFrontendUI() {
			if (status == Started && mainWindow != null && mainWindow.isShowing()) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						SwingUtilities.updateComponentTreeUI(mainWindow);
						if (fullScreenFrame != null) {
							SwingUtilities.updateComponentTreeUI(fullScreenFrame);
						}
						if (preferencesWindow != null) {
							SwingUtilities.updateComponentTreeUI(preferencesWindow);
						}
						if (helpWindow != null) {
							SwingUtilities.updateComponentTreeUI(helpWindow);
						}
					}
				});
			}
		}
		
		/**
		 * Installs a new look and feel on this controllers gui
		 * @param lnfClassName the class name of the lnf class
		 */
		public void installLookAndFeel(String lnfClassName) {
			if (lnfClassName.equals("system_lnf_classname")) {
				lnfClassName = UIManager.getSystemLookAndFeelClassName();
			}
			if (lnfClassName.equals("cross_platform_lnf_classname")) {
				lnfClassName = UIManager.getCrossPlatformLookAndFeelClassName();
			}
			try { 
				UIManager.setLookAndFeel(lnfClassName);
			} catch (Exception e) {
				System.err.println("Cannot install look and feel: " + lnfClassName);
			}
			// notify UIFlavors
			for (Plugin p : plugins) {
				if (p instanceof UIFlavor) {
					((UIFlavor)p).mainUIChanged(lnfClassName);
				}
			}
		}
		
		
		/**
		 * Activates the full-screen mode of this controller
		 * @param fs 
		 */
		public void setFullscreen(boolean fs) {
			if (mainWindow == null) {
				return;
			}
			GraphicsDevice gd = mainWindow.getGraphicsConfiguration().getDevice();
			if (!gd.isFullScreenSupported()) {
				return;
			}
			if (fs) {
				Container content = mainWindow.getContentPane();
				JMenuBar menuBar = mainWindow.getJMenuBar();
				mainWindow.setVisible(false);
				fullScreenFrame = new Frame(mainWindow.getTitle());
				setIconImage(fullScreenFrame, mainWindow.getIconImage());
				fullScreenFrame.setUndecorated(true);
				fullScreenFrame.setLayout(new BorderLayout());
				fullScreenFrame.add(content, CENTER);
				if (menuBar != null) {
					menuBar.setPreferredSize(new Dimension(10, 0));
					fullScreenFrame.add(menuBar, NORTH);
				}
				mainWindow.dispose();
				gd.setFullScreenWindow(fullScreenFrame);
			} else {
				Component[] c = fullScreenFrame.getComponents();
				mainWindow.setContentPane((Container)c[0]);
				if (c.length > 1) {
					mainWindow.setJMenuBar((JMenuBar)c[1]);
				}
				gd.setFullScreenWindow(null);
				fullScreenFrame.dispose();
				mainWindow.setVisible(true);
				mainWindow.requestFocus();
			}
		}
	
		/**
		 * Check whether the window of the controller is in 
		 * full-screen mode
		 */
		public boolean isFullscreen() {
			return fullScreenFrame != null && fullScreenFrame.isShowing();
		}
		
		
		/**
		 * Set the menu bars visibility
		 * @param show
		 */
		public void setShowMenuBar(final boolean show) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					if (mainWindow.getJMenuBar() != null) {
						Dimension d = show ? null : new Dimension(10, 0);
						mainWindow.getJMenuBar().setPreferredSize(d);
						mainWindow.getJMenuBar().revalidate();
					}
				}
			});
		}
		
		/**
		 * Sets the tool bars visibility
		 * @param show
		 */
		public void setShowToolBar(final boolean show) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					toolBarPanel.setVisible(show);
				}
			});
		}
		
		/**
		 * Sets the status bars visibility
		 * @param show
		 */
		public void setShowStatusBar(final boolean show) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					statusLabel.setVisible(show);
				}
			});
		}
		
		/**
		 * Displays the help page of the given plug-in
		 * @param hf the help flavor plug-in to show 
		 */
		public void showHelpPage(HelpFlavor hf) {
			if (helpWindow != null) {
				helpWindow.activateHelpPage(hf);
				helpWindowAction.actionPerformed(null);
			}
		}

		@SuppressWarnings("unchecked")
		public void readProperties(Reader r) {
			try {
				properties = (HashMap<String, Object>) propertyxStream.fromXML(r);
			} catch (Exception e) {
				System.out.println("Could not read properties: " + e.getLocalizedMessage());
				return;
			}
			for (Plugin p : plugins) {
				try {
					p.restoreStates(SimpleController.this);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		public void writeProperties(Writer w) {
			for (Plugin p : plugins) {
				try {
					p.storeStates(SimpleController.this);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			propertyxStream.toXML(properties, w);
		}
		
		public void loadDefaultProperties() {
			properties = new HashMap<String, Object>();
			for (Plugin p : plugins) {
				try {
					p.restoreStates(SimpleController.this);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}
	
	
	protected class MenuFlavorComparator implements Comparator<MenuFlavor> {
		public int compare(MenuFlavor o1, MenuFlavor o2) {
			return o1.getPriority() < o2.getPriority() ? -1 : 1;
		}
	}
	
	protected class ToolBarFlavorComparator implements Comparator<ToolBarFlavor> {
		public int compare(ToolBarFlavor o1, ToolBarFlavor o2) {
			return o1.getToolBarPriority() < o2.getToolBarPriority() ? -1 : 1;
		}
	}
	
	
	@SuppressWarnings("unchecked")
	protected void loadProperties() {
		if (propFile == null && propInputStream == null) {
			return;
		}
		propertiesAreSafe = true;
		try {
			InputStream in = null;
			if (propInputStream != null) {
				in = propInputStream;
			} else {
				in = new FileInputStream(propFile);
			}
			properties = properties.getClass().cast(propertyxStream.fromXML(in));
		} catch (IOException e) {
//			System.out.println("could not load properties file " + propFile + ": " + e.getMessage());
		} catch (Exception e) {
			System.out.println("error while loading properties file " + propFile + ": " + e.getMessage());
			propertiesAreSafe = false;
		}
	}
	
	
	protected class ShutdownHook extends Thread {
		
		@Override
		public void run() {
			if (!propertiesAreSafe) return;
			for (Plugin p : plugins) {
				try {
					p.storeStates(SimpleController.this);
				} catch (Exception e) {
					System.out.println("could not store states of plugin " + p + ": " + e.getMessage());
				}
			}
			if (propFile == null) {
				return;
			}
			try {
				OutputStream out = new FileOutputStream(propFile);
				propertyxStream.toXML(properties, out);
				out.close();
			} catch (IOException e) {
				System.out.println("could not write properties file " + propFile + ": " + e.getMessage());
			}
		}
		
	}
	
	
	/**
	 * The SimpleController manages the swing look and feel if this flag is set
	 * @param manageLookAndFeel
	 */
	public void setManageLookAndFeel(boolean manageLookAndFeel) {
		this.manageLookAndFeel = manageLookAndFeel;
	}
	
	
	/**
	 * Hides or reveals this controllers menu bar
	 * @param show a flag
	 */
	public void setShowMenuBar(boolean show) {
		if (status != Started) throw new UnsupportedOperationException("Method invocation not supported before startup.");
		flavorListener.setShowMenuBar(show);
	}
	
	
	/**
	 * Hides or reveals this controllers tool bar
	 * @param show a flag
	 */
	public void setShowToolBar(boolean show) {
		if (status != Started) throw new UnsupportedOperationException("Method invocation not supported before startup.");
		flavorListener.setShowToolBar(show);	
	}
	
	/**
	 * Hides or reveals this controllers status bar
	 * @param show a flag
	 */
	public void setShowStatusBar(boolean show) {
		if (status != Started) throw new UnsupportedOperationException("Method invocation not supported before startup.");
		flavorListener.setShowStatusBar(show);
	}
	
	
	/**
	 * If there is a main window it's full-screen mode
	 * can be changed with this method
	 * @param fs
	 */
	public void setFullscreen(final boolean fs) {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					flavorListener.setFullscreen(fs);				
				}
			});
		} catch (Exception e) { }
	}
	
	/**
	 * Sets the properties File of this SimpleController. If a
	 * properties InputStream was set before this is set to null.
	 * @param propertiesFile A file or null
	 */
	public void setPropertiesFile(File propertiesFile) {
		this.propFile = propertiesFile;
		propInputStream = null;
	}
	
	/**
	 * Sets the properties InputStream of this SimpleController. If a 
	 * properties File was set before this is set to null.
	 * @param in An InputStream or null
	 */
	public void setPropertiesInputStream(InputStream in) {
		this.propInputStream = in;
		propFile = null;
	}
	
	
	/**
	 * Sets the visibility state of the preferences windows if there is one
	 * @param show
	 */
	public void setShowPreferencesWindow(boolean show) {
		if (preferencesWindow == null) {
			return;
		}
		if (preferencesWindow.isShowing()) {
			preferencesWindow.toFront();
			return;
		}
		preferencesWindow.setLocationByPlatform(true);
		preferencesWindow.setLocationRelativeTo(preferencesWindow.getParent());
		preferencesWindow.updateData();
		preferencesWindow.setVisible(true);
	}
	
}
