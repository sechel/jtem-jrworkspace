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

package de.jtem.jrworkspace.plugin.pluginmanager;

import static java.util.Collections.singleton;
import static javax.tools.StandardLocation.locationFor;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileManager.Location;
import javax.tools.JavaFileObject.Kind;

import org.eclipse.jdt.internal.compiler.tool.EclipseFileManager;

import de.jtem.jrworkspace.plugin.Controller;
import de.jtem.jrworkspace.plugin.Plugin;
import de.jtem.jrworkspace.plugin.PluginInfo;
import de.jtem.jrworkspace.plugin.flavor.PreferencesFlavor;
import de.jtem.jrworkspace.plugin.flavor.UIFlavor;
import de.jtem.jrworkspace.plugin.pluginmanager.image.ImageHook;
import de.jtem.jrworkspace.plugin.simplecontroller.SimpleController;

public class PluginManager extends Plugin implements PreferencesFlavor, ActionListener, UIFlavor {

	private JPanel
		panel = new JPanel();
	private List<String>
		pluginList = new ArrayList<String>();
	private JTable
		pluginTable = new JTable(new PluginModel());
	private JScrollPane
		scroller = new JScrollPane(pluginTable);
	private JButton
		addButton = new JButton(ImageHook.getIcon("add.png")),
		removeButton = new JButton(ImageHook.getIcon("delete.png"));
	private JFileChooser
		chooser = new JFileChooser();
	private EclipseFileManager 
		fm = new EclipseFileManager(Locale.ROOT, Charset.defaultCharset());
	
	public PluginManager() {
		panel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.weightx = 0.0;
		panel.add(addButton, gbc);
		panel.add(removeButton, gbc);
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		panel.add(new JPanel(), gbc);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		pluginTable.getTableHeader().setPreferredSize(new Dimension(10, 0));
		scroller.setPreferredSize(new Dimension(10, 200));
		panel.add(scroller, gbc);
		
		chooser.setDialogTitle("Choose Plugin Jar-File");
		chooser.setDialogType(JFileChooser.OPEN_DIALOG);
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setMultiSelectionEnabled(true);
		chooser.setFileFilter(new FileFilter() {
			@Override
			public String getDescription() {
				return "Plugin Jar-Files (*.jar)";
			}
			@Override
			public boolean accept(File f) {
				return f.isDirectory() || f.getName().toLowerCase().endsWith(".jar");
			}
		});
		
		addButton.addActionListener(this);
		removeButton.addActionListener(this);
	}
	
	public void actionPerformed(ActionEvent e) {
		Object s = e.getSource();
		if (addButton == s) {
			Window w = SwingUtilities.getWindowAncestor(panel);
			int result = chooser.showOpenDialog(w);
			if (result != JFileChooser.APPROVE_OPTION) {
				return;
			}
			for (File f : chooser.getSelectedFiles()) {
				pluginList.add(f.getAbsolutePath());
			}
			Collections.sort(pluginList);
		}
		if (removeButton == s) {
			List<String> selected = new LinkedList<String>();
			for (int row : pluginTable.getSelectedRows()) {
				selected.add(pluginList.get(row));
			}
			pluginList.removeAll(selected);
		}
		pluginTable.updateUI();
	}
	
	private class PluginModel extends DefaultTableModel {

		private static final long 
			serialVersionUID = 1L;
		
		@Override
		public int getColumnCount() {
			return 1;
		}
		
		@Override
		public int getRowCount() {
			return pluginList.size();
		}
		
		@Override
		public Object getValueAt(int row, int column) {
			String name = pluginList.get(row);
			return name;
		}
		
	}
	
	private Location getLocation(File f) throws IOException {
		Location location = locationFor(f.toString());
		fm.setLocation(location, singleton(f));
		return location;
	}
	
	
	private void retrievePlugins(File pluginFile, Controller c) throws IOException {
		URL url = null;
		try {
			url = pluginFile.toURI().toURL();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return;
		}
		URL[] urls = new URL[]{url};
		URLClassLoader loader = new URLClassLoader(urls);
		Location location = getLocation(pluginFile);
		List<Plugin> plugins = getPlugins(location, loader);
		for (Plugin p : plugins) {
			c.getPlugin(p.getClass());
		}
	}
	
	
	public List<Plugin> getPlugins(Location location, ClassLoader loader) throws IOException {
		List<Plugin> plugins = new LinkedList<Plugin>();
		Iterable<JavaFileObject> fIt = fm.list(location, "", singleton(Kind.CLASS), true);
		for (JavaFileObject jfo : fIt) {
			String className = fm.inferBinaryName(location, jfo);
			if (!className.toLowerCase().contains("plugin")) {
				continue;
			}
			Class<?> clazz = null;
			try {
				clazz = loader.loadClass(className);
				if (Modifier.isAbstract(clazz.getModifiers())) {
					continue;
				}
			} catch (Error e) {
				System.out.println("failed to load class " + jfo.getName());
				continue;
			} catch (Exception e) {
				System.out.println("failed to load class " + jfo.getName());
				continue;
			}
			try {
				if (Plugin.class.isAssignableFrom(clazz)) {
					plugins.add((Plugin)clazz.newInstance());
				}
			} catch (Error e) {
				System.out.println("Failed to instantiate plugin " + jfo.getName());
			} catch (IllegalAccessException e) {
				System.out.println("The plugin " + jfo.getName() + " has no public empty constructor");
			} catch (InstantiationException e) {
				System.out.println("The Plugin " + jfo.getName() + " cannot be instantiated");
			} catch (Exception e) {
				System.out.println("failed to instantiate plugin " + jfo.getName());
			} 
		}
		return plugins;
	}

	
	@Override
	public void storeStates(Controller c) throws Exception {
		super.storeStates(c);
		c.storeProperty(getClass(), "pluginList", pluginList);
	}
	
	@Override
	public void restoreStates(Controller c) throws Exception {
		super.restoreStates(c);
		pluginList = c.getProperty(getClass(), "pluginList", new ArrayList<String>());
	}
	
	
	@Override
	public void install(Controller c) throws Exception {
		super.install(c);
		for (String fName : pluginList) {
			File f = new File(fName);
			try {
				retrievePlugins(f, c);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public Icon getMainIcon() {
		return ImageHook.getIcon("puzzle.png");
	}

	public String getMainName() {
		return "Plugins";
	}

	public JPanel getMainPage() {
		return panel;
	}

	public int getNumSubPages() {
		return 0;
	}

	public JPanel getSubPage(int i) {
		return null;
	}

	public Icon getSubPageIcon(int i) {
		return null;
	}

	public String getSubPageName(int i) {
		return null;
	}
	
	@Override
	public PluginInfo getPluginInfo() {
		PluginInfo inf = new PluginInfo("Plugin Manager", "Stefan Sechelmann");
		inf.icon = ImageHook.getIcon("puzzle.png");
		return inf;
	}
	
	
	public static void main(String[] args) {
		SimpleController c = new SimpleController();
		c.setPropertiesFile(new File("test.xml"));
		c.registerPlugin(new EmptyPerspective());
		c.registerPlugin(new PluginManager());
		c.startup();
	}

	public void mainUIChanged(String uiClass) {
		chooser.updateUI();
	}

}
