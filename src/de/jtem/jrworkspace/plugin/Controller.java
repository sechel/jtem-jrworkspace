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

import java.util.List;

/**
 * The Controller interface of the jRWorkspace plug-in mechanism.
 * It manages the communication between plug-ins through the getPlugin() 
 * methods.
 * A plug-in that needs another plug-in to work with should call 
 * getPlugin() with the plug-ins class as argument. 
 * @author Stefan Sechelmann
 */
public interface Controller {

	/**
	 * Returns an instance of the plug-in class clazz, if there
	 * is a plug-in of this class available. If the result is available
	 * but not yet installed it will be installed.
	 * @param <T> the class type of the plug-in to get
	 * @param clazz the class
	 * @return a plug-in or null if there is no plug-in of type class<T>
	 */
	public <T extends Plugin> T getPlugin(Class<T> clazz);

	/**
	 * Returns all plug-ins for which the following expression evaluates to true
	 * pClass.isAssignableFrom(plug-in)
	 * @param <T> the plug-in class type
	 * @param pClass the class of type T
	 * @return A list of plug-ins 
	 */
	public <T> List<T> getPlugins(Class<T> pClass);
	
	/**
	 * Stores the property with the given key and context class
	 * @param context the context class e.g. the plug-in class which stores the value
	 * @param key a key string
	 * @param property the property to save
	 * @return The old value of this property
	 */
	public Object storeProperty(Class<?> context, String key, Object property);
	
	
	/**
	 * Retrieves a property from this Controller.
	 * @param <T> The property type
	 * @param context A context
	 * @param key the key name of the property to retrieve
	 * @param defaultValue a default value, which is returned if the 
	 * property was not saved before
	 * @return the stored property or defaultValue
	 */
	public <T> T getProperty(Class<?> context, String key, T defaultValue);

	
	/**
	 * Deletes a property from this controller
	 * @param <T> The property type
	 * @param context A context
	 * @param key The key name of the property to delete
	 * @return The deleted value or null if the key was not found
	 */
	public <T> T deleteProperty(Class<?> context, String key);
	
	
	/**
	 * Returns true if the controller believes this plug-in is active
	 * @param p the plug-in
	 * @return active
	 */
	public boolean isActive(Plugin p);
	
}
