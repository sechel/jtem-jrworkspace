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

package de.jtem.jrworkspace.plugin.sidecontainer;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.EAST;
import static java.awt.BorderLayout.NORTH;
import static java.awt.BorderLayout.SOUTH;
import static java.awt.BorderLayout.WEST;
import static javax.swing.KeyStroke.getKeyStroke;
import static javax.swing.SwingUtilities.isDescendingFrom;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JPanel;

import de.jtem.jrworkspace.plugin.Controller;
import de.jtem.jrworkspace.plugin.Plugin;
import de.jtem.jrworkspace.plugin.flavor.PerspectiveFlavor;
import de.jtem.jrworkspace.plugin.sidecontainer.image.ImageHook;
import de.jtem.jrworkspace.plugin.sidecontainer.widget.ShrinkSlot;
import de.jtem.jrworkspace.plugin.sidecontainer.widget.ShrinkSlotHorizontal;
import de.jtem.jrworkspace.plugin.sidecontainer.widget.ShrinkSlotVertical;

public abstract class SideContainerPerspective extends Plugin implements PerspectiveFlavor, ActionListener {

	private JPanel
		mainPanel = new JPanel();
	private ShrinkSlotVertical
		leftSlot = new ShrinkSlotVertical(250),
		rightSlot = new ShrinkSlotVertical(250);
	private ShrinkSlotHorizontal
		upperSlot = new ShrinkSlotHorizontal(),
		lowerSlot = new ShrinkSlotHorizontal();
	private JPanel
		centerPanel = new JPanel(),
		content = new JPanel();
	private JMenu
		panelsMenu = new JMenu("Panels"),
		containersMenu = new JMenu("Side Panels");
	private JCheckBoxMenuItem
		hidePanelsItem = new JCheckBoxMenuItem("Hide Alle Panels"),	
		showLeftSlotItem = new JCheckBoxMenuItem("Left Slot"),
		showRightSlotItem = new JCheckBoxMenuItem("Right Slot"),
		showTopSlotItem = new JCheckBoxMenuItem("Top Slot"),
		showBottomSlotItem = new JCheckBoxMenuItem("Bottom Slot");
	private boolean 
		hidePanels = false,
		showLeft = true,
		showRight = true,
		showTop = true,
		showBottom = true;
	
	public SideContainerPerspective() {
		this(false);
	}
	
	public SideContainerPerspective(boolean hidePanels) {
		this.hidePanels = hidePanels;
		leftSlot.setBorder(BorderFactory.createEtchedBorder());
		rightSlot.setBorder(BorderFactory.createEtchedBorder());
		upperSlot.setBorder(BorderFactory.createEtchedBorder());
		lowerSlot.setBorder(BorderFactory.createEtchedBorder());
		
		centerPanel.setLayout(new BorderLayout());
		centerPanel.add(getContentPanel(), CENTER);
		mainPanel.setLayout(new ShrinkLayout());
		mainPanel.add(centerPanel, CENTER);
		
		panelsMenu.setIcon(ImageHook.getIcon("menu.png"));
		
		containersMenu.add(hidePanelsItem);
		containersMenu.add(showLeftSlotItem);
		containersMenu.add(showRightSlotItem);
		containersMenu.add(showTopSlotItem);
		containersMenu.add(showBottomSlotItem);
		
		hidePanelsItem.setAccelerator(getKeyStroke(KeyEvent.VK_H, InputEvent.ALT_MASK | InputEvent.SHIFT_MASK));
		showLeftSlotItem.setAccelerator(getKeyStroke(KeyEvent.VK_LEFT, InputEvent.ALT_MASK | InputEvent.SHIFT_MASK));
		showRightSlotItem.setAccelerator(getKeyStroke(KeyEvent.VK_RIGHT, InputEvent.ALT_MASK | InputEvent.SHIFT_MASK));
		showTopSlotItem.setAccelerator(getKeyStroke(KeyEvent.VK_UP, InputEvent.ALT_MASK | InputEvent.SHIFT_MASK));
		showBottomSlotItem.setAccelerator(getKeyStroke(KeyEvent.VK_DOWN, InputEvent.ALT_MASK | InputEvent.SHIFT_MASK));
	}
	
	
	
