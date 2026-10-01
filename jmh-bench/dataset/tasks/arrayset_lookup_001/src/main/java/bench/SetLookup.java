package bench;

import java.util.List;
import java.util.Set;

public final class SetLookup {

    private SetLookup() {}

    /** Count how many of {@code probes} exist in {@code set}. */
    public static int countHits(Set<Integer> set, List<Integer> probes) {
        int hits = 0;
        for (int i = 0, n = probes.size(); i < n; i++) {
            if (set.contains(probes.get(i))) {
                hits++;
            }
        }
        return hits;
    }
}
