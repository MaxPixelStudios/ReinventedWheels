package cn.maxpixel.rewh.logging.config;

import cn.maxpixel.rewh.logging.Level;
import cn.maxpixel.rewh.logging.LogManager;
import cn.maxpixel.rewh.logging.msg.publisher.MessagePublisher;
import cn.maxpixel.rewh.logging.msg.publisher.OutputStreamMessagePublisher;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.Since;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

public final class Config {
    private static final Config DEFAULT = new Config();
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

    static {
        reload();
    }
    public static void init() {}

    private static String CONFIG_PATH;
    private static volatile Config CONFIG;

    @Since(0.1)
    @Expose
    public Map<String, LoggerConfig> loggers = new Object2ObjectOpenHashMap<>();

    @Since(0.1)
    @Expose
    public MessagePublisher[] publishers = new MessagePublisher[] {OutputStreamMessagePublisher.STDOUT};

    private Config() {
        loggers.put("root", new LoggerConfig());
    }

    private Config(Config config) {
        Object2ObjectOpenHashMap<String, LoggerConfig> map = new Object2ObjectOpenHashMap<>(config.loggers.size());
        config.loggers.forEach((k, v) -> map.put(k, new LoggerConfig(v)));
        this.loggers = map;
        this.publishers = config.publishers.clone();
    }

    public static void setConfig(Config config) {
        CONFIG = Objects.requireNonNull(config);
        LogManager.reload();
    }

    public static LoggerConfig getConfig(String name) {
        return CONFIG.loggers.getOrDefault(name, CONFIG.loggers.get("root"));
    }

    public static MessagePublisher[] getPublishers() {
        return CONFIG.publishers;
    }

    public static void reload() {
        CONFIG_PATH = System.getProperty("rewh.logging.config.path", "rewh_logging_config.json");
        CONFIG = load();
        LogManager.reload();
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
        File f = new File(CONFIG_PATH);
        if (f.exists()) {
            if (f.isDirectory()) throw new IllegalArgumentException("Not a file: " + CONFIG_PATH);
            try (FileReader reader = new FileReader(f)) {
                return GSON.fromJson(reader, Config.class);
            } catch (IOException e) {
                System.err.println("Failed to load config file: " + CONFIG_PATH + ", using default config.");
            }
        }
        return new Config(DEFAULT);
    }
}