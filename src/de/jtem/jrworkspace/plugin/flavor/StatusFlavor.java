package de.jtem.jrworkspace.plugin.flavor;

/**
 * A flavor which enables a plug-in to write a status text to the 
 * application
 * @author Stefan Sechelmann
 *
 */
public interface StatusFlavor {

	/**
	 * The status listener
	 * @author Stefan Sechelmann
	 */
	public static interface StatusChangedListener {
		public void statusChanged(String status);
	}
	
	/**
	 * Sets the status listener of the plug-in that implemens
	 * this interface
	 * @param scl the listener
	 */
	public void setStatusListener(StatusChangedListener scl);
	
}
