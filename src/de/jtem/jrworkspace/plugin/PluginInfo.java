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

import java.net.URL;

import javax.swing.Icon;


/**
 * This class holds information about a plug-in. Every plug-in class
 * has to return an instance of this class in their getPluginInfo
 * method.
 * @author Stefan Sechelmann
 */
public class PluginInfo {

	
	public String 
		/**
		 * The plug-in name
		 */
		name = "unnamed",
		/**
		 * The vendor name of the plug-in
		 */
		vendorName = "unknown",
		/**
		 * An email address of this plug-ins vendor
		 */
		email = "unknown";
	public Icon 
		/**
		 * An icon which will be the plug-ins icon 
		 * in the application
		 */
		icon = null;
	public URL 
		/**
		 * An URL to the Documentation of the plug-in.
		 */
		documentationURL = null;
	public boolean
		/**
		 * Indicates if this plug-in is meant to be dynamically 
		 * installed or uninstalled
		 */
		isDynamic = true;
	
	
	public PluginInfo() {

	}
	
	
	public PluginInfo(String name) {
		this.name = name;
	}
	
	
	public PluginInfo(String name, String vendor) {
		this(name);
		this.vendorName = vendor;
	}
	
	public static PluginInfo create(Class<?> pluginClass) {
		PluginInfo pi;
		if (pluginClass == null) {
			pi = new PluginInfo();
		} else {
			pi = new PluginInfo(pluginClass.getSimpleName());
		}
		if (pluginClass != null &&  pluginClass.getPackage() != null) {
			pi.vendorName = pluginClass.getPackage().getImplementationVendor();
		}
		return pi;
	}
	
	
}
