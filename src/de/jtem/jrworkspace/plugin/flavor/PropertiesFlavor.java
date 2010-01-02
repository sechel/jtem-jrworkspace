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

import java.io.Reader;
import java.io.Writer;

import de.jtem.jrworkspace.plugin.Plugin;


/** 
 * A {@link Plugin} that wants to access the methods listed in the 
 * {@link PropertiesListener} interface, e.g., a menu entry that 
 * allows the user to call these methods, should implement this flavor.
 * 
 * @author G. Paul Peters, Dec 14, 2009
 */
public interface PropertiesFlavor {

	
	/** 
	 * A controller that is aware of the <code>PropertiesFlavor</code> will 
	 * delegate calls to the appropriate methods.
	 */
	public static interface PropertiesListener {
		
		public void writeProperties(Writer w);
		public void readProperties(Reader r);
		public void loadDefaultProperties();
		
		public boolean isSaveOnExit();
		public void setSaveOnExit(boolean saveOnExit);
		public boolean isAskBeforeSaveOnExit();
		public void setAskBeforeSaveOnExit(boolean askBeforeSaveOnExit);
		public boolean isLoadFromUserPropertyFile();
		public void setLoadFromUserPropertyFile(boolean loadFromUserPropertyFile);
		public String getUserPropertyFile();
		public void setUserPropertyFile(String userPropertyFile);		
	}
	
	
	public void setPropertiesListener(PropertiesListener l);
	
}
