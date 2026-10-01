package bench;

import static org.junit.Assert.assertEquals;
import java.util.Arrays;
import org.junit.Test;

public class ResultBuilderTest {
    @Test public void zeroLength() {
        assertEquals(0, ResultBuilder.build(0).size());
    }
    @Test public void singleEntry() {
        assertEquals(Integer.valueOf(0), ResultBuilder.build(1).get(0));
    }
    @Test public void valuesAreMultiplesOfSeven() {
        assertEquals(Arrays.asList(0, 7, 14, 21), ResultBuilder.build(4));
    }
    @Test public void sizeMatches() {
        assertEquals(10, ResultBuilder.build(10).size());
    }
}
