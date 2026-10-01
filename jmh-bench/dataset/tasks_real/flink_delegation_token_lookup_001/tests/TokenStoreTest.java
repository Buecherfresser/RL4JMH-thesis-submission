package bench;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

public class TokenStoreTest {
    private static Map<String, String> seed() {
        Map<String, String> m = new HashMap<>();
        m.put("a", "1");
        m.put("b", "2");
        m.put("c", "3");
        return m;
    }
    @Test public void hits() {
        assertEquals("2", new TokenStore(seed()).lookup("b"));
    }
    @Test public void firstKey() {
        assertEquals("1", new TokenStore(seed()).lookup("a"));
    }
    @Test public void miss() {
        assertNull(new TokenStore(seed()).lookup("z"));
    }
    @Test public void emptyStoreMisses() {
        assertNull(new TokenStore(new HashMap<>()).lookup("x"));
    }
}
