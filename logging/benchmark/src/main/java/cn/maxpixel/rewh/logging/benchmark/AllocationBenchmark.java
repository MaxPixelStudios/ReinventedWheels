package cn.maxpixel.rewh.logging.benchmark;

import cn.maxpixel.rewh.logging.Level;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

import static cn.maxpixel.rewh.logging.Level.*;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class AllocationBenchmark {
    private static final Level[] la = new Level[] {OFF, TRACE, DEBUG, INFO, WARN, ERROR, FATAL, ALL};

    @Benchmark
    public void allocObject(Blackhole bh) {
        bh.consume(new Object());
        bh.consume(new Object());
        bh.consume(new Object());
        bh.consume(new Object());
    }

    @Benchmark
    public Object[] allocObjectArray() {
        return new Object[] {new Object(), new Object(), new Object(), new Object()};
    }

    @Benchmark
    public Level[] levelCopy() {
        Level[] levels = new Level[la.length];
        System.arraycopy(la, 0, levels, 0, la.length);
        return levels;
    }

    @Benchmark
    public Level[] levelCreation() {
        return new Level[] {OFF, TRACE, DEBUG, INFO, WARN, ERROR, FATAL, ALL};
    }
}