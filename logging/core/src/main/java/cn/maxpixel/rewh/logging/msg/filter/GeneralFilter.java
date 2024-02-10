package cn.maxpixel.rewh.logging.msg.filter;

import cn.maxpixel.rewh.logging.Config;
import cn.maxpixel.rewh.logging.Level;
import cn.maxpixel.rewh.logging.Logger;
import cn.maxpixel.rewh.logging.Marker;
import cn.maxpixel.rewh.logging.msg.Message;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

import java.util.Collection;

public class GeneralFilter implements Filter {// TODO
    private final ObjectOpenHashSet<Marker> markers = new ObjectOpenHashSet<>();
    private final ObjectOpenHashSet<String> loggerNames = new ObjectOpenHashSet<>();
    public Level level;

    @Override
    public boolean isLoggable(Logger logger, Config.Logger config, Message msg) {
        return markers.contains(msg.getMarker()) &&
                loggerNames.contains(logger.name) &&
                (level == null || level.isLoggable(msg.getLevel()));
    }

    public boolean addMarker(Marker marker) {
        return markers.add(marker);
    }

    public boolean addAllMarker(Collection<? extends Marker> c) {
        return markers.addAll(c);
    }

    public boolean removeMarker(Marker k) {
        return markers.remove(k);
    }

    public boolean addLoggerName(String marker) {
        return loggerNames.add(marker);
    }

    public boolean addAllLoggerName(Collection<? extends String> c) {
        return loggerNames.addAll(c);
    }

    public boolean removeLoggerName(String k) {
        return loggerNames.remove(k);
    }
}