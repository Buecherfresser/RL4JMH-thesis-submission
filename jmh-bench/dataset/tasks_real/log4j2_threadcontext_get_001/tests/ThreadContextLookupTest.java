package bench;

import static org.junit.Assert.assertEquals;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

public class ThreadContextLookupTest {
    private static Map<String, String> ctx() {
        Map<String, String> m = new HashMap<>();
        m.put("user", "alice");
        m.put("session", "42");
        return m;
    }
    @Test public void hits() {
        assertEquals("alice", ThreadContextLookup.get(ctx(), "user"));
    }
    @Test public void miss() {
        assertEquals("", ThreadContextLookup.get(ctx(), "missing"));
    }
    @Test public void anotherHit() {
        assertEquals("42", ThreadContextLookup.get(ctx(), "session"));
    }
    @Test public void emptyMap() {
        assertEquals("", ThreadContextLookup.get(new HashMap<String, String>(), "x"));
    }
}
