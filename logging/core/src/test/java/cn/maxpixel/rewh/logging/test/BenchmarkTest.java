package cn.maxpixel.rewh.logging.test;

import cn.maxpixel.rewh.logging.test.benchmark.FormatterBenchmark;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class BenchmarkTest {
    public void test() throws RunnerException {
        Options options = new OptionsBuilder()
                .include(FormatterBenchmark.class.getSimpleName())
                .warmupIterations(3)
                .measurementIterations(5)
                .forks(1)
                .build();
        new Runner(options).run();
    }
}