	/**
	 * This layout honors the preferred size as well as 
	 * the minimum size of the shrink slots 
	 * @author Stefan Sechelmann
	 */
	private class ShrinkLayout extends BorderLayout {

		private static final long 
			serialVersionUID = 1L;

		@Override
		public Dimension preferredLayoutSize(Container target) {
			layoutContainer(target);
			Dimension size = new Dimension();
			if (target.isAncestorOf(leftSlot)) {
				size.width += leftSlot.getSize().width;
			}
			if (target.isAncestorOf(rightSlot)) {
				size.width += rightSlot.getSize().width;
			}
			size.width += centerPanel.getPreferredSize().width;
			size.height = centerPanel.getPreferredSize().height;
			return size;
		}
		
		
		@Override
		public Dimension minimumLayoutSize(Container target) {
			layoutContainer(target);
			Dimension size = new Dimension();
			if (target.isAncestorOf(leftSlot)) {
				size.width += leftSlot.getMinimumSize().width;
			}
			if (target.isAncestorOf(rightSlot)) {
				size.width += rightSlot.getMinimumSize().width;
			}
			size.width += centerPanel.getMinimumSize().width;
			size.height = centerPanel.getMinimumSize().height;
			return size;
		}
		
		
		@Override
		public void layoutContainer(Container target) {
			synchronized (target.getTreeLock()) {
				Insets insets = target.getInsets();
				int top = insets.top;
				int bottom = target.getHeight() - insets.bottom;
				int left = insets.left;
				int right = target.getWidth() - insets.right;

				if (target.isAncestorOf(rightSlot)) {
					int rightWidth = rightSlot.getPreferredSize().width;
					if (rightWidth < rightSlot.getMinimumSize().width) {
						rightWidth = rightSlot.getMinimumSize().width;
					}
					rightSlot.setSize(rightWidth, bottom - top);
					rightSlot.setBounds(right - rightWidth, top, rightWidth, bottom - top);
					right -= rightWidth + getHgap();
				}

				if (target.isAncestorOf(leftSlot)) {
					int leftWidth = leftSlot.getPreferredSize().width;
					if (leftWidth < leftSlot.getMinimumSize().width) {
						leftWidth = leftSlot.getMinimumSize().width;
					}
					leftSlot.setSize(leftWidth, bottom - top);
					leftSlot.setBounds(left, top, leftWidth, bottom - top);
					left += leftWidth + getHgap();
				}

				centerPanel.setSize(right - left, bottom - top);
				centerPanel.setBounds(left, top, right - left, bottom - top);
			}
		}
		
	}
	
	
	
	private void updateStates() {
		if (hidePanels) {
			centerPanel.remove(upperSlot);
			centerPanel.remove(lowerSlot);
			mainPanel.remove(rightSlot);
			mainPanel.remove(leftSlot);
		} else {
			if (showTop) {
				if (!isDescendingFrom(upperSlot, centerPanel)) {
					centerPanel.add(upperSlot, NORTH);
				}
			} else {
				centerPanel.remove(upperSlot);	
			}
			if (showBottom) {
				if (!isDescendingFrom(lowerSlot, centerPanel)) {
					centerPanel.add(lowerSlot, SOUTH);
				}
			} else {
				centerPanel.remove(lowerSlot);
			}
			if (showLeft) {
				if (!isDescendingFrom(leftSlot, mainPanel)) {
					mainPanel.add(leftSlot, WEST);
				}
			} else {
				mainPanel.remove(leftSlot);
			}
			if (showRight) {
				if (!isDescendingFrom(rightSlot, mainPanel)) {
					mainPanel.add(rightSlot, EAST);
				}
			} else {
				mainPanel.remove(rightSlot);
			}
		}
	}
	
	
	public void actionPerformed(ActionEvent e) {
		hidePanels = hidePanelsItem.isSelected();
		showLeft = showLeftSlotItem.isSelected();
		showRight = showRightSlotItem.isSelected();
		showTop = showTopSlotItem.isSelected();
		showBottom = showBottomSlotItem.isSelected();
		updateStates();
		mainPanel.doLayout();
		mainPanel.updateUI();
	}
	
	
	@Override
	public void storeStates(Controller c) throws Exception {
		super.storeStates(c);
		c.storeProperty(getClass(), "hidePanels", hidePanels);
		c.storeProperty(getClass(), "showLeft", showLeft);
		c.storeProperty(getClass(), "showRight", showRight);
		c.storeProperty(getClass(), "showTop", showTop);
		c.storeProperty(getClass(), "showBottom", showBottom);
	}
	
