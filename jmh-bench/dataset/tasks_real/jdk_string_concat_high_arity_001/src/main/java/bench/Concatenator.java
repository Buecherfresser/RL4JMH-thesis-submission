package bench;

public final class Concatenator {

    private Concatenator() {}

    public static String join(String[] parts) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            sb.append(parts[i]);
        }
        return sb.toString();
    }
}
