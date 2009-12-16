package de.jtem.jrworkspace.plugin;


/**
 * The base class for all plug-ins of the jRWorkspace plug-in mechanism.
 * The order of calls in the life cycle of a plug-in is
 * 1. - restoreStates(Controller c)
 * 2. - install(Controller c)
 * 3. - uninstall(Controller c)
 * 4. - storeStates(Controller c)
 * 
 * <h4>PluginInfo<h4>
 * It is strongly recommended that you override {@link #getPluginInfo()} to return a 
 * descriptive {@link PluginInfo}. This method is called in the constructor of this class and must not
 * return <code>null</code>.
 *
 * @author Stefan Sechelmann
 */
public abstract class Plugin {

	/**
	 * Returns an instance of {@link PluginInfo}
	 * @return plug-in information
	 * @see {@link PluginInfo}
	 */
	public PluginInfo getPluginInfo() {
		return PluginInfo.create(getClass());
	}
	
	/**
	 * Is called when the plug-in is installed 
	 * @param c the applications {@link Controller}
	 * @throws Exception
	 * @see {@link Controller}
	 */
	public void install(Controller c) throws Exception{
	}
	
	
	/**
	 * Id called when this plug-in is about to be uninstalled
	 * @param c this applications {@link Controller}
	 * @throws Exception
	 * @see {@link Controller}
	 */
	public void uninstall(Controller c) throws Exception {
	}
	

	/**
	 * Is called before the installation of this plug-in. 
	 * @param c this applications {@link Controller}
	 * @throws Exception
	 * @see {@link Controller}
	 */
	public void restoreStates(Controller c) throws Exception {
	}
	
	/**
	 * Is called after this plug-in has been uninstalled.
	 * @param cthis applications {@link Controller}
	 * @throws Exception
	 * @see {@link Controller}
	 */
	public void storeStates(Controller c) throws Exception {
	}
	
	/**
	 * Returns this plug-ins name or "No Name" if name is null
	 */
	@Override
	public String toString() {
		if (getPluginInfo().name == null)
			return "No Name";
		else
			return getPluginInfo().name;
	}
	
	/**
	 * A plug-in is unique throughout the controller so the 
	 * equals method compares the classes
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		} else {
			return getClass().equals(obj.getClass());
		}
	}
	
	/**
	 * This method simply returns getClass().hashCode()
	 * @see equals
	 */
	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
	
}
