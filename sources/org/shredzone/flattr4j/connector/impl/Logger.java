package org.shredzone.flattr4j.connector.impl;

import android.util.Log;
import java.text.MessageFormat;
import java.util.logging.Level;

public class Logger {
    private final java.util.logging.Logger logger;
    private final String tag;

    public Logger(String tag, String className) {
        this.tag = tag;
        java.util.logging.Logger log = null;
        try {
            Log.isLoggable(tag, 4);
        } catch (Throwable th) {
            log = java.util.logging.Logger.getLogger(className);
        }
        this.logger = log;
    }

    public void debug(String msg, Throwable ex) {
        java.util.logging.Logger logger = this.logger;
        if (logger != null) {
            if (logger.isLoggable(Level.FINER)) {
                this.logger.log(Level.FINER, msg, ex);
            }
        } else if (Log.isLoggable(this.tag, 3)) {
            Log.v(this.tag, msg, ex);
        }
    }

    public void verbose(String msg, Object... args) {
        java.util.logging.Logger logger = this.logger;
        if (logger != null) {
            if (logger.isLoggable(Level.FINE)) {
                this.logger.log(Level.FINE, msg, args);
            }
        } else if (Log.isLoggable(this.tag, 2)) {
            Log.v(this.tag, MessageFormat.format(msg, args));
        }
    }

    public void info(String msg, Object... args) {
        java.util.logging.Logger logger = this.logger;
        if (logger != null) {
            if (logger.isLoggable(Level.INFO)) {
                this.logger.log(Level.INFO, msg, args);
            }
        } else if (Log.isLoggable(this.tag, 4)) {
            Log.i(this.tag, MessageFormat.format(msg, args));
        }
    }

    public void error(String msg, Object... args) {
        java.util.logging.Logger logger = this.logger;
        if (logger != null) {
            if (logger.isLoggable(Level.WARNING)) {
                this.logger.log(Level.WARNING, msg, args);
            }
        } else if (Log.isLoggable(this.tag, 5)) {
            Log.w(this.tag, MessageFormat.format(msg, args));
        }
    }
}
