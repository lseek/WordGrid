package com.game.wordgrid;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.Handler;
import java.util.logging.Level;


class OneLineFormatter extends Formatter {
    @Override
    public String format(LogRecord record) {
        SimpleDateFormat logTime = new SimpleDateFormat("HH:mm:ss.S");
        Date logMsecs = new Date(record.getMillis());
        String outStr = String.format("[%1$s %2$8s] %3$s%n",
                                      logTime.format(logMsecs),
                                      record.getLevel(),
                                      record.getMessage());
        if (record.getThrown() != null) {
            try {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                record.getThrown().printStackTrace(pw);
                pw.close();
                outStr = outStr + sw.toString();
            } catch (Exception ex) {
                // ignore
            }
        }

        return outStr;
    }
}

public class LOG {
    static private Logger _LOG;
    static private OneLineFormatter oneLineFmt;

    static void initLog(Level logLevel) {
        Handler[] handlers;
        Handler   console;

        _LOG = Logger.getLogger("");
        console = _LOG.getHandlers()[0];
        assert (console.getClass().getName() == "java.util.logging.ConsoleHandler");

        oneLineFmt = new OneLineFormatter();
        console.setFormatter(oneLineFmt);
        _LOG.setLevel(logLevel);
    }

    static void severe(String fmt, Object... args) {
        _LOG.severe(String.format(fmt, args));
    }

    static void warning(String fmt, Object... args) {
        _LOG.warning(String.format(fmt, args));
    }

    static void info(String fmt, Object... args) {
        _LOG.info(String.format(fmt, args));
    }

    static void config(String fmt, Object... args) {
        _LOG.config(String.format(fmt, args));
    }

    static void fine(String fmt, Object... args) {
        _LOG.fine(String.format(fmt, args));
    }

    static void finer(String fmt, Object... args) {
        _LOG.finer(String.format(fmt, args));
    }

    static void finest(String fmt, Object... args) {
        _LOG.finest(String.format(fmt, args));
    }

    static void setLevel(Level newLevel) {
        _LOG.setLevel(newLevel);
    }
}
