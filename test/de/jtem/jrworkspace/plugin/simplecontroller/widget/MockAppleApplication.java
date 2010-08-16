package de.jtem.jrworkspace.plugin.simplecontroller.widget;

import static de.jtem.jrworkspace.logging.LoggingSystem.LOGGER;

import java.util.EventObject;

public class MockAppleApplication {
	
	private static MockAppleApplication mockAppl = new MockAppleApplication();
	private MockAppleApplicationListener listener;

	public static MockAppleApplication getApplication() {
		return mockAppl;
	}
	
	public void addAboutMenuItem() {
		LOGGER.entering(MockAppleApplication.class.getName(), "addAboutMenuItem");
	}
	
	public void addPreferencesMenuItem(){
		LOGGER.entering(MockAppleApplication.class.getName(), "addPreferencesMenuItem");
	}
	
	public void addApplicationListener(MockAppleApplicationListener listener) {
		LOGGER.entering(MockAppleApplication.class.getName(), "addApplicationListener");
		this.listener = listener;
	}
	
	public void callHandleQuitOnListener() {
		listener.handleQuit(new EventObject(this));
	}
	
	public void callHandlePreferencesOnListener() {
		listener.handlePreferences(new EventObject(this));
	}
	
	public void callHandleAboutOnListener() {
		listener.handleAbout(new EventObject(this));
	}
	
	
}
