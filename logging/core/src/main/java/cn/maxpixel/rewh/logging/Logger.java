package cn.maxpixel.rewh.logging;

import cn.maxpixel.rewh.logging.msg.Message;
import cn.maxpixel.rewh.logging.msg.MessageFactory;
import cn.maxpixel.rewh.logging.msg.filter.Filter;
import cn.maxpixel.rewh.logging.msg.publisher.MessagePublisher;
import cn.maxpixel.rewh.logging.util.CallerFinder;
import cn.maxpixel.rewh.logging.util.Reusable;

import java.io.IOException;
import java.time.Clock;
import java.time.ZoneId;
import java.util.Objects;

public final class Logger {
    public static final StackTraceElement UNKNOWN = new StackTraceElement("Unknown Class", "Unknown Method", null, 0);
    public static final ZoneId ZONE = ZoneId.systemDefault();
    /*
     * Some notes:
     * https://github.com/openjdk/jdk/commit/6c838c568c2c99145fd0ae8059de2b2865b65863
     * Since the introduction of java.time.InstantSource in jdk 17,
     * Instant.now() calls no longer create Clock instances
     */
    public static final Clock CLOCK = Clock.systemUTC();
    private static final String FQCN = Logger.class.getTypeName();
    public final String name;
    private Config.Logger config;
    private MessagePublisher[] publishers;

    Logger(String name) {
        this.name = Objects.requireNonNull(name);
        reload();
    }

    void reload() {
        this.config = Config.getConfig(name);
        this.publishers = Config.get().publishers;
    }

    private StackTraceElement fetchCaller() {
        return config.fetchCaller ? CallerFinder.findCaller(FQCN) : null;
    }
    
    private static long mills() {
        return System.currentTimeMillis();
    }

    // Core methods

    public boolean isLoggable(Level level) {
        return config.level.isLoggable(level);
    }

    public void log(Message message) {
        try {
            if (!isLoggable(message.getLevel())) return;
            for (Filter filter : config.filters) if (!filter.isLoggable(this, config, message)) return;
            StringBuilder formatted = null;
            String colorPrefix = config.formatter.getColorPrefix(message.getLevel());
            for (MessagePublisher publisher : publishers)
                if (publisher.isLoggable(this, config, message)) {
                    if (formatted == null) {
                        formatted = config.formatter.format(message.getTimestamp(), message.getCaller(), name,
                                message.getMarker(), message.getLevel(), message.makeFormattedMessage(config));
                    }
                    publisher.publish(formatted, message.getThrowable(), colorPrefix);
                }
        } catch (IOException e) {
            System.err.println("Failed to log the message");
            e.printStackTrace();
        } finally {
            Reusable.tryReady(message);
        }
    }

    public void log(Level level, String msg) {
        if(isLoggable(level)) log(MessageFactory.get().create(null, fetchCaller(), level, mills(), msg));
    }

    public void log(Level level, String msg, Object arg0) {
        if(isLoggable(level)) log(MessageFactory.get().create(null, fetchCaller(), level, mills(), msg, arg0));
    }

    public void log(Level level, String msg, Object arg0, Object arg1) {
        if(isLoggable(level)) log(MessageFactory.get().create(null, fetchCaller(), level, mills(), msg, arg0, arg1));
    }

    public void log(Level level, String msg, Object arg0, Object arg1, Object arg2) {
        if(isLoggable(level)) log(MessageFactory.get().create(null, fetchCaller(), level, mills(), msg, arg0, arg1, arg2));
    }

    public void log(Level level, String msg, Object arg0, Object arg1, Object arg2, Object arg3) {
        if(isLoggable(level)) log(MessageFactory.get().create(null, fetchCaller(), level, mills(), msg, arg0, arg1, arg2, arg3));
    }

    public void log(Level level, String msg, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
        if(isLoggable(level)) log(MessageFactory.get().create(null, fetchCaller(), level, mills(), msg, arg0, arg1, arg2, arg3, arg4));
    }

