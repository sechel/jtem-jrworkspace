package de.jtem.jrworkspace.plugin.simplecontroller.widget;

import java.util.EventObject;

public interface MockAppleApplicationListener {
	public void handleAbout(EventObject e);
	public void handlePreferences(EventObject e);
	public void handleQuit(EventObject e);
}
