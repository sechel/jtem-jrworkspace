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

package de.jtem.jrworkspace.plugin.aggregators;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import de.jtem.jrworkspace.plugin.Controller;
import de.jtem.jrworkspace.plugin.Plugin;
import de.jtem.jrworkspace.plugin.flavor.FrontendFlavor;
import de.jtem.jrworkspace.plugin.flavor.MenuFlavor;

public abstract class MenuAggregator extends Plugin implements MenuFlavor, FrontendFlavor {

	private HashMap<Class<?>, HashSet<MenuEntry>>
		menuMap = new HashMap<Class<?>, HashSet<MenuEntry>>();
	private Set<JMenu>
		automaticMenus = new HashSet<JMenu>(); 
	private Set<Component>
		addedMenuComponents = new HashSet<Component>();
	private FrontendListener
		updateListener = null;
	private long 
		separatorIndex = 0;
	
	
	private class MenuEntry {
		
		public Class<?> 
			context = null;
		public String[]
			menuPath = null;
		public Object 
			item = null;
		public double
			priority = 0.0;
		
		public MenuEntry(Class<?> context, String[] path, Object item, double priority) {
			this.context = context;
			this.menuPath = path;
			this.item = item;
			this.priority = priority;
		}
		
		@Override
		public String toString() {
			String contextName = context != null ? context.getSimpleName() : "null";
			return "MenuEntry(" + contextName + "): " + Arrays.toString(menuPath) + " -> " + item;
		}
		
	}
	

	private class MenuNode implements Comparable<MenuNode>{
		
		public String 
			name = "";
		public MenuEntry
			entry = null;
		public int 
			treeDepth = 0;
		public HashMap<String, MenuNode>
			children = new HashMap<String, MenuNode>();
		
		public MenuNode(String name, MenuEntry entry, int depth) {
			this.name = name;
			this.entry = entry;
			this.treeDepth = depth;
		}
	
		public Object getMenuItem() {
			if (entry != null && !(entry.item instanceof JMenu)) {
				return entry.item;
			} else {
				JMenu menu = null;
				if (entry != null && entry.item instanceof JMenu) {
					menu = (JMenu)entry.item;
					for (Component c : menu.getMenuComponents()) {
						if (automaticMenus.contains(c)) {
							menu.remove(c);
							automaticMenus.remove(c);
						}
						if (addedMenuComponents.contains(c)) {
							menu.remove(c);
							addedMenuComponents.remove(c);
						}
					}
				} else {
					menu = new JMenu(name);
					automaticMenus.add(menu);
				}
				List<MenuNode> nodes = new ArrayList<MenuNode>(children.values());
				Collections.sort(nodes);
				for (MenuNode child : nodes) {
					Object m = child.getMenuItem();
					if (m instanceof JMenuItem) {
						JMenuItem item = (JMenuItem)m;
						menu.add(item);
						addedMenuComponents.add(item);
					} else if (m instanceof JPopupMenu.Separator) {
						JPopupMenu.Separator separator = (JPopupMenu.Separator)m;
						menu.add(separator);
						addedMenuComponents.add(separator);
					}
				}
				return menu;
			}
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof MenuNode) {
				MenuNode mn = (MenuNode)obj;
				return name.equals(mn.name) && (treeDepth == mn.treeDepth);
			} else {
				return false;
			}
		}

		@Override
		public int compareTo(MenuNode o) {
			double p1 = entry == null ? Double.MAX_VALUE : entry.priority;
			double p2 = o.entry == null ? Double.MAX_VALUE : o.entry.priority;
			if (p1 == p2 && entry != null && o.entry != null) {
				if (entry.item instanceof JMenuItem && o.entry.item instanceof JMenuItem) {
					JMenuItem m1 = (JMenuItem)entry.item;
					JMenuItem m2 = (JMenuItem)o.entry.item;
					return m1.getText().compareTo(m2.getText());
				} else {
					return 0;
				}
			} else {
				return p1 < p2 ? -1 : 1;
			}
		}
		
