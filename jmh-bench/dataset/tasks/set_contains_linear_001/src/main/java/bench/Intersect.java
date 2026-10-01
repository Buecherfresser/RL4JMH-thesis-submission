package bench;

import java.util.Set;

public final class Intersect {

    private Intersect() {}

    /** Count common elements; iterates the smaller set. */
    public static int sizeOfIntersection(Set<Integer> a, Set<Integer> b) {
        Set<Integer> smaller = a.size() <= b.size() ? a : b;
        Set<Integer> larger  = smaller == a ? b : a;
        int hits = 0;
        for (Integer v : smaller) {
            if (larger.contains(v)) hits++;
        }
        return hits;
    }
}
