package de.jtem.jrworkspace.plugin.widget;

import java.util.EventObject;

public interface MockAppleApplicationListener {
	public void handleAbout(EventObject e);
	public void handlePreferences(EventObject e);
	public void handleQuit(EventObject e);
}
