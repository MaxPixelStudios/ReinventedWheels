package cn.maxpixel.rewh.logging.benchmark;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import java.util.concurrent.ConcurrentHashMap;

@State(Scope.Benchmark)
public class MapBenchmark {
    private final ConcurrentHashMap<String, String> concurrent = new ConcurrentHashMap<>();
    private final Object2ObjectMap<String, String> fastutil = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>());
}