    public void log(Level level, String msg, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
        if(isLoggable(level)) log(MessageFactory.get().create(null, fetchCaller(), level, mills(), msg, arg0, arg1, arg2, arg3, arg4, arg5));
    }

    public void log(Level level, String msg, Object... args) {
        if(isLoggable(level)) log(MessageFactory.get().create(null, fetchCaller(), level, mills(), msg, args));
    }

    public void log(Marker marker, Level level, String msg) {
        if(isLoggable(level)) log(MessageFactory.get().create(marker, fetchCaller(), level, mills(), msg));
    }

    public void log(Marker marker, Level level, String msg, Object arg0) {
        if(isLoggable(level)) log(MessageFactory.get().create(marker, fetchCaller(), level, mills(), msg, arg0));
    }

    public void log(Marker marker, Level level, String msg, Object arg0, Object arg1) {
        if(isLoggable(level)) log(MessageFactory.get().create(marker, fetchCaller(), level, mills(), msg, arg0, arg1));
    }

    public void log(Marker marker, Level level, String msg, Object arg0, Object arg1, Object arg2) {
        if(isLoggable(level)) log(MessageFactory.get().create(marker, fetchCaller(), level, mills(), msg, arg0, arg1, arg2));
    }

    public void log(Marker marker, Level level, String msg, Object arg0, Object arg1, Object arg2, Object arg3) {
        if(isLoggable(level)) log(MessageFactory.get().create(marker, fetchCaller(), level, mills(), msg, arg0, arg1, arg2, arg3));
    }

    public void log(Marker marker, Level level, String msg, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
        if(isLoggable(level)) log(MessageFactory.get().create(marker, fetchCaller(), level, mills(), msg, arg0, arg1, arg2, arg3, arg4));
    }

    public void log(Marker marker, Level level, String msg, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
        if(isLoggable(level)) log(MessageFactory.get().create(marker, fetchCaller(), level, mills(), msg, arg0, arg1, arg2, arg3, arg4, arg5));
    }

    public void log(Marker marker, Level level, String msg, Object... args) {
        if(isLoggable(level)) log(MessageFactory.get().create(marker, fetchCaller(), level, mills(), msg, args));
    }

    // Convenience methods

    public boolean isTraceLoggable() {
        return isLoggable(Level.TRACE);
    }

    public void trace(String msg) {
        log(Level.TRACE, msg);
    }

    public void trace(String msg, Object arg0) {
        log(Level.TRACE, msg, arg0);
    }

    public void trace(String msg, Object arg0, Object arg1) {
        log(Level.TRACE, msg, arg0, arg1);
    }

    public void trace(String msg, Object arg0, Object arg1, Object arg2) {
        log(Level.TRACE, msg, arg0, arg1, arg2);
    }

    public void trace(String msg, Object arg0, Object arg1, Object arg2, Object arg3) {
        log(Level.TRACE, msg, arg0, arg1, arg2, arg3);
    }

    public void trace(String msg, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
        log(Level.TRACE, msg, arg0, arg1, arg2, arg3, arg4);
    }

    public void trace(String msg, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
        log(Level.TRACE, msg, arg0, arg1, arg2, arg3, arg4, arg5);
    }

    public void trace(String msg, Object... args) {
        log(Level.TRACE, msg, args);
    }

    public void trace(Marker marker, String msg) {
        log(marker, Level.TRACE, msg);
    }

    public void trace(Marker marker, String msg, Object arg0) {
        log(marker, Level.TRACE, msg, arg0);
    }

    public void trace(Marker marker, String msg, Object arg0, Object arg1) {
        log(marker, Level.TRACE, msg, arg0, arg1);
    }

    public void trace(Marker marker, String msg, Object arg0, Object arg1, Object arg2) {
        log(marker, Level.TRACE, msg, arg0, arg1, arg2);
    }

