package de.jtem.jrworkspace.plugin;

import java.util.Comparator;

public class PluginNameComparator implements Comparator<Plugin> {

	public int compare(Plugin o1, Plugin o2) {
		if (o1 == o2) {
			return 0;
		} else if (o1.getPluginInfo().name.equals(o2.getPluginInfo().name)) {
			return o1.getClass().getName().compareTo(o2.getClass().getName());
		} else {
			return o1.getPluginInfo().name.compareTo(o2.getPluginInfo().name);
		}
	}

}
