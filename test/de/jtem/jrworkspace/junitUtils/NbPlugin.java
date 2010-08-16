package de.jtem.jrworkspace.junitUtils;

import de.jtem.jrworkspace.plugin.Controller;
import de.jtem.jrworkspace.plugin.Plugin;

public class NbPlugin extends Plugin {
	private int number;

	@Override
	public void storeStates(Controller c) throws Exception {
		c.storeProperty(getClass(), "number", number);
	}
	
	@Override
	public void restoreStates(Controller c) throws Exception {
		number = c.getProperty(getClass(), "number", number);
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}
	
	
}