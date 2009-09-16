package de.varylab.jrworkspace.plugin;

import java.util.List;

/**
 * The Controller interface of the jRWorkspace plug-in mechanism.
 * It manages the communication between plug-ins through the getPlugin() 
 * methods.
 * A plug-in that needs another plug-in to work with should call 
 * getPlugin() with the plug-ins class as argument. 
 * @author Stefan Sechelmann
 */
public interface Controller {

	/**
	 * Returns an instance of the plug-in class clazz, if there
	 * is a plug-in of this class available. If the result is available
	 * but not yet installed it will be installed.
	 * @param <T> the class type of the plug-in to get
	 * @param clazz the class
	 * @return a plug-in or null if there is no plug-in of type class<T>
	 */
	public <T extends Plugin> T getPlugin(Class<T> clazz);

	/**
	 * Returns all plug-ins for which the following expression evaluates to true
	 * pClass.isAssignableFrom(plug-in)
	 * @param <T> the plug-in class type
	 * @param pClass the class of type T
	 * @return A list of plug-ins 
	 */
	public <T> List<T> getPlugins(Class<T> pClass);
	
	/**
	 * Stores the property with the given key and context class
	 * @param context the context class e.g. the plug-in class which stores the value
	 * @param key a key string
	 * @param property the property to save
	 * @return The old value of this property
	 */
	public Object storeProperty(Class<?> context, String key, Object property);
	
	
	/**
	 * Retrieves a property from this Controller.
	 * @param <T> The property type
	 * @param context A context
	 * @param key the key name of the property to retrieve
	 * @param defaultValue a default value, which is returned if the 
	 * property was not saved before
	 * @return the stored property or defaultValue
	 */
	public <T> T getProperty(Class<?> context, String key, T defaultValue);

	
	/**
	 * Deletes a property from this controller
	 * @param <T> The property type
	 * @param context A context
	 * @param key The key name of the property to delete
	 * @return The deleted value or null if the key was not found
	 */
	public <T> T deleteProperty(Class<?> context, String key);
	
	
	/**
	 * Returns true if the controller believes this plug-in is active
	 * @param p the plug-in
	 * @return active
	 */
	public boolean isActive(Plugin p);
	
}
