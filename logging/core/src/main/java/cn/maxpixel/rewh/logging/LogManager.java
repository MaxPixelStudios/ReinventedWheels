package cn.maxpixel.rewh.logging;

import cn.maxpixel.rewh.logging.util.CallerFinder;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

import java.util.function.Function;

public class LogManager {
    private static final String FQCN = LogManager.class.getTypeName();
    private static final Object2ObjectOpenHashMap<String, Logger> LOGGERS = new Object2ObjectOpenHashMap<>();
    private static final Object2ObjectOpenHashMap<String, Marker> MARKERS = new Object2ObjectOpenHashMap<>();

    static {
        Config.init();
    }

    /**
     * Get the marker with given name. Will create one with no parents if the marker doesn't exist
     * @param name Marker name
     * @return The marker with given name
     */
    public static Marker getMarker(String name) {
        synchronized (MARKERS) {
            return MARKERS.computeIfAbsent(name, (Function<String, Marker>) Marker::new);
        }
    }

    /**
     * Get the marker with given name. Will create one with provided parents if the marker doesn't exist
     * @param name Marker name
     * @param parents The provided parents
     * @return The marker with given name
     */
    public static Marker getMarker(String name, String... parents) {
        Marker[] ma = new Marker[parents.length];
        for (int i = 0; i < parents.length; i++) {
            ma[i] = getMarker(parents[i]);
        }
        return getMarker(name, ma);
    }

    /**
     * Get the marker with given name. Will create one with provided parents if the marker doesn't exist
     * @param name Marker name
     * @param parents The provided parents
     * @return The marker with given name
     */
    public static Marker getMarker(String name, Marker... parents) {
        synchronized (MARKERS) {
            return MARKERS.computeIfAbsent(name, (Function<String, Marker>) key -> new Marker(key, ObjectOpenHashSet.of(parents)));
        }
    }

    /**
     * Get the logger with the name of the caller class name. Will create one if the logger doesn't exist
     * @return The logger
     */
    public static Logger getLogger() {
        return getLogger(CallerFinder.findCaller(FQCN).getClassName());
    }

    /**
     * Get the logger with given name. Will create one with given name if the logger doesn't exist
     * @param name Logger name
     * @return The logger with given name
     */
    public static Logger getLogger(String name) {
        synchronized (LOGGERS) {
            return LOGGERS.computeIfAbsent(name, Logger::new);
        }
    }

    static void reload() {
        synchronized (LOGGERS) {
            LOGGERS.forEach((name, logger) -> logger.reload());
        }
    }
}