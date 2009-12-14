package de.jtem.jrworkspace.plugin.flavor;

import de.jtem.jrworkspace.plugin.Plugin;

/** A {@link Plugin} that wishes to shutdown the application at some point should implement this flavor. 
 * 
 * @author G. Paul Peters, Dec 14, 2009
 *
 */
public interface ShutdownFlavor {
	
	public static interface ShutdownListener {	
		/** The call of this method triggers final housekeeping (properties saving)
		 *  and <code>System.exit</code>. 
		 */
		public void shutdown();
	}
	
   public void setShutdownListener(ShutdownListener shutdownListener);
}