    public void trace(Marker marker, String msg, Object arg0, Object arg1, Object arg2, Object arg3) {
        log(marker, Level.TRACE, msg, arg0, arg1, arg2, arg3);
    }

    public void trace(Marker marker, String msg, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
        log(marker, Level.TRACE, msg, arg0, arg1, arg2, arg3, arg4);
    }

    public void trace(Marker marker, String msg, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
        log(marker, Level.TRACE, msg, arg0, arg1, arg2, arg3, arg4, arg5);
    }

    public void trace(Marker marker, String msg, Object... args) {
        log(marker, Level.TRACE, msg, args);
    }

    public boolean isDebugLoggable() {
        return isLoggable(Level.DEBUG);
    }

    public void debug(String msg) {
        log(Level.DEBUG, msg);
    }

    public void debug(String msg, Object arg0) {
        log(Level.DEBUG, msg, arg0);
    }

    public void debug(String msg, Object arg0, Object arg1) {
        log(Level.DEBUG, msg, arg0, arg1);
    }

    public void debug(String msg, Object arg0, Object arg1, Object arg2) {
        log(Level.DEBUG, msg, arg0, arg1, arg2);
    }

    public void debug(String msg, Object arg0, Object arg1, Object arg2, Object arg3) {
        log(Level.DEBUG, msg, arg0, arg1, arg2, arg3);
    }

    public void debug(String msg, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
        log(Level.DEBUG, msg, arg0, arg1, arg2, arg3, arg4);
    }

    public void debug(String msg, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
        log(Level.DEBUG, msg, arg0, arg1, arg2, arg3, arg4, arg5);
    }

    public void debug(String msg, Object... args) {
        log(Level.DEBUG, msg, args);
    }

    public void debug(Marker marker, String msg) {
        log(marker, Level.DEBUG, msg);
    }

    public void debug(Marker marker, String msg, Object arg0) {
        log(marker, Level.DEBUG, msg, arg0);
    }

    public void debug(Marker marker, String msg, Object arg0, Object arg1) {
        log(marker, Level.DEBUG, msg, arg0, arg1);
    }

    public void debug(Marker marker, String msg, Object arg0, Object arg1, Object arg2) {
        log(marker, Level.DEBUG, msg, arg0, arg1, arg2);
    }

    public void debug(Marker marker, String msg, Object arg0, Object arg1, Object arg2, Object arg3) {
        log(marker, Level.DEBUG, msg, arg0, arg1, arg2, arg3);
    }

    public void debug(Marker marker, String msg, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
        log(marker, Level.DEBUG, msg, arg0, arg1, arg2, arg3, arg4);
    }

    public void debug(Marker marker, String msg, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
        log(marker, Level.DEBUG, msg, arg0, arg1, arg2, arg3, arg4, arg5);
    }

    public void debug(Marker marker, String msg, Object... args) {
        log(marker, Level.DEBUG, msg, args);
    }

    public boolean isInfoLoggable() {
        return isLoggable(Level.INFO);
    }

    public void info(String msg) {
        log(Level.INFO, msg);
    }

    public void info(String msg, Object arg0) {
        log(Level.INFO, msg, arg0);
    }

    public void info(String msg, Object arg0, Object arg1) {
        log(Level.INFO, msg, arg0, arg1);
    }

    public void info(String msg, Object arg0, Object arg1, Object arg2) {
        log(Level.INFO, msg, arg0, arg1, arg2);
    }

    public void info(String msg, Object arg0, Object arg1, Object arg2, Object arg3) {
        log(Level.INFO, msg, arg0, arg1, arg2, arg3);
    }

    public void info(String msg, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
        log(Level.INFO, msg, arg0, arg1, arg2, arg3, arg4);
    }

    public void info(String msg, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
        log(Level.INFO, msg, arg0, arg1, arg2, arg3, arg4, arg5);
    }

    public void info(String msg, Object... args) {
        log(Level.INFO, msg, args);
    }

