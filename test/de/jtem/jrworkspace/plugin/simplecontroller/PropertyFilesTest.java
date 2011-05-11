package de.jtem.jrworkspace.plugin.simplecontroller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.prefs.Preferences;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.jtem.jrworkspace.junitUtils.NbPlugin;


public class PropertyFilesTest {
	
	
	private SimpleController controller;
	private File dir = new File(System.getProperty("java.io.tmpdir") + "/BackupTest");
	private File file = new File(dir.getAbsolutePath() + "/test.xml");
	private NbPlugin nbPlugin; 
	
	@Before
	public void initDir() {
		dir.mkdir();
	}
	
	@After
	public void rmDir() {
		for (File file : dir.listFiles()) {
			file.delete();
		}
		dir.delete();
	}


	@Before
	public void initController() {
		Preferences userPreferences = Preferences.userNodeForPackage(this.getClass());
		userPreferences.put("userPropertyFile", "");
		controller = new SimpleController();
		controller.setAskBeforeSaveOnExit(false);
		controller.setLoadFromUserPropertyFile(false);
		controller.setSaveOnExit(true);
		controller.setPropertiesFile(file);
		nbPlugin = new NbPlugin();
		controller.registerPlugin(nbPlugin);		
		controller.startupLocal();
//		Runtime.getRuntime().removeShutdownHook(controller.shutdownHook);
	}
	
	@Test
	public void testPropertyFileSaving() throws Exception {
		nbPlugin.setNumber(42);
		controller.savePropertiesOnExit();
		
		nbPlugin.setNumber(43);
		assertEquals(43, nbPlugin.getNumber());
		nbPlugin.restoreStates(controller);
		assertEquals(42, nbPlugin.getNumber());
		controller.setLoadFromUserPropertyFile(true);
		controller.loadProperties();
		nbPlugin.restoreStates(controller);
		
		assertEquals(42, nbPlugin.getNumber());
	}
	
	@Test
	public void testNbOfPropertyBackups() throws Exception {
		controller.setNbOfPropertyFileBackups(2);
		nbPlugin.setNumber(42);
		controller.savePropertiesOnExit();
		nbPlugin.setNumber(43);
		controller.savePropertiesOnExit();
		nbPlugin.setNumber(45);
		controller.savePropertiesOnExit();
		nbPlugin.setNumber(46);
		controller.savePropertiesOnExit();
		nbPlugin.setNumber(47);
		controller.savePropertiesOnExit();
		
		assertEquals(3, dir.list().length);
		assertThat(backupFileAsString(-1), containsString("<int>47</int>"));
		assertThat(backupFileAsString(0), containsString("<int>46</int>"));
		assertThat(backupFileAsString(1), containsString("<int>45</int>"));
	}

	private String backupFileAsString(int i) throws IOException {
		char[] buffer = new char[1024];
		FileReader reader = null;
		if (-1 == i) {
			reader = new FileReader(file);
		} else {
			reader = new FileReader(controller.backupFile(file, i));
		}
		reader.read(buffer);
		reader.close();
		return new String(buffer);
	}

	
}
