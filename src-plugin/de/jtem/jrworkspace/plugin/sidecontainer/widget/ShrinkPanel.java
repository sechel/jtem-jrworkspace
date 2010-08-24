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

import static de.jtem.jrworkspace.plugin.sidecontainer.image.ImageHook.setDialogIconImage;
import static java.awt.event.MouseEvent.BUTTON1;
import static java.awt.event.MouseEvent.NOBUTTON;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridBagLayoutInfo;
import java.awt.Image;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import de.jtem.jrworkspace.plugin.sidecontainer.image.ImageHook;



/**
 * A swing container class 
 * <p>
 * Copyright 2005 <a href="http://www.sechel.de">Stefan Sechelmann</a>
 * <a href="http://www.math.tu-berlin.de/geometrie">TU-Berlin</a> 
 * @author Stefan Sechelmann
 */
public class ShrinkPanel extends JPanel {
	
	private static final long 
		serialVersionUID = 1L;
	private int 
		preferredPosition = 0,
		inset = 2,
		contentInset = inset + 2,
		boxInset = 6,
		boxHeight = 13,
		boxMiddle = inset + boxHeight / 2;
	private boolean
		shrinked = false,
		floating = false,
		showHelpIcon = false;
	private HelpCalledListener
		helpCalledListener = null;
	private Color
		header_color = new Color(0.7f, 0.7f, 0.7f);
	private Font
		boldFont = new Font("Tahoma", Font.BOLD, 9),
		normalFont = new Font("Tahoma", Font.PLAIN, 9);
	private FloatDialog 
		floatingDialog = new FloatDialog();
	private boolean
		floatable = true,
		fillSpace = false;
	private ShrinkSlot
		parentContainer = null;
	private Dimension
		floatingSize = null;
	private Point
		floatingLocation = null;
	private Dimension	
		shrinkedSize = new Dimension(10, boxHeight + inset * 2);
	
	private JPanel 
		content = new JPanel();
	private Icon
		icon = null;
	private Image
		iconImage = null;
	
	private ClickShrinkAdapter
		clickAdapter = new ClickShrinkAdapter();
	private DragAdapter
		dragAdapter = new DragAdapter();
	private GridBagConstraints 
		c = new GridBagConstraints();
	
	
	private String name = "A Shrink Panel";
	
	
	public ShrinkPanel(String name, boolean fill) {
	    this(name);
	    this.fillSpace = fill;
	}
	
	public ShrinkPanel(String name) {
		this.name = name;
		setName(name);
		super.setLayout(new GridBagLayout());
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.insets = new Insets(boxHeight + contentInset, contentInset, contentInset, contentInset);
		
		content.setLayout(new MinSizeGridBagLayout());
		super.add(content, c);
		setBorder(null);
		
		addMouseListener(clickAdapter);
		addMouseMotionListener(dragAdapter);
		addMouseListener(dragAdapter);
	}
	
	
	public static class MinSizeGridBagLayout extends GridBagLayout  {
		
		private static final long 
			serialVersionUID = 1L;

		@Override
		protected GridBagLayoutInfo getLayoutInfo(Container parent, int sizeflag) {
			return super.getLayoutInfo(parent, GridBagLayout.MINSIZE);
		}
		
	}
	
	public static interface HelpCalledListener {
		
		public void helpCalled();
		
	}
	
	private static class FloatDialog extends JDialog {

		private static final long serialVersionUID = 1L;

		public FloatDialog() {
			super();
		}

		public FloatDialog(Frame owner, boolean modal) {
			super(owner, modal);
		}
		
	}
	
	
	@Override
	public void setLayout(LayoutManager layout) {
		if (content == null) {
			super.setLayout(layout);
		} else {
			content.setLayout(layout);
		}
	}
	

	@Override
	public Component add(Component arg0) {
		content.add(arg0);
		return arg0;
	}
	

	@Override
	public void add(Component arg0, Object arg1) {
		content.add(arg0, arg1);
	}
	

	@Override
	public Component add(Component arg0, int arg1) {
		return content.add(arg0, arg1);
	}
	

