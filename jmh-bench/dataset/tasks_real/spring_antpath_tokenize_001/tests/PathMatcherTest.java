package bench;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class PathMatcherTest {
    @Test public void exactMatch() {
        assertTrue(new PathMatcher().matches("/a/b/c", "/a/b/c"));
    }
    @Test public void wildcardMatch() {
        assertTrue(new PathMatcher().matches("/a/*/c", "/a/anything/c"));
    }
    @Test public void differingDepthFails() {
        assertFalse(new PathMatcher().matches("/a/b", "/a/b/c"));
    }
    @Test public void differingSegmentFails() {
        assertFalse(new PathMatcher().matches("/a/b", "/a/x"));
    }
}
