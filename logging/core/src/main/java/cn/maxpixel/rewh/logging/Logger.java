package cn.maxpixel.rewh.logging;

import cn.maxpixel.rewh.logging.config.Config;
import cn.maxpixel.rewh.logging.config.LoggerConfig;
import cn.maxpixel.rewh.logging.msg.Message;
import cn.maxpixel.rewh.logging.msg.MessageFactory;
import cn.maxpixel.rewh.logging.msg.filter.Filter;
import cn.maxpixel.rewh.logging.msg.publisher.MessagePublisher;
import cn.maxpixel.rewh.logging.util.CallerFinder;
import org.fusesource.jansi.Ansi;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;
import java.util.function.UnaryOperator;

public final class Logger {
    public static final StackTraceElement UNKNOWN = new StackTraceElement("Unknown Class", "Unknown Method", null, 0);
    private static final String FQCN = Logger.class.getTypeName();
    private static final Clock CLOCK = Clock.systemDefaultZone();
    public final String name;
    private LoggerConfig config;
    private MessagePublisher[] publishers;

    Logger(String name) {
        this.name = Objects.requireNonNull(name);
        reload();
    }

    void reload() {
        this.config = Config.getConfig(name);// TODO: hierarchy
        this.publishers = Config.getPublishers();
    }

    private StackTraceElement fetchCaller() {
        return config.fetchCaller ? CallerFinder.findCaller(FQCN) : null;
    }

    // Core methods

    public boolean isLoggable(Level level) {
        return config.level.isLoggable(level);
    }

    public void log(Message message) {
        try {
            if (!isLoggable(message.getLevel())) return;
            for (Filter filter : config.filters) if (!filter.isLoggable(message)) return;
            String formatted = config.formatter.format(message.getInstant(), message.getCaller(), name, message.getMarker(),
                    message.getLevel(), message.makeFormattedMessage(config));
            UnaryOperator<Ansi> colorApplicator = config.formatter.getColorApplicator(message.getLevel());
            for (MessagePublisher publisher : publishers)
                if (publisher.isLoggable(this, config, message))
                    publisher.publish(formatted, colorApplicator);
        } catch (IOException e) {
            System.err.println("Failed to log the message");
            e.printStackTrace();
        } finally {
            Reusable.tryReady(message);
        }
    }

    public void log(Level level, String msg) {
        if(isLoggable(level)) log(MessageFactory.get().create(null, fetchCaller(), level, Instant.now(CLOCK), msg));
    }

    public void log(Level level, String msg, Object arg0) {
        if(isLoggable(level)) log(MessageFactory.get().create(null, fetchCaller(), level, Instant.now(CLOCK), msg, arg0));
    }

    public void log(Level level, String msg, Object arg0, Object arg1) {
        if(isLoggable(level)) log(MessageFactory.get().create(null, fetchCaller(), level, Instant.now(CLOCK), msg, arg0, arg1));
    }

    public void log(Level level, String msg, Object arg0, Object arg1, Object arg2) {
        if(isLoggable(level)) log(MessageFactory.get().create(null, fetchCaller(), level, Instant.now(CLOCK), msg, arg0, arg1, arg2));
    }

    public void log(Level level, String msg, Object arg0, Object arg1, Object arg2, Object arg3) {
        if(isLoggable(level)) log(MessageFactory.get().create(null, fetchCaller(), level, Instant.now(CLOCK), msg, arg0, arg1, arg2, arg3));
    }

    public void log(Level level, String msg, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
        if(isLoggable(level)) log(MessageFactory.get().create(null, fetchCaller(), level, Instant.now(CLOCK), msg, arg0, arg1, arg2, arg3, arg4));
    }

    public void log(Level level, String msg, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
        if(isLoggable(level)) log(MessageFactory.get().create(null, fetchCaller(), level, Instant.now(CLOCK), msg, arg0, arg1, arg2, arg3, arg4, arg5));
    }

    public void log(Level level, String msg, Object... args) {
        if(isLoggable(level)) log(MessageFactory.get().create(null, fetchCaller(), level, Instant.now(CLOCK), msg, args));
    }

    public void log(Marker marker, Level level, String msg) {
        if(isLoggable(level)) log(MessageFactory.get().create(marker, fetchCaller(), level, Instant.now(CLOCK), msg));
    }

