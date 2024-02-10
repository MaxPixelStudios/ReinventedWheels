package cn.maxpixel.rewh.logging.benchmark;

import cn.maxpixel.rewh.logging.Config;
import cn.maxpixel.rewh.logging.LogManager;
import cn.maxpixel.rewh.logging.Logger;
import cn.maxpixel.rewh.logging.msg.publisher.MessagePublisher;
import cn.maxpixel.rewh.logging.msg.publisher.OutputStreamMessagePublisher;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.OutputStreamAppender;
import org.apache.logging.log4j.core.config.AbstractConfiguration;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.openjdk.jmh.annotations.*;

import java.io.OutputStream;
import java.net.URI;
import java.util.concurrent.TimeUnit;

@Fork(1)
@Warmup(iterations = 3)
@Measurement(iterations = 5)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class LoggerBenchmark {
    static {
        Config config = new Config();
        config.publishers = new MessagePublisher[] {new OutputStreamMessagePublisher(new OutputStream() {
            @Override
            public void write(byte[] b) {
            }
            @Override
            public void write(byte[] b, int off, int len) {
            }
            @Override
            public void flush() {
            }
            @Override
            public void close() {
            }
            @Override
            public void write(int b) {
            }
        }, true, false, false)};
        config.loggers.get("root").fetchCaller = false;
        Config.set(config);

        ConfigurationFactory.setConfigurationFactory(new ConfigurationFactory() {
            @Override
            protected String[] getSupportedTypes() {
                return new String[] {"*"};
            }

            @Override
            public Configuration getConfiguration(LoggerContext loggerContext, ConfigurationSource source) {
                AbstractConfiguration configuration = new AbstractConfiguration(null, ConfigurationSource.NULL_SOURCE) {
                    @Override
                    protected void doConfigure() {
                    }
                };
                configuration.setName("BenchmarkNoOp");
                Appender appender = OutputStreamAppender.newBuilder().setName("NoOpOutput").setTarget(new OutputStream() {
                    @Override
                    public void write(byte[] b) {
                    }
                    @Override
                    public void write(byte[] b, int off, int len) {
                    }
                    @Override
                    public void flush() {
                    }
                    @Override
                    public void close() {
                    }
                    @Override
                    public void write(int b) {

                    }// %d{ISO8601}
                }).setLayout(PatternLayout.newBuilder().withPattern("[%d{ISO8601}] [] [%c] [%p] %m%n%ex").build()).build();
                appender.start();
                configuration.getRootLogger().setLevel(Level.INFO);
                configuration.getRootLogger().addAppender(appender, null, null);
                configuration.initialize();
                return configuration;
            }

            @Override
            public Configuration getConfiguration(LoggerContext loggerContext, String name, URI configLocation) {
                return getConfiguration(loggerContext, null);
            }

            @Override
            public Configuration getConfiguration(LoggerContext loggerContext, String name, URI configLocation, ClassLoader loader) {
                return getConfiguration(loggerContext, null);
            }
        });
        LOGGER_LOG4J = org.apache.logging.log4j.LogManager.getLogger();
    }
    private static final Logger LOGGER_REWH = LogManager.getLogger();
    private static final org.apache.logging.log4j.Logger LOGGER_LOG4J;

    @Benchmark
    public void simpleREWH() {
        LOGGER_REWH.info("Plain message");
    }

    @Benchmark
    public void simpleLog4j() {
        LOGGER_LOG4J.info("Plain message");
    }

    @Benchmark
    public void oneArgREWH() {
        LOGGER_REWH.info("Plain message with one arg \"{}\"", "arg0");
    }

    @Benchmark
    public void oneArgLog4j() {
        LOGGER_LOG4J.info("Plain message with one arg \"{}\"", "arg0");
    }
//
//    @Benchmark
//    public void twoArgsREWH() {
//        LOGGER_REWH.info("Plain message with two args \"{}, {}\"", "arg0", "arg1");
//    }
//
//    @Benchmark
//    public void twoArgsLog4j() {
//        LOGGER_LOG4J.info("Plain message with two args \"{}, {}\"", "arg0", "arg1");
//    }
//
//    @Benchmark
//    public void threeArgsREWH() {
//        LOGGER_REWH.info("Plain message with three args \"{}, {}, {}\"", "arg0", "arg1", "arg2");
//    }
//
//    @Benchmark
//    public void threeArgsLog4j() {
//        LOGGER_LOG4J.info("Plain message with three args \"{}, {}, {}\"", "arg0", "arg1", "arg2");
//    }
//
//    @Benchmark
//    public void fourArgsREWH() {
//        LOGGER_REWH.info("Plain message with four args \"{}, {}, {}, {}\"", "arg0", "arg1", "arg2", "arg3");
//    }
//
//    @Benchmark
//    public void fourArgsLog4j() {
//        LOGGER_LOG4J.info("Plain message with four args \"{}, {}, {}, {}\"", "arg0", "arg1", "arg2", "arg3");
//    }
//
//    @Benchmark
//    public void fiveArgsREWH() {
//        LOGGER_REWH.info("Plain message with five args \"{}, {}, {}, {}, {}\"", "arg0", "arg1", "arg2", "arg3", "arg4");
//    }
//
//    @Benchmark
//    public void fiveArgsLog4j() {
//        LOGGER_LOG4J.info("Plain message with five args \"{}, {}, {}, {}, {}\"", "arg0", "arg1", "arg2", "arg3", "arg4");
//    }
//
//    @Benchmark
//    public void manyArgsREWH() {
//        LOGGER_REWH.info("Plain message with many args \"{}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}\"", "arg0", "arg1", "arg2", "arg3", "arg4", "arg5", "arg6", "arg7", "arg8", "arg9", "arg1");
//    }
//
//    @Benchmark
//    public void manyArgsLog4j() {
//        LOGGER_LOG4J.info("Plain message with many args \"{}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}\"", "arg0", "arg1", "arg2", "arg3", "arg4", "arg5", "arg6", "arg7", "arg8", "arg9", "arg1");
//    }
}