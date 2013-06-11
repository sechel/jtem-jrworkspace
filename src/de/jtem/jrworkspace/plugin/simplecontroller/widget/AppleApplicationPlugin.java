package de.jtem.jrworkspace.plugin.simplecontroller.widget;

import static de.jtem.jrworkspace.logging.LoggingSystem.LOGGER;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import de.jtem.jrworkspace.plugin.Plugin;
import de.jtem.jrworkspace.plugin.PluginInfo;
import de.jtem.jrworkspace.plugin.flavor.OpenAboutFlavor;
import de.jtem.jrworkspace.plugin.flavor.OpenPreferencesFlavor;
import de.jtem.jrworkspace.plugin.flavor.ShutdownFlavor;

/** Register this plugin if you wish the controller to respond to the following Apple 
 * events when running in an Apple JRE (this plugin does nothing if it runs in another JRE despite 
 * logging a config level message, so it does not hurt on other platforms):
 * <ul>
 * <li>Shut down the controller when a user chooses Quit from the application menu, Dock icon, or types Command-Q</li>
 * <li>Display the About dialog when a user chooses About from the application menu</li>
 * <li>Open the preferences window when the users chooses Preferences from the application menu</li>
 * </ul>
 * 
 * @author G. Paul Peters, Mar 25, 2010
 *
 */
public class AppleApplicationPlugin extends Plugin 
	implements ShutdownFlavor, OpenAboutFlavor, OpenPreferencesFlavor {
	
	public static String APPLE_APPLICATION_CLASS = "com.apple.eawt.Application";
	public static String APPLE_APPLICATION_LISTENER = "com.apple.eawt.ApplicationListener";

	private Object appleApplicationInstance;
	private ShutdownListener shutdownListener;
	private OpenAboutListener openAboutListener;
	private OpenPreferencesListener openPreferencesListener;
	
	public AppleApplicationPlugin() {
		Class<?> appleApplication = null;
		try {
			appleApplication = Class.forName(APPLE_APPLICATION_CLASS);
		} catch (Exception e) {
			LOGGER.config("Seems we are not running in an Apple JRE. Could not access: " + APPLE_APPLICATION_CLASS);
			LOGGER.fine(e.toString());
		}
		if (appleApplication != null) {
			instanciateAppleApplication(appleApplication);
			registerApplicationListener(appleApplication);
			addMenuItems(appleApplication);
			LOGGER.config("Seems we are running in an Apple JRE. Handling of Apple events enabled.");
		}
	}
	
	/**
	 * @return the apple application object or null
	 * (http://developer.apple.com/mac/library/documentation/Java/Reference/JavaSE6_AppleExtensionsRef/api/com/apple/eawt/ApplicationListener.html)
	 */
	public Object getAppleApplication() {
		return appleApplicationInstance;
	}
	
	@Override
	public PluginInfo getPluginInfo() {
		PluginInfo info = new PluginInfo("apple application", "G. Paul Peters");
		return info;
	}
	
	@Override
	public void setShutdownListener(ShutdownListener shutdownListener) {
		this.shutdownListener = shutdownListener;
	}
	
	
	@Override
	public void setOpenAboutListener(OpenAboutListener openAboutListener) {
		this.openAboutListener = openAboutListener;
	}

	@Override
	public void setOpenPreferencesListener(OpenPreferencesListener openPreferencesListener) {
		this.openPreferencesListener = openPreferencesListener;
	}

	private class AppleApplicationListener implements InvocationHandler {
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			LOGGER.finer(method.getName());
			if (method.getName() == "handleQuit" && shutdownListener != null) {
				shutdownListener.shutdown();
			}
			if (method.getName() == "handleAbout" && openAboutListener != null) {
				openAboutListener.openAboutWindow();
			}			
			if (method.getName() == "handlePreferences" && openPreferencesListener != null) {
				openPreferencesListener.openPreferencesWindow();
			}
			return null;
		}	
	}
	
	private void registerApplicationListener(Class<?> appleApplication) {
		Object appListener = null;
		try {
			appListener = Proxy.newProxyInstance(
					this.getClass().getClassLoader(), 
					new Class[] {Class.forName(APPLE_APPLICATION_LISTENER)}, 
					new AppleApplicationListener());
			appleApplication.getMethod(
					"addApplicationListener", Class.forName(APPLE_APPLICATION_LISTENER))
					.invoke(appleApplicationInstance, appListener);
		} catch (Exception e) {
			LOGGER.info(e.toString());
		}
	}

	private void instanciateAppleApplication(Class<?> appleApplication) {
		try {
			appleApplicationInstance = appleApplication.getMethod("getApplication").invoke(null);
		} catch (Exception e) {
			LOGGER.info(e.toString());
		}
	}
	
	private void addMenuItems(Class<?> appleApplication) {
		try {
			appleApplication.getMethod("addPreferencesMenuItem").invoke(appleApplicationInstance);
			appleApplication.getMethod("addAboutMenuItem").invoke(appleApplicationInstance);
		} catch (Exception e) {
			LOGGER.info(e.toString());
		}
	}

	
}
