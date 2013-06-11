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

package de.jtem.jrworkspace.plugin.simplecontroller.preferences;

import static javax.swing.JSplitPane.HORIZONTAL_SPLIT;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Image;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

import de.jtem.jrworkspace.plugin.flavor.PreferencesFlavor;
import de.jtem.jrworkspace.plugin.simplecontroller.image.ImageHook;
import de.jtem.jrworkspace.plugin.simplecontroller.widget.AbstractTreeModel;

public class PreferencesWindow extends JDialog implements TreeSelectionListener {

	private static final long 
		serialVersionUID = 1L;
	private Image
		dialogIcon = ImageHook.getImage("prefs.png");
	private LinkedList<PreferencesFlavor>
		plugins = new LinkedList<PreferencesFlavor>();
	private NavTreeModel
		navTreeModel = new NavTreeModel();
	private JTree
		navTree = new JTree(navTreeModel);
	private JPanel
		pagePanel = new JPanel();	
	private JScrollPane
		navScroller = new JScrollPane(navTree),
		pageScroller = new JScrollPane(pagePanel);
	private JSplitPane
		splitter = new JSplitPane(HORIZONTAL_SPLIT, navScroller, pageScroller);
	private Icon
		defaultIcon = ImageHook.getIcon("prefs.png");

	
	public PreferencesWindow(Frame parent) {
		super(parent);
		ImageHook.setIconImage(this, dialogIcon);
		setTitle("jRWorkspace Preferences");
		makeLayout();
		navTree.setCellRenderer(navTreeModel);
		navTree.getSelectionModel().addTreeSelectionListener(this);
	}
	
	
	private void makeLayout() {
		setLayout(new BorderLayout());
		splitter.setContinuousLayout(true);
		splitter.setDividerLocation(200);
		add(splitter, BorderLayout.CENTER);
		navTree.setShowsRootHandles(true);
		navTree.setExpandsSelectedPaths(false);
		navTree.setRootVisible(false);
		pagePanel.setLayout(new GridLayout());
	}

	
	public void updateData() {
		navTreeModel.update();
		int count = navTree.getRowCount();
		while (count-- > 0) {
			navTree.expandRow(count);
		}
	}

	public void showWindow() {
		if (isShowing()) {
			toFront();
			return;
		}
		setLocationByPlatform(true);
		setLocationRelativeTo(getParent());
		updateData();
		setVisible(true);
	}
	
	private static class PageTreeRow {
		
		public String 
			pageName = "";
		public int
			pageIndex = 0;
		public JPanel
			pagePanel = null;
		public Icon
			pageIcon = null;
		
		@Override
		public String toString() {
			return pageName + ": " + pageIndex;
		}
		
	}
	
	
	private class NavTreeModel extends AbstractTreeModel implements TreeCellRenderer{

		private DefaultTreeCellRenderer
			defaultTreeCellRenderer = new DefaultTreeCellRenderer();
		
		public NavTreeModel() {
			super("Root Node");
		}
		
		@Override
		public Object getChild(Object parent, int index) {
			if (parent == getRoot()) {
				return plugins.get(index);
			} else if (parent instanceof PreferencesFlavor) {
				PreferencesFlavor plugin = (PreferencesFlavor)parent;
				PageTreeRow pageTreeRow = new PageTreeRow();
				pageTreeRow.pageIndex = index;
				pageTreeRow.pageName = plugin.getSubPageName(index);
				pageTreeRow.pagePanel = plugin.getSubPage(index);
				pageTreeRow.pageIcon = plugin.getSubPageIcon(index);
				return pageTreeRow;
			} else {
				return null;
			}
		}

		@Override
		public int getChildCount(Object parent) {
			if (parent == getRoot()) {
				return plugins.size();
			} else if (parent instanceof PreferencesFlavor) {
				PreferencesFlavor plugin = (PreferencesFlavor)parent;
				return plugin.getNumSubPages();
			} else {
				return 0;
			}
		}

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean selected, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {
			JLabel l = (JLabel)defaultTreeCellRenderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
			if (value instanceof PreferencesFlavor) {
				PreferencesFlavor p = (PreferencesFlavor)value;
				l.setText(p.getMainName());
				if (p.getMainIcon() != null) {
					Icon i = ImageHook.scaleIcon(p.getMainIcon(), 16, 16);
					l.setIcon(i);
				} else {
					l.setIcon(defaultIcon);
				}
			} else if (value instanceof PageTreeRow) {
				PageTreeRow ptr = (PageTreeRow)value;
				if (ptr.pageIcon != null) {
					Icon i = ImageHook.scaleIcon(ptr.pageIcon, 16, 16);
					l.setIcon(i);
				} else {
					l.setIcon(defaultIcon);
				}
			}
			return l;
		}
		
		public void update() {
			fireTreeStructureChanged(getRoot());
		}

		@Override
		public Object getParent(Object o) {
			return null;
		}

	}
	
	
	public boolean addPreferencesPlugin(PreferencesFlavor plugin) {
		boolean result = plugins.add(plugin);
		Collections.sort(plugins, new PageComparator());
		navTree.updateUI();
		return result;
	}
	
	
	public boolean removePreferencesPlugin(PreferencesFlavor plugin) {
		boolean result = plugins.remove(plugin);
		navTree.updateUI();
		return result;
	}


	@Override
	public void valueChanged(TreeSelectionEvent e) {
		TreePath path = e.getNewLeadSelectionPath();
		if (path == null) {
			return;
		}
		if (path.getPathCount() < 2)
			return;
		if (path.getPathCount() == 2) {
			PreferencesFlavor plugin = (PreferencesFlavor)path.getLastPathComponent();
			pagePanel.removeAll();
			pagePanel.add(plugin.getMainPage());
		} else {
			PageTreeRow pageTreeRow = (PageTreeRow)path.getLastPathComponent();
			pagePanel.removeAll();
			pagePanel.add(pageTreeRow.pagePanel);
		}
		SwingUtilities.updateComponentTreeUI(pagePanel);
	}
	
	
	private class PageComparator implements Comparator<PreferencesFlavor> {

		@Override
		public int compare(PreferencesFlavor o1, PreferencesFlavor o2) {
			return o1.getMainName().compareTo(o2.getMainName());
		}
		
	}
	
}
