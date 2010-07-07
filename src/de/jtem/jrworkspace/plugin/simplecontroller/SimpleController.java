/**
This file is part of a jTEM project.
All jTEM projects are licensed under the FreeBSD license 
or 2-clause BSD license (see http://www.opensource.org/licenses/bsd-license.php). 

Copyright (c) 2002-2009, Technische Universität Berlin, jTEM
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, 
are permitted provided that the following conditions are met:

-	Redistributions of source code must retain the above copyright notice, 
	this list of conditions and the following disclaimer.

-	Redistributions in binary form must reproduce the above copyright notice, 
	this list of conditions and the following disclaimer in the documentation 
	and/or other materials provided with the distribution.
 
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS 
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, 
OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT 
OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING 
IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY 
OF SUCH DAMAGE.
**/

package de.jtem.jrworkspace.plugin.simplecontroller;

import static de.jtem.jrworkspace.logging.LoggingSystem.LOGGER;
import static de.jtem.jrworkspace.plugin.simplecontroller.SimpleController.Status.Started;
import static de.jtem.jrworkspace.plugin.simplecontroller.SimpleController.Status.Starting;
import static de.jtem.jrworkspace.plugin.simplecontroller.image.ImageHook.setIconImage;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;
import static java.awt.BorderLayout.SOUTH;
import static java.awt.FlowLayout.LEADING;
import static javax.swing.SwingUtilities.invokeAndWait;
import static javax.swing.SwingUtilities.isEventDispatchThread;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;

import de.jtem.jrworkspace.plugin.Controller;
import de.jtem.jrworkspace.plugin.Plugin;
import de.jtem.jrworkspace.plugin.PluginNameComparator;
import de.jtem.jrworkspace.plugin.flavor.FrontendFlavor;
import de.jtem.jrworkspace.plugin.flavor.HelpFlavor;
import de.jtem.jrworkspace.plugin.flavor.MenuFlavor;
import de.jtem.jrworkspace.plugin.flavor.OpenAboutFlavor;
import de.jtem.jrworkspace.plugin.flavor.OpenPreferencesFlavor;
import de.jtem.jrworkspace.plugin.flavor.PerspectiveFlavor;
import de.jtem.jrworkspace.plugin.flavor.PreferencesFlavor;
import de.jtem.jrworkspace.plugin.flavor.PropertiesFlavor;
import de.jtem.jrworkspace.plugin.flavor.ShutdownFlavor;
import de.jtem.jrworkspace.plugin.flavor.StatusFlavor;
import de.jtem.jrworkspace.plugin.flavor.ToolBarFlavor;
import de.jtem.jrworkspace.plugin.flavor.UIFlavor;
import de.jtem.jrworkspace.plugin.flavor.FrontendFlavor.FrontendListener;
import de.jtem.jrworkspace.plugin.flavor.HelpFlavor.HelpListener;
import de.jtem.jrworkspace.plugin.flavor.OpenAboutFlavor.OpenAboutListener;
import de.jtem.jrworkspace.plugin.flavor.OpenPreferencesFlavor.OpenPreferencesListener;
import de.jtem.jrworkspace.plugin.flavor.PropertiesFlavor.PropertiesListener;
import de.jtem.jrworkspace.plugin.flavor.ShutdownFlavor.ShutdownListener;
import de.jtem.jrworkspace.plugin.flavor.StatusFlavor.StatusChangedListener;
import de.jtem.jrworkspace.plugin.simplecontroller.action.AboutAction;
import de.jtem.jrworkspace.plugin.simplecontroller.action.HelpWindowAction;
import de.jtem.jrworkspace.plugin.simplecontroller.action.PreferencesWindowAction;
import de.jtem.jrworkspace.plugin.simplecontroller.help.HelpWindow;
import de.jtem.jrworkspace.plugin.simplecontroller.image.ImageHook;
import de.jtem.jrworkspace.plugin.simplecontroller.preferences.PreferencesWindow;
import de.jtem.jrworkspace.plugin.simplecontroller.widget.AboutDialog;
import de.jtem.jrworkspace.plugin.simplecontroller.widget.SaveOnExitDialog;
import de.jtem.jrworkspace.plugin.simplecontroller.widget.WrappingLayout;


