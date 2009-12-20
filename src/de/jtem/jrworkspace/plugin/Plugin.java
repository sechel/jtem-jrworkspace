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

package de.jtem.jrworkspace.plugin;


/**
 * The base class for all plug-ins of the jRWorkspace plug-in mechanism.
 * The order of calls in the life cycle of a plug-in is
 * 1. - restoreStates(Controller c)
 * 2. - install(Controller c)
 * 3. - uninstall(Controller c)
 * 4. - storeStates(Controller c)
 * 
 * <h4>PluginInfo</h4>
 * It is strongly recommended that you override {@link #getPluginInfo()} to return a 
 * descriptive {@link PluginInfo}. This method is called in the constructor of this class and must not
 * return <code>null</code>.
 *
 * @author Stefan Sechelmann
 */
public abstract class Plugin {

	/**
	 * Returns an instance of {@link PluginInfo}
	 * @return plug-in information
	 * @see {@link PluginInfo}
	 */
	public PluginInfo getPluginInfo() {
		return PluginInfo.create(getClass());
	}
	
	/**
	 * Is called when the plug-in is installed 
	 * @param c the applications {@link Controller}
	 * @throws Exception
	 * @see {@link Controller}
	 */
	public void install(Controller c) throws Exception{
	}
	
	
	/**
	 * Id called when this plug-in is about to be uninstalled
	 * @param c this applications {@link Controller}
	 * @throws Exception
	 * @see {@link Controller}
	 */
	public void uninstall(Controller c) throws Exception {
	}
	

	/**
	 * Is called before the installation of this plug-in. The recommended way to read properties
	 * is to call {@link Controller#getProperty(Class, String, Object)} on the controller provided
	 * as argument.
	 * @param c this applications {@link Controller}
	 * @throws Exception
	 * @see {@link Controller}
	 */
	public void restoreStates(Controller c) throws Exception {
	}
	
	/**
	 * Is called after this plug-in has been uninstalled. The recommended way to save properties
	 * is to call {@link Controller#storeProperty(Class, String, Object)} on the controller provided
	 * as argument.
	 * @param cthis applications {@link Controller}
	 * @throws Exception
	 * @see {@link Controller}
	 */
	public void storeStates(Controller c) throws Exception {
	}
	
	/**
	 * Returns this plug-ins name or "No Name" if name is null
	 */
	@Override
	public String toString() {
		if (getPluginInfo().name == null)
			return "No Name";
		else
			return getPluginInfo().name;
	}
	
	/**
	 * A plug-in is unique throughout the controller so the 
	 * equals method compares the classes
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		} else {
			return getClass().equals(obj.getClass());
		}
	}
	
	/**
	 * This method simply returns getClass().hashCode()
	 * @see equals
	 */
	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
	
}
