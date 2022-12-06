package cn.maxpixel.rewh.logging;

public interface Reusable {
    void ready();

    static void tryReady(Object o) {
        if (o instanceof Reusable) {
            ((Reusable) o).ready();
        }
    }
}