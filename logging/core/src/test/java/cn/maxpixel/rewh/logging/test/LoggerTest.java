package cn.maxpixel.rewh.logging.test;

import cn.maxpixel.rewh.logging.LogManager;
import cn.maxpixel.rewh.logging.Logger;
import cn.maxpixel.rewh.logging.Marker;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class LoggerTest {
    @BeforeAll
    static void setup() {
//        System.setProperty("rewh.logging.colorful", "false");
        Marker marker1 = LogManager.getMarker("Marker1");
        LogManager.getMarker("Marker2", marker1);
        LogManager.getLogger();
    }

    @Test
    void testMarker() {
        Marker marker1 = LogManager.getMarker("Marker1");
        Marker marker2 = LogManager.getMarker("Marker2");
        assert marker2.isChildOf(marker1) : marker1 + "\n" + marker2;
    }

    @Test
    void testLogger() {
        Logger logger = LogManager.getLogger();
        logger.info("Plain message");
        logger.info("Plain message with one arg \"{}\"", "arg0");
        logger.info("Plain message with two args \"{}, {}\"", "arg0", "arg1");
        logger.info("Plain message with three args \"{}, {}, {}\"", "arg0", "arg1", "arg2");
        logger.info("Plain message with four args \"{}, {}, {}, {}\"", "arg0", "arg1", "arg2", "arg3");
        logger.info("Plain message with five args \"{}, {}, {}, {}, {}\"", "arg0", "arg1", "arg2", "arg3", "arg4");
        logger.info("Plain message with lots of args \"{}, {}, {}, {}, {}, {}, {}\"", "arg0", "arg1", "arg2", "arg3", "arg4", "arg5", "arg6");
        Throwable throwable = new Throwable("Dummy throwable");
        logger.info("Plain message with a throwable", throwable);
        logger.info("Plain message with one arg and a throwable \"{}\"", "arg0", throwable);
        logger.info("Plain message with two args and a throwable \"{}, {}\"", "arg0", "arg1", throwable);
        logger.info("Plain message with three args and a throwable \"{}, {}, {}\"", "arg0", "arg1", "arg2", throwable);
        logger.info("Plain message with four args and a throwable \"{}, {}, {}, {}\"", "arg0", "arg1", "arg2", "arg3", throwable);
        logger.info("Plain message with lots of args and a throwable \"{}, {}, {}, {}, {}, {}, {}\"", "arg0", "arg1", "arg2", "arg3", "arg4", "arg5", "arg6", throwable);
        Marker marker = LogManager.getMarker("Marker");
        logger.info(marker, "Plain message with marker");
        logger.info(marker, "Plain message with marker and one arg \"{}\"", "arg0");
        logger.info(marker, "Plain message with marker and two args \"{}, {}\"", "arg0", "arg1");
        logger.info(marker, "Plain message with marker and three args \"{}, {}, {}\"", "arg0", "arg1", "arg2");
        logger.info(marker, "Plain message with marker and four args \"{}, {}, {}, {}\"", "arg0", "arg1", "arg2", "arg3");
        logger.info(marker, "Plain message with marker and five args \"{}, {}, {}, {}, {}\"", "arg0", "arg1", "arg2", "arg3", "arg4");
        logger.info(marker, "Plain message with lots of args \"{}, {}, {}, {}, {}, {}, {}\"", "arg0", "arg1", "arg2", "arg3", "arg4", "arg5", "arg6");
        logger.info(marker, "Plain message with marker and a throwable", throwable);
        logger.info(marker, "Plain message with marker, one arg and a throwable \"{}\"", "arg0", throwable);
        logger.info(marker, "Plain message with marker, two args and a throwable \"{}, {}\"", "arg0", "arg1", throwable);
        logger.info(marker, "Plain message with marker, three args and a throwable \"{}, {}, {}\"", "arg0", "arg1", "arg2", throwable);
        logger.info(marker, "Plain message with marker, four args and a throwable \"{}, {}, {}, {}\"", "arg0", "arg1", "arg2", "arg3", throwable);
        logger.info(marker, "Plain message with marker, lots of args and a throwable \"{}, {}, {}, {}, {}, {}, {}\"", "arg0", "arg1", "arg2", "arg3", "arg4", "arg5", "arg6", throwable);
    }
}