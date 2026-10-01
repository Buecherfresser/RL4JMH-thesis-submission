package bench;

public final class JoinUtils {

    private JoinUtils() {}

    public static String join(String[] xs, String sep) {
        if (xs.length == 0) return "";
        StringBuilder sb = new StringBuilder();
        sb.append(xs[0]);
        for (int i = 1; i < xs.length; i++) {
            sb.append(sep).append(xs[i]);
        }
        return sb.toString();
    }
}
