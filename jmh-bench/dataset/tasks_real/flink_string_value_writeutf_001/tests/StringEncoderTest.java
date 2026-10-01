package bench;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class StringEncoderTest {
    @Test public void asciiRoundTrip() {
        assertArrayEquals(new byte[]{'a','b','c'}, StringEncoder.encode("abc"));
    }
    @Test public void emptyString() {
        assertEquals(0, StringEncoder.encode("").length);
    }
    @Test public void twoByteSequence() {
        byte[] b = StringEncoder.encode("©");
        assertArrayEquals(new byte[]{(byte)0xC2, (byte)0xA9}, b);
    }
    @Test public void threeByteSequence() {
        byte[] b = StringEncoder.encode("€");
        assertArrayEquals(new byte[]{(byte)0xE2, (byte)0x82, (byte)0xAC}, b);
    }
}
