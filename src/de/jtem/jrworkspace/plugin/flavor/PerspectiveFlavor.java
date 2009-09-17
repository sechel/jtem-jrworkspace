package de.jtem.jrworkspace.plugin.flavor;

import java.awt.Component;

import javax.swing.Icon;


/**
 * A flavor which adds a new perspective to the host application. A perspective
 * consists of a main panel which will be displayed in the center of the host
 * applications main frame.
 * @author Stefan Sechelmann
 */
public interface PerspectiveFlavor {

	/**
	 * The title of the perspective
	 * @return
	 */
	public String getTitle();
	
	/**
	 * An icon for this perspective
	 * @return an icon
	 */
	public Icon getIcon();
	
	/**
	 * Is called which this Perspective is about to become visible or hidden
	 * @param visible a visible flag
	 */
	public void setVisible(boolean visible);
	
	/**
	 * Returns the center component that will be displayed in the
	 * center of the hosts main frame
	 * @return the center panel of the perspective or null if the perspective
	 * shall not be displayed
	 */
	public abstract Component getCenterComponent();
	
}
