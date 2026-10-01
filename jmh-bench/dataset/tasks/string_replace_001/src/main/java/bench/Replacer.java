package bench;

public final class Replacer {

    private Replacer() {}

    /** Remove ASCII spaces via a single-pass scan. */
    public static String stripSpaces(String s) {
        int n = s.length();
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            char c = s.charAt(i);
            if (c != ' ') sb.append(c);
        }
        return sb.toString();
    }
}