    public void info(Marker marker, String msg) {
        log(marker, Level.INFO, msg);
    }

    public void info(Marker marker, String msg, Object arg0) {
        log(marker, Level.INFO, msg, arg0);
    }

    public void info(Marker marker, String msg, Object arg0, Object arg1) {
        log(marker, Level.INFO, msg, arg0, arg1);
    }

    public void info(Marker marker, String msg, Object arg0, Object arg1, Object arg2) {
        log(marker, Level.INFO, msg, arg0, arg1, arg2);
    }

    public void info(Marker marker, String msg, Object arg0, Object arg1, Object arg2, Object arg3) {
        log(marker, Level.INFO, msg, arg0, arg1, arg2, arg3);
    }

    public void info(Marker marker, String msg, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
        log(marker, Level.INFO, msg, arg0, arg1, arg2, arg3, arg4);
    }

    public void info(Marker marker, String msg, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
        log(marker, Level.INFO, msg, arg0, arg1, arg2, arg3, arg4, arg5);
    }

    public void info(Marker marker, String msg, Object... args) {
        log(marker, Level.INFO, msg, args);
    }

    public boolean isWarnLoggable() {
        return isLoggable(Level.WARN);
    }

    public void warn(String msg) {
        log(Level.WARN, msg);
    }

    public void warn(String msg, Object arg0) {
        log(Level.WARN, msg, arg0);
    }

    public void warn(String msg, Object arg0, Object arg1) {
        log(Level.WARN, msg, arg0, arg1);
    }

    public void warn(String msg, Object arg0, Object arg1, Object arg2) {
        log(Level.WARN, msg, arg0, arg1, arg2);
    }

    public void warn(String msg, Object arg0, Object arg1, Object arg2, Object arg3) {
        log(Level.WARN, msg, arg0, arg1, arg2, arg3);
    }

    public void warn(String msg, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
        log(Level.WARN, msg, arg0, arg1, arg2, arg3, arg4);
    }

    public void warn(String msg, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
        log(Level.WARN, msg, arg0, arg1, arg2, arg3, arg4, arg5);
    }

    public void warn(String msg, Object... args) {
        log(Level.WARN, msg, args);
    }

    public void warn(Marker marker, String msg) {
        log(marker, Level.WARN, msg);
    }

    public void warn(Marker marker, String msg, Object arg0) {
        log(marker, Level.WARN, msg, arg0);
    }

    public void warn(Marker marker, String msg, Object arg0, Object arg1) {
        log(marker, Level.WARN, msg, arg0, arg1);
    }

    public void warn(Marker marker, String msg, Object arg0, Object arg1, Object arg2) {
        log(marker, Level.WARN, msg, arg0, arg1, arg2);
    }

    public void warn(Marker marker, String msg, Object arg0, Object arg1, Object arg2, Object arg3) {
        log(marker, Level.WARN, msg, arg0, arg1, arg2, arg3);
    }

    public void warn(Marker marker, String msg, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
        log(marker, Level.WARN, msg, arg0, arg1, arg2, arg3, arg4);
    }

    public void warn(Marker marker, String msg, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
        log(marker, Level.WARN, msg, arg0, arg1, arg2, arg3, arg4, arg5);
    }

    public void warn(Marker marker, String msg, Object... args) {
        log(marker, Level.WARN, msg, args);
    }

    public boolean isErrorLoggable() {
        return isLoggable(Level.ERROR);
    }

    public void error(String msg) {
        log(Level.ERROR, msg);
    }

    public void error(String msg, Object arg0) {
        log(Level.ERROR, msg, arg0);
    }

    public void error(String msg, Object arg0, Object arg1) {
        log(Level.ERROR, msg, arg0, arg1);
    }

    public void error(String msg, Object arg0, Object arg1, Object arg2) {
        log(Level.ERROR, msg, arg0, arg1, arg2);
    }

