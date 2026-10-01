package bench;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class MailboxProcessorTest {
    @Test public void emptyArray() {
        assertEquals(0L, MailboxProcessor.process(new int[0]));
    }
    @Test public void singleElement() {
        assertEquals(31L, MailboxProcessor.process(new int[]{1}));
    }
    @Test public void multipleElements() {
        assertEquals(31L * (1 + 2 + 3 + 4), MailboxProcessor.process(new int[]{1,2,3,4}));
    }
    @Test public void handlesNegatives() {
        assertEquals(-62L, MailboxProcessor.process(new int[]{-1, -1}));
    }
}
