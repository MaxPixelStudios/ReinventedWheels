package cn.maxpixel.rewh.logging.test;

import cn.maxpixel.rewh.logging.util.CallerFinder;

public class CallerTest {
    public void testFindCaller() {
        assert CallerFinder.findCaller(getClass().getTypeName()) != null;
    }
}