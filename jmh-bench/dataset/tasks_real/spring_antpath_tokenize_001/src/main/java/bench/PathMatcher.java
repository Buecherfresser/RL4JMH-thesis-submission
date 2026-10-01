package bench;

import java.util.HashMap;
import java.util.Map;

public final class PathMatcher {

    private final Map<String, String[]> cache = new HashMap<>();

    public boolean matches(String pattern, String path) {
        String[] patTokens = cache.computeIfAbsent(pattern, this::tokenize);
        String[] pathTokens = tokenize(path);
        if (patTokens.length != pathTokens.length) return false;
        for (int i = 0; i < patTokens.length; i++) {
            if (!matchSegment(patTokens[i], pathTokens[i])) return false;
        }
        return true;
    }

    private String[] tokenize(String s) {
        return s.split("/");
    }

    private boolean matchSegment(String pat, String seg) {
        return pat.equals("*") || pat.equals(seg);
    }
}
