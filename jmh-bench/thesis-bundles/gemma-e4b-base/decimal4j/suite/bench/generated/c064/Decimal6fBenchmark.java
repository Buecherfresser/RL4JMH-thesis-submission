package bench.generated.c064;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.immutable.Decimal6f;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.decimal4j.api.Decimal;
import org.decimal4j.mutable.MutableDecimal6f;
import org.decimal4j.exact.Multipliable6f;
import org.decimal4j.scale.Scale6f;
import org.decimal4j.factory.Factory6f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Decimal6fBenchmark {

    private Decimal6f decimalFromString;
    private Decimal6f decimalFromDouble;
    private Decimal6f decimalFromBigDecimal;
    private Decimal6f decimalFromBigInteger;
    private Decimal6f decimalFromGeneric;
    private Decimal6f decimalFromLong;
    private Decimal6f decimalFromStringRounding;

    @Setup(Level.Trial)
    public void setup() {
        // 1. String construction
        decimalFromString = Decimal6f.valueOf("123.456789");

        // 2. Double construction
        decimalFromDouble = Decimal6f.valueOf(123.456789);

        // 3. BigDecimal construction
        decimalFromBigDecimal = Decimal6f.valueOf(new BigDecimal("123.456789"));

        // 4. BigInteger construction
        decimalFromBigInteger = Decimal6f.valueOf(BigInteger.valueOf(1234567890123L));

        // 5. Generic Decimal conversion
        decimalFromGeneric = Decimal6f.ONE;

        // 6. Long construction
        decimalFromLong = Decimal6f.valueOf(123456789L);

        // 7. String construction with specific rounding mode
        decimalFromStringRounding = Decimal6f.valueOf("1.1234567", RoundingMode.HALF_DOWN);
    }

    @Benchmark
    public Decimal6f testValueOfString() {
        return Decimal6f.valueOf(decimalFromString.toString());
    }

    @Benchmark
    public Decimal6f testValueOfDouble() {
        return Decimal6f.valueOf(decimalFromDouble);
    }

    @Benchmark
    public Decimal6f testValueOfBigDecimal() {
        return Decimal6f.valueOf(decimalFromBigDecimal);
    }

    @Benchmark
    public Decimal6f testValueOfBigInteger() {
        return Decimal6f.valueOf(decimalFromBigInteger);
    }

    @Benchmark
    public Decimal6f testValueOfGenericDecimal() {
        return Decimal6f.valueOf(decimalFromGeneric);
    }

    @Benchmark
    public Decimal6f testValueOfLong() {
        return Decimal6f.valueOf(decimalFromLong.longValue());
    }

    @Benchmark
    public Decimal6f testValueOfStringWithRounding() {
        return Decimal6f.valueOf(decimalFromStringRounding.toString(), RoundingMode.HALF_DOWN);
    }

    @Benchmark
    public MutableDecimal6f testToMutableDecimal() {
        return decimalFromDouble.toMutableDecimal();
    }

    @Benchmark
    public Decimal6f testToImmutableDecimal() {
        return decimalFromDouble.toImmutableDecimal();
    }

    @Benchmark
    public Multipliable6f testMultiplyExact() {
        return decimalFromDouble.multiplyExact();
    }

    @Benchmark
    public int testGetScale() {
        return decimalFromDouble.getScale();
    }

    @Benchmark
    public Scale6f testGetScaleMetrics() {
        return decimalFromDouble.getScaleMetrics();
    }

    @Benchmark
    public Factory6f testGetFactory() {
        return decimalFromDouble.getFactory();
    }
}
