package bench;

import java.nio.charset.StandardCharsets;

public final class Decoder {

    private Decoder() {}

    public static String decode(byte[] b) {
        return new String(b, StandardCharsets.UTF_8);
    }
}
