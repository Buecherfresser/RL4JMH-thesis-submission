package bench;

public final class Splitter {

    private Splitter() {}

    /** Count tokens delimited by `sep` using a single pass. */
    public static int count(String s, char sep) {
        if (s.isEmpty()) return 0;
        int count = 1;
        for (int i = 0, n = s.length(); i < n; i++) {
            if (s.charAt(i) == sep) count++;
        }
        return count;
    }
}
