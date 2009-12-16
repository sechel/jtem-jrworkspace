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

package de.jtem.jrworkspace.plugin.simplecontroller.action;

import static de.jtem.jrworkspace.plugin.simplecontroller.image.ImageHook.getIcon;
import static java.awt.event.KeyEvent.VK_F1;
import static javax.swing.KeyStroke.getKeyStroke;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import de.jtem.jrworkspace.plugin.simplecontroller.help.HelpWindow;

public class HelpWindowAction extends AbstractAction {

	private static final long 
		serialVersionUID = 1L;
	private HelpWindow
		helpWindow = null;

	public HelpWindowAction(HelpWindow win) {
		putValue(NAME, "Help Center");
		putValue(LONG_DESCRIPTION, "jRWorkspace Plugin Help Pages");
		putValue(SMALL_ICON, getIcon("helpred.png"));
		putValue(ACCELERATOR_KEY, getKeyStroke(VK_F1, 0));
		
		helpWindow = win;
		helpWindow.setSize(800, 500);
		helpWindow.setMinimumSize(new Dimension(400, 400));
	}
	
	
	public void actionPerformed(ActionEvent e) {
		if (helpWindow.isShowing()) {
			helpWindow.toFront();
			return;
		}
		helpWindow.setLocationByPlatform(true);
		helpWindow.setLocationRelativeTo(helpWindow.getParent());
		helpWindow.updateData();
		helpWindow.setVisible(true);
	}

}
