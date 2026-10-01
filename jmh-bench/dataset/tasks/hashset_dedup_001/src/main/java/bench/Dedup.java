package bench;

import java.util.HashSet;
import java.util.Set;

public final class Dedup {

    private Dedup() {}

    /** Count distinct integers in {@code arr} via a HashSet. */
    public static int countUnique(int[] arr) {
        Set<Integer> seen = new HashSet<>(arr.length * 2);
        for (int i = 0, n = arr.length; i < n; i++) {
            seen.add(arr[i]);
        }
        return seen.size();
    }
}
