package bench;

import java.util.Arrays;

public final class SortShort {

    private SortShort() {}

    /** Sort each small int[] and return their summed first elements. */
    public static long sortAndSum(int[][] arrays) {
        long total = 0L;
        for (int i = 0; i < arrays.length; i++) {
            int[] a = arrays[i].clone();
            Arrays.sort(a);
            total += a[0];
        }
        return total;
    }
}
