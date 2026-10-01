package bench;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MapLookup {

    private MapLookup() {}

    /** Sum values returned by map.get(key) for each requested key. */
    public static long sumGet(Map<String, Long> map, List<String> keys) {
        long total = 0L;
        for (int i = 0, n = keys.size(); i < n; i++) {
            Long v = map.get(keys.get(i));
            if (v != null) total += v.longValue();
        }
        return total;
    }

    public static Map<String, Long> newFastMap(int capacity) {
        return new HashMap<>(capacity);
    }
}
