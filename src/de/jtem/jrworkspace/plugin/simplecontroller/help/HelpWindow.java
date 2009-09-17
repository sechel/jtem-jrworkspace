package de.jtem.jrworkspace.plugin.simplecontroller.help;

import static javax.swing.JSplitPane.HORIZONTAL_SPLIT;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Image;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

import de.jtem.jrworkspace.plugin.flavor.HelpFlavor;
import de.jtem.jrworkspace.plugin.simplecontroller.image.ImageHook;
import de.jtem.jrworkspace.plugin.simplecontroller.widget.AbstractTreeModel;

public class HelpWindow extends JDialog implements TreeSelectionListener {

	private static final long 
		serialVersionUID = 1L;
	private Image
		dialogIcon = ImageHook.getImage("helpblue.png");
	private LinkedList<HelpFlavor>
		plugins = new LinkedList<HelpFlavor>();
	private NavTreeModel
		navTreeModel = new NavTreeModel();
	private JTree
		navTree = new JTree(navTreeModel);
	private HelpBrowser
		helpBrowser = new HelpBrowser();	
	private JScrollPane
		navScroller = new JScrollPane(navTree);
	private JSplitPane
		splitter = new JSplitPane(HORIZONTAL_SPLIT, navScroller, helpBrowser);
	private Icon
		defaultIcon = ImageHook.getIcon("helpblue.png");

	
	public HelpWindow(Frame parent) {
		super(parent);
		ImageHook.setIconImage(this, dialogIcon);
		setTitle("Plugin Help Center");
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
	}

	
	public void updateData() {
		navTreeModel.update();
		int count = navTree.getRowCount();
		while (count-- > 0) {
			navTree.expandRow(count);
		}
	}

	
	
	private class NavTreeModel extends AbstractTreeModel implements TreeCellRenderer{

		private static final long 
			serialVersionUID = 1L;
		private DefaultTreeCellRenderer
			defaultTreeCellRenderer = new DefaultTreeCellRenderer();
		
		public NavTreeModel() {
			super("Root Node");
		}
		
		@Override
		public Object getChild(Object parent, int index) {
			if (parent == getRoot()) {
				return plugins.get(index);
			} else {
				return null;
			}
		}

		@Override
		public int getChildCount(Object parent) {
			if (parent == getRoot()) {
				return plugins.size();
			} else {
				return 0;
			}
		}

		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean selected, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {
			JLabel l = null;
			if (value instanceof HelpFlavor) {
				HelpFlavor hf = (HelpFlavor)value;
				l = (JLabel)defaultTreeCellRenderer.getTreeCellRendererComponent(tree, hf.getHelpTitle(), selected, expanded, leaf, row, hasFocus);
				Icon icon = hf.getHelpIcon();
				if (icon != null) {
					Icon i = ImageHook.scaleIcon(icon, 16, 16);
					l.setIcon(i);
				} else {
					l.setIcon(defaultIcon);
				}
			} else {
				l = (JLabel)defaultTreeCellRenderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
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
	
	
	public boolean addHelpPlugin(HelpFlavor plugin) {
		boolean result = plugins.add(plugin);
		Collections.sort(plugins, new PageComparator());
		navTree.updateUI();
		return result;
	}
	
	
	public boolean removeHelpPlugin(HelpFlavor plugin) {
		boolean result = plugins.remove(plugin);
		navTree.updateUI();
		return result;
	}

	
	public void activateHelpPage(HelpFlavor plugin) {
		int pluginIndex = plugins.indexOf(plugin);
		navTree.setSelectionRow(pluginIndex);
	}
	
	

	public void valueChanged(TreeSelectionEvent e) {
		TreePath path = e.getNewLeadSelectionPath();
		if (path == null) {
			return;
		}
		HelpFlavor plugin = (HelpFlavor)path.getLastPathComponent();
		helpBrowser.setDocument(plugin);
		SwingUtilities.updateComponentTreeUI(helpBrowser);
	}

	
	private class PageComparator implements Comparator<HelpFlavor> {

		public int compare(HelpFlavor o1, HelpFlavor o2) {
			return o1.getHelpTitle().compareTo(o2.getHelpTitle());
		}
		
	}
	
}
