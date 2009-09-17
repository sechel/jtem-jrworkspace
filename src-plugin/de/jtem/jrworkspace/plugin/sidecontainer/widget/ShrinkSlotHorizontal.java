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
