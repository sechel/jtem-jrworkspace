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

/**
 * A container class which can stack ShrinkPanels and scroll them
 * <p>
 * Copyright 2005 <a href="http://www.sechel.de">Stefan Sechelmann</a>
 * <a href="http://www.math.tu-berlin.de/geometrie">TU-Berlin</a> 
 * @author Stefan Sechelmann
 */
public class ShrinkSlotHorizontal extends ShrinkSlotVertical {
	
	private static final long 
		serialVersionUID = 1L;
	
	public ShrinkSlotHorizontal(){
		super(100);
	}
	
	protected void updateLayout() {
		content.removeAll();
		for (ShrinkPanel sp : panels) {
			content.add(sp, normalConstraints);
		}
		if (panels.size() == 0) {
			setPreferredSize(new Dimension(10, 10));
			setMinimumSize(new Dimension(10, 10));
		} else {
			setPreferredSize(null);
			setMinimumSize(new Dimension(100, 10));
		}
		content.doLayout();
		revalidate();
		updateUI();
	}
	
	protected void shrinkOtherPanels(ShrinkPanel p) {
		for (ShrinkPanel sp : panels) {
			if (sp != p) {
				sp.setShrinked(true);
			}
		}
	}
	
	@Override
	public void addShrinkPanelAt(ShrinkPanel panel, Point p) {
		super.addShrinkPanelAt(panel, p);
		panel.setShrinked(false);
	}
	
	@Override
	public void addShrinkPanel(ShrinkPanel panel) {
		super.addShrinkPanel(panel);
		panel.setShrinked(false);
	}
    
}
