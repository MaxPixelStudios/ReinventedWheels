package cn.maxpixel.rewh.logging.test.benchmark;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.openjdk.jmh.annotations.*;

import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
@Fork(2)
public class FormatterBenchmark {
    @Param({"[%time%] [%source_class%/%source_method%] [%name%] [%level%] %msg%\n", "%1%msg%", "%1%1%msg%"})
    public String formatString;

    @Benchmark
    public AbstractFormatter benchmarkIndexOf() {
        return new IndexOfFormatter(formatString);
    }

    @Benchmark
    public AbstractFormatter benchmarkSplit() {
        return new SplitFormatter(formatString);
    }

    @Benchmark
    public AbstractFormatter benchmarkSplit2() {
        return new Split2Formatter(formatString);
    }

    public abstract static class AbstractFormatter {
        static final byte TYPE_MSG = 1;
        static final byte TYPE_LEVEL = 2;
        static final byte TYPE_NAME = 3;
        static final byte TYPE_MARKER = 4;
        static final byte TYPE_TIME = 5;
        static final byte TYPE_SOURCE_CLASS = 6;
        static final byte TYPE_SOURCE_METHOD = 7;
        static final byte TYPE_STRING = 8;
        public final String[] strings;
        public final byte[] components;

        public AbstractFormatter(String format) {
            ObjectArrayList<String> strings = new ObjectArrayList<>();
            ByteArrayList components = new ByteArrayList(32);
            parse(format, strings, components);
            this.strings = strings.toArray(strings.toArray(new String[0]));
            this.components = components.toByteArray();
        }

        public abstract void parse(String format, ObjectArrayList<String> strings, ByteArrayList components);

        static byte identifyVariable(String s) {
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
    }

    public static class IndexOfFormatter extends AbstractFormatter {
        public IndexOfFormatter(String format) {
            super(format);
        }

        @Override
        public void parse(String format, ObjectArrayList<String> strings, ByteArrayList components) {
            int index = format.indexOf('%'), next = format.indexOf('%', index + 1), prev = 0;
            if (index < 0 || next < 0) return;
            do {
                components.add(TYPE_STRING);
                int nameLen = next - index; // Calculated length contains a % char
                if (next == -1) {
                    strings.add(format.substring(prev));
                    return;
                } else if (nameLen > 3 && nameLen <= 14) { // May contain a variable("msg".length() == 3, "source_method".length() == 13)
                    byte type = identifyVariable(format.substring(index + 1, next));
                    if (type == TYPE_STRING) {
                        strings.add(format.substring(prev, next));
                    } else {
                        strings.add(prev == index ? "" : format.substring(prev, index));
                        components.add(type);
                        index = format.indexOf('%', next + 1);
                        prev = next + 1;
                        next = format.indexOf('%', index + 1);
                        continue;
                    }
                } else {
                    strings.add(format.substring(prev, next));
                }
                index = next;
                prev = next;
                next = format.indexOf('%', index + 1);
            } while(index != -1);
            if (prev < format.length()) {
                components.add(TYPE_STRING);
                strings.add(format.substring(prev));
            }
        }
    }

    public static class SplitFormatter extends AbstractFormatter {
        public SplitFormatter(String format) {
            super(format);
        }

        @Override
        public void parse(String format, ObjectArrayList<String> strings, ByteArrayList components) {
            String[] parts = format.split("%");
            int lastMatch = 0;
            for (int i = 0; i < parts.length; i++) {
                String part = parts[i];
                byte type = identifyVariable(part);
                if (type != TYPE_STRING) {
                    int j = i - lastMatch;
                    if (j == 1) {
                        components.add(TYPE_STRING);
                        strings.add(parts[lastMatch]);
                    } else if (j > 1) {
                        components.add(TYPE_STRING);
                        StringJoiner joiner = new StringJoiner("%");
                        for (int k = lastMatch; k < i; k++) joiner.add(parts[k]);
                        strings.add(joiner.toString());
                    }
                    components.add(type);
                    lastMatch = i + 1;
                }
            }
            if (lastMatch < parts.length) {
                components.add(TYPE_STRING);
                if (lastMatch == parts.length - 1) {
                    strings.add(parts[lastMatch]);
                } else {
                    StringJoiner joiner = new StringJoiner("%");
                    for (int k = lastMatch; k < parts.length; k++)
                        joiner.add(parts[k]);
                    strings.add(joiner.toString());
                }
            }
        }
    }

    public static class Split2Formatter extends AbstractFormatter {
        public Split2Formatter(String format) {
            super(format);
        }

        @Override
        public void parse(String format, ObjectArrayList<String> strings, ByteArrayList components) {
            String[] parts = format.split("%");
            int lastMatch = 0;
            for (int i = 0; i < parts.length; i++) {
                String part = parts[i];
                byte type = identifyVariable(part);
                if (type != TYPE_STRING) {
                    for (int k = lastMatch; k < i; k++) {
                        components.add(TYPE_STRING);
                        strings.add(parts[k]);
                        if (k != i - 1) {
                            components.add(TYPE_STRING);
                            strings.add("%");
                        }
                    }
                    components.add(type);
                    lastMatch = i + 1;
                }
            }
            if (lastMatch < parts.length) {
                for (int k = lastMatch; k < parts.length; k++) {
                    components.add(TYPE_STRING);
                    strings.add(parts[k]);
                    if (k != parts.length - 1) {
                        components.add(TYPE_STRING);
                        strings.add("%");
                    }
                }
            }
        }
    }
}