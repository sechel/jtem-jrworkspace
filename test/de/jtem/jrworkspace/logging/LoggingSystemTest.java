package de.jtem.jrworkspace.logging;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Level;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.matchers.JUnitMatchers;

public class LoggingSystemTest {
	
	private Level[] levels = new Level[] { 
			Level.ALL, Level.FINEST, Level.FINER, Level.FINE,
			Level.CONFIG, Level.INFO, Level.WARNING, Level.SEVERE, Level.OFF };
	private static final ByteArrayOutputStream stdErrForThisTest = new ByteArrayOutputStream();
	private static final PrintStream stdErr = System.err;
	private static final String message = "Test Message";
	private static File tmpLogFile = tryCreateTmpFile();
	private static Level defaultLogLevel = Level.CONFIG;

	@BeforeClass
	public static void redirectStdErr() {
		System.setErr(new PrintStream(stdErrForThisTest, true));
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
	public void resetStdErrorForThisTest(){
		stdErrForThisTest.reset();
	}
	
	@AfterClass
	public static void resetStdErr() {
		System.setErr(stdErr);
	}
	
	
	@Test
	public void writeLogMessagesToConsole() {
		for (Level level : levels) {
			LoggingSystem.LOGGER.log(level, message);
			if (level.intValue() >= defaultLogLevel.intValue()) { 
				Assert.assertThat("logLevel: " + level,
					stdErrForThisTest.toString(), JUnitMatchers.containsString(message));
			} else {
				Assert.assertEquals("logLevel: " + level +", Message size ",
						0, stdErrForThisTest.size());
			}
			stdErrForThisTest.reset();
		}
	}
	
	@Test
	public void changeLoggersLogLevel() {
		LoggingSystem.LOGGER.log(Level.FINEST, message);
		Assert.assertEquals("Message size ", 0, stdErrForThisTest.size());
		
		LoggingSystem.LOGGER.setLevel(Level.FINEST);
		LoggingSystem.LOGGER.log(Level.FINEST, message);
		Assert.assertThat(stdErrForThisTest.toString(), JUnitMatchers.containsString(message));

		stdErrForThisTest.reset();
		LoggingSystem.LOGGER.log(Level.ALL, message);
		Assert.assertEquals("Message size ", 0, stdErrForThisTest.size());
	}
	
	@Test
	public void fileHandler() throws IOException {
		String message = LoggingSystemTest.message + " test file logging";
		LoggingSystem.LOGGER.log(Level.INFO, message);
		
		BufferedReader reader = new BufferedReader(new FileReader(tmpLogFile));
		boolean found = false;
		String line;
		while (!found && null != (line =reader.readLine())) {
			found = line.contains(message);
		}

		Assert.assertTrue("Log file does not contain \"" + message + "\"", found);
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
}