		@Override
		public String toString() {
			return "MenuNode: " + name;
		}
		
	}
	
	public MenuAggregator() {

	}
	
	
	private void insertEntry(MenuNode root, MenuEntry entry) {
		int depth = root.treeDepth;
		if (entry.menuPath.length <= depth) {
			String name = "Leaf";
			if (entry.item instanceof JMenuItem) {
				JMenuItem menu = (JMenuItem)entry.item;
				name = menu.getText();
			} else if (entry.item instanceof JPopupMenu.Separator) {
				name = entry.item.toString() + " " + separatorIndex++;
			}
			MenuNode leaf = new MenuNode(name, entry, depth + 1);
			MenuNode prev = root.children.put(name, leaf);
			if (prev != null && prev.children.size() != 0) { // get children of previously linked node
				leaf.children = prev.children;
			}
		} else {
			String name = entry.menuPath[depth];
			if (!root.children.containsKey(name)) {
				MenuNode cascadeNode = new MenuNode(name, null, depth + 1);
				root.children.put(name, cascadeNode); 
			}
			MenuNode node = root.children.get(name);
			insertEntry(node, entry);
		}
	}
	
	
	@Override
	public List<JMenu> getMenus() {
		MenuNode root = new MenuNode("Menu Bar", null, 0);
		for (Set<MenuEntry> mSet : menuMap.values()) {
			for (MenuEntry e : mSet) {
				insertEntry(root, e);
			}
		}
		List<JMenu> result = new LinkedList<JMenu>();
		Object rm = root.getMenuItem();
		if (rm instanceof JMenu) {
			JMenu rootMenu = (JMenu)rm;
			for (int i = 0; i < rootMenu.getMenuComponentCount(); i++) {
				Component c = rootMenu.getMenuComponent(i);
				if (c instanceof JMenu) {
					result.add((JMenu)c);
				}
			}
		}
		return result;
	}
	
	
	private void addMenuEntry(Class<?> context, MenuEntry entry) {
		if (!menuMap.containsKey(context)) {
			menuMap.put(context, new HashSet<MenuEntry>());
		}
		Set<MenuEntry> set = menuMap.get(context);
		set.add(entry);
		if (updateListener != null) {
			updateListener.updateMenuBar();
//			updateListener.updateFrontendUI();
		}
	}
	
	private void removeItem(Class<?> context, Object item) {
		Set<MenuEntry> set = menuMap.get(context);
		if (set == null) {
			return;
		}
		MenuEntry entry = null;
		for (MenuEntry e : set) {
			if (item instanceof Action) {
				if (e.item instanceof ActionJMenuItem) {
					ActionJMenuItem actionItem = (ActionJMenuItem)e.item;
					if (actionItem.getAction() == item) {
						entry = e;
						break;
					}
				}
			} else if (e.item == item) {
				entry = e;
				break;
			}
		}
		if (entry != null) {
			set.remove(entry);
			if (updateListener != null) {
				updateListener.updateMenuBar();
//				updateListener.updateFrontendUI();
			}
		}
	}
	
	public void addMenuItem(Class<?> context, double priority, JMenuItem item, String... menuPath) {
		MenuEntry entry = new MenuEntry(context, menuPath, item, priority);
		addMenuEntry(context, entry);
	}
	
	private static class ActionJMenuItem extends JMenuItem {

		private static final long serialVersionUID = 1L;

		public ActionJMenuItem(Action a) {
			super(a);
		}
		
	}
	
	
	public void addMenuItem(Class<?> context, double priority, Action menuAction, String... menuPath) {
		ActionJMenuItem actionItem = new ActionJMenuItem(menuAction);
		MenuEntry entry = new MenuEntry(context, menuPath, actionItem, priority);
		addMenuEntry(context, entry);
	}
	
	public void addMenu(Class<?> context, double priority, JMenu menu, String... menuPath) {
		MenuEntry entry = new MenuEntry(context, menuPath, menu, priority);
		addMenuEntry(context, entry);
	}
	
	public void addMenuSeparator(Class<?> context, double priority, String... menuPath) {
		MenuEntry entry = new MenuEntry(context, menuPath, new JPopupMenu.Separator(), priority);
		addMenuEntry(context, entry);
	}
	
	public void removeAll(Class<?> context) {
		menuMap.remove(context);
		if (updateListener != null) {
			updateListener.updateMenuBar();
			updateListener.updateFrontendUI();
		}
	}
	
	public void removeMenu(Class<?> context, JMenu menu) {
		removeItem(context, menu);
	}
	
	public void removeMenuItem(Class<?> context, JMenuItem item) {
		removeItem(context, item);
	}
	
	public void removeMenuItem(Class<?> context, Action item) {
		removeItem(context, item);
	}

	public void reset() {
		menuMap.clear();
		if (updateListener != null) {
			updateListener.updateMenuBar();
			updateListener.updateFrontendUI();
		}
	}
	
	@Override
	public double getPriority() {
		return 1.0;
	}

	@Override
	public void setFrontendListener(FrontendListener l) {
		updateListener = l;
		l.updateMenuBar();
		l.updateFrontendUI();
	}
	
	@Override
	public void install(Controller c) throws Exception {
	}

	@Override
	public void uninstall(Controller c) throws Exception {
	}
	
}
