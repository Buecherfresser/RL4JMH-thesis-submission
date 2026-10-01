package bench;

import static org.junit.Assert.assertEquals;
import java.util.Arrays;
import java.util.HashSet;
import org.junit.Test;

public class IntersectTest {
    @Test public void disjoint() {
        assertEquals(0, Intersect.sizeOfIntersection(
            new HashSet<>(Arrays.asList(1, 2)), new HashSet<>(Arrays.asList(3, 4))));
    }
    @Test public void identical() {
        assertEquals(3, Intersect.sizeOfIntersection(
            new HashSet<>(Arrays.asList(1, 2, 3)), new HashSet<>(Arrays.asList(1, 2, 3))));
    }
    @Test public void partial() {
        assertEquals(2, Intersect.sizeOfIntersection(
            new HashSet<>(Arrays.asList(1, 2, 3, 4)), new HashSet<>(Arrays.asList(3, 4, 5))));
    }
}
