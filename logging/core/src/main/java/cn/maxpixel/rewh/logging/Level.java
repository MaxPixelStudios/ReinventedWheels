package cn.maxpixel.rewh.logging;

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public final class Level implements Comparable<Level> {
    public static final Level OFF = new Level("OFF", (short) -1);

    public static final Level TRACE = new Level("TRACE", (short) 1_0000);

    public static final Level DEBUG = new Level("DEBUG", (short) 1_2000);

    public static final Level INFO = new Level("INFO", (short) 1_4000);

    public static final Level WARN = new Level("WARN", (short) 1_6000);

    public static final Level ERROR = new Level("ERROR", (short) 1_8000);

    public static final Level FATAL = new Level("FATAL", (short) 2_0000);

    public static final Level ALL = new Level("ALL", (short) 3_0000);

    private static final Level[] PRE_DEFINED = new Level[] {OFF, TRACE, DEBUG, INFO, WARN, ERROR, FATAL, ALL};

    public static Level[] getPreDefinedLevels() {
        Level[] levels = new Level[PRE_DEFINED.length];
        System.arraycopy(PRE_DEFINED, 0, levels, 0, PRE_DEFINED.length);
        return levels;
    }

    public final String name;
    public final short priority;

    public Level(String name, short priority) {
        if(priority < -1 || priority > 30000) throw new IllegalArgumentException();
        this.name = Objects.requireNonNull(name);
        this.priority = priority;
    }

    public boolean isLoggable(Level level) {
        return level.priority >= this.priority;
    }

    @Override
    public int compareTo(Level o) {
        return priority - o.priority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Level)) return false;
        Level level = (Level) o;
        return priority == level.priority;
    }

    @Override
    public int hashCode() {
        return priority;
    }

    public static class Adapter extends TypeAdapter<Level> {
        @Override
        public void write(JsonWriter out, Level value) throws IOException {
            if (value.name.equals("CUSTOM")) out.value(value.priority);
            else if (Arrays.binarySearch(PRE_DEFINED, value) >= 0) out.value(value.name);
            else out.beginObject()
                        .name("name").value(value.name)
                        .name("priority").value(value.priority)
                        .endObject();
        }

        @Override
        public Level read(JsonReader in) throws IOException {
            switch (in.peek()) {
                case NUMBER:
                    return new Level("CUSTOM", toShort(in.nextInt()));
                case STRING:
                    String name = in.nextString();
                    for (Level level : PRE_DEFINED) if (level.name.equals(name)) return level;
                case BEGIN_OBJECT:
                    // throws exception when the structure is invalid
                    String n = null;
                    short priority = -2;
                    in.beginObject();
                    do switch (in.nextName()) {
                        case "name":
                            n = in.nextString();
                            break;
                        case "priority":
                            priority = toShort(in.nextInt());
                            break;
                    } while(in.peek() == JsonToken.NAME);
                    in.endObject();
                    return new Level(n, priority);
            }
            throw new JsonParseException("Invalid level object");
        }

        private static short toShort(int value) {
            short sValue = (short) value;
            if (sValue != value) throw new NumberFormatException("Expected a short, but found an int");
            return sValue;
        }
    }
}