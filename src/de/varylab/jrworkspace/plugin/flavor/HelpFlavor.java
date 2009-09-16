package de.varylab.jrworkspace.plugin.flavor;

import javax.swing.Icon;

public interface HelpFlavor {

	/**
	 * Returns the title for these help pages
	 * @return
	 */
	public String getHelpTitle();
	
	/**
	 * Returns an help icon
	 * @return
	 */
	public Icon getHelpIcon();
	
	/**
	 * Returns a path to the help HTML file
	 * @see {@link HelpFlavor#getHelpHandle()}
	 * @return
	 */
	public String getHelpPath();
	
	
	/**
	 * Returns the class which is the root of the help
	 * page file system. A help page name is then resolved like this:<br>
	 * {@code String location = }{@link HelpFlavor#getHelpPath()} + {@link HelpFlavor#getHelpDocument()} <br>
	 * {@code InputStream in = }{@link HelpFlavor#getHelpHandle()}{@code .getResourceAsStream(location)}
	 * @return a class which is the root handle for these help pages
	 */
	public Class<?> getHelpHandle();
	
	
	/**
	 * Returns the name of the HTML root help document
	 * @see {@link HelpFlavor#getHelpHandle()}
	 * @return the name of the root document
	 */
	public String getHelpDocument();

	
	/**
	 * Returns the name of the style sheet file to use
	 * for this help pages
	 * @return a file name or null
	 */
	public String getHelpStyleSheet();
	
	
	/**
	 * The HelpListener implements a method to show a help page
	 * @author Stefan Sechelmann
	 */
	public static interface HelpListener {
		
		/**
		 * Displays the plug-in help center an activates the
		 * given help page
		 * @param hf
		 */
		public void showHelpPage(HelpFlavor hf);
		
	}
	
	/**
	 * Sets the help listener which implements help 
	 * functionality of the controller
	 * @param l the listener
	 */
	public void setHelpListener(HelpListener l);
	
}

