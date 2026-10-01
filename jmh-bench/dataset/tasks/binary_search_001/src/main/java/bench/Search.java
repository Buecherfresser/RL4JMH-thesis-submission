package bench;

public final class Search {

    private Search() {}

    /** Binary search; assumes {@code arr} is sorted ascending. */
    public static int indexOf(int[] arr, int target) {
        int lo = 0;
        int hi = arr.length - 1;
        while (lo <= hi) {
            int mid = (lo + hi) >>> 1;
            int v = arr[mid];
            if (v == target) {
                return mid;
            } else if (v < target) {
                lo = mid + 1;
            } else {
                hi = mid - 1;
            }
        }
        return -1;
    }
}
