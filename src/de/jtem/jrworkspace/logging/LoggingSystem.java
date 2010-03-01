package de.jtem.jrworkspace.logging;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/** LoggingSystem for de.jtem.jrworkspace. To log a massage use
 * <blockquote><code>
 *		LoggingSystem.LOGGER.log()
 * </code></blockquote>
 * or any of the convenience log level methods.
 *  
 * The jrworkspace LoggingSystem uses the {@link ConsoleHandler} by default. 
 * To use a FileHandler as well set the system property:
 * <blockquote><code>
 * 		-Dde.jtem.jrworkspace.logfile="mylogfile"
 * </code></blockquote>
 * 
 * Both handlers are set to handle ALL logs. The logger uses the default loglevel, but
 * you may change the log {@link Level} by <code>LoggingSystem.LOGGER.setLevel</code> or
 * set the the system property
 * <blockquote><code>
 * 		-Dde.jtem.jrworkspace.loglevel=LEVEL
 * </blockquote></code>
 *
 * 
 * @author G. Paul Peters, Feb 12, 2010
 *
 * @see Logger
 * @see Level
 */
public class LoggingSystem {
	
	public static final Logger LOGGER = Logger.getLogger("de.jtem.jrworkspace");
	private static final Level DEFAULT_LOG_LEVEL = tryToGetLoglevel(); 
	
	static {
        tryToInitLogger();
        tryToInitFileHandler();
	}

	static Level tryToGetLoglevel() {
		Level level = null;
		String levelName = null;
		try {
			levelName = System.getProperty("de.jtem.jrworkspace.loglevel");
		} catch (SecurityException se) {
			LOGGER.config("No permission to read system property: " + se.getMessage());
		}
		try {
			level = levelName == null ? null : Level.parse(levelName);
		} catch (IllegalArgumentException e) {
			LOGGER.config("Could not parse the value of the system property de.jtem.jrworkspace.loglevel \"" + levelName +"\" to a logging level:");  
		}
		if (level == null) {
			level = Logger.getLogger("de.jtem.jrworkspace").getLevel();
		}
		return level;
	}

	private static void tryToInitLogger() {
		try {
            initLogger();
        } catch (SecurityException se) {
        	LOGGER.config("No permission to init logger: " + se.getMessage());
        }
	}

	private static void initLogger(){
		LOGGER.setLevel(DEFAULT_LOG_LEVEL);
		LOGGER.setUseParentHandlers(false);
        useConsoleHandler();
	}


	private static void useConsoleHandler() {
		Handler handler = new ConsoleHandler();
        handler.setLevel(Level.ALL);
        LOGGER.addHandler(handler);
	}
	
	private static void tryToInitFileHandler() {
		try {
			initFileHandler(System.getProperty("de.jtem.jrworkspace.logfile"));
		} catch (SecurityException se) {
			LOGGER.config("No permission to read system property or add file handler: " + se.getMessage());
		} catch (IOException ioe) {
			LOGGER.config(ioe.getMessage());
		} catch (Exception e) {
			LOGGER.config("Could not initializen file handler: " + e.getMessage());
		}
	}

	private static void initFileHandler(String filename) throws IOException {
		Handler handler = new FileHandler(filename);
		handler.setLevel(Level.ALL);
		LOGGER.addHandler(handler);
	}
}