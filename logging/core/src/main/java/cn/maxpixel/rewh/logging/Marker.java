package cn.maxpixel.rewh.logging;

import it.unimi.dsi.fastutil.objects.ObjectSet;

import java.util.Objects;

public final class Marker {
    public final String name;
    private final ObjectSet<Marker> parents;

    Marker(String name) {
        this.name = Objects.requireNonNull(name);
        this.parents = null;
    }

    Marker(String name, ObjectSet<Marker> parents) {
        this.name = Objects.requireNonNull(name);
        this.parents = Objects.requireNonNull(parents);
        if (isChildOf(this)) throw new IllegalStateException("Recursive marker hierarchy");
    }

    public boolean isChildOf(Marker marker) {
        if (parents != null) {
            if (parents.contains(marker)) return true;
            for (Marker parent : parents) {
                if (parent.isChildOf(marker)) return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Marker.class != o.getClass()) return false;
        Marker marker = (Marker) o;
        return name.equals(marker.name) && Objects.equals(parents, marker.parents);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, parents);
    }

    @Override
    public String toString() {
        return "Marker{" +
                "name='" + name + '\'' +
                ", parents=" + parents +
                '}';
    }
}