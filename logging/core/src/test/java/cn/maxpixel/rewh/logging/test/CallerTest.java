package cn.maxpixel.rewh.logging.test;

import cn.maxpixel.rewh.logging.util.CallerFinder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class CallerTest {
    @Test
    void testFindCaller() {
        assertNotNull(CallerFinder.findCaller(getClass().getName()));
    }
}