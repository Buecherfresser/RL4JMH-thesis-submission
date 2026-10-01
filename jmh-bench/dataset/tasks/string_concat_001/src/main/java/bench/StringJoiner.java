package bench;

import java.util.List;

public final class StringJoiner {

    private StringJoiner() {}

    /** Join {@code parts} with {@code sep}, using a single StringBuilder. */
    public static String join(List<String> parts, String sep) {
        if (parts.isEmpty()) {
            return "";
        }
        int estimate = 0;
        for (int i = 0; i < parts.size(); i++) {
            estimate += parts.get(i).length();
        }
        estimate += sep.length() * (parts.size() - 1);

        StringBuilder sb = new StringBuilder(estimate);
        sb.append(parts.get(0));
        for (int i = 1; i < parts.size(); i++) {
            sb.append(sep);
            sb.append(parts.get(i));
        }
        return sb.toString();
    }
}
