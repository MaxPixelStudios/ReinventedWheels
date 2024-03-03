package cn.maxpixel.rewh.logging;

import cn.maxpixel.rewh.logging.msg.MessageFactory;
import cn.maxpixel.rewh.logging.msg.filter.Filter;
import cn.maxpixel.rewh.logging.msg.publisher.MessagePublisher;
import cn.maxpixel.rewh.logging.msg.publisher.OutputStreamMessagePublisher;
import cn.maxpixel.rewh.logging.util.Formatter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.Since;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.fusesource.jansi.Ansi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public final class Config {
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Map.class, (InstanceCreator<Map<?, ?>>) type -> new Object2ObjectOpenHashMap<>())
            .registerTypeAdapter(Level.class, new Level.Adapter())
            .registerTypeAdapter(Formatter.class, new Formatter.Adapter())
            .registerTypeAdapter(Formatter.Color.class, new Formatter.Color.Adapter())
            .registerTypeAdapter(MessagePublisher.class, new MessagePublisher.Adapter())
            .excludeFieldsWithoutExposeAnnotation()
            .setPrettyPrinting()
            .setVersion(0.1)
            .create();
    public static final boolean JANSI_INSTALLED;
    public static final boolean COLORFUL;
    static {
        boolean jansiInstalled;
        try {
            Ansi.class.getName();
            jansiInstalled = true;
        } catch (NoClassDefFoundError e) {// JAnsi not found
            jansiInstalled = false;
        }
        JANSI_INSTALLED = jansiInstalled;
        COLORFUL = jansiInstalled && Boolean.parseBoolean(System.getProperty("rewh.logging.colorful", "true"));
        reload();
    }
    public static void init() {}

    private static String CONFIG_PATH;
    private static volatile Config CONFIG;

    @Since(0.1)
    @Expose
    public Map<String, Logger> loggers = new Object2ObjectOpenHashMap<>();

    @Since(0.1)
    @Expose
    public MessagePublisher[] publishers = new MessagePublisher[] {OutputStreamMessagePublisher.STDOUT};

    @Since(0.1)
    @Expose
    @SerializedName("factory")
    public String messageFactory = "cn.maxpixel.rewh.logging.msg.ReusableMessageFactory";

    public Config() {
        loggers.put("root", new Logger());
    }

    public Config(Config config) {
        Object2ObjectOpenHashMap<String, Logger> map = new Object2ObjectOpenHashMap<>(config.loggers.size());
        config.loggers.forEach((k, v) -> map.put(k, new Logger(v)));
        this.loggers = map;
        this.publishers = config.publishers.clone();
        this.messageFactory = config.messageFactory;
    }

    public static void set(Config config) {
        CONFIG = config == null ? new Config() : config;
        LogManager.reload();
        MessageFactory.reload();
    }

    public static Config get() {
        return CONFIG;
    }

    public static Logger getConfig(String name) {
        Logger config = CONFIG.loggers.get(name);
        if (config != null) return config;
        String parent = getParent(name);
        if (parent != null) return getConfig(parent);
        return CONFIG.loggers.get("root");
    }

    public static void reload() {
        CONFIG_PATH = System.getProperty("rewh.logging.config.path", "rewh_logging_config.json");
        set(load());
    }

    private static Config load() {
        try (InputStream is = Config.class.getClassLoader().getResourceAsStream(CONFIG_PATH)) {
            if (is != null) {
                try (InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                    return GSON.fromJson(isr, Config.class);
                }
            }
        } catch (IOException e) {
            // Keep going
        }
        Path p = Paths.get(CONFIG_PATH);
        if (Files.isReadable(p)) {
            try (BufferedReader reader = Files.newBufferedReader(p)) {
                return GSON.fromJson(reader, Config.class);
            } catch (IOException e) {
                System.err.println("Failed to load config file: " + CONFIG_PATH + ", using default config.");
            }
        }
        return new Config();
    }

    private static String getParent(String name) {
        int lastDot = name.lastIndexOf('.');
        return lastDot < 0 ? null : name.substring(0, lastDot);
    }

    public static final class Logger {
        @Since(0.1)
        @Expose
        public Formatter formatter = Formatter.DEFAULT;

        @Since(0.1)
        @Expose
        public Level level = Level.INFO;

        @Since(0.1)
        @SerializedName("enable_string_format")
        @Expose
        public boolean stringFormat = false; // String.format

        @Since(0.1)
        @SerializedName("enable_message_format")
        @Expose
        public boolean messageFormat = false; // MessageFormat.format

        @Since(0.1)
        @SerializedName("fetch_caller")
        @Expose
        public boolean fetchCaller = true;

        @Since(0.1)
        @Expose
        public Filter[] filters = new Filter[0];// TODO: filter

        public Logger() {
        }

        public Logger(Logger config) {
            this.formatter = config.formatter;
            this.level = config.level;
            this.stringFormat = config.stringFormat;
            this.messageFormat = config.messageFormat;
            this.fetchCaller = config.fetchCaller;
            this.filters = config.filters.clone();
        }
    }
}