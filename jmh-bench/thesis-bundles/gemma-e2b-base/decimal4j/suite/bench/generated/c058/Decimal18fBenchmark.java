package bench.generated.c058;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;

import org.decimal4j.immutable.Decimal18f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 5, time = 2)
@Measurement(iterations = 5, time = 2)
public class Decimal18fBenchmark {

    // --- State Fields ---
    private Decimal18f decimalFromDouble;
    private double inputDouble;

    // --- Setup ---
    @Setup
    public void setup() {
        // Setup input data once per trial
        // Use a double value that requires conversion to Decimal18f
        this.inputDouble = 12345.67890123456789;
        // Pre-calculate the result if needed, but we will calculate it in the benchmark
    }

    @Benchmark
    public void benchmarkValueOfDouble(Blackhole bh) {
        // Call the subject method exactly once per invocation
        Decimal18f result = Decimal18f.valueOf(inputDouble);
        bh.consume(result);
    }
}
