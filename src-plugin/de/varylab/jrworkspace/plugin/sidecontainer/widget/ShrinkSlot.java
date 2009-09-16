package de.varylab.jrworkspace.plugin.sidecontainer.widget;

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
    public static ShrinkSlot getContainerUnderMouse(Point onScreen) {
    	for (ShrinkSlot c : instances) {
    		if (!c.isShowing()) continue;
    		Point l = c.getLocationOnScreen();
    		Dimension size = c.getSize();
    		if (l.x < onScreen.x && onScreen.x < l.x + size.width &&
    			l.y < onScreen.y && onScreen.y < l.y + size.height) {
    			if (c instanceof ShrinkSlotHorizontal) {
    				ShrinkSlotHorizontal slh = (ShrinkSlotHorizontal)c;
    				if (slh.isOccupied()) {
    					continue;
    				}
    			}
    			return c;
    		}
    	}
    	return null;
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
