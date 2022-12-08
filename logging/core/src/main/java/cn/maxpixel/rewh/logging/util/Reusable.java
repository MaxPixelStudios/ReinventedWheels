package cn.maxpixel.rewh.logging.util;

public interface Reusable {
    void ready();

    static void tryReady(Object o) {
        if (o instanceof Reusable) {
            ((Reusable) o).ready();
        }
    }
}