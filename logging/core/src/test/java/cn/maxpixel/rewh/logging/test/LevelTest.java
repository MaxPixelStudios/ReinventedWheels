package cn.maxpixel.rewh.logging.test;

import cn.maxpixel.rewh.logging.Level;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class LevelTest {
    @Test
    void testConstructor() {
        assertThrows(IllegalArgumentException.class, () -> new Level("TEST", (short) -2));
        assertThrows(IllegalArgumentException.class, () -> new Level("TEST", (short) 30001));
        assertThrows(NullPointerException.class, () -> new Level(null, (short) 5000));
    }
}