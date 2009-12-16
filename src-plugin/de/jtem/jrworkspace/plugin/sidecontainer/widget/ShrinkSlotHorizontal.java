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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;

public class ShrinkSlotHorizontal extends ShrinkSlot {

	private static final long 
		serialVersionUID = 1L;
	private GridBagConstraints
		gbc = new GridBagConstraints();
	private ShrinkPanel
		panel = null;
	private Dimension
		emptySize = null;
	private boolean
		showInsertHint = false;
	private int
		height = 150;
	
	public ShrinkSlotHorizontal(int height) {
		this.height = height;
		emptySize = new Dimension(10, height);
		setPreferredSize(emptySize);
		setLayout(new GridBagLayout());
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridwidth = 1;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
	}
	
	
	@Override
	public void addShrinkPanel(ShrinkPanel sp) {
		this.panel = sp;
		sp.setParentSlot(this);
		setPreferredSize(null);
		add(sp, gbc);
		revalidate();
	}
	
	@Override
	public void addShrinkPanelAt(ShrinkPanel s, Point p) {
		addShrinkPanel(s);
	}
	
	
	@Override
	public void removeShrinkPanel(ShrinkPanel sp) {
		this.panel = null;
		setPreferredSize(emptySize);
		remove(sp);
		revalidate();
	}

	
	public boolean isOccupied() {
		return panel != null;
	}

	
	@Override
	public void showInsertHint(boolean show, Point p) {
		showInsertHint = show;
		repaint();
	}
	
    
    @Override
    public void paint(Graphics g) {
    	super.paint(g);
    	if (showInsertHint) {
    		Dimension size = getSize();
    		g.setColor(Color.BLACK);
    		g.drawRect(0, 0, size.width - 1, size.height - 1);
    	}
    }
    
    public int getPreferredHeight() {
    	return height;
    }
    
}
