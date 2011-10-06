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
import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.REMAINDER;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

/**
 * A container class which can stack ShrinkPanels and scroll them
 * <p>
 * Copyright 2005 <a href="http://www.sechel.de">Stefan Sechelmann</a>
 * <a href="http://www.math.tu-berlin.de/geometrie">TU-Berlin</a> 
 * @author Stefan Sechelmann
 */
public class ShrinkSlotVertical extends ShrinkSlot implements MouseMotionListener, MouseListener, MouseWheelListener {
	
	private static final long 
		serialVersionUID = 1L;
	protected JPanel 
		content = new JPanel();
	protected JScrollPane
		scroller = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	protected List<ShrinkPanel>
		panels = new LinkedList<ShrinkPanel>();
	private Map<ShrinkPanel, Integer>
		positionMap = new HashMap<ShrinkPanel, Integer>(); 
	private GridBagLayout 
		gbl = new GridBagLayout();
	protected GridBagConstraints 
		normalConstraints = new GridBagConstraints(),
		fillConstraints = new GridBagConstraints();
	private JPanel
		fillPanel = new JPanel();
	private int 
		lastDrag = 0;
	private boolean
		showInsertHint = false;
	private Point
		insertHintPoint = new Point();
	private int
		width = 200;
	
	
	public ShrinkSlotVertical(int width){
		this.width = width;
		content.setLayout(gbl);
		
		fillConstraints.fill = BOTH;
		fillConstraints.gridheight = 1;
		fillConstraints.gridwidth = REMAINDER;
		fillConstraints.weightx = 1.0;
		fillConstraints.weighty = 1.0;
		
		normalConstraints.fill = BOTH;
		normalConstraints.gridheight = 1;
		normalConstraints.gridwidth = REMAINDER;
		normalConstraints.weightx = 1.0;
		normalConstraints.weighty = 0.0;

		scroller.setViewportView(content);
		scroller.setBorder(BorderFactory.createEmptyBorder());
		scroller.addMouseWheelListener(this);
		
		setLayout(new GridLayout());
		super.add(scroller);
		updateLayout();
	}
	
	protected void updateLayout() {
		content.removeAll();
		boolean fillerNeeded = true;
		for (ShrinkPanel sp : panels) {
			if (sp.isFillSpace()) {
				if (sp.isShrinked()) {
					content.add(sp, normalConstraints);
				} else {
					fillerNeeded = false;
					content.add(sp, fillConstraints);
				}
			} else {
				content.add(sp, normalConstraints);
			}
		}
		if (fillerNeeded) {
			content.add(fillPanel, fillConstraints);
		}
		if (panels.size() == 0) {
			setPreferredSize(new Dimension(10, 10));
			setMinimumSize(new Dimension(10, 10));
		} else {
			setPreferredSize(null);
			setMinimumSize(new Dimension(width, 10));
		}
		content.doLayout();
		revalidate();
		updateUI();
	}
	
	
	@Override
    public Component add(Component arg0) {
		throw new UnsupportedOperationException("Use addShrinkPanel()");
    }

    
	@Override
	public void addShrinkPanel(ShrinkPanel panel){
		addShrinkPanelAt(panel, panel.getPreferredPosition());
	}

	
	@Override
	public void addShrinkPanelAt(ShrinkPanel panel, Point p) {
		int scroll = scroller.getVerticalScrollBar().getValue();
		Point scrollPoint = new Point(p.x, p.y + scroll);
		Component c = content.getComponentAt(scrollPoint);
		if (c instanceof ShrinkPanel) {
			addShrinkPanelAt(panel, positionMap.get(c));
		} else {
			TreeSet<Integer> posSet = new TreeSet<Integer>(positionMap.values());
			if (posSet.size() != 0) {
				addShrinkPanelAt(panel, posSet.last() + 1);
			} else {
				addShrinkPanelAt(panel, 0);
			}
		}
		positionMap.clear();
		for (ShrinkPanel pl : panels) {
			positionMap.put(pl, panels.lastIndexOf(pl));
		}
	}
	
	
	protected void addShrinkPanelAt(ShrinkPanel p, int pos) {
		Collection<Integer> occupied = positionMap.values();
		if (occupied.contains(pos)) {
			List<ShrinkPanel> panelList = new LinkedList<ShrinkPanel>(positionMap.keySet());
			for (ShrinkPanel panel : panelList) {
				int panelPos = positionMap.get(panel);
				if (panelPos >= pos) {
					positionMap.remove(panel);
					positionMap.put(panel, panelPos + 1);
					panel.setPreferredPosition(panelPos + 1);
				}
			}
		} 
		positionMap.put(p, pos);
		p.setPreferredPosition(pos); 

		TreeSet<Integer> posSet = new TreeSet<Integer>(positionMap.values());
		ShrinkPanel[] panelArray = new ShrinkPanel[posSet.last() + 1];
		for (ShrinkPanel panel : positionMap.keySet()) {
			panelArray[positionMap.get(panel)] = panel;
		}
		
		panels.clear();
		for (ShrinkPanel panel : panelArray) {
			if (panel != null) {
				panels.add(panel);
			}
		}
		
		updateMouseListeners(p);
		p.setParentSlot(this);
		updateLayout();
		revalidate();
		repaint();
	}
	

