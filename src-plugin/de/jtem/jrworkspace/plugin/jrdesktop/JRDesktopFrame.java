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

package de.jtem.jrworkspace.plugin.jrdesktop;

import java.awt.Container;

import javax.swing.JInternalFrame;

import de.jtem.jrworkspace.plugin.Controller;
import de.jtem.jrworkspace.plugin.Plugin;

public abstract class JRDesktopFrame extends Plugin {

	private JInternalFrame
		frame = null;
	
	
	@Override
	public void install(Controller c) throws Exception {
		JRDesktopPlugin jrdesktop = c.getPlugin(JRDesktopPlugin.class);
		jrdesktop.addDesktopPlugin(this);
	}

	@Override
	public void uninstall(Controller c) throws Exception {
		JRDesktopPlugin jrdesktop = c.getPlugin(JRDesktopPlugin.class);
		jrdesktop.removeDesktopPlugin(this);
	}
	
	
	protected abstract Container getContent();

	
	protected JInternalFrame getInternalFrame() {
		if (frame == null) {
			frame = new JInternalFrame(getPluginInfo().name, true, true, false, true);
			Container content = getContent();
			frame.setSize(content.getSize());
			frame.setContentPane(content);
		}
		return frame;
	}
	
	
}