	@Override
	public void restoreStates(Controller c) throws Exception {
		super.restoreStates(c);
		hidePanels = c.getProperty(getClass(), "hidePanels", hidePanels);
		showLeft = c.getProperty(getClass(), "showLeft", showLeft);
		showRight = c.getProperty(getClass(), "showRight", showRight);
		showTop = c.getProperty(getClass(), "showTop", showTop);
		showBottom = c.getProperty(getClass(), "showBottom", showBottom);
		
		hidePanelsItem.setSelected(hidePanels);
		showLeftSlotItem.setSelected(showLeft);
		showRightSlotItem.setSelected(showRight);
		showTopSlotItem.setSelected(showTop);
		showBottomSlotItem.setSelected(showBottom);
		
		hidePanelsItem.addActionListener(this);
		showLeftSlotItem.addActionListener(this);
		showRightSlotItem.addActionListener(this);
		showTopSlotItem.addActionListener(this);
		showBottomSlotItem.addActionListener(this);
		
		updateStates();
	}
	
	
	public final Component getCenterComponent() {
		return mainPanel;
	}
	
	/**
	 * Returns the panel between the shrink slots
	 * When overriding this method you have to be aware of the fact that
	 * it will be called from the constructor. This means before your 
	 * local variables get initialized.
	 * it is a better idea to use the default content JPanel provided by 
	 * this method, set some Layout to it and forget about overriding.
	 * @return The panel that appears between the shrink slots
	 */
	public JPanel getContentPanel() {
		return content;
	}
	
	
	public ShrinkSlot getLeftSlot() {
		return leftSlot;
	}
	
	public ShrinkSlot getRightSlot() {
		return rightSlot;
	}
	
	public ShrinkSlot getUpperSlot() {
		return upperSlot;
	}
	
	public ShrinkSlot getLowerSlot() {
		return lowerSlot;
	}
	
	public JMenu getPanelsMenu() {
		return panelsMenu;
	}
	
	public JMenu getContaintersMenu() {
		return containersMenu;
	}
	
	public void setHidePanels(boolean hidePanels) {
		this.hidePanels = hidePanels;
		hidePanelsItem.setSelected(hidePanels);
		updateStates();
	}
	
	public boolean isHidePanels() {
		return hidePanels;
	}
	
	public void setShowLeft(boolean showLeft) {
		this.showLeft = showLeft;
		showLeftSlotItem.setSelected(showLeft);
		updateStates();
	}
	
	public void setShowRight(boolean showRight) {
		this.showRight = showRight;
		showRightSlotItem.setSelected(showRight);
		updateStates();
	}
	
	public void setShowTop(boolean showTop) {
		this.showTop = showTop;
		showTopSlotItem.setSelected(showTop);
		updateStates();
	}
	
	public void setShowBottom(boolean showBottom) {
		this.showBottom = showBottom;
		showBottomSlotItem.setSelected(showBottom);
		updateStates();
	}
	
}
