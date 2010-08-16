package de.jtem.jrworkspace.logging;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.matchers.JUnitMatchers;

public class LoggingSystemTest {
	
	private Level[] levels = new Level[] { 
			Level.ALL, Level.FINEST, Level.FINER, Level.FINE,
			Level.CONFIG, Level.INFO, Level.WARNING, Level.SEVERE, Level.OFF };
	private static final ByteArrayOutputStream logStream = new ByteArrayOutputStream();
	private static StreamHandler testHandler;
	private static final String message = "Test Message";
	private static File tmpLogFile = tryCreateTmpFile();
	private static Level defaultLogLevel = Level.CONFIG;

	@BeforeClass
	public static void initTestHandler() {
		testHandler = new StreamHandler(logStream, new SimpleFormatter()) {
			@Override
			public synchronized void publish(LogRecord record) {
					super.publish(record);
					flush();
			}
		};
		testHandler.setLevel(Level.ALL);
	}

	
	@BeforeClass
	public static void initFileHandler() {
		if (System.getProperty("de.jtem.jrworkspace.logfile") == null) {
			System.setProperty("de.jtem.jrworkspace.logfile", tmpLogFile.getAbsolutePath());
		} else {
			tmpLogFile = new File(System.getProperty("de.jtem.jrworkspace.logfile"));
		}
	}
	
	@BeforeClass
	public static void initDefaulLogLevel() {
		if (System.getProperty("de.jtem.jrworkspace.loglevel") == null) {
			System.setProperty("de.jtem.jrworkspace.loglevel", defaultLogLevel.toString());
		} else {
			defaultLogLevel = Level.parse(System.getProperty("de.jtem.jrworkspace.loglevel"));
		}
	}
	
	@Before
	public void setTestHandler(){
		LoggingSystem.LOGGER.addHandler(testHandler);
		logStream.reset();
	}
	
	
	@Test
	public void writeLogMessages() {
		for (Level level : levels) {
			LoggingSystem.LOGGER.log(level, message);
			if (level.intValue() >= defaultLogLevel.intValue()) { 
				Assert.assertThat("logLevel: " + level,
					logStream.toString(), JUnitMatchers.containsString(message));
			} else {
				Assert.assertEquals("logLevel: " + level +", Message size ",
						0, logStream.size());
			}
		}
	}
	
	@Test
	public void changeLoggersLogLevel() {
		LoggingSystem.LOGGER.log(Level.FINEST, message);
		Assert.assertEquals("Message size ", 0, logStream.size());
		
		LoggingSystem.LOGGER.setLevel(Level.FINEST);
		LoggingSystem.LOGGER.log(Level.FINEST, message);
		Assert.assertThat(logStream.toString(), JUnitMatchers.containsString(message));

		logStream.reset();
		LoggingSystem.LOGGER.log(Level.ALL, message);
		Assert.assertEquals("Message size ", 0, logStream.size());
	}
	
	@Test
	public void fileHandler() throws IOException {
		String message = LoggingSystemTest.message + " test file logging";
		LoggingSystem.LOGGER.log(Level.INFO, message);
		
		boolean found = doesTmpLogFileContain(message);
		Assert.assertTrue("Log file does not contain \"" + message + "\"", found);
	}
	
	@Test
	public void testWrongLogLevelNameInSystemProperty() {
		System.setProperty("de.jtem.jrworkspace.loglevel", "Severe");
		Assert.assertEquals(Logger.getLogger("").getLevel(),
				LoggingSystem.tryToGetLoglevel());
		Assert.assertThat(logStream.toString(), JUnitMatchers.containsString("Could not parse the value of the system property"));
		
		System.setProperty("de.jtem.jrworkspace.loglevel", "SEVERE");
		Assert.assertEquals(Level.SEVERE, 
				LoggingSystem.tryToGetLoglevel());
	}
	
	private static File tryCreateTmpFile() {
		File file = null; 
		try {
			file = File.createTempFile("log", ".txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}
	
	private static boolean doesTmpLogFileContain(String string) throws IOException {
		Reader reader = new FileReader(tmpLogFile);
		return doesReaderContain(reader, string);
	}

	private static boolean doesReaderContain(Reader reader, String string) throws IOException {
		BufferedReader bReader = new BufferedReader(reader);
		String line;
		while (null != (line = bReader.readLine())) {
			if (line.contains(message)) {
				return true;
			}
		}
		return false;
	}
}
