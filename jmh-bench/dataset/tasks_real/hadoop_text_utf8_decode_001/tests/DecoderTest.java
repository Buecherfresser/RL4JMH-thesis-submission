package bench;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class DecoderTest {
    @Test public void decodesAscii() {
        assertEquals("hello", Decoder.decode("hello".getBytes()));
    }
    @Test public void decodesEmpty() {
        assertEquals("", Decoder.decode(new byte[0]));
    }
    @Test public void decodesTwoByte() {
        byte[] b = new byte[]{(byte)0xC2, (byte)0xA9};
        assertEquals("©", Decoder.decode(b));
    }
    @Test public void decodesThreeByte() {
        byte[] b = new byte[]{(byte)0xE2, (byte)0x82, (byte)0xAC};
        assertEquals("€", Decoder.decode(b));
    }
}