    public void log(Marker marker, Level level, String msg, Object arg0) {
        if(isLoggable(level)) log(MessageFactory.get().create(marker, fetchCaller(), level, Instant.now(CLOCK), msg, arg0));
    }

    public void log(Marker marker, Level level, String msg, Object arg0, Object arg1) {
        if(isLoggable(level)) log(MessageFactory.get().create(marker, fetchCaller(), level, Instant.now(CLOCK), msg, arg0, arg1));
    }

    public void log(Marker marker, Level level, String msg, Object arg0, Object arg1, Object arg2) {
        if(isLoggable(level)) log(MessageFactory.get().create(marker, fetchCaller(), level, Instant.now(CLOCK), msg, arg0, arg1, arg2));
    }

    public void log(Marker marker, Level level, String msg, Object arg0, Object arg1, Object arg2, Object arg3) {
        if(isLoggable(level)) log(MessageFactory.get().create(marker, fetchCaller(), level, Instant.now(CLOCK), msg, arg0, arg1, arg2, arg3));
    }

    public void log(Marker marker, Level level, String msg, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
        if(isLoggable(level)) log(MessageFactory.get().create(marker, fetchCaller(), level, Instant.now(CLOCK), msg, arg0, arg1, arg2, arg3, arg4));
    }

    public void log(Marker marker, Level level, String msg, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
        if(isLoggable(level)) log(MessageFactory.get().create(marker, fetchCaller(), level, Instant.now(CLOCK), msg, arg0, arg1, arg2, arg3, arg4, arg5));
    }

    public void log(Marker marker, Level level, String msg, Object... args) {
        if(isLoggable(level)) log(MessageFactory.get().create(marker, fetchCaller(), level, Instant.now(CLOCK), msg, args));
    }

    // Convenience methods

    public boolean isTraceLoggable() {
        return isLoggable(Level.TRACE);
    }

    public void trace(String msg) {
        log(Level.FATAL, msg);
    }

    public void trace(String msg, Object arg0) {
        log(Level.FATAL, msg, arg0);
    }

    public void trace(String msg, Object arg0, Object arg1) {
        log(Level.FATAL, msg, arg0, arg1);
    }

    public void trace(String msg, Object arg0, Object arg1, Object arg2) {
        log(Level.FATAL, msg, arg0, arg1, arg2);
    }

    public void trace(String msg, Object arg0, Object arg1, Object arg2, Object arg3) {
        log(Level.FATAL, msg, arg0, arg1, arg2, arg3);
    }

    public void trace(String msg, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
        log(Level.FATAL, msg, arg0, arg1, arg2, arg3, arg4);
    }

    public void trace(String msg, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
        log(Level.FATAL, msg, arg0, arg1, arg2, arg3, arg4, arg5);
    }

    public void trace(String msg, Object... args) {
        log(Level.FATAL, msg, args);
    }

    public void trace(Marker marker, String msg) {
        log(marker, Level.FATAL, msg);
    }

    public void trace(Marker marker, String msg, Object arg0) {
        log(marker, Level.FATAL, msg, arg0);
    }

    public void trace(Marker marker, String msg, Object arg0, Object arg1) {
        log(marker, Level.FATAL, msg, arg0, arg1);
    }

    public void trace(Marker marker, String msg, Object arg0, Object arg1, Object arg2) {
        log(marker, Level.FATAL, msg, arg0, arg1, arg2);
    }

    public void trace(Marker marker, String msg, Object arg0, Object arg1, Object arg2, Object arg3) {
        log(marker, Level.FATAL, msg, arg0, arg1, arg2, arg3);
    }

    public void trace(Marker marker, String msg, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
        log(marker, Level.FATAL, msg, arg0, arg1, arg2, arg3, arg4);
    }

    public void trace(Marker marker, String msg, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
        log(marker, Level.FATAL, msg, arg0, arg1, arg2, arg3, arg4, arg5);
    }

    public void trace(Marker marker, String msg, Object... args) {
        log(marker, Level.FATAL, msg, args);
    }

    public boolean isDebugLoggable() {
        return isLoggable(Level.DEBUG);
    }

    public void debug(String msg) {
        log(Level.FATAL, msg);
    }

    public void debug(String msg, Object arg0) {
        log(Level.FATAL, msg, arg0);
    }

