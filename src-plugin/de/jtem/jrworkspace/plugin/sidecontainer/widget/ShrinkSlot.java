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

package de.jtem.jrworkspace.plugin.sidecontainer.widget;

import java.awt.Dimension;
import java.awt.Point;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;

public abstract class ShrinkSlot extends JPanel {

	private static final long 
		serialVersionUID = 1L;

	private static List<ShrinkSlot>
		instances = new LinkedList<ShrinkSlot>();
	

	public ShrinkSlot() {
		instances.add(this);
	}
	
	
    /**
     * Returns the slot under the mouse or null if there is none.
     * @param onScreen
     * @return
     */
    public static ShrinkSlot getSlotUnderMouse(Point onScreen) {
    	ShrinkSlot result = null;
    	for (ShrinkSlot c : instances) {
    		if (!c.isShowing()) continue;
    		Point l = c.getLocationOnScreen();
    		Dimension size = c.getSize();
    		if (l.x < onScreen.x && onScreen.x < l.x + size.width &&
    			l.y < onScreen.y && onScreen.y < l.y + size.height
    		) {
    			result = c;
    		}
    	}
    	return result;
    }
	
    
    public static void hideAllInsertHints() {
    	for (ShrinkSlot s : instances) {
    		s.showInsertHint(false, new Point());
    	}
    }
    
    
    public abstract void addShrinkPanel(ShrinkPanel s);
    
    public abstract void addShrinkPanelAt(ShrinkPanel s, Point p);
    
    public abstract void removeShrinkPanel(ShrinkPanel s);
    
    public abstract void showInsertHint(boolean show, Point p);
    
}
