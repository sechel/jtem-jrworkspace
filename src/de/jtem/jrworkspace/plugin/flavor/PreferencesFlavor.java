package de.jtem.jrworkspace.plugin.flavor;

import javax.swing.Icon;
import javax.swing.JPanel;


/**
 * A flavor which adds a preferences page to the host application
 * @author Stefan Sechelmann
 *
 */
public interface PreferencesFlavor {

	/**
	 * Returns a name which will be the name of the main page
	 * @return a name
	 */
	public String getMainName();
	
	/**
	 * The main preferences page
	 * @return a {@link JPanel}
	 */
	public JPanel getMainPage();
	
	/**
	 * An icon for the main page
	 * @return an icon
	 */
	public Icon getMainIcon();
	
	
	/**
	 * The number of sub-pages of this preference page
	 * @return a Integer number
	 */
	public int getNumSubPages();
	
	/**
	 * The name of sub-page number i
	 * @param i the index of the sub-page
	 * @return a name
	 */
	public String getSubPageName(int i);
	
	/**
	 * The JPanel of the sub-page with index i
	 * @param i the index of the sub-page
	 * @return
	 */
	public JPanel getSubPage(int i);
	
	/**
	 * The icon of sub-page i
	 * @param i the index of the sub-page
	 * @return
	 */
	public Icon getSubPageIcon(int i);

}
