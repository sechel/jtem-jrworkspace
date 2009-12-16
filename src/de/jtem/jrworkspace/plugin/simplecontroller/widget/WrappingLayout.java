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

package de.jtem.jrworkspace.plugin.simplecontroller.widget;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

public class WrappingLayout implements LayoutManager {
	public static final int LEFT = 0;
	public static final int CENTER = 1;
	public static final int RIGHT = 2;
	public static final int LEADING = 3;
	public static final int TRAILING = 4;

	int align;
	int hgap;
	int vgap;

	public WrappingLayout() {
		this(CENTER, 5, 5);
	}

	public WrappingLayout(int align) {
		this(align, 5, 5);
	}

	public WrappingLayout(int align, int hgap, int vgap) {
		this.hgap = hgap;
		this.vgap = vgap;
		setAlignment(align);
	}

	public int getAlignment() {
		return align;
	}

	public void setAlignment(int align) {
		switch (align) {
		case LEADING:
			this.align = LEFT;
			break;
		case TRAILING:
			this.align = RIGHT;
			break;
		default:
			this.align = align;
			break;
		}
	}

	public int getHgap() {
		return hgap;
	}

	public void setHgap(int hgap) {
		this.hgap = hgap;
	}

	public int getVgap() {
		return vgap;
	}

	public void setVgap(int vgap) {
		this.vgap = vgap;
	}

	public Dimension preferredLayoutSize(Container parent) {
		synchronized (parent.getTreeLock()) {
			Dimension dim = new Dimension(0, 0);
			int maxWidth = 0;
			int componentCount = parent.getComponentCount();

			for (int i = 0; i < componentCount; i++) {
				Component c = parent.getComponent(i);
				if (c.isVisible()) {
					Dimension d = c.getPreferredSize();
					if ((dim.width + d.width + hgap) <= parent.getWidth()) {
						dim.height = Math.max(dim.height, d.height);
					} else {
						dim.height += vgap + d.height;
						dim.width = 0;
					}
					if (dim.width > 0) {
						dim.width += hgap;
					}
					dim.width += d.width;
					if (dim.width > maxWidth) {
						maxWidth = dim.width;
					}
				}
			}
			Insets insets = parent.getInsets();
			dim.width = Math.max(dim.width, maxWidth);
			dim.width += insets.left + insets.right + 2 * hgap;
			dim.height += insets.top + insets.bottom + 2 * vgap;
			return dim;
		}
	}

	public Dimension minimumLayoutSize(Container parent) {
		synchronized (parent.getTreeLock()) {
			Dimension dim = new Dimension(0, 0);
			int componentCount = parent.getComponentCount();

			for (int i = 0; i < componentCount; i++) {
				Component c = parent.getComponent(i);
				if (c.isVisible()) {
					Dimension d = c.getMinimumSize();
					dim.height = Math.max(dim.height, d.height);
					if (i > 0) {
						dim.width += hgap;
					}
					dim.width += d.width;
				}
			}
			Insets insets = parent.getInsets();
			dim.width += insets.left + insets.right + 2 * hgap;
			dim.height += insets.top + insets.bottom + 2 * vgap;
			return dim;
		}
	}

	public void layoutContainer(Container parent) {
		synchronized (parent.getTreeLock()) {
			Insets insets = parent.getInsets();
			int maxWidth = parent.getWidth()
					- (insets.left + insets.right + hgap * 2);
			int componentCount = parent.getComponentCount();
			int x = 0, y = insets.top + vgap;
			int rowh = 0, start = 0;
			boolean ltr = parent.getComponentOrientation().isLeftToRight();

			for (int i = 0; i < componentCount; i++) {
				Component c = parent.getComponent(i);
				if (c.isVisible()) {
					Dimension d = c.getPreferredSize();
					c.setSize(d.width, d.height);
					if ((x == 0) || ((x + d.width) <= maxWidth)) {
						if (x > 0) {
							x += hgap;
						}
						x += d.width;
						rowh = Math.max(rowh, d.height);
					} else {
						rowh = moveComponents(parent, insets.left + hgap, y,
								maxWidth - x, rowh, start, i, ltr);
						x = d.width;
						y += vgap + rowh;
						rowh = d.height;
						start = i;
					}
				}
			}
			moveComponents(parent, insets.left + hgap, y, maxWidth - x, rowh,
					start, componentCount, ltr);
		}
	}

	private int moveComponents(Container parent, int x, int y, int width,
			int height, int rowStart, int rowEnd, boolean ltr) {
		switch (align) {
		case LEFT:
			x += ltr ? 0 : width;
			break;
		case CENTER:
			x += width / 2;
			break;
		case RIGHT:
			x += ltr ? width : 0;
			break;
		case LEADING:
			break;
		case TRAILING:
			x += width;
			break;
		}
		for (int i = rowStart; i < rowEnd; i++) {
			Component c = parent.getComponent(i);
			if (c.isVisible()) {
				int cy;
				cy = y + (height - c.getHeight()) / 2;
				if (ltr) {
					c.setLocation(x, cy);
				} else {
					c.setLocation(parent.getWidth() - x - c.getWidth(), cy);
				}
				x += c.getWidth() + hgap;
			}
		}
		return height;
	}

	public void addLayoutComponent(String name, Component comp) {
	}

	public void removeLayoutComponent(Component comp) {
	}
}