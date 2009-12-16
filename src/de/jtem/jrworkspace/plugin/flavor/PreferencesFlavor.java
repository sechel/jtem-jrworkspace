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
import javax.swing.JPanel;


/**
 * A flavor which adds a preferences page to the host application
 * @author Stefan Sechelmann
 *
 */
public interface PreferencesFlavor {

	/**
	 * Returns a name which will be the name of the main page
	 * @return a name
	 */
	public String getMainName();
	
	/**
	 * The main preferences page
	 * @return a {@link JPanel}
	 */
	public JPanel getMainPage();
	
	/**
	 * An icon for the main page
	 * @return an icon
	 */
	public Icon getMainIcon();
	
	
	/**
	 * The number of sub-pages of this preference page
	 * @return a Integer number
	 */
	public int getNumSubPages();
	
	/**
	 * The name of sub-page number i
	 * @param i the index of the sub-page
	 * @return a name
	 */
	public String getSubPageName(int i);
	
	/**
	 * The JPanel of the sub-page with index i
	 * @param i the index of the sub-page
	 * @return
	 */
	public JPanel getSubPage(int i);
	
	/**
	 * The icon of sub-page i
	 * @param i the index of the sub-page
	 * @return
	 */
	public Icon getSubPageIcon(int i);

}
