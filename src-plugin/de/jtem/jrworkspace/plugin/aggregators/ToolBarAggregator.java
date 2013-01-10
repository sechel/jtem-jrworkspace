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

import de.jtem.jrworkspace.plugin.Plugin;
import de.jtem.jrworkspace.plugin.flavor.ToolBarFlavor;

public abstract class ToolBarAggregator extends Plugin implements ToolBarFlavor {

	private HashMap<Class<?>, HashSet<ToolBarItem>>
		itemMap = new HashMap<Class<?>, HashSet<ToolBarItem>>();
	private JToolBar
		toolbar = new JToolBar();

	
	public ToolBarAggregator() {
		toolbar.setName(getPluginInfo().name);
	}
	
	
	private class ToolBarItem implements Comparable<ToolBarItem> {
		
		public Object 
			item = null;
		public double 
			priority = 0.0;
		
		public ToolBarItem(Object item, double priority) {
			this.item = item;
			this.priority = priority;
		}

		public int compareTo(ToolBarItem o) {
			if (priority == o.priority) {
				return getName().compareTo(o.getName());
			}
			return priority < o.priority ? -1 : 1;
		}
		
		public String getName() {
			if (item instanceof Action) {
				Action a = (Action)item;
				return (String)a.getValue(Action.NAME);
			}
			if (item instanceof Component) {
				Component c = (Component)item;
				if (c.getName() != null) {
					return c.getName();
				} else {
					return "";
				}
			}
			return "";
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
