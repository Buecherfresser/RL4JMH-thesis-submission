package bench;

public final class Quicksort {

    private Quicksort() {}

    public static void sort(int[] arr) {
        qsort(arr, 0, arr.length - 1);
    }

    private static void qsort(int[] a, int lo, int hi) {
        if (lo >= hi) {
            return;
        }
        int pi = pivotIndex(a, lo, hi);
        swap(a, pi, hi);
        int p = partition(a, lo, hi);
        qsort(a, lo, p - 1);
        qsort(a, p + 1, hi);
    }

    /** Median-of-three pivot: return the *index* whose value is the median of a[lo], a[mid], a[hi]. */
    private static int pivotIndex(int[] a, int lo, int hi) {
        int mid = lo + (hi - lo) / 2;
        int x = a[lo], y = a[mid], z = a[hi];
        if ((x <= y && y <= z) || (z <= y && y <= x)) return mid;
        if ((y <= x && x <= z) || (z <= x && x <= y)) return lo;
        return hi;
    }

    /** Standard Lomuto partition; assumes the pivot is at a[hi]. */
    private static int partition(int[] a, int lo, int hi) {
        int pivot = a[hi];
        int i = lo - 1;
        for (int j = lo; j < hi; j++) {
            if (a[j] <= pivot) {
                i++;
                swap(a, i, j);
            }
        }
        swap(a, i + 1, hi);
        return i + 1;
    }

    private static void swap(int[] a, int i, int j) {
        int tmp = a[i]; a[i] = a[j]; a[j] = tmp;
    }
}
