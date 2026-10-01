package bench.generated.c066;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.immutable.Decimal8f;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.decimal4j.api.Decimal;
import org.decimal4j.mutable.MutableDecimal8f;
import org.decimal4j.exact.Multipliable8f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Decimal8fBenchmark {

    private Decimal8f inputDecimal8f;
    private BigDecimal inputBigDecimal;
    private double inputDouble;
    private long inputLong;
    private String inputString;
    private Decimal<?> inputGenericDecimal;

    @Setup(Level.Trial)
    public void setup() {
        // 1. Simple Decimal8f instance
        inputDecimal8f = Decimal8f.valueOf(12345678.12345678);

        // 2. BigDecimal input
        inputBigDecimal = new BigDecimal("987654321.00000001");

        // 3. Double input
        inputDouble = 123.4567890123456;

        // 4. Long input
        inputLong = 92233720368L; // Near MAX_VALUE

        // 5. String input
        inputString = "12345.67890123";

        // 6. Generic Decimal input (using Decimal8f as a concrete implementation of Decimal<?>)
        inputGenericDecimal = inputDecimal8f;
    }

    @Benchmark
    public void benchmarkValueOfLong(Blackhole bh) {
        Decimal8f result = Decimal8f.valueOf(inputLong);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfDouble(Blackhole bh) {
        Decimal8f result = Decimal8f.valueOf(inputDouble);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfBigDecimal(Blackhole bh) {
        Decimal8f result = Decimal8f.valueOf(inputBigDecimal);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfDecimal(Blackhole bh) {
        Decimal8f result = Decimal8f.valueOf(inputGenericDecimal);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfUnscaledString(Blackhole bh) {
        Decimal8f result = Decimal8f.valueOf(inputString);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkToMutableDecimal(Blackhole bh) {
        MutableDecimal8f result = inputDecimal8f.toMutableDecimal();
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkToImmutableDecimal(Blackhole bh) {
        Decimal8f result = inputDecimal8f.toImmutableDecimal();
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkMultiplyExact(Blackhole bh) {
        // This benchmarks the creation of the Multipliable8f object
        Multipliable8f multiplier = inputDecimal8f.multiplyExact();
        bh.consume(multiplier);
    }
}