    public void debug(String msg, Object arg0, Object arg1) {
        log(Level.FATAL, msg, arg0, arg1);
    }

    public void debug(String msg, Object arg0, Object arg1, Object arg2) {
        log(Level.FATAL, msg, arg0, arg1, arg2);
    }

    public void debug(String msg, Object arg0, Object arg1, Object arg2, Object arg3) {
        log(Level.FATAL, msg, arg0, arg1, arg2, arg3);
    }

    public void debug(String msg, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
        log(Level.FATAL, msg, arg0, arg1, arg2, arg3, arg4);
    }

    public void debug(String msg, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
        log(Level.FATAL, msg, arg0, arg1, arg2, arg3, arg4, arg5);
    }

    public void debug(String msg, Object... args) {
        log(Level.FATAL, msg, args);
    }

    public void debug(Marker marker, String msg) {
        log(marker, Level.FATAL, msg);
    }

    public void debug(Marker marker, String msg, Object arg0) {
        log(marker, Level.FATAL, msg, arg0);
    }

    public void debug(Marker marker, String msg, Object arg0, Object arg1) {
        log(marker, Level.FATAL, msg, arg0, arg1);
    }

    public void debug(Marker marker, String msg, Object arg0, Object arg1, Object arg2) {
        log(marker, Level.FATAL, msg, arg0, arg1, arg2);
    }

    public void debug(Marker marker, String msg, Object arg0, Object arg1, Object arg2, Object arg3) {
        log(marker, Level.FATAL, msg, arg0, arg1, arg2, arg3);
    }

    public void debug(Marker marker, String msg, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
        log(marker, Level.FATAL, msg, arg0, arg1, arg2, arg3, arg4);
    }

    public void debug(Marker marker, String msg, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
        log(marker, Level.FATAL, msg, arg0, arg1, arg2, arg3, arg4, arg5);
    }

    public void debug(Marker marker, String msg, Object... args) {
        log(marker, Level.FATAL, msg, args);
    }

    public boolean isInfoLoggable() {
        return isLoggable(Level.INFO);
    }

    public void info(String msg) {
        log(Level.FATAL, msg);
    }

    public void info(String msg, Object arg0) {
        log(Level.FATAL, msg, arg0);
    }

    public void info(String msg, Object arg0, Object arg1) {
        log(Level.FATAL, msg, arg0, arg1);
    }

    public void info(String msg, Object arg0, Object arg1, Object arg2) {
        log(Level.FATAL, msg, arg0, arg1, arg2);
    }

    public void info(String msg, Object arg0, Object arg1, Object arg2, Object arg3) {
        log(Level.FATAL, msg, arg0, arg1, arg2, arg3);
    }

    public void info(String msg, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
        log(Level.FATAL, msg, arg0, arg1, arg2, arg3, arg4);
    }

    public void info(String msg, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
        log(Level.FATAL, msg, arg0, arg1, arg2, arg3, arg4, arg5);
    }

    public void info(String msg, Object... args) {
        log(Level.FATAL, msg, args);
    }

    public void info(Marker marker, String msg) {
        log(marker, Level.FATAL, msg);
    }

    public void info(Marker marker, String msg, Object arg0) {
        log(marker, Level.FATAL, msg, arg0);
    }

    public void info(Marker marker, String msg, Object arg0, Object arg1) {
        log(marker, Level.FATAL, msg, arg0, arg1);
    }

    public void info(Marker marker, String msg, Object arg0, Object arg1, Object arg2) {
        log(marker, Level.FATAL, msg, arg0, arg1, arg2);
    }

    public void info(Marker marker, String msg, Object arg0, Object arg1, Object arg2, Object arg3) {
        log(marker, Level.FATAL, msg, arg0, arg1, arg2, arg3);
    }

    public void info(Marker marker, String msg, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
        log(marker, Level.FATAL, msg, arg0, arg1, arg2, arg3, arg4);
    }

    public void info(Marker marker, String msg, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
        log(marker, Level.FATAL, msg, arg0, arg1, arg2, arg3, arg4, arg5);
    }

    public void info(Marker marker, String msg, Object... args) {
        log(marker, Level.FATAL, msg, args);
    }

    public boolean isWarnLoggable() {
        return isLoggable(Level.WARN);
    }

    public void warn(String msg) {
        log(Level.FATAL, msg);
    }

