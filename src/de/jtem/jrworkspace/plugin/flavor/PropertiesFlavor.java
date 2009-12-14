package de.jtem.jrworkspace.plugin.flavor;

import java.io.Reader;
import java.io.Writer;

import de.jtem.jrworkspace.plugin.Plugin;


/** A {@link Plugin} that wants to access the methods listed in the {@link PropertiesListener} interface, e.g., a menu entry that 
 * allows the user to call these methods, should implement this flavor.
 * 
 * @author G. Paul Peters, Dec 14, 2009
 *
 */
public interface PropertiesFlavor {

	
	/** A controller that is aware of the <code>PropertiesFlavor</code> will delegate calls to the appropriate methods.
	 *
	 */
	public static interface PropertiesListener {
		
		public void writeProperties(Writer w);
		public void readProperties(Reader r);
		public void loadDefaultProperties();
		
		public boolean isSaveOnExit();
		public void setSaveOnExit(boolean saveOnExit);
		public boolean isAskBeforeSaveOnExit();
		public void setAskBeforeSaveOnExit(boolean askBeforeSaveOnExit);
		public boolean isLoadFromUserPropertyFile();
		public void setLoadFromUserPropertyFile(boolean loadFromUserPropertyFile);
		public String getUserPropertyFile();
		public void setUserPropertyFile(String userPropertyFile);		
	}
	
	
	public void setPropertiesListener(PropertiesListener l);
	
}
