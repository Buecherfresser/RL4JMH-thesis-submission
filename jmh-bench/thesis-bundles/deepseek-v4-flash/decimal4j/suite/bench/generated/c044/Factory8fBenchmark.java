package bench.generated.c044;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.decimal4j.api.Decimal;
import org.decimal4j.factory.DecimalFactory;
import org.decimal4j.factory.Factory8f;
import org.decimal4j.immutable.Decimal8f;
import org.decimal4j.mutable.MutableDecimal8f;
import org.decimal4j.scale.Scale8f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Factory8fBenchmark {

    @State(Scope.Benchmark)
    public static class BenchState {
        Factory8f factory = Factory8f.INSTANCE;
        long longValue = 123456789L;
        float floatValue = 123.456f;
        double doubleValue = 123.45678901;
        BigInteger bigInteger = new BigInteger("123456789012345678901234567890");
        BigDecimal bigDecimal = new BigDecimal("123456789.12345678");
        Decimal<?> decimalValue = Decimal8f.valueOf("123.45678901");
        String stringValue = "123.45678901";
        RoundingMode roundingMode = RoundingMode.HALF_UP;
        int scale = 8;
        int arrayLength = 10;
        Scale8f scale8f = Scale8f.INSTANCE;

        @Setup(Level.Trial)
        public void setup() {
            // All inputs are already initialized; nothing else needed.
        }
    }

    @Benchmark
    public Scale8f getScaleMetrics(BenchState state) {
        return state.factory.getScaleMetrics();
    }

    @Benchmark
    public int getScale(BenchState state) {
        return state.factory.getScale();
    }

    @Benchmark
    public Class<Decimal8f> immutableType(BenchState state) {
        return state.factory.immutableType();
    }

    @Benchmark
    public Class<MutableDecimal8f> mutableType(BenchState state) {
        return state.factory.mutableType();
    }

    @Benchmark
    public DecimalFactory<?> deriveFactoryInt(BenchState state) {
        return state.factory.deriveFactory(state.scale);
    }

    @Benchmark
    public DecimalFactory<Scale8f> deriveFactoryScale(BenchState state) {
        return state.factory.deriveFactory(state.scale8f);
    }

    @Benchmark
    public Decimal8f valueOfLong(BenchState state) {
        return state.factory.valueOf(state.longValue);
    }

    @Benchmark
    public Decimal8f valueOfFloat(BenchState state) {
        return state.factory.valueOf(state.floatValue);
    }

    @Benchmark
    public Decimal8f valueOfFloatRounding(BenchState state) {
        return state.factory.valueOf(state.floatValue, state.roundingMode);
    }

    @Benchmark
    public Decimal8f valueOfDouble(BenchState state) {
        return state.factory.valueOf(state.doubleValue);
    }

    @Benchmark
    public Decimal8f valueOfDoubleRounding(BenchState state) {
        return state.factory.valueOf(state.doubleValue, state.roundingMode);
    }

    @Benchmark
    public Decimal8f valueOfBigInteger(BenchState state) {
        return state.factory.valueOf(state.bigInteger);
    }

    @Benchmark
    public Decimal8f valueOfBigDecimal(BenchState state) {
        return state.factory.valueOf(state.bigDecimal);
    }

    @Benchmark
    public Decimal8f valueOfBigDecimalRounding(BenchState state) {
        return state.factory.valueOf(state.bigDecimal, state.roundingMode);
    }

    @Benchmark
    public Decimal8f valueOfDecimal(BenchState state) {
        return state.factory.valueOf(state.decimalValue);
    }

    @Benchmark
    public Decimal8f valueOfDecimalRounding(BenchState state) {
        return state.factory.valueOf(state.decimalValue, state.roundingMode);
    }

    @Benchmark
    public Decimal8f parseString(BenchState state) {
        return state.factory.parse(state.stringValue);
    }

    @Benchmark
    public Decimal8f parseStringRounding(BenchState state) {
        return state.factory.parse(state.stringValue, state.roundingMode);
    }

    @Benchmark
    public Decimal8f valueOfUnscaledLong(BenchState state) {
        return state.factory.valueOfUnscaled(state.longValue);
    }

    @Benchmark
    public Decimal8f valueOfUnscaledLongScale(BenchState state) {
        return state.factory.valueOfUnscaled(state.longValue, state.scale);
    }

    @Benchmark
    public Decimal8f valueOfUnscaledLongScaleRounding(BenchState state) {
        return state.factory.valueOfUnscaled(state.longValue, state.scale, state.roundingMode);
    }

    @Benchmark
    public Decimal8f[] newArray(BenchState state) {
        return state.factory.newArray(state.arrayLength);
    }

    @Benchmark
    public MutableDecimal8f newMutable(BenchState state) {
        return state.factory.newMutable();
    }

    @Benchmark
    public MutableDecimal8f[] newMutableArray(BenchState state) {
        return state.factory.newMutableArray(state.arrayLength);
    }
}
