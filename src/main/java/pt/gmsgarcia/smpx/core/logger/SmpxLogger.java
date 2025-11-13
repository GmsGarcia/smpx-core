package pt.gmsgarcia.smpx.core.logger;

import java.util.logging.Logger;

/**
 * This class is only used because IntelliJ doesn't print
 * colors when executing runServer gradle task
 */
public class SmpxLogger {
    private final Logger logger;

    private static final String RESET = "\u001B[0m";
    private static final String BLUE = "\u001B[34m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";

    // remove this in prod
    private static final boolean DEBUG = true;

    public SmpxLogger(Logger logger) {
        this.logger = logger;
    }

    public void info(String message) {
        if (DEBUG) {
            this.logger.info(BLUE + message + RESET);
        } else {
            this.logger.info(message);
        }
    }

    public void warning(String message) {
        if (DEBUG) {
            this.logger.severe(YELLOW + message + RESET);
        } else {
            this.logger.warning(message);
        }
    }

    public void severe(String message) {
        if (DEBUG) {
            this.logger.severe(RED + message + RESET);
        } else {
            this.logger.severe(message);
        }
    }
}