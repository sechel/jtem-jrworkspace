package de.jtem.jrworkspace.plugin.sidecontainer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;

/**
 * This layout honors the preferred size as well as 
 * the minimum size of the shrink slots 
 * @author Stefan Sechelmann
 */
public class SideContainerLayout extends BorderLayout {

	private static final long 
		serialVersionUID = 1L;
	private Component
		leftSlot = null,
		rightSlot = null,
		centerPanel = null;
	
	public SideContainerLayout(Component leftSlot, Component rightSlot, Component centerPanel) {
		super();
		this.leftSlot = leftSlot;
		this.rightSlot = rightSlot;
		this.centerPanel = centerPanel;
	}

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