package bench;

import java.util.ArrayList;
import java.util.List;

public final class ChangelogFilter {

    private ChangelogFilter() {}

    public static List<Integer> retain(List<Integer> xs, int threshold) {
        ArrayList<Integer> out = new ArrayList<>(xs.size());
        for (int i = 0, n = xs.size(); i < n; i++) {
            int v = xs.get(i);
            if (v >= threshold) out.add(v);
        }
        return out;
    }
}
