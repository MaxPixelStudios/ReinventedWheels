package cn.maxpixel.rewh.logging;

import cn.maxpixel.rewh.logging.config.Config;
import cn.maxpixel.rewh.logging.msg.MessageFactory;
import cn.maxpixel.rewh.logging.util.CallerFinder;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;

import java.util.function.Function;

public class LogManager {
    static {
        Config.init();
    }

    private static final String FQCN = LogManager.class.getTypeName();
    private static final Object2ObjectOpenHashMap<String, Logger> LOGGERS = new Object2ObjectOpenHashMap<>();
    private static final Object2ObjectOpenHashMap<String, Marker> MARKERS = new Object2ObjectOpenHashMap<>();

    public static Marker getMarker(String name) {
        synchronized (MARKERS) {
            return MARKERS.computeIfAbsent(name, (Function<String, Marker>) key -> new Marker(key, ObjectSets.emptySet()));
        }
    }

    public static Marker getMarker(String name, String... parents) {
        Marker[] ma = new Marker[parents.length];
        for (int i = 0; i < parents.length; i++) {
            ma[i] = getMarker(parents[i]);
        }
        return getMarker(name, ma);
    }

    public static Marker getMarker(String name, Marker... parents) {
        synchronized (MARKERS) {
            return MARKERS.computeIfAbsent(name, (Function<String, Marker>) key -> new Marker(key, ObjectOpenHashSet.of(parents)));
        }
    }

    public static Logger getLogger() {
        return getLogger(CallerFinder.findCaller(FQCN).getClassName());
    }

    public static Logger getLogger(String name) {
        synchronized (LOGGERS) {
            return LOGGERS.computeIfAbsent(name, Logger::new);
        }
    }

    public static void reload() {
        synchronized (LOGGERS) {
            LOGGERS.forEach((name, logger) -> logger.reload());
        }
        MessageFactory.reload();
    }
}