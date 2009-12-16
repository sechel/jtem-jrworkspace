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

import javax.swing.Icon;

public interface HelpFlavor {

	/**
	 * Returns the title for these help pages
	 * @return
	 */
	public String getHelpTitle();
	
	/**
	 * Returns an help icon
	 * @return
	 */
	public Icon getHelpIcon();
	
	/**
	 * Returns a path to the help HTML file. <br><br>
	 * 
	 * Note: relative backward paths (..) don't seem to work with the Web Start ClassLoader! 
	 * 
	 * @see {@link HelpFlavor#getHelpHandle()}
	 * @return
	 */
	public String getHelpPath();
	
	
	/**
	 * Returns the class which is the root of the help
	 * page file system. A help page name is then resolved like this:<br>
	 * {@code String location = }{@link HelpFlavor#getHelpPath()} + {@link HelpFlavor#getHelpDocument()} <br>
	 * {@code InputStream in = }{@link HelpFlavor#getHelpHandle()}{@code .getResourceAsStream(location)}
	 * @return a class which is the root handle for these help pages
	 */
	public Class<?> getHelpHandle();
	
	
	/**
	 * Returns the name of the HTML root help document
	 * @see {@link HelpFlavor#getHelpHandle()}
	 * @return the name of the root document
	 */
	public String getHelpDocument();

	
	/**
	 * Returns the name of the style sheet file to use
	 * for this help pages
	 * @return a file name or null
	 */
	public String getHelpStyleSheet();
	
	
	/**
	 * The HelpListener implements a method to show a help page
	 * @author Stefan Sechelmann
	 */
	public static interface HelpListener {
		
		/**
		 * Displays the plug-in help center an activates the
		 * given help page
		 * @param hf
		 */
		public void showHelpPage(HelpFlavor hf);
		
	}
	
	/**
	 * Sets the help listener which implements help 
	 * functionality of the controller
	 * @param l the listener
	 */
	public void setHelpListener(HelpListener l);
	
}

