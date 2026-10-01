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

    // --- State for Instance Method Benchmarks ---
    // We test representative constants
    private TruncatedPart zeroPart;
    private TruncatedPart halfPart;
    private TruncatedPart greaterThanHalfPart;

    // --- State for Static Method Benchmarks (valueOf) ---
    // Representative inputs for valueOf(int firstTruncatedDigit, boolean zeroAfterFirstTruncatedDigit)
    private int inputDigit1;
    private boolean inputZeroAfter1;
    private int inputDigit2;
    private boolean inputZeroAfter2;
    private int inputDigit3;
    private boolean inputZeroAfter3;

    @Setup(Level.Trial)
    public void setup() {
        // Setup instance parts
        zeroPart = TruncatedPart.ZERO;
        halfPart = TruncatedPart.EQUAL_TO_HALF;
        greaterThanHalfPart = TruncatedPart.GREATER_THAN_HALF;

        // Setup static method inputs
        // Case 1: ZERO (0, true)
        inputDigit1 = 0;
        inputZeroAfter1 = true;
        // Case 2: EQUAL_TO_HALF (5, true)
        inputDigit2 = 5;
        inputZeroAfter2 = true;
        // Case 3: GREATER_THAN_HALF (8, false)
        inputDigit3 = 8;
        inputZeroAfter3 = false;
    }

    // --- Instance Method Benchmarks ---

    @Benchmark
    public void zeroPart_isGreaterThanZero(Blackhole bh) {
        bh.consume(zeroPart.isGreaterThanZero());
    }

    @Benchmark
    public void zeroPart_isEqualToHalf(Blackhole bh) {
        bh.consume(zeroPart.isEqualToHalf());
    }

    @Benchmark
    public void zeroPart_isGreaterEqualHalf(Blackhole bh) {
        bh.consume(zeroPart.isGreaterEqualHalf());
    }

    @Benchmark
    public void zeroPart_isGreaterThanHalf(Blackhole bh) {
        bh.consume(zeroPart.isGreaterThanHalf());
    }

    @Benchmark
    public void halfPart_isGreaterThanZero(Blackhole bh) {
        bh.consume(halfPart.isGreaterThanZero());
    }

    @Benchmark
    public void halfPart_isEqualToHalf(Blackhole bh) {
        bh.consume(halfPart.isEqualToHalf());
    }

    @Benchmark
    public void halfPart_isGreaterEqualHalf(Blackhole bh) {
        bh.consume(halfPart.isGreaterEqualHalf());
    }

    @Benchmark
    public void halfPart_isGreaterThanHalf(Blackhole bh) {
        bh.consume(halfPart.isGreaterThanHalf());
    }

    @Benchmark
    public void greaterThanHalfPart_isGreaterThanZero(Blackhole bh) {
        bh.consume(greaterThanHalfPart.isGreaterThanZero());
    }

    @Benchmark
    public void greaterThanHalfPart_isEqualToHalf(Blackhole bh) {
        bh.consume(greaterThanHalfPart.isEqualToHalf());
    }

    @Benchmark
    public void greaterThanHalfPart_isGreaterEqualHalf(Blackhole bh) {
        bh.consume(greaterThanHalfPart.isGreaterEqualHalf());
    }

    @Benchmark
    public void greaterThanHalfPart_isGreaterThanHalf(Blackhole bh) {
        bh.consume(greaterThanHalfPart.isGreaterThanHalf());
    }

    // --- Static Method Benchmarks (valueOf) ---

    @Benchmark
    public TruncatedPart valueOf_CaseZero(Blackhole bh) {
        TruncatedPart result = TruncatedPart.valueOf(inputDigit1, inputZeroAfter1);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public TruncatedPart valueOf_CaseHalf(Blackhole bh) {
        TruncatedPart result = TruncatedPart.valueOf(inputDigit2, inputZeroAfter2);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public TruncatedPart valueOf_CaseGreaterThanHalf(Blackhole bh) {
        TruncatedPart result = TruncatedPart.valueOf(inputDigit3, inputZeroAfter3);
        bh.consume(result);
        return result;
    }
}
