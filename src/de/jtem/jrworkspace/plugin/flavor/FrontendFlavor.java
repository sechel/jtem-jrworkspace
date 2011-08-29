/**
This file is part of a jTEM project.
All jTEM projects are licensed under the FreeBSD license 
or 2-clause BSD license (see http://www.opensource.org/licenses/bsd-license.php). 

Copyright (c) 2002-2009, Technische Universität Berlin, jTEM
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, 
are permitted provided that the following conditions are met:

-	Redistributions of source code must retain the above copyright notice, 
	this list of conditions and the following disclaimer.

-	Redistributions in binary form must reproduce the above copyright notice, 
	this list of conditions and the following disclaimer in the documentation 
	and/or other materials provided with the distribution.
 
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS 
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, 
OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT 
OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING 
IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY 
OF SUCH DAMAGE.
**/

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
		public void setFullscreen(boolean fs, boolean exclusive);
		
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
		public boolean isShowMenuBar();
		
		/**
		 * Shows or hides the tool bar of the main windows 
		 * @param show
		 */
		public void setShowToolBar(boolean show);
		public boolean isShowToolBar();
		
		/**
		 * Shows of hides the status bar of the main window
		 * @param show
		 */
		public void setShowStatusBar(boolean show);
		public boolean isShowStatusBar();
		
		/**
		 * Set the application title
		 * @param title
		 */
		public void setTitle(String title);
		
	}

	/**
	 * Sets the {@link FrontendListener} of the plug-in
	 * @param l the listener
	 */
	public void setFrontendListener(FrontendListener l);
	
}
