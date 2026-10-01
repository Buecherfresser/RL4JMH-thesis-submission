package bench;

import java.math.BigDecimal;
import java.math.BigInteger;

public final class DecimalCodec {

    private DecimalCodec() {}

    public static BigDecimal decode(byte[] b, int scale) {
        return new BigDecimal(new BigInteger(b), scale);
    }
}
