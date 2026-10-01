package bench;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import org.junit.Test;

public class SetLookupTest {

    @Test
    public void allHit() {
        assertEquals(3, SetLookup.countHits(new HashSet<>(Arrays.asList(1, 2, 3)),
                Arrays.asList(1, 2, 3)));
    }

    @Test
    public void someMiss() {
        assertEquals(1, SetLookup.countHits(new HashSet<>(Arrays.asList(1, 2, 3)),
                Arrays.asList(2, 99, 100)));
    }
}
