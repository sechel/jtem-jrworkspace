package de.jtem.jrworkspace.plugin.simplecontroller.widget;

import static de.jtem.jrworkspace.logging.LoggingSystem.LOGGER;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;

import java.io.ByteArrayOutputStream;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.jtem.jrworkspace.plugin.flavor.OpenAboutFlavor.OpenAboutListener;
import de.jtem.jrworkspace.plugin.flavor.OpenPreferencesFlavor.OpenPreferencesListener;
import de.jtem.jrworkspace.plugin.flavor.ShutdownFlavor.ShutdownListener;

public class AppleApplicationPluginTest {
	private static ByteArrayOutputStream testOut = new ByteArrayOutputStream();
	
	@Test
	public void failCreateNoClass() {
		AppleApplicationPlugin.APPLE_APPLICATION_CLASS = "does.not.exist.Class";
		
		new AppleApplicationPlugin();
		
		assertThat(testOut.toString(), containsString(AppleApplicationPlugin.APPLE_APPLICATION_CLASS));
		assertThat(testOut.toString(), containsString("Could not"));
	}
	
	@Test
	public void createWithExistingClass() {
		new AppleApplicationPlugin();
		
		assertThat(testOut.toString(), containsString("de.jtem.jrworkspace.plugin.simplecontroller.widget.MockAppleApplication addApplicationListener"));
	}
	
	@Test
	public void addMenuItems() {
		new AppleApplicationPlugin();

		assertThat(testOut.toString(), containsString("de.jtem.jrworkspace.plugin.simplecontroller.widget.MockAppleApplication addPreferencesMenuItem"));
		assertThat(testOut.toString(), containsString("de.jtem.jrworkspace.plugin.simplecontroller.widget.MockAppleApplication addAboutMenuItem"));
	}
	
	@Test
	public void shutdown() {
		LOGGER.setLevel(Level.INFO);
		AppleApplicationPlugin p = new AppleApplicationPlugin();
		p.setShutdownListener(new ShutdownListener() {
			@Override
			public void shutdown() {
				LOGGER.info("shutdown called");
			}
		});
		((MockAppleApplication) p.getAppleApplication()).callHandleQuitOnListener();
		
		assertThat(testOut.toString(), containsString("shutdown called"));
	}
	

	@Test
	public void openAbout() {
		LOGGER.setLevel(Level.INFO);
		AppleApplicationPlugin p = new AppleApplicationPlugin();
		p.setOpenAboutListener(new OpenAboutListener() {
			@Override
			public void openAboutWindow() {
				LOGGER.info("about called");				
			}
		});
		((MockAppleApplication) p.getAppleApplication()).callHandleAboutOnListener();
		
		assertThat(testOut.toString(), containsString("about called"));
	}
	

	@Test
	public void openPreferences() {
		LOGGER.setLevel(Level.INFO);
		AppleApplicationPlugin p = new AppleApplicationPlugin();
		p.setOpenPreferencesListener(new OpenPreferencesListener() {
			@Override
			public void openPreferencesWindow() {
				LOGGER.info("preferences called");				
			}
		});
		((MockAppleApplication) p.getAppleApplication()).callHandlePreferencesOnListener();
		
		assertThat(testOut.toString(), containsString("preferences called"));
	}
	
	@Test
	public void callHandlersWithoutListenersShouldFailQuietly() {
		LOGGER.setLevel(Level.INFO);
		AppleApplicationPlugin p = new AppleApplicationPlugin();
		((MockAppleApplication) p.getAppleApplication()).callHandlePreferencesOnListener();
		((MockAppleApplication) p.getAppleApplication()).callHandleAboutOnListener();
		((MockAppleApplication) p.getAppleApplication()).callHandleQuitOnListener();
	}


	
	@BeforeClass
	public static void addTestHandler() {
		StreamHandler testHandler = new StreamHandler(testOut, new SimpleFormatter()) {
			@Override
			public synchronized void publish(LogRecord record) {
				super.publish(record);
				flush();
			}
		};
		testHandler.setLevel(Level.ALL);
		LOGGER.addHandler(testHandler);
	}
	
	@Before
	public void resetTestOut() {
		LOGGER.setLevel(Level.FINEST);
		testOut.reset();
	}

	@Before
	public void setMockClasses() {
		AppleApplicationPlugin.APPLE_APPLICATION_CLASS = "de.jtem.jrworkspace.plugin.simplecontroller.widget.MockAppleApplication";
		AppleApplicationPlugin.APPLE_APPLICATION_LISTENER = "de.jtem.jrworkspace.plugin.simplecontroller.widget.MockAppleApplicationListener";
	}
}
