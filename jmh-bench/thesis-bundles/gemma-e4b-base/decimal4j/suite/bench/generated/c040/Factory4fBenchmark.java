package bench.generated.c040;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.factory.Factory4f;
import org.decimal4j.scale.Scale4f;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.immutable.Decimal4f;
import org.decimal4j.mutable.MutableDecimal4f;
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
public class Factory4fBenchmark {

    private BigDecimal bigDecimalInput;
    private String stringInput;
    private long longInput;
    private double doubleInput;
    private BigInteger bigIntegerInput;
    private Decimal<?> decimalInput;

    @Setup(Level.Trial)
    public void setup() {
        // Setup representative inputs
        longInput = 123456789L;
        doubleInput = 123.456789;
        bigIntegerInput = new BigInteger("9876543210987654321");
        bigDecimalInput = new BigDecimal("123.45678901234567");
        stringInput = "123.4567";
        
        // Create a dummy Decimal object for testing valueOf(Decimal<?>)
        decimalInput = Decimal4f.valueOf(1.0);
    }

    @Benchmark
    public ScaleMetrics testGetScaleMetrics(Blackhole bh) {
        return Factory4f.INSTANCE.getScaleMetrics();
    }

    @Benchmark
    public int testGetScale(Blackhole bh) {
        return Factory4f.INSTANCE.getScale();
    }

    @Benchmark
    public Class<Decimal4f> testImmutableType(Blackhole bh) {
        return Factory4f.INSTANCE.immutableType();
    }

    @Benchmark
    public Class<MutableDecimal4f> testMutableType(Blackhole bh) {
        return Factory4f.INSTANCE.mutableType();
    }

    @Benchmark
    public org.decimal4j.factory.DecimalFactory<?> testDeriveFactoryInt(Blackhole bh) {
        return Factory4f.INSTANCE.deriveFactory(4);
    }

    @Benchmark
    public org.decimal4j.factory.DecimalFactory<ScaleMetrics> testDeriveFactoryScaleMetrics(Blackhole bh) {
        return Factory4f.INSTANCE.deriveFactory(Scale4f.INSTANCE);
    }

    @Benchmark
    public Decimal4f testValueOfLong(Blackhole bh) {
        return Factory4f.INSTANCE.valueOf(longInput);
    }

    @Benchmark
    public Decimal4f testValueOfFloat(Blackhole bh) {
        return Factory4f.INSTANCE.valueOf((float) doubleInput);
    }

    @Benchmark
    public Decimal4f testValueOfFloatRoundingMode(Blackhole bh) {
        return Factory4f.INSTANCE.valueOf((float) doubleInput, RoundingMode.HALF_UP);
    }

    @Benchmark
    public Decimal4f testValueOfDouble(Blackhole bh) {
        return Factory4f.INSTANCE.valueOf(doubleInput);
    }

    @Benchmark
    public Decimal4f testValueOfDoubleRoundingMode(Blackhole bh) {
        return Factory4f.INSTANCE.valueOf(doubleInput, RoundingMode.HALF_DOWN);
    }

    @Benchmark
    public Decimal4f testValueOfBigInteger(Blackhole bh) {
        return Factory4f.INSTANCE.valueOf(bigIntegerInput);
    }

    @Benchmark
    public Decimal4f testValueOfBigDecimal(Blackhole bh) {
        return Factory4f.INSTANCE.valueOf(bigDecimalInput);
    }

    @Benchmark
    public Decimal4f testValueOfBigDecimalRoundingMode(Blackhole bh) {
        return Factory4f.INSTANCE.valueOf(bigDecimalInput, RoundingMode.CEILING);
    }

    @Benchmark
    public Decimal4f testValueOfDecimal(Blackhole bh) {
        return Factory4f.INSTANCE.valueOf(decimalInput);
    }

    @Benchmark
    public Decimal4f testValueOfDecimalRoundingMode(Blackhole bh) {
        return Factory4f.INSTANCE.valueOf(decimalInput, RoundingMode.UP);
    }

    @Benchmark
    public Decimal4f testParseString(Blackhole bh) {
        return Factory4f.INSTANCE.parse(stringInput);
    }

    @Benchmark
    public Decimal4f testParseStringRoundingMode(Blackhole bh) {
        return Factory4f.INSTANCE.parse(stringInput, RoundingMode.HALF_EVEN);
    }

    @Benchmark
    public Decimal4f testValueOfUnscaledLong(Blackhole bh) {
        return Factory4f.INSTANCE.valueOfUnscaled(longInput);
    }

    @Benchmark
    public Decimal4f testValueOfUnscaledLongScale(Blackhole bh) {
        return Factory4f.INSTANCE.valueOfUnscaled(longInput, 4);
    }

    @Benchmark
    public Decimal4f testValueOfUnscaledLongScaleRoundingMode(Blackhole bh) {
        return Factory4f.INSTANCE.valueOfUnscaled(longInput, 4, RoundingMode.DOWN);
    }

    @Benchmark
    public Decimal4f[] testNewArray(Blackhole bh) {
        return Factory4f.INSTANCE.newArray(100);
    }

    @Benchmark
    public MutableDecimal4f testNewMutable(Blackhole bh) {
        return Factory4f.INSTANCE.newMutable();
    }

    @Benchmark
    public MutableDecimal4f[] testNewMutableArray(Blackhole bh) {
        return Factory4f.INSTANCE.newMutableArray(100);
    }
}
