package de.jtem.jrworkspace.plugin.simplecontroller;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.jtem.jrworkspace.junitUtils.NbPlugin;


public class PropertyFilesTest {
	
	
	private SimpleController controller;
	private File dir = new File(System.getProperty("java.io.tmpdir") + "/BackupTest");
	private File file = new File(dir.getAbsolutePath() + "\test.xml");
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
		controller = new SimpleController();
		controller.setAskBeforeSaveOnExit(false);
		controller.setLoadFromUserPropertyFile(false);
		controller.setSaveOnExit(false);
		controller.setPropertiesFile(file);
		nbPlugin = new NbPlugin();
		controller.registerPlugin(nbPlugin);		
		controller.startupLocal();
	}
	
	@Test
	public void testPropertyFileSaving() {
		nbPlugin.setNumber(42);
		controller.savePropertiesOnExit();
		
		nbPlugin.setNumber(43);
		controller.loadProperties();
		
		assertEquals(42, nbPlugin.getNumber());
		
	}
	
}