	@Override
	public void add(Component arg0, Object arg1, int arg2) {
		content.add(arg0, arg1, arg2);
	}
	
	@Override
	public Component add(String arg0, Component arg1) {
		return content.add(arg0, arg1);
	}


    @Override
	public void remove(Component arg0) {
        content.remove(arg0);
    }


    @Override
	public void removeAll() {
        content.removeAll();
    }

    
    
	public void setFloating(boolean floating){
		if (this.floating == floating) {
			return;
		}
		this.floating = floating;
	    if (floating){
	    	floatingLocation = this.getLocationOnScreen();
	        if (!shrinked) {
	            setShrinked(true);
	        }
			super.remove(content);
			Window w = SwingUtilities.getWindowAncestor(parentContainer);
			floatingDialog = new FloatDialog((Frame)w, false);
			if (iconImage != null) {
				setDialogIconImage(floatingDialog, iconImage);
			}
			floatingDialog.setTitle(name);
			floatingDialog.addWindowListener(new WindowAdapter(){
		        @Override
				public void windowClosing(WindowEvent arg0) {
		            super.windowClosed(arg0);
		            setFloating(false);
		            setShrinked(false);
		        } 
			});
			floatingDialog.getContentPane().add(content);
		    floatingSize = getSize();
		    floatingSize.height += 10;
			floatingDialog.setSize(floatingSize);
			floatingDialog.setResizable(true);
			floatingDialog.setLocation(floatingLocation);
	        floatingDialog.setVisible(true);
	        Dimension minSize = floatingDialog.getLayout().minimumLayoutSize(floatingDialog);
	        floatingDialog.setMinimumSize(minSize);
	    } else {
	        floatingSize = floatingDialog.getSize();
	        floatingLocation = floatingDialog.getLocationOnScreen();
	        floatingDialog.remove(content);
	        floatingDialog.setVisible(false);
			super.add(content, c);
			super.doLayout();
			parentContainer.addShrinkPanel(this);
			revalidate();
	    }
	}


	public boolean isFloating() {
		return floating;
	}
	
	
	public void setShrinked(boolean shrink){
		this.shrinked = shrink;
		if (shrink) {
			setMinimumSize(shrinkedSize);
			setMaximumSize(shrinkedSize);
			setPreferredSize(shrinkedSize);
		} else {
			setMinimumSize(null);
			setMaximumSize(null);
			setPreferredSize(null);
		}
		if (getParent() != null && getParent() instanceof JComponent) {
			Window w = SwingUtilities.getWindowAncestor(this);
			if (w != null && (w instanceof FloatDialog)) {
				Dimension d = w.getLayout().minimumLayoutSize(w);
		        w.setMinimumSize(d);
		        w.setSize(w.getWidth(), d.height);
		        w.invalidate();
		        w.validate();
			}
			((JComponent)getParent()).revalidate();
		}
		if (parentContainer != null) {
			parentContainer.revalidate();
		}
		if (parentContainer instanceof ShrinkSlotVertical) {
			ShrinkSlotVertical ssv = (ShrinkSlotVertical)parentContainer;
			ssv.updateLayout();
		}
	}

	
	
