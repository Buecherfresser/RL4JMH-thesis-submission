package bench;

import static org.junit.Assert.assertArrayEquals;
import org.junit.Test;

public class QuicksortTest {
    @Test public void empty() {
        int[] a = new int[0];
        Quicksort.sort(a);
        assertArrayEquals(new int[0], a);
    }
    @Test public void single() {
        int[] a = {42};
        Quicksort.sort(a);
        assertArrayEquals(new int[]{42}, a);
    }
    @Test public void mixed() {
        int[] a = {3, 1, 4, 1, 5, 9, 2, 6};
        Quicksort.sort(a);
        assertArrayEquals(new int[]{1, 1, 2, 3, 4, 5, 6, 9}, a);
    }
    @Test public void alreadySorted() {
        int[] a = {1, 2, 3, 4, 5};
        Quicksort.sort(a);
        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, a);
    }
}
