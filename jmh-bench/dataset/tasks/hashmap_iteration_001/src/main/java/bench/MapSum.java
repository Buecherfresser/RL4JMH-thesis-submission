package bench;

import java.util.Map;

public final class MapSum {

    private MapSum() {}

    /** Sum all values of a {@code Map<String, Integer>}. */
    public static long sumValues(Map<String, Integer> map) {
        long total = 0L;
        for (Map.Entry<String, Integer> e : map.entrySet()) {
            total += e.getValue().longValue();
        }
        return total;
    }
}
