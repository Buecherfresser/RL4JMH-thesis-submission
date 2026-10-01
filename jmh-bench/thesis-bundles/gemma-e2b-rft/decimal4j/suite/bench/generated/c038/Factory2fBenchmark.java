package bench.generated.c038;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.decimal4j.factory.Factory2f;
import org.decimal4j.immutable.Decimal2f;
import org.decimal4j.mutable.MutableDecimal2f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class Factory2fBenchmark {

    private Decimal2f immutableDecimal;
    private MutableDecimal2f mutableDecimal;
    private BigDecimal bigDecimal;
    private String parseString;

    @Setup
    public void setup() {
        // Setup immutable decimal value
        long value = 1234567890123L;
        immutableDecimal = Factory2f.INSTANCE.valueOf(value);

        // Setup mutable decimal value
        mutableDecimal = Factory2f.INSTANCE.newMutable();

        // Setup BigDecimal value
        bigDecimal = new BigDecimal("9876543210.123456789");

        // Setup String for parsing
        parseString = "1234567890123";
    }

    @Benchmark
    public void immutableValueOfLong(Blackhole bh) {
        Decimal2f result = Factory2f.INSTANCE.valueOf(1234567890123L);
        bh.consume(result);
    }

    @Benchmark
    public void immutableValueOfDouble(Blackhole bh) {
        double value = 3.1415926535;
        Decimal2f result = Factory2f.INSTANCE.valueOf(value);
        bh.consume(result);
    }

    @Benchmark
    public void immutableValueOfDoubleWithRounding(Blackhole bh) {
        double value = 1.23456789;
        Decimal2f result = Factory2f.INSTANCE.valueOf(value, RoundingMode.HALF_UP);
        bh.consume(result);
    }

    @Benchmark
    public void immutableValueOfBigDecimal(Blackhole bh) {
        Decimal2f result = Factory2f.INSTANCE.valueOf(bigDecimal);
        bh.consume(result);
    }

    @Benchmark
    public void immutableValueOfBigDecimalWithRounding(Blackhole bh) {
        Decimal2f result = Factory2f.INSTANCE.valueOf(bigDecimal, RoundingMode.HALF_DOWN);
        bh.consume(result);
    }

    @Benchmark
    public void immutableParseString(Blackhole bh) {
        Decimal2f result = Factory2f.INSTANCE.parse(parseString);
        bh.consume(result);
    }

    @Benchmark
    public void immutableParseStringWithRounding(Blackhole bh) {
        Decimal2f result = Factory2f.INSTANCE.parse(parseString, RoundingMode.CEILING);
        bh.consume(result);
    }

    @Benchmark
    public void mutableNewMutable(Blackhole bh) {
        MutableDecimal2f result = Factory2f.INSTANCE.newMutable();
        bh.consume(result);
    }
}
