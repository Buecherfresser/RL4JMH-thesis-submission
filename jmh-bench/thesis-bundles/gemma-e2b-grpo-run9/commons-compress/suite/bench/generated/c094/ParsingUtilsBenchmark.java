package bench.generated.c094;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.compress.utils.ParsingUtils;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ParsingUtilsBenchmark {

    // Since ParsingUtils methods are static, we don't need instance state,
    // but we must ensure inputs are not compile-time constants.

    @Benchmark
    public void benchmarkParseIntValue(Blackhole bh) {
        try {
            // Test base 10 parsing
            int result = ParsingUtils.parseIntValue("12345");
            bh.consume(result);
        } catch (IOException e) {
            // Ignore exceptions for benchmarking purposes if input is invalid,
            // though in a real scenario, this indicates a setup failure.
        }
    }

    @Benchmark
    public void benchmarkParseIntValueWithRadix(Blackhole bh) {
        try {
            // Test parsing with radix 16 (hexadecimal)
            int result = ParsingUtils.parseIntValue("FF", 16);
            bh.consume(result);
        } catch (IOException e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void benchmarkParseLongValue(Blackhole bh) {
        try {
            // Test base 10 parsing
            long result = ParsingUtils.parseLongValue("9876543210L");
            bh.consume(result);
        } catch (IOException e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void benchmarkParseLongValueWithRadix(Blackhole bh) {
        try {
            // Test parsing with radix 8 (octal)
            long result = ParsingUtils.parseLongValue("177", 8);
            bh.consume(result);
        } catch (IOException e) {
            // Ignore exceptions
        }
    }
}
