package de.jtem.jrworkspace.plugin;

import java.util.Comparator;

public class PluginNameComparator implements Comparator<Plugin> {

	public int compare(Plugin o1, Plugin o2) {
		return o1.getPluginInfo().name.compareTo(o2.getPluginInfo().name);
	}

}
