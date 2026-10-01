package bench;

import java.util.Map;

public final class ThreadContextLookup {

    private ThreadContextLookup() {}

    public static String get(Map<String, String> ctx, String key) {
        String v = ctx.get(key);
        return v == null ? "" : v;
    }
}
