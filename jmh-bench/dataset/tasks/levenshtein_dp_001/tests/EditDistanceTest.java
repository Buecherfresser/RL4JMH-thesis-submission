package bench;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class EditDistanceTest {
    @Test public void empties() { assertEquals(0, EditDistance.distance("", "")); }
    @Test public void prefix() { assertEquals(3, EditDistance.distance("", "abc")); }
    @Test public void identical() { assertEquals(0, EditDistance.distance("hello", "hello")); }
    @Test public void textbook() { assertEquals(3, EditDistance.distance("kitten", "sitting")); }
}