	@Override
	public void removeShrinkPanel(ShrinkPanel panel){
	    content.remove(panel);
	    panels.remove(panel);
	    positionMap.remove(panel);
	    removeMouseListeners(panel);
	    updateLayout();
	    revalidate();
	}
	
	
	private void updateMouseListeners(Container container) {
		if (container instanceof JScrollPane) {
			return;
		}
		container.removeMouseListener(this);
		container.removeMouseMotionListener(this);
		container.removeMouseWheelListener(this);
		container.addMouseListener(this);
		container.addMouseMotionListener(this);
		container.addMouseWheelListener(this);
		for (Component c : container.getComponents()) {
			if (c instanceof Container) {
				updateMouseListeners((Container) c);
			}
		}
	}
	
	
	
	private void removeMouseListeners(Container container) {
		container.removeMouseWheelListener(this);
		container.removeMouseMotionListener(this);
		container.removeMouseListener(this);
		for (Component c : container.getComponents()) {
			if (c instanceof Container) {
				removeMouseListeners((Container) c);
			}
		}
	}
	
	
	public void mouseDragged(MouseEvent e) {
		JScrollBar vert = scroller.getVerticalScrollBar();
		int schroll_pos = vert.getValue();
		vert.setValue(schroll_pos - (e.getY() - lastDrag));
		lastDrag = e.getY() - (e.getY() - lastDrag);
	}

	public void mouseMoved(MouseEvent arg0) {
	}
	public void mouseClicked(MouseEvent arg0) {
	}


	public void mousePressed(MouseEvent arg0) {
		lastDrag = arg0.getY();
	}

	public void mouseReleased(MouseEvent arg0) {
	}
	public void mouseEntered(MouseEvent arg0) {
	}
	public void mouseExited(MouseEvent arg0) {
	}

    public void mouseWheelMoved(MouseWheelEvent m) {
        int dx = -m.getUnitsToScroll() * 5;
		JScrollBar vert = scroller.getVerticalScrollBar();
		int schroll_pos = vert.getValue();
		vert.setValue(schroll_pos - dx);
		lastDrag = dx;
    }

    
    @Override
    protected void paintChildren(Graphics g) {
    	scroller.setBorder(null);
    	super.paintChildren(g);
    }
    
    @Override
    public void showInsertHint(boolean show, Point p) {
    	showInsertHint = show;
    	insertHintPoint = p;
    	repaint();
    }
    
    
    @Override
    public void paint(Graphics g) {
    	super.paint(g);
    	if (showInsertHint) {
    		Dimension size = getSize();
    		g.setColor(Color.BLACK);
    		g.drawRect(0, 0, size.width - 1, size.height - 1);
    		
    		int scroll = scroller.getVerticalScrollBar().getValue();
    		Point scrollPoint = new Point(insertHintPoint.x, insertHintPoint.y + scroll);
    		Component c = content.getComponentAt(scrollPoint);
    		if (c != null) { 
				Rectangle r = c.getBounds();
				int hint = Math.max(r.y + 1 - scroll, 0);
				g.draw3DRect(0, hint, getSize().width, 2, false);
    		}
    	}
    }
    
    public int getPreferredWidth() {
    	return width;
    }
    
    @Override
    public void updateUI() {
    	super.updateUI();
    	if (fillPanel != null) {
    		fillPanel.updateUI();
    	}
    }
    
}
