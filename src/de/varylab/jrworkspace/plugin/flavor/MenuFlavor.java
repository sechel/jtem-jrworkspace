package de.varylab.jrworkspace.plugin.flavor;

import java.util.List;

import javax.swing.JMenu;

/**
 * This flavor adds a menu bar to a perspective
 * @author Stefan Sechelmann
 */
public interface MenuFlavor {

	/**
	 * Returns the menus for this menu flavor
	 * @return the menu list
	 */
	public List<JMenu> getMenus();
	
	/**
	 * Returns the Perspective flavor which is associated with this menu
	 * @return the perspective
	 */
	public Class<? extends PerspectiveFlavor> getPerspective();
	
	/**
	 * Returns the priority of this set of menus. The actual 
	 * order of menus in a menu bar of a perspective is determined 
	 * trough to this number
	 * @return a priority
	 */
	public double getPriority();
	
}
