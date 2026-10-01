package bench;

import java.util.List;

public final class Prefix {

    private Prefix() {}

    /** Sum the first k elements using a subList view. */
    public static long sumPrefix(List<Integer> list, int k) {
        List<Integer> view = list.subList(0, k);
        long total = 0L;
        for (int i = 0, n = view.size(); i < n; i++) {
            total += view.get(i).longValue();
        }
        return total;
    }
}
