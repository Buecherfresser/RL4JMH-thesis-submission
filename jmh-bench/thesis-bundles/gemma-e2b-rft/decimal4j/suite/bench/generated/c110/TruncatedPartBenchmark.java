package bench.generated.c110;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.truncate.TruncatedPart;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class TruncatedPartBenchmark {

    // State fields for inputs
    private int firstTruncatedDigit;
    private boolean zeroAfterFirstTruncatedDigit;
    private TruncatedPart calculatedTruncatedPart;

    @Setup
    public void setup() {
        // Setup a representative input for testing the static valueOf method
        this.firstTruncatedDigit = 3;
        this.zeroAfterFirstTruncatedDigit = false;
        this.calculatedTruncatedPart = TruncatedPart.valueOf(this.firstTruncatedDigit, this.zeroAfterFirstTruncatedDigit);
    }

    @Benchmark
    public void benchmarkValueOf(Blackhole bh) {
        // Test the static factory method
        TruncatedPart result = TruncatedPart.valueOf(firstTruncatedDigit, zeroAfterFirstTruncatedDigit);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOf_Zero(Blackhole bh) {
        // Test case resulting in ZERO
        firstTruncatedDigit = 0;
        zeroAfterFirstTruncatedDigit = true;
        TruncatedPart result = TruncatedPart.valueOf(firstTruncatedDigit, zeroAfterFirstTruncatedDigit);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOf_LessThanHalf(Blackhole bh) {
        // Test case resulting in LESS_THAN_HALF_BUT_NOT_ZERO
        firstTruncatedDigit = 4;
        zeroAfterFirstTruncatedDigit = false;
        TruncatedPart result = TruncatedPart.valueOf(firstTruncatedDigit, zeroAfterFirstTruncatedDigit);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOf_EqualToHalf(Blackhole bh) {
        // Test case resulting in EQUAL_TO_HALF
        firstTruncatedDigit = 5;
        zeroAfterFirstTruncatedDigit = true;
        TruncatedPart result = TruncatedPart.valueOf(firstTruncatedDigit, zeroAfterFirstTruncatedDigit);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOf_GreaterThanHalf(Blackhole bh) {
        // Test case resulting in GREATER_THAN_HALF
        firstTruncatedDigit = 6;
        zeroAfterFirstTruncatedDigit = false;
        TruncatedPart result = TruncatedPart.valueOf(firstTruncatedDigit, zeroAfterFirstTruncatedDigit);
        bh.consume(result);
    }
}
