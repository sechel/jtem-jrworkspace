package de.jtem.jrworkspace.plugin.flavor;

import java.awt.Component;

import javax.swing.JToolBar;

/**
 * A flavor that is interpreted as a tool-bar by the Controller
 * @author Stefan Sechelmann
 *
 */
public interface ToolBarFlavor {

	/**
	 * Returns a tool-bar component usually this is a {@link JToolBar}
	 * @return
	 */
	public Component getToolBarComponent();
	
	/**
	 * Returns the Perspective flavor which is associated with this tool bar
	 * @return the perspective
	 */
	public Class<? extends PerspectiveFlavor> getPerspective();
	
	/**
	 * The tool-bar priority. If there are more that one tool-bar
	 * installed the tool-bar get sorted according to this priority.
	 * If {@link JToolBar}s are used the user can change the 
	 * @return
	 */
	public double getToolBarPriority();
	
}
