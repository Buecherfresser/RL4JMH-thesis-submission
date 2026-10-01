package bench;

import java.util.List;

public final class ListContains {

    private ListContains() {}

    /**
     * Return the first index at which {@code list} contains an element equal
     * to {@code target}, or -1 if no such element exists. Short-circuits on
     * the first hit.
     */
    public static int firstIndexOf(List<Integer> list, Integer target) {
        for (int i = 0, n = list.size(); i < n; i++) {
            Integer v = list.get(i);
            if (v == target || (v != null && v.equals(target))) {
                return i;
            }
        }
        return -1;
    }
}
