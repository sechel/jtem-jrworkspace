package de.jtem.jrworkspace.plugin.flavor;


/**
 * A flavor that enables a plug-in to trigger frontend updates
 * of the hosts graphical user interface
 * @author Stefan Sechelmann
 *
 */
public interface FrontendFlavor {

	/**
	 * The listener 
	 * @author Stefan Sechelmann
	 */
	public static interface FrontendListener {
		
		/**
		 * Updates the front-end of the controller. All menu flavors
		 * and content flavors are loaded again
		 */
		public void updateFrontend();
		
		/**
		 * Reloads the content of the active perspective flavor
		 */
		public void updateContent();
		
		/**
		 * Reloads the menus of the active menu flavor
		 */
		public void updateMenuBar();
		
		/**
		 * Invokes updateUI on all swing components known 
		 * to the controller
		 */
		public void updateFrontendUI();
		
		/**
		 * Installs a swing look and feel
		 * @param lnfClassName the look and feel class name
		 */
		public void installLookAndFeel(String lnfClassName);
		
		/**
		 * Triggers the controller to maximize the main window 
		 * to full-screen
		 * @param fs
		 */
		public void setFullscreen(boolean fs);
		
		/**
		 * Check whether the window of the controller is in 
		 * full-screen mode
		 */
		public boolean isFullscreen();
		
		/**
		 * Shows or hides the menu bar of the main window
		 * @param show
		 */
		public void setShowMenuBar(boolean show);
		
		/**
		 * Shows or hides the tool bar of the main windows 
		 * @param show
		 */
		public void setShowToolBar(boolean show);
		
		/**
		 * Shows of hides the status bar of the main window
		 * @param show
		 */
		public void setShowStatusBar(boolean show);
		
	}

	/**
	 * Sets the {@link FrontendListener} of the plug-in
	 * @param l the listener
	 */
	public void setFrontendListener(FrontendListener l);
	
}
