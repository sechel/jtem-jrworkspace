package de.varylab.jrworkspace.plugin;

import java.net.URL;

import javax.swing.Icon;


/**
 * This class holds information about a plug-in. Every plug-in class
 * has to return an instance of this class in their getPluginInfo
 * method.
 * @author Stefan Sechelmann
 */
public class PluginInfo {

	
	public String 
		/**
		 * The plug-in name
		 */
		name = "unnamed",
		/**
		 * The vendor name of the plug-in
		 */
		vendorName = "unknown",
		/**
		 * An email address of this plug-ins vendor
		 */
		email = "unknown";
	public Icon 
		/**
		 * An icon which will be the plug-ins icon 
		 * in the application
		 */
		icon = null;
	public URL 
		/**
		 * An URL to the Documentation of the plug-in.
		 */
		documentationURL = null;
	public boolean
		/**
		 * Indicates if this plug-in is meant to be dynamically 
		 * installed or uninstalled
		 */
		isDynamic = true;
	
	
	public PluginInfo() {

	}
	
	
	public PluginInfo(String name) {
		this.name = name;
	}
	
	
	public PluginInfo(String name, String vendor) {
		this(name);
		this.vendorName = vendor;
	}
	
	
}