    public void warn(String msg, Object arg0) {
        log(Level.FATAL, msg, arg0);
    }

    public void warn(String msg, Object arg0, Object arg1) {
        log(Level.FATAL, msg, arg0, arg1);
    }

    public void warn(String msg, Object arg0, Object arg1, Object arg2) {
        log(Level.FATAL, msg, arg0, arg1, arg2);
    }

    public void warn(String msg, Object arg0, Object arg1, Object arg2, Object arg3) {
        log(Level.FATAL, msg, arg0, arg1, arg2, arg3);
    }

    public void warn(String msg, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
        log(Level.FATAL, msg, arg0, arg1, arg2, arg3, arg4);
    }

    public void warn(String msg, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
        log(Level.FATAL, msg, arg0, arg1, arg2, arg3, arg4, arg5);
    }

    public void warn(String msg, Object... args) {
        log(Level.FATAL, msg, args);
    }

    public void warn(Marker marker, String msg) {
        log(marker, Level.FATAL, msg);
    }

    public void warn(Marker marker, String msg, Object arg0) {
        log(marker, Level.FATAL, msg, arg0);
    }

    public void warn(Marker marker, String msg, Object arg0, Object arg1) {
        log(marker, Level.FATAL, msg, arg0, arg1);
    }

    public void warn(Marker marker, String msg, Object arg0, Object arg1, Object arg2) {
        log(marker, Level.FATAL, msg, arg0, arg1, arg2);
    }

    public void warn(Marker marker, String msg, Object arg0, Object arg1, Object arg2, Object arg3) {
        log(marker, Level.FATAL, msg, arg0, arg1, arg2, arg3);
    }

    public void warn(Marker marker, String msg, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
        log(marker, Level.FATAL, msg, arg0, arg1, arg2, arg3, arg4);
    }

    public void warn(Marker marker, String msg, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
        log(marker, Level.FATAL, msg, arg0, arg1, arg2, arg3, arg4, arg5);
    }

    public void warn(Marker marker, String msg, Object... args) {
        log(marker, Level.FATAL, msg, args);
    }

    public boolean isErrorLoggable() {
        return isLoggable(Level.ERROR);
    }

    public void error(String msg) {
        log(Level.FATAL, msg);
    }

    public void error(String msg, Object arg0) {
        log(Level.FATAL, msg, arg0);
    }

    public void error(String msg, Object arg0, Object arg1) {
        log(Level.FATAL, msg, arg0, arg1);
    }

    public void error(String msg, Object arg0, Object arg1, Object arg2) {
        log(Level.FATAL, msg, arg0, arg1, arg2);
    }

    public void error(String msg, Object arg0, Object arg1, Object arg2, Object arg3) {
        log(Level.FATAL, msg, arg0, arg1, arg2, arg3);
    }

    public void error(String msg, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
        log(Level.FATAL, msg, arg0, arg1, arg2, arg3, arg4);
    }

    public void error(String msg, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
        log(Level.FATAL, msg, arg0, arg1, arg2, arg3, arg4, arg5);
    }

    public void error(String msg, Object... args) {
        log(Level.FATAL, msg, args);
    }

    public void error(Marker marker, String msg) {
        log(marker, Level.FATAL, msg);
    }

    public void error(Marker marker, String msg, Object arg0) {
        log(marker, Level.FATAL, msg, arg0);
    }

    public void error(Marker marker, String msg, Object arg0, Object arg1) {
        log(marker, Level.FATAL, msg, arg0, arg1);
    }

    public void error(Marker marker, String msg, Object arg0, Object arg1, Object arg2) {
        log(marker, Level.FATAL, msg, arg0, arg1, arg2);
    }

    public void error(Marker marker, String msg, Object arg0, Object arg1, Object arg2, Object arg3) {
        log(marker, Level.FATAL, msg, arg0, arg1, arg2, arg3);
    }

    public void error(Marker marker, String msg, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
        log(marker, Level.FATAL, msg, arg0, arg1, arg2, arg3, arg4);
    }

    public void error(Marker marker, String msg, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
        log(marker, Level.FATAL, msg, arg0, arg1, arg2, arg3, arg4, arg5);
    }

    public void error(Marker marker, String msg, Object... args) {
        log(marker, Level.FATAL, msg, args);
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