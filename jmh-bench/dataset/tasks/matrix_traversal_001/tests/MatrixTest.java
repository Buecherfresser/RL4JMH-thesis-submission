package bench;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class MatrixTest {
    @Test public void smallMatrix() {
        int[][] m = {{1, 2}, {3, 4}};
        assertEquals(10L, Matrix.sum(m));
    }
    @Test public void rowOf3() {
        int[][] m = {{1, 2, 3}, {4, 5, 6}};
        assertEquals(21L, Matrix.sum(m));
    }
    @Test public void zeros() {
        int[][] m = {{0, 0}, {0, 0}};
        assertEquals(0L, Matrix.sum(m));
    }
}