	public boolean isShrinked() {
		return shrinked;
	}

	
	@Override
	public void paint(Graphics g) {
		try {
			super.paint(g);
		} catch (Exception e) {}
		Graphics2D g2D = (Graphics2D)g;
		g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Dimension dim = getSize();
		g.setColor(Color.BLACK);
		g.drawRoundRect(inset, boxMiddle, dim.width - inset * 2 - 1, dim.height - boxMiddle - inset - 1, 7, 7);
		// header box
//		if (parentContainer != null) { // only top level shrink panels can have colors
//			g.setColor(header_color);
			g2D.setPaint(new GradientPaint(0, 0, getBackground(), 0, boxHeight, header_color));
//		} else {
//			g.setColor(getBackground());
//		}
//		if (parentContainer != null) {
//			g.fillRoundRect(boxInset, inset, dim.width - boxInset * 2 - 1, boxHeight, 7, 7);
//		} else {
			g.fillRect(boxInset, inset, dim.width - boxInset * 2 - 1, boxHeight);
//		}
		// border
		g.setColor(Color.BLACK);
//		if (parentContainer != null) {
//			g.drawRoundRect(boxInset, inset, dim.width - boxInset * 2 - 1, boxHeight, 7, 7);
//		} else {
			g.drawRect(boxInset, inset, dim.width - boxInset * 2 - 1, boxHeight);
//		}
		// header text
//		Font font = UIManager.getFont("Label.font");
		if (parentContainer != null) {
			g.setFont(boldFont);
		} else {
			g.setFont(normalFont);
		}
		g.setColor(Color.BLACK);
		Rectangle2D bounds = boldFont.getStringBounds(name, g2D.getFontRenderContext());
		g.drawString(name, boxInset + (dim.width - boxInset * 2) / 2 - (int)bounds.getWidth() / 2, inset + boxHeight - 3);
		// help button
		if (showHelpIcon) {
			Rectangle2D bounds2 = boldFont.getStringBounds("?", g2D.getFontRenderContext());
			g.drawString("?", dim.width - boxInset - inset * 2 - (int)bounds2.getWidth(), inset + boxHeight - 3);
		}
		if (icon != null) {
			icon.paintIcon(this, g2D, boxInset + inset, inset + 1);
		} else {
			g.drawString(shrinked ? "+" : "-" , boxInset + inset, inset + boxHeight - 3);
		}
	}

	
    public void setParentSlot(ShrinkSlot ps) {
    	parentContainer = ps;
    }
    
    
    public ShrinkSlot getParentSlot() {
        return parentContainer;
    }

    
    protected class ClickShrinkAdapter extends MouseAdapter{
    	
		@Override
		public void mouseClicked(MouseEvent m) {
			Dimension dim = getSize();
			if (showHelpIcon) { // check if help is clicked
				Graphics2D g2D = (Graphics2D)m.getComponent().getGraphics();
				Font font = UIManager.getFont("Label.font");
				Rectangle2D bounds2 = font.getStringBounds("?", g2D.getFontRenderContext());
				int startX = dim.width - boxInset - inset * 2 - (int)bounds2.getWidth();
				int endX = dim.width - boxInset - inset * 2;
				if (startX <= m.getX() && m.getX() <= endX &&
						inset <= m.getY() && m.getY() <= inset + boxHeight) {
					if (helpCalledListener != null) {
						helpCalledListener.helpCalled();
					}
					return;
				}
			}
			if (boxInset <= m.getX() && m.getX() <= dim.width - boxInset &&
				inset <= m.getY() && m.getY() <= inset + boxHeight) {
				if (m.getButton() == MouseEvent.BUTTON3) {
					fillSpace = !fillSpace;
					if (fillSpace) {
						setShrinked(false);
					}
					if (parentContainer instanceof ShrinkSlotVertical) {
						ShrinkSlotVertical ssv = (ShrinkSlotVertical)parentContainer;
						ssv.updateLayout();
					}
				} 
				if (m.getButton() == MouseEvent.BUTTON1) {
					setShrinked(!shrinked);
				}
			}
		}
		
    }
    
    
    
    protected class DragAdapter extends MouseAdapter implements MouseMotionListener {
    	
