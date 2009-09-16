package de.varylab.jrworkspace.plugin.aggregators;

import static java.util.Collections.sort;

import java.awt.Component;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.Action;
import javax.swing.JToolBar;
import javax.swing.JToolBar.Separator;

import de.varylab.jrworkspace.plugin.Plugin;
import de.varylab.jrworkspace.plugin.flavor.ToolBarFlavor;

public abstract class ToolBarAggregator extends Plugin implements ToolBarFlavor {

	private HashMap<Class<?>, HashSet<ToolBarItem>>
		itemMap = new HashMap<Class<?>, HashSet<ToolBarItem>>();
	private JToolBar
		toolbar = new JToolBar();

	
	public ToolBarAggregator() {
		toolbar.setName(getPluginInfo().name);
	}
	
	
	private class ToolBarItem implements Comparable<ToolBarItem> {
		
		public  Object 
			item = null;
		public double 
			priority = 0.0;
		
		public ToolBarItem(Object item, double priority) {
			this.item = item;
			this.priority = priority;
		}

		public int compareTo(ToolBarItem o) {
			return priority < o.priority ? -1 : 1;
		}
		
	}
	
	
	private void updateToolBar() {
		List<ToolBarItem> itemList = new LinkedList<ToolBarItem>();
		for (Set<ToolBarItem> mSet : itemMap.values()) {
			itemList.addAll(mSet);
		}
		sort(itemList);
		toolbar.removeAll();
		for (ToolBarItem tba : itemList) {
			if (tba.item instanceof Action) {
				toolbar.add((Action)tba.item);
			} 
			else if (tba.item instanceof Separator) {
				toolbar.addSeparator();
			}
			else if (tba.item instanceof Component) {
				toolbar.add((Component)tba.item);
			}
		}
	}
	
	
	private void addItem(Class<?> context, ToolBarItem item) {
		if (!itemMap.containsKey(context)) {
			itemMap.put(context, new HashSet<ToolBarItem>());
		}
		Set<ToolBarItem> set = itemMap.get(context);
		set.add(item);
		updateToolBar();
	}
	
	private void removeItem(Class<?> context, Object item) {
		Set<ToolBarItem> set = itemMap.get(context);
		if (set == null) {
			return;
		}
		ToolBarItem entry = null;
		for (ToolBarItem i : set) {
			if (i.item == item) {
				entry = i;
				break;
			}
		}
		if (entry != null) {
			set.remove(entry);
			updateToolBar();
		}
	}
	
	
	
	public void addAction(Class<?> context, double priority, Action a) {
		ToolBarItem item = new ToolBarItem(a, priority);
		addItem(context, item);
	}
	
	public void addTool(Class<?> context, double priority, Component c) {
		ToolBarItem item = new ToolBarItem(c, priority);
		addItem(context, item);
	}
	
	public void addSeparator(Class<?> context, double priority) {
		Separator separator = new Separator();
		ToolBarItem item = new ToolBarItem(separator, priority);
		addItem(context, item);
	}
	
	
	public void removeAll(Class<?> context) {
		itemMap.remove(context);
		updateToolBar();
	}
	
	
	public void removeAction(Class<?> context, Action a) {
		removeItem(context, a);
	}
	
	public void removeTool(Class<?> context, Component c) {
		removeItem(context, c);
	}
	
	
	public Component getToolBarComponent() {
		return toolbar;
	}

	public double getToolBarPriority() {
		return 0;
	}

	public void setFloatable(boolean fl) {
		toolbar.setFloatable(fl);
	}
	
}
