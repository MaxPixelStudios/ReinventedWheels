package cn.maxpixel.rewh.logging.benchmark;

import org.openjdk.jmh.profile.JavaFlightRecorderProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class Main {
    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
//                .include(FormatterBenchmark.class.getSimpleName())
                .include(LoggerBenchmark.class.getSimpleName())
//                .include(EncodeBenchmark.class.getSimpleName())
                .addProfiler(JavaFlightRecorderProfiler.class, "-dir=jfr")
                .build();
        new Runner(options).run();
    }
}
