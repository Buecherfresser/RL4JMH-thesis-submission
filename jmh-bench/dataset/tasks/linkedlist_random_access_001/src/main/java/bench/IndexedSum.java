package bench;

import java.util.Iterator;
import java.util.List;

public final class IndexedSum {

    private IndexedSum() {}

    /** Return the sum of every element in the list. */
    public static long sum(List<Integer> list) {
        long total = 0L;
        Iterator<Integer> it = list.iterator();
        while (it.hasNext()) {
            total += it.next().longValue();
        }
        return total;
    }
}
