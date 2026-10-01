package bench;

public final class Matrix {

    private Matrix() {}

    /** Sum all elements of the matrix and return the total. */
    public static long sum(int[][] m) {
        long total = 0L;
        int rows = m.length;
        int cols = m[0].length;
        for (int r = 0; r < rows; r++) {
            int[] row = m[r];
            for (int c = 0; c < cols; c++) {
                total += row[c];
            }
        }
        return total;
    }
}