/**
 * A simple implementation of the {@link Controller} interface. 
 * It creates a JFrame if a plug-in  with {@link PerspectiveFlavor}
 * is registered. First call {@link #registerPlugin(Plugin)} to insert a plug-in. Then call
 * {@link #startup()} to initialize the application.
 * 
 * <h4>Plugin properties</h4>
 * The SimpleController allows {@link Plugin}s to read and save properties. These properties 
 * are read from and saved to a file 
 * at startup and shutdown via <a href="http://xstream.codehaus.org/">XStream</a>.
 * 
 * <p>When {@link #shutdown()} is called (from the main windows closing method or from a plugin that implements 
 * {@link ShutdownFlavor}) the user gets the chance to decide where the properties are saved. 
 * The decisions are saved
 * via the java preferences API (see {@link #setPropertiesResource(Class, String)}. If nothing
 * else is specified the <code>SimpleController</code> tries to read the plugin properties from
 * <pre> 
 * 		System.getProperty("user.home") + "/.jrworkspace/default_simple.xml"
 * </pre>
 * and saves the user decisions in the preferences node of the package of the SimpleController.
 *  
 * <p> Besides the name (and path) of the properties file the user may choose
 * <ul>
 * 	<li> whether to load properties from the file at startup (default: <code>true</code>), </li>
 *  <li> whether to save the properties file,</li>
 *  <li> whether to remember the users decisions, which disables the dialog next time (so the 
 *  user should get the chance to revise this decision via a plugin that implements {@link PropertiesFlavor}).</li>
 * </ul>
 * The user may cancel the dialog, which also cancels the shutdown process.
 *
 * <p>It is recommended that applications call
 * <pre>
 * 		setPropertiesResource(MyClass.class,"propertiesFileName")
 * </pre>
 * before calling {@link #startup()}. Then the controller loads the plugin properties from this resource. After deployment this 
 * resource may most likely only be opened for reading, which has the effect that it will only be used
 * to call {@link #setPropertiesInputStream(InputStream)} and the properties file will retain its default 
 * value or whatever it is set to via {@link #setPropertiesFile(File)}.
 * 
 * <p>When loading properties the availability of a properties file is checked in the following order
 * <ol>
 * <li> the user properties file (from the java preferences), when loadFromUserPropertyFile is <code>true</code></li>
 * <li> the propertiesInputStream</li>
 * <li> the propertiesFile</li>
 * </ol>
 * When saving properties the availability of a properties file for output is checked in the following order
 * <ol>
 * <li> the user properties file (from the java preferences)</li>
 * <li> the propertiesFile</li>
 * </ol>
 * The user is prompted when askBeforeSaveOnExit is <code>true</code> or both files above can't be opened for writing.
 * 
 * <p>Note to Eclipse developers: if you change  the path of the file to save the properties into
 * in the dialog at shutdown 
 * to point to the source folder  and DISABLE the load from this file check box, then the resource will be accessed
 * to load the properties (and the situation after deployment is always tested) and the source folder file 
 * is used to save (which then may be included in version control). Copying of the properties xml file to the bin folder
 * needs to be triggered manually by refreshing the source folder
 * 
 * 
 * @author Stefan Sechelmann
 * @author Paul Peters
 * @see Plugin
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
		registerSPIPlugins = true,
		manageLookAndFeel = true;
	protected Status
		status = Status.PreStartup;

	protected XStream 
		propertyxStream = new XStream(new PureJavaReflectionProvider());
	protected File 
		propFile = null;
	protected InputStream
		propInputStream = null;
	protected Preferences 
		userPreferences=null;
	protected final Thread shutdownHook = new ShutDownHook();
	
	protected static boolean 
		DEFAULT_SAVE_ON_EXIT=true,
		DEFAULT_ASK_BEFORE_SAVE_ON_EXIT=true,
		DEFAULT_LOAD_FROM_USER_PROPERTY_FILE=true;
	protected static String 
		DEFAULT_USER_PROPERTY_FILE=null;

	private boolean 
		saveOnExit=DEFAULT_SAVE_ON_EXIT,
		askBeforeSaveOnExit=DEFAULT_ASK_BEFORE_SAVE_ON_EXIT,
		loadFromUserPropertyFile=DEFAULT_LOAD_FROM_USER_PROPERTY_FILE;
	private String userPropertyFile=DEFAULT_USER_PROPERTY_FILE;
	
	public static enum Status {
		PreStartup,
		Starting,
		Started
	}
	
	protected class ShutDownHook extends Thread{
		public void run() {
			LOGGER.info("Unexpacted shutdown, no properties saved.");
		}
	};
	
	
	/**
	 * Construct a SimpleController. Initialize the properties file to 
	 * System.getProperty("user.home") + "/.jrworkspace/default_simple.xml".
	 * This will be written on {@link #shutdown()}.
	 */
	public SimpleController() {
		LOGGER.entering(SimpleController.class.getName(), "SimpleController");
		
		// get the default properties file path if the security manager allows
		String filename=null;
		try {
			String userHome = System.getProperty("user.home");
			filename = userHome + "/.jrworkspace/default_simple.xml";
		} catch (SecurityException se) {}
		setPropertiesFile(filename == null? null : new File(filename));
		
		//init with user preferences associated with the controllers class, may be overridden by package specific properties
		userPreferences = Preferences.userNodeForPackage(this.getClass());
		
		Runtime.getRuntime().addShutdownHook(shutdownHook);
		
		LOGGER.exiting(SimpleController.class.getName(), "SimpleController");
	}
	

	/**
	 * Registers a plug-in with this SimpleController
	 * @param p the plug-in to register
	 */
	public void registerPlugin(Plugin p) {
		plugins.add(p);
	}
	
	/**
	 * Installs all registered plug-ins, reads user preferences, restores plugin properties, and 
	 * opens the main window if there
	 * is a plug-in implementing {@link PerspectiveFlavor}
	 */
	public void startup() {
		LOGGER.entering(SimpleController.class.getName(), "startup");
		status = Status.Starting;
		registerSPIPlugins();
		readUserPreferences();
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
						LOGGER.severe("SimpleController: No main perspective flavor found. Exit.");
						System.exit(-1);
					}
				}
				if (mainWindow != null) {
					if (!localStartup) {
						mainWindow.setSize(perspective.getCenterComponent().getPreferredSize());
						mainWindow.setVisible(true);
						LOGGER.finer("mainWindow visible");
					}
					if (manageLookAndFeel) {
						try {
							String defaultLnF = "cross_platform_lnf_classname";
							String lnfClass = getProperty(SimpleController.class, "lookAndFeelClass", defaultLnF);
							flavorListener.installLookAndFeel(lnfClass);
							flavorListener.updateFrontendUI();
						} catch (Exception e) {
							LOGGER.config("Could not load look and feel.");
						}
					}
					SwingUtilities.updateComponentTreeUI(mainWindow);
				}
			}
		};
		try {
			if (isEventDispatchThread()) {
				LOGGER.finer("startup directly on the event dispatch thread");
				r.run();
			} else {
				LOGGER.finer("startup on the event dispatch thread via invoke and wait");
				invokeAndWait(r);
			}
		} catch (Exception e) {
			StringWriter stackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(stackTrace));
			LOGGER.severe(stackTrace.toString());
		}
		
		LOGGER.exiting(SimpleController.class.getName(), "startup");
	}

	
	/**
	 * Starts this SimpleController but does not open the main window
	 * instead it returns the content panel of this window that can be used 
	 * outside of the controller.
	 * @return The content pane of the main window if there is one 
	 * an empty Container else
	 */
	public JRootPane startupLocal() {
		LOGGER.entering(SimpleController.class.getName(), "startupLocal");
		
		localStartup = true;
		startup();
		if (mainWindow != null) {
			LOGGER.exiting(SimpleController.class.getName(), "startupLocal", mainWindow.getRootPane());
			return mainWindow.getRootPane();
		} else {
			LOGGER.exiting(SimpleController.class.getName(), "startupLocal", "a brand new JRootPane");
			return new JRootPane();
		}
		
	}
	
	
	@SuppressWarnings("unchecked")
	private void registerSPIPlugins() {
		if (!registerSPIPlugins) return;
		Class<?> slClass = null;
		try {
			slClass = Class.forName("java.util.ServiceLoader");
		} catch (ClassNotFoundException e) {
			return; // we are java 5 and have no service loader
		}
		try {
			Method loadMethod = slClass.getMethod("load", Class.class);
			Iterable<Plugin> loader = (Iterable<Plugin>)loadMethod.invoke(slClass, Plugin.class);
			for (Plugin p : loader) {
	           registerPlugin(p);
			}
		} catch (Exception e) {
			System.out.println("Error while loading SPI plugins: " + e.getLocalizedMessage());
		}
	}
	
	
	protected void initializeComponents() {
		LOGGER.entering(SimpleController.class.getName(), "initializeComponents");
		
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
			mainWindow.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			mainWindow.setLayout(new BorderLayout());
			mainWindow.add(centerPanel, CENTER);
			mainWindow.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					shutdown();
				}
			});
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
		
		LOGGER.exiting(SimpleController.class.getName(), "initializeComponents");
	}

	
	protected void activatePlugin(Plugin p) {
		LOGGER.entering(SimpleController.class.getName(), "activatePlugin", new Object[]{p});
		
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
		if (p instanceof ShutdownFlavor) {
			((ShutdownFlavor)p).setShutdownListener(flavorListener);
		}
		if (p instanceof OpenAboutFlavor) {
			((OpenAboutFlavor)p).setOpenAboutListener(flavorListener);
		}
		if (p instanceof OpenPreferencesFlavor) {
			((OpenPreferencesFlavor)p).setOpenPreferencesListener(flavorListener);
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
		
		LOGGER.exiting(SimpleController.class.getName(), "activatePlugin");
	}

	
	public <T extends Plugin> T getPlugin(Class<T> clazz) {
		for (Plugin p : new LinkedList<Plugin>(plugins)) {
			if (p.getClass().equals(clazz)) {
				if (status == Starting && !isActive(p)) {
					try {
						activatePlugin(p);
					} catch (Exception e) {
						LOGGER.info("could not install dependent plug-in: " + p);
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
				LOGGER.info("could not instantiate dependent plug-in: " + clazz.getSimpleName() + ": " + e.getClass().getSimpleName());
				return null;
			}
			registerPlugin(p);
			initializeComponents();
			try {
				activatePlugin(p);
			} catch (Exception e) {
				LOGGER.info("could not install dependent plug-in \"" + p + "\" exceptiion: " + e.getClass().getSimpleName());
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
		JMenuBar oldBar = mainWindow.getJMenuBar();
		if (oldBar != null) mBar.setPreferredSize(oldBar.getPreferredSize());
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
	protected class FlavorListener implements 
		StatusChangedListener, FrontendListener, HelpListener, PropertiesListener, ShutdownListener, OpenPreferencesListener, OpenAboutListener {

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
		
		public void setTitle(String title) {
			if (mainWindow != null) {
				mainWindow.setTitle(title);
			}
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

		public String getUserPropertyFile() {
			return SimpleController.this.getUserPropertyFile();
		}

		public boolean isAskBeforeSaveOnExit() {
			return SimpleController.this.isAskBeforeSaveOnExit();
		}

		public boolean isLoadFromUserPropertyFile() {
			return SimpleController.this.isLoadFromUserPropertyFile();
		}

		public boolean isSaveOnExit() {
			return SimpleController.this.isSaveOnExit();
		}

		public void setAskBeforeSaveOnExit(boolean askBeforeSaveOnExit) {
			SimpleController.this.setAskBeforeSaveOnExit(askBeforeSaveOnExit);
		}

		public void setLoadFromUserPropertyFile(boolean loadFromUserPropertyFile) {
			SimpleController.this.setLoadFromUserPropertyFile(loadFromUserPropertyFile);
		}

		public void setSaveOnExit(boolean saveOnExit) {
			SimpleController.this.setSaveOnExit(saveOnExit);
		}

		public void setUserPropertyFile(String userPropertyFile) {
			SimpleController.this.setUserPropertyFile(userPropertyFile);
		}

		public void shutdown() {
			SimpleController.this.shutdown();
		}

		public void openAboutWindow() {
			aboutDialog.showWindow();
		}

		public void openPreferencesWindow() {
			preferencesWindow.showWindow();
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
	

	protected void readUserPreferences() {
		LOGGER.entering(SimpleController.class.getName(), "readUserPreferences");

		if (userPreferences == null) return;
		
		saveOnExit = userPreferences.getBoolean("saveOnExit",DEFAULT_SAVE_ON_EXIT);
		askBeforeSaveOnExit = userPreferences.getBoolean("askBeforeSaveOnExit",DEFAULT_ASK_BEFORE_SAVE_ON_EXIT);
		loadFromUserPropertyFile = userPreferences.getBoolean("loadFromUserPropertyFile",DEFAULT_LOAD_FROM_USER_PROPERTY_FILE) ;
		userPropertyFile=userPreferences.get("userPropertyFile", DEFAULT_USER_PROPERTY_FILE);

		LOGGER.finer("saveOnExit: " + saveOnExit);
		LOGGER.finer("askBeforeSaveOnExit: " + askBeforeSaveOnExit);
		LOGGER.finer("loadFromUserPropertyFile: " + loadFromUserPropertyFile);
		LOGGER.finer("userPropertyFile: " + userPropertyFile);
		LOGGER.exiting(SimpleController.class.getName(), "readUserPreferences");
	}
	
	
	protected void writeUserPreferences() {
		LOGGER.entering(SimpleController.class.getName(), "writeUserPreferences");
		
		if (userPreferences == null) return;
		
		userPreferences.putBoolean("saveOnExit",saveOnExit);
		userPreferences.putBoolean("askBeforeSaveOnExit",askBeforeSaveOnExit);
		userPreferences.putBoolean("loadFromUserPropertyFile",loadFromUserPropertyFile);
		userPreferences.put("userPropertyFile", userPropertyFile==null ? "" : userPropertyFile);
		
		LOGGER.exiting(SimpleController.class.getName(), "writeUserPreferences");
	}
	
	@SuppressWarnings("unchecked")
	protected void loadProperties() {
		LOGGER.entering(SimpleController.class.getName(), "loadProperties");
		
		propertiesAreSafe = true;

		InputStream in = null;
		if (loadFromUserPropertyFile) {
			try {
				in = new FileInputStream(userPropertyFile);
				LOGGER.finer("userPropertyFile \"" + userPropertyFile + "\" used to set input stream");
			} catch (Exception e) {
				// just fall through
			}
		}
		if (in == null) {
			in = propInputStream;
			LOGGER.finer("property input stream used as input stream");
		}
		
		try {
			if (in != null && in.available() == 0) {
				in = null;
			}
		} catch (IOException e) {
			in = null;
		}
		
		if (in == null && propFile != null) {
			try {
				in = new FileInputStream(propFile);
				LOGGER.finer("propFile \"" + propFile + "\" used as input stream");
			} catch (Exception e) {
				// ok thats also not accessible  
			}
		}
		
		if (in == null ) {
			LOGGER.finer("no accessible properties File or input stream found");
			return;
		}
		
		try {
			properties = properties.getClass().cast(propertyxStream.fromXML(in));
		} catch (Exception e) {
			LOGGER.info("error while loading properties: " + e.getMessage());
			propertiesAreSafe = false;
		} finally {
			try {
				in.close();
			} catch (IOException e) {
			}
		}
		
		LOGGER.finer("propertiesAreSafe: " + propertiesAreSafe);
		LOGGER.exiting(SimpleController.class.getName(), "loadProperties");
	}
	
	/**
	 * @return false when the shutdown was canceled by the user
	 */
	protected boolean savePropertiesOnExit() {
		LOGGER.entering(SimpleController.class.getName(), "savePropertiesOnExit");
		LOGGER.finer("propertiesAreSafe: " + propertiesAreSafe);
		LOGGER.finer("askBeforeSaveOnExit: " + askBeforeSaveOnExit);
		LOGGER.finer("saveOnExit: " + saveOnExit);
		
		if (!propertiesAreSafe || (!askBeforeSaveOnExit && !saveOnExit)) {
			LOGGER.exiting(SimpleController.class.getName(), "savePropertiesOnExit", true);
			return true; 
		}
		
		for (Plugin p : plugins) {
			try {
				p.storeStates(SimpleController.this);
			} catch (Exception e) {
				LOGGER.info("could not store states of plugin \"" + p + "\": " + e.getMessage());
			}
		}
		
		File file=null;
		if (userPropertyFile != null) {
			file=new File(userPropertyFile);
			LOGGER.finer("userPropertyFile: " + userPropertyFile);
		}
		if (file == null){
			file = propFile;
			LOGGER.finer("propFile: " + (propFile == null ? "null" : propFile.getAbsolutePath()));
		}

		SaveOnExitDialog dialog = new SaveOnExitDialog(file, mainWindow, this);
		if (askBeforeSaveOnExit){
			boolean canceled=!dialog.show();
			if (canceled) {
				LOGGER.exiting(SimpleController.class.getName(), "savePropertiesOnExit (property file saving dialog canceled)", false);
				return false;
			}
			file = dialog.getFile();
			LOGGER.finer("file obtained from dialog: " + (file == null ? "null" : file.getAbsolutePath()));
		} else {
			try {
				if (file != null && file.getParentFile()!=null && !file.getParentFile().exists()) {
					LOGGER.finer("try to make dirs: " + file.getParentFile());
					file.getParentFile().mkdirs();
					LOGGER.finer("dirs successfully made");
				}
			} catch (Exception e) {
				assert saveOnExit;
				LOGGER.finer("Although we should not ask the user, we do now, because save on exit was true and we could not save to the given file.");
				boolean canceled = !dialog.show();
				if (canceled) {
					LOGGER.finer("property file saving dialog canceled");
					LOGGER.exiting(SimpleController.class.getName(), "savePropertiesOnExit (property file saving dialog canceled)", false);
					return false;
				}
				file = dialog.getFile();
				LOGGER.finer("file obtained from dialog: " + (file == null ? "null" : file.getAbsolutePath()));
			}
		}
		
		if (file != null) {
			try {
				LOGGER.finer("try to write properties to file: " + file);
				String xml = propertyxStream.toXML(properties);
				LOGGER.finest("properties: " + properties);
				FileWriter writer = new FileWriter(file);
				writer.write(xml);
				writer.flush();
				writer.close();
			} catch (IOException e) {
				LOGGER.info("writing properties failed: " + e.getMessage());
			}
		}
		
		try {
			if (userPreferences != null) {
				LOGGER.finer("flush the user preferences");
				userPreferences.flush();
			}
		} catch (BackingStoreException e) {
			LOGGER.info("could not persist user preferences: " + e.getMessage());
		}
		
		LOGGER.exiting(SimpleController.class.getName(), "savePropertiesOnExit", true);
		return true;
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
	
	/** The provided resource serves 2 purposes: 
	 * <ol>
	 * <li>to set the properties <code>File</code> and <code>InputStream</code> via {@link #setPropertiesFile(File)} 
	 * (if this resource allows write access) and {@link #setPropertiesInputStream(InputStream)} 
	 * (if this resource allows read access),</li>
	 * <li>to save and read user decisions about the reading and loading of the property file in a package specific node, via
	 * the <a href="http://java.sun.com/javase/6/docs/technotes/guides/preferences/index.html">Java Preferences API</a>.  
	 * </li>
	 * </ol> 
	 * 
	 * @param clazz the class from which the resource may be obtained. The 
	 * properties node of the package of this class is used to save the user decisions. 
	 * Can be null, then the user preferences are disabled and only static property files are used.
	 * @param propertiesFileName name of the resource that contains the plug-in properties. This argument may 
	 * be null, then only the second purpose is served and the properties <code>File</code> and 
	 * <code>InputStream</code> are NOT set to null and may be set independently.
	 */
	public void setPropertiesResource(Class<?> clazz, String propertiesFileName) {
		LOGGER.entering(SimpleController.class.getName(), "setPropertiesResource", new Object[]{clazz, propertiesFileName});
//		boolean propertiesFileSet = false;
		if (clazz != null && propertiesFileName != null) {
			URL url=clazz.getResource(propertiesFileName);
			LOGGER.fine("url: " + url);
			if (url != null) {
				File file = new File(url.getFile());
				LOGGER.fine("file: " + file);
				if (file.canWrite()) {
					LOGGER.fine("can write to file: " + file);
					setPropertiesFile(file);
//					propertiesFileSet = true;
				}
				try {
					setPropertiesInputStream(url.openStream());
					
				} catch (IOException e) { 
					LOGGER.config("Can not access property resource "+ propertiesFileName + " of " + clazz);
				}
			}
		}

		if (clazz != null) {
			userPreferences = Preferences.userNodeForPackage(clazz);
			LOGGER.fine("userPreferences: " + userPreferences);
		} else {
			userPreferences = null;
			LOGGER.finer("user preferences set to null (disabled)");
		}
		readUserPreferences();
		LOGGER.exiting(SimpleController.class.getName(), "setPropertiesResource");
	}
 
	/**
	 * Sets the properties File of this SimpleController. This does not overwrite a file chosen by the the user
	 * and persisted as user properties.
	 * @param propertiesFile a file or null
	 * @see #setPropertiesResource(Class, String)
	 */
	public void setPropertiesFile(File propertiesFile) {
		LOGGER.entering(SimpleController.class.getName(), "setPropertiesFile", new Object[]{propertiesFile});
		this.propFile = propertiesFile;
		LOGGER.exiting(SimpleController.class.getName(), "setPropertiesFile");
	}
	
	/**
	 * Sets the properties InputStream of this SimpleController. If also a properties <code>File</code> is provided
	 * the <code>InputStream</code> is used for reading the properties.
	 * 
	 * @param in an InputStream or null
	 * @see #setPropertiesResource(Class, String)
	 */
	public void setPropertiesInputStream(InputStream in) {
		LOGGER.entering(SimpleController.class.getName(), "setPropertiesInputStream: " + in);
		this.propInputStream = in;
		LOGGER.exiting(SimpleController.class.getName(), "setPropertiesInputStream");
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


	public boolean isSaveOnExit() {
		return saveOnExit;
	}


	public void setSaveOnExit(boolean saveOnExit) {
		this.saveOnExit = saveOnExit;
		writeUserPreferences();
	}


	public boolean isAskBeforeSaveOnExit() {
		return askBeforeSaveOnExit;
	}


	public void setAskBeforeSaveOnExit(boolean askBeforeSaveOnExit) {
		this.askBeforeSaveOnExit = askBeforeSaveOnExit;
		writeUserPreferences();
	}


	public boolean isLoadFromUserPropertyFile() {
		return loadFromUserPropertyFile;
	}


	public void setLoadFromUserPropertyFile(boolean loadFromUserPropertyFile) {
		this.loadFromUserPropertyFile = loadFromUserPropertyFile;
		writeUserPreferences();
	}


	public String getUserPropertyFile() {
		return userPropertyFile;
	}

	/** Overwrite or initialize the file chosen by the user for reading and writing of properties.
	 * 
	 * @param userPropertyFile
	 */
	public void setUserPropertyFile(String userPropertyFile) {
		this.userPropertyFile = userPropertyFile;
		writeUserPreferences();
	}
	
	public boolean isRegisterSPIPlugins() {
		return registerSPIPlugins;
	}
	public void setRegisterSPIPlugins(boolean loadSPIPlugins) {
		this.registerSPIPlugins = loadSPIPlugins;
	}
	
	/** Call this method to save the properties and exit the application. This is done in a newly 
	 * created thread to allow user interaction during saving. The user is allowed to cancel that process,
	 * in which case the application is not finished.
	 * 	 
	 * @see #setPropertiesResource(Class, String)
	 * 
	 */
	public void shutdown() {
		LOGGER.entering(SimpleController.class.getName(), "shutdown", new Object[]{});
		
		Runnable doSaveAndExit=new Runnable(){
			public void run() {
				LOGGER.entering("doSaveAndExit Runnable", "run (do the save and exit stuff)", new Object[]{});
				if (savePropertiesOnExit()) { //not canceled
					dispose();
					LOGGER.finer("system exit");
					System.exit(0);
				}
			}
		};
		LOGGER.finer("start a new thread to do save on exit");
		new Thread(doSaveAndExit,this.getClass()+"shutdown").run();
		
		LOGGER.exiting(SimpleController.class.getName(), "shutdown");
	}
	
	
	public void dispose() {
		if (mainWindow != null) {
			LOGGER.finer("dispose main window");
			mainWindow.dispose();
			mainWindow = null;
		}
		if (fullScreenFrame != null) {
			LOGGER.finer("dispose full screen");
			fullScreenFrame.dispose();
			fullScreenFrame = null;
		}
		if (helpWindow != null) {
			LOGGER.finer("dispose help window");
			helpWindow.dispose();
			helpWindow = null;
		}
		if (aboutDialog != null) {
			LOGGER.finer("dispose about dialog");
			aboutDialog.dispose();
			aboutDialog = null;
		}
		if (preferencesWindow != null) {
			LOGGER.finer("dispose preferences window");
			preferencesWindow.dispose();
			preferencesWindow = null;
		}
		Runtime.getRuntime().removeShutdownHook(shutdownHook);
	}
			
}
