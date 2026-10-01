package bench;

import static org.junit.Assert.assertEquals;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

public class MapLookupTest {
    @Test public void empty() {
        assertEquals(0L, MapLookup.sumGet(new HashMap<String, Long>(), Arrays.asList("a", "b")));
    }
    @Test public void allHits() {
        Map<String, Long> m = new HashMap<>();
        m.put("a", 1L); m.put("b", 2L);
        assertEquals(3L, MapLookup.sumGet(m, Arrays.asList("a", "b")));
    }
    @Test public void someMisses() {
        Map<String, Long> m = new HashMap<>();
        m.put("a", 7L);
        assertEquals(7L, MapLookup.sumGet(m, Arrays.asList("a", "missing")));
    }
    @Test public void factoryReturnsMap() {
        assertEquals(0L, MapLookup.sumGet(MapLookup.newFastMap(4), Arrays.asList("a")));
    }
}
