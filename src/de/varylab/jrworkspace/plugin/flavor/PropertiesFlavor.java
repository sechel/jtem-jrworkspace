package de.varylab.jrworkspace.plugin.flavor;

import java.io.Reader;
import java.io.Writer;

public interface PropertiesFlavor {

	
	public static interface PropertiesListener {
		
		public void writeProperties(Writer w);
		public void readProperties(Reader r);
		public void loadDefaultProperties();
		
	}
	
	
	public void setPropertiesListener(PropertiesListener l);
	
}
