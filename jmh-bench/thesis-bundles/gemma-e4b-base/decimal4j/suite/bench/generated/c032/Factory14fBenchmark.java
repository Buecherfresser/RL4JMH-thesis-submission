package bench.generated.c032;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.factory.Factory14f;
import org.decimal4j.immutable.Decimal14f;
import org.decimal4j.mutable.MutableDecimal14f;
import org.decimal4j.scale.Scale14f;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.decimal4j.api.Decimal;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Factory14fBenchmark {

    private Factory14f factory;

    // Inputs for various methods
    private long longInput;
    private float floatInput;
    private double doubleInput;
    private BigInteger bigIntegerInput;
    private BigDecimal bigDecimalInput;
    private String stringInput;
    private Decimal<?> decimalInput;
    private int arrayLength;

    @Setup(Level.Trial)
    public void setup() {
        factory = Factory14f.INSTANCE;

        // Setup inputs
        longInput = 1234567890123L;
        floatInput = 3.14159f;
        doubleInput = 3.141592653589793;
        bigIntegerInput = new BigInteger("9876543210987654321");
        bigDecimalInput = new BigDecimal("12345.678901234567");
        stringInput = "12345.6789";
        
        // Create a dummy Decimal instance for testing Decimal<?> inputs
        decimalInput = Decimal14f.valueOf(1.0); 

        arrayLength = 100;
    }

    @Benchmark
    public void testValueOfLong(Blackhole bh) {
        Decimal14f result = factory.valueOf(longInput);
        bh.consume(result);
    }

    @Benchmark
    public void testValueOfFloat(Blackhole bh) {
        Decimal14f result = factory.valueOf(floatInput);
        bh.consume(result);
    }

    @Benchmark
    public void testValueOfFloatWithRounding(Blackhole bh) {
        Decimal14f result = factory.valueOf(floatInput, RoundingMode.HALF_UP);
        bh.consume(result);
    }

    @Benchmark
    public void testValueOfDouble(Blackhole bh) {
        Decimal14f result = factory.valueOf(doubleInput);
        bh.consume(result);
    }

    @Benchmark
    public void testValueOfDoubleWithRounding(Blackhole bh) {
        Decimal14f result = factory.valueOf(doubleInput, RoundingMode.HALF_DOWN);
        bh.consume(result);
    }

    @Benchmark
    public void testValueOfBigInteger(Blackhole bh) {
        Decimal14f result = factory.valueOf(bigIntegerInput);
        bh.consume(result);
    }

    @Benchmark
    public void testValueOfBigDecimal(Blackhole bh) {
        Decimal14f result = factory.valueOf(bigDecimalInput);
        bh.consume(result);
    }

    @Benchmark
    public void testValueOfBigDecimalWithRounding(Blackhole bh) {
        Decimal14f result = factory.valueOf(bigDecimalInput, RoundingMode.CEILING);
        bh.consume(result);
    }

    @Benchmark
    public void testValueOfDecimal(Blackhole bh) {
        Decimal14f result = factory.valueOf(decimalInput);
        bh.consume(result);
    }

    @Benchmark
    public void testValueOfDecimalWithRounding(Blackhole bh) {
        Decimal14f result = factory.valueOf(decimalInput, RoundingMode.DOWN);
        bh.consume(result);
    }

    @Benchmark
    public void testParseString(Blackhole bh) {
        Decimal14f result = factory.parse(stringInput);
        bh.consume(result);
    }

    @Benchmark
    public void testParseStringWithRounding(Blackhole bh) {
        Decimal14f result = factory.parse(stringInput, RoundingMode.HALF_EVEN);
        bh.consume(result);
    }

    @Benchmark
    public void testValueOfUnscaledLong(Blackhole bh) {
        Decimal14f result = factory.valueOfUnscaled(longInput);
        bh.consume(result);
    }

    @Benchmark
    public void testValueOfUnscaledLongWithScale(Blackhole bh) {
        // Using a scale different from the factory's default 14
        Decimal14f result = factory.valueOfUnscaled(longInput, 5);
        bh.consume(result);
    }

    @Benchmark
    public void testValueOfUnscaledLongWithScaleAndRounding(Blackhole bh) {
        Decimal14f result = factory.valueOfUnscaled(longInput, 5, RoundingMode.UP);
        bh.consume(result);
    }

    @Benchmark
    public void testNewArray(Blackhole bh) {
        Decimal14f[] result = factory.newArray(arrayLength);
        bh.consume(result);
    }

    @Benchmark
    public void testNewMutable(Blackhole bh) {
        MutableDecimal14f result = factory.newMutable();
        bh.consume(result);
    }

    @Benchmark
    public void testNewMutableArray(Blackhole bh) {
        MutableDecimal14f[] result = factory.newMutableArray(arrayLength);
        bh.consume(result);
    }
}
