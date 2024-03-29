package cn.maxpixel.rewh.logging.util;

import cn.maxpixel.rewh.logging.Config;
import cn.maxpixel.rewh.logging.Level;
import cn.maxpixel.rewh.logging.Logger;
import cn.maxpixel.rewh.logging.Marker;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.fusesource.jansi.Ansi;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;

public final class Formatter {
    /**
     * Message
     */
    private static final byte TYPE_MSG = 1;
    /**
     * Level
     */
    private static final byte TYPE_LEVEL = 2;
    /**
     * Logger name
     */
    private static final byte TYPE_NAME = 3;
    /**
     * Marker name
     */
    private static final byte TYPE_MARKER = 4;
    /**
     * Time when the log method was called
     */
    private static final byte TYPE_TIME = 5;
    /**
     * Class of the method that attempts to log this message
     */
    private static final byte TYPE_SOURCE_CLASS = 6;
    /**
     * Method that attempts to log this message
     */
    private static final byte TYPE_SOURCE_METHOD = 7;
    private static final byte TYPE_STRING = 8;
    private static final Object2ObjectOpenHashMap<String, DateTimeFormatter> BY_NAME = new Object2ObjectOpenHashMap<>();
    static {
        for (Field field : DateTimeFormatter.class.getFields()) {
            try {
                BY_NAME.put(field.getName(), (DateTimeFormatter) field.get(null));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public static final Formatter DEFAULT = new Formatter("[%time%] [%source_class%/%source_method%] [%name%] [%level%] %msg%\n");

    private final Map<Level, Color> color = new Object2ObjectOpenHashMap<>();
    {
        if (Config.JANSI_INSTALLED) {
            color.put(Level.TRACE, new Color(Ansi.Color.BLACK, true));
            color.put(Level.DEBUG, new Color(Ansi.Color.WHITE, false));
            color.put(Level.INFO, new Color(Ansi.Color.WHITE, true));
            color.put(Level.WARN, new Color(Ansi.Color.YELLOW, false));
            color.put(Level.ERROR, new Color(Ansi.Color.RED, false));
            color.put(Level.FATAL, new Color(Ansi.Color.RED, true));
        }
    }
    private final String time;
    private final String formatString;

    private final String[] strings;
    private final byte[] components;
    private final DateTimeFormatter dateTimeFormatter;
    private final ThreadLocal<StringBuilder> buffers;

    public Formatter(String formatString) {
        this(formatString, "ISO_LOCAL_DATE_TIME", null);
    }

    public Formatter(String formatString, String time) {
        this(formatString, time, null);
    }

    public Formatter(String formatString, Map<Level, Color> color) {
        this(formatString, "ISO_LOCAL_DATE_TIME", color);
    }

    public Formatter(String formatString, String time, Map<Level, Color> color) {
        this.formatString = formatString;
        this.buffers = ThreadLocal.withInitial(() -> new StringBuilder(this.formatString.length() + 16));
        this.time = time;
        if (color != null) this.color.putAll(color);
        DateTimeFormatter dateTimeFormatter = BY_NAME.get(time);
        this.dateTimeFormatter = dateTimeFormatter == null ? DateTimeFormatter.ofPattern(time) : dateTimeFormatter;
        int index = formatString.indexOf('%'), next = formatString.indexOf('%', index + 1), prev = 0;
        if (index < 0 || next < 0) { // No variables, but why are you using a logging framework?
            this.strings = null;
            this.components = null;
            return;
        }
        ObjectArrayList<String> strings = new ObjectArrayList<>();
        ByteArrayList components = new ByteArrayList(32);
        do {
            components.add(TYPE_STRING);
            int nameLen = next - index; // Calculated length contains a % char
            if (next == -1) {
                strings.add(formatString.substring(prev));
                this.components = components.toByteArray();
                this.strings = strings.toArray(new String[0]);
                return;
            } else if (nameLen > 3 && nameLen <= 14) { // May contain a variable("msg".length() == 3, "source_method".length() == 13)
                byte type = identifyVariable(formatString.substring(index + 1, next));
                if (type == TYPE_STRING) {
                    strings.add(formatString.substring(prev, next));
                } else {
                    strings.add(prev == index ? "" : formatString.substring(prev, index));
                    components.add(type);
                    index = formatString.indexOf('%', next + 1);
                    prev = next + 1;
                    next = formatString.indexOf('%', index + 1);
                    continue;
                }
            } else {
                strings.add(formatString.substring(prev, next));
            }
            index = next;
            prev = next;
            next = formatString.indexOf('%', index + 1);
        } while(index != -1);
        if (prev < formatString.length()) {
            components.add(TYPE_STRING);
            strings.add(formatString.substring(prev));
        }
        this.components = components.toByteArray();
        this.strings = strings.toArray(new String[0]);
    }

    private static byte identifyVariable(String s) {
        switch (s) {
            case "msg":
                return TYPE_MSG;
            case "level":
                return TYPE_LEVEL;
            case "name":
                return TYPE_NAME;
            case "marker":
                return TYPE_MARKER;
            case "time":
                return TYPE_TIME;
            case "source_class":
                return TYPE_SOURCE_CLASS;
            case "source_method":
                return TYPE_SOURCE_METHOD;
            default:
                return TYPE_STRING;
        }
    }

    public StringBuilder format(long timestamp, StackTraceElement caller, String name, Marker marker, Level level, StringBuilder msg) {
        if (components != null) {
            Objects.requireNonNull(name);
            if (caller == null) caller = Logger.UNKNOWN;
            Objects.requireNonNull(level);
            Objects.requireNonNull(msg);
            StringBuilder sb = buffers.get();
            sb.setLength(0);
            int index = 0;
            for (byte component : components) {
                switch (component) {
                    case TYPE_MSG:
                        sb.append(msg);
                        break;
                    case TYPE_LEVEL:
                        sb.append(level.name);
                        break;
                    case TYPE_NAME:
                        sb.append(name);
                        break;
                    case TYPE_MARKER:
                        if (marker == null) sb.append("No marker");
                        else sb.append(marker.name);
                    case TYPE_TIME:
                        dateTimeFormatter.formatTo(ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestamp), Logger.ZONE), sb);
                        break;
                    case TYPE_SOURCE_CLASS:
                        sb.append(caller.getClassName());
                        break;
                    case TYPE_SOURCE_METHOD:
                        sb.append(caller.getMethodName());
                        break;
                    case TYPE_STRING:
                        sb.append(strings[index++]);
                        break;
                }
            }
            return sb;
        }
        return new StringBuilder(formatString);// This won't be executed the most cases
    }

    public String getColorPrefix(Level level) {
        Color c;
        if (!Config.COLORFUL || (c = color.get(level)) == null) return "";
        return c.colorString;
    }

    public static class Adapter extends TypeAdapter<Formatter> {
        @Override
        public void write(JsonWriter out, Formatter value) throws IOException {
            if (DEFAULT.time.equals(value.time) && DEFAULT.color.equals(value.color)) out.value(value.formatString);
            else Config.GSON.toJson(value.color, Map.class, out.beginObject().name("format").value(value.formatString)
                    .name("time").value(value.time)
                    .name("color"));
        }

        @Override
        public Formatter read(JsonReader in) throws IOException {
            switch (in.peek()) {
                case STRING:
                    return new Formatter(in.nextString());
                case BEGIN_OBJECT:
                    in.beginObject();
                    String formatString = DEFAULT.formatString;
                    String time = DEFAULT.time;
                    Map<Level, Color> color = null;
                    do switch (in.nextName()) {
                        case "format":
                            formatString = in.nextString();
                            break;
                        case "time":
                            time = in.nextString();
                            break;
                        case "color":
                            color = Config.GSON.fromJson(in, Map.class);
                            break;
                    } while (in.peek() == JsonToken.NAME);
                    in.endObject();
                    return new Formatter(formatString, time, color);
                default:
                    throw new JsonParseException("Invalid format: expected a string or object, but found " + in.peek());
            }
        }
    }

    public static final class Color {
        public enum Type {
            COLOR_8,
            COLOR_256,
            COLOR_RGB
        }

        public final Type type;
        public final int value;
        public final Ansi.Color val;
        public final boolean bright;

        public final String colorString;

        private Color(Type type, int value) {
            if (value < 0) throw new IllegalArgumentException("value must be positive");
            this.type = type;
            this.value = value;
            this.val = null;
            this.bright = false;
            StringBuilder sb = new StringBuilder("\33[38;");// 33 == (char) 27
            if (type == Type.COLOR_256) {
                sb.append("5;").append(value & 0xff);
            } else if (type == Type.COLOR_RGB) {
                sb.append("2;").append((value >> 16) & 0xff)
                        .append(';').append((value >> 8) & 0xff)
                        .append(';').append(value & 0xff);
            } else throw new IllegalArgumentException("Invalid color type");
            this.colorString = sb.append('m').toString();
        }

        private Color(Ansi.Color val, boolean bright) {
            this.type = Type.COLOR_8;
            this.value = -1;
            this.val = val;
            this.bright = bright;
            this.colorString = "\33[" + (bright ? val.fgBright() : val.fg()) + 'm';// 33 == (char) 27
        }

        public static final class Adapter extends TypeAdapter<Color> {
            @Override
            public void write(JsonWriter out, Color value) throws IOException {
                switch (value.type) {
                    case COLOR_8:
                        out.value("8:" + value.val + (value.bright ? ":BRIGHT" : ""));
                        break;
                    case COLOR_256:
                        out.value("256:" + value.value);
                        break;
                    case COLOR_RGB:
                        out.value("rgb:" + value.value);
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown color type");
                }
            }

            @Override
            public Color read(JsonReader in) throws IOException {
                if (!Config.JANSI_INSTALLED) return null;
                String[] sa = in.nextString().split(":");
                if (sa.length != 2 && sa.length != 3) throw new JsonParseException("Invalid color format");
                switch (sa[0]) {
                    case "8":
                        return new Color(Ansi.Color.valueOf(sa[1].toUpperCase()),
                                sa.length == 3 && sa[2].equalsIgnoreCase("bright"));
                    case "256":
                        return new Color(Type.COLOR_256, Integer.parseInt(sa[1]));
                    case "rgb":
                        return new Color(Type.COLOR_RGB, Integer.parseInt(sa[1]));
                    default:
                        throw new JsonParseException("Invalid color type");
                }
            }
        }
    }
}