    	private Point
			dragHandle = new Point();
    	private JDialog
    		dialog = null;
    	private Color
    		borderColor = new Color(240, 200, 190);
    		
    	
		public void mouseDragged(MouseEvent e) {
			if (!isFloatable()) {
				return;
			}
			if (parentContainer == null) {
				mouseReleased(e);
				return;
			}
			if (e.getButton() != NOBUTTON && e.getButton() != BUTTON1) {
				mouseReleased(e);
				return;
			}
			if (dialog == null) {
				if (e.getY() > inset + boxHeight) {
					return;
				}
				dragHandle = e.getPoint();
				Window w = SwingUtilities.getWindowAncestor(parentContainer);
				dialog = new JDialog((Frame)w, false);
				dialog.setUndecorated(true);
				dialog.setTitle(name);
				getParentSlot().removeShrinkPanel(ShrinkPanel.this);
				Dimension size = getSize();
				size.width += 4; // border insets
				size.height += 4;
				dialog.setSize(size);
				dialog.add(ShrinkPanel.this);
				dialog.setLocationByPlatform(false);
				Point l = getLocationOnScreen(e);
				Point p = new Point(l.x - dragHandle.x, l.y - dragHandle.y);
				dialog.setLocation(p);
				dialog.setVisible(true);
			}
			Point l = getLocationOnScreen(e);
			Point p = new Point(l.x - dragHandle.x, l.y - dragHandle.y);
			dialog.setLocation(p);
			ShrinkSlot slot = ShrinkSlot.getContainerUnderMouse(getLocationOnScreen(e));
			if (slot == null) {
				ShrinkSlot.hideAllInsertHints();
				dialog.getRootPane().setBorder(BorderFactory.createEtchedBorder());
			} else {
				SwingUtilities.convertPointFromScreen(l, slot);
		    	slot.showInsertHint(true, l);
				dialog.getRootPane().setBorder(BorderFactory.createEtchedBorder(borderColor, Color.GRAY));
			}
			repaint();
		}
	
		public void mouseMoved(MouseEvent e) {
		}
	
		@Override
		public void mouseReleased(MouseEvent e) {
			if (dialog == null)
				return;
			ShrinkSlot c = ShrinkSlot.getContainerUnderMouse(getLocationOnScreen(e));
			if (c != null) {
				dialog.setVisible(false);
				dialog.removeAll();
		    	dialog.dispose();
		    	Point p = getLocationOnScreen(e);
		    	SwingUtilities.convertPointFromScreen(p, c);
		    	c.addShrinkPanelAt(ShrinkPanel.this, p);
		    	c.showInsertHint(false, p);
		    	c.revalidate();
			} else {
				setFloating(true);
		    	dialog.setVisible(false);
			}
			dialog = null;
			repaint();
		}
		
    }

	public boolean isFillSpace() {
		return fillSpace;
	}

	public void setFillSpace(boolean fillSpace) {
		this.fillSpace = fillSpace;
	}
	
	public JPanel getContentPanel() {
		return content;
	}
	
	public void setContentPanel(JPanel content) {
		super.removeAll();
		this.content = content;
		super.add(this.content, c);
	}
	
	
	public int getPreferredPosition() {
		return preferredPosition;
	}
	
	public void setPreferredPosition(int preferredPosition) {
		this.preferredPosition = preferredPosition;
	}
	
	@Override
	public String toString() {
		return "ShrinkPanel (" + name + ")";
	}
	
	public void setHeaderColor(Color color) {
		this.header_color = color;
		repaint();
	}
	
	public void setTitle(String title) {
		this.name = title;
		repaint();
	}
	
	public void setShowHelpIcon(boolean showHelpIcon) {
		if (showHelpIcon) {
			setToolTipText("Click ? For Help");
		} else {
			setToolTipText(null);
		}
		this.showHelpIcon = showHelpIcon;
	}
	
	public void setHelpCalledListener(HelpCalledListener helpCalledListener) {
		this.helpCalledListener = helpCalledListener;
	}
	
	private Point getLocationOnScreen(MouseEvent e) {
		PointerInfo info = MouseInfo.getPointerInfo();
		return info.getLocation();
	}
	
	public void setIcon(Icon icon) {
		if (icon != null) {
			this.icon = ImageHook.scaleIcon(icon, 15, 15);
			this.iconImage = ImageHook.toImage(icon);
		}
	}
	
	public Icon getIcon() {
		return icon;
	}
	
	@Override
	public void updateUI() {
		super.updateUI();
		if (floatingDialog != null) {
			SwingUtilities.updateComponentTreeUI(floatingDialog);
		}
	}
	
	public void setFloatable(boolean floatable) {
		if (!floatable) {
			setFloating(false);
		}
		this.floatable = floatable;
	}
	
	public boolean isFloatable() {
		return floatable;
	}
	
}
