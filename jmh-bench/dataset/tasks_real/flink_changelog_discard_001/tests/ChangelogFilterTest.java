package bench;

import static org.junit.Assert.assertEquals;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;

public class ChangelogFilterTest {
    @Test public void emptyInput() {
        assertEquals(Collections.emptyList(),
            ChangelogFilter.retain(Collections.emptyList(), 0));
    }
    @Test public void retainsAtThreshold() {
        assertEquals(Arrays.asList(3, 4),
            ChangelogFilter.retain(Arrays.asList(1, 2, 3, 4), 3));
    }
    @Test public void keepsOrder() {
        assertEquals(Arrays.asList(5, 7, 9),
            ChangelogFilter.retain(Arrays.asList(5, 2, 7, 1, 9), 4));
    }
    @Test public void dropsAll() {
        assertEquals(Collections.emptyList(),
            ChangelogFilter.retain(Arrays.asList(1, 2, 3), 10));
    }
}
