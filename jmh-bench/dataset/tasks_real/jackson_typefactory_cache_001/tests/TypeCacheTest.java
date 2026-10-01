package bench;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

public class TypeCacheTest {
    private static TypeCache build() {
        Map<TypeCache.Key, Integer> m = new HashMap<>();
        m.put(new TypeCache.Key("a", "b"), 1);
        m.put(new TypeCache.Key("a", "c"), 2);
        m.put(new TypeCache.Key("x", "y"), 3);
        return new TypeCache(m);
    }
    @Test public void hits() {
        assertEquals(Integer.valueOf(1), build().lookup(new TypeCache.Key("a", "b")));
    }
    @Test public void differingSecondField() {
        assertEquals(Integer.valueOf(2), build().lookup(new TypeCache.Key("a", "c")));
    }
    @Test public void miss() {
        assertNull(build().lookup(new TypeCache.Key("nope", "nope")));
    }
    @Test public void thirdKey() {
        assertEquals(Integer.valueOf(3), build().lookup(new TypeCache.Key("x", "y")));
    }
}