    public void error(String msg, Object arg0, Object arg1, Object arg2, Object arg3) {
        log(Level.ERROR, msg, arg0, arg1, arg2, arg3);
    }

    public void error(String msg, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
        log(Level.ERROR, msg, arg0, arg1, arg2, arg3, arg4);
    }

    public void error(String msg, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
        log(Level.ERROR, msg, arg0, arg1, arg2, arg3, arg4, arg5);
    }

    public void error(String msg, Object... args) {
        log(Level.ERROR, msg, args);
    }

    public void error(Marker marker, String msg) {
        log(marker, Level.ERROR, msg);
    }

    public void error(Marker marker, String msg, Object arg0) {
        log(marker, Level.ERROR, msg, arg0);
    }

    public void error(Marker marker, String msg, Object arg0, Object arg1) {
        log(marker, Level.ERROR, msg, arg0, arg1);
    }

    public void error(Marker marker, String msg, Object arg0, Object arg1, Object arg2) {
        log(marker, Level.ERROR, msg, arg0, arg1, arg2);
    }

    public void error(Marker marker, String msg, Object arg0, Object arg1, Object arg2, Object arg3) {
        log(marker, Level.ERROR, msg, arg0, arg1, arg2, arg3);
    }

    public void error(Marker marker, String msg, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
        log(marker, Level.ERROR, msg, arg0, arg1, arg2, arg3, arg4);
    }

    public void error(Marker marker, String msg, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
        log(marker, Level.ERROR, msg, arg0, arg1, arg2, arg3, arg4, arg5);
    }

    public void error(Marker marker, String msg, Object... args) {
        log(marker, Level.ERROR, msg, args);
    }

    public boolean isFatalLoggable() {
        return isLoggable(Level.FATAL);
    }

    public void fatal(String msg) {
        log(Level.FATAL, msg);
    }

    public void fatal(String msg, Object arg0) {
        log(Level.FATAL, msg, arg0);
    }

    public void fatal(String msg, Object arg0, Object arg1) {
        log(Level.FATAL, msg, arg0, arg1);
    }

    public void fatal(String msg, Object arg0, Object arg1, Object arg2) {
        log(Level.FATAL, msg, arg0, arg1, arg2);
    }

    public void fatal(String msg, Object arg0, Object arg1, Object arg2, Object arg3) {
        log(Level.FATAL, msg, arg0, arg1, arg2, arg3);
    }

    public void fatal(String msg, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
        log(Level.FATAL, msg, arg0, arg1, arg2, arg3, arg4);
    }

    public void fatal(String msg, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
        log(Level.FATAL, msg, arg0, arg1, arg2, arg3, arg4, arg5);
    }

    public void fatal(String msg, Object... args) {
        log(Level.FATAL, msg, args);
    }

    public void fatal(Marker marker, String msg) {
        log(marker, Level.FATAL, msg);
    }

    public void fatal(Marker marker, String msg, Object arg0) {
        log(marker, Level.FATAL, msg, arg0);
    }

    public void fatal(Marker marker, String msg, Object arg0, Object arg1) {
        log(marker, Level.FATAL, msg, arg0, arg1);
    }

    public void fatal(Marker marker, String msg, Object arg0, Object arg1, Object arg2) {
        log(marker, Level.FATAL, msg, arg0, arg1, arg2);
    }

    public void fatal(Marker marker, String msg, Object arg0, Object arg1, Object arg2, Object arg3) {
        log(marker, Level.FATAL, msg, arg0, arg1, arg2, arg3);
    }

    public void fatal(Marker marker, String msg, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
        log(marker, Level.FATAL, msg, arg0, arg1, arg2, arg3, arg4);
    }

    public void fatal(Marker marker, String msg, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
        log(marker, Level.FATAL, msg, arg0, arg1, arg2, arg3, arg4, arg5);
    }

    public void fatal(Marker marker, String msg, Object... args) {
        log(marker, Level.FATAL, msg, args);
    }
}