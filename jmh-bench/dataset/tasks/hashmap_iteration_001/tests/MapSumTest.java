package bench;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

public class MapSumTest {

    @Test
    public void empty_returnsZero() {
        assertEquals(0L, MapSum.sumValues(new HashMap<String, Integer>()));
    }

    @Test
    public void smallMap_sumsAllValues() {
        Map<String, Integer> m = new HashMap<>();
        m.put("a", 1);
        m.put("b", 2);
        m.put("c", 3);
        assertEquals(6L, MapSum.sumValues(m));
    }
}
