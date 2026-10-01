package bench.generated.c056;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import jodd.typeconverter.impl.LongConverter;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class LongConverterBenchmark {

    private LongConverter converter;

    @Setup
    public void setup() {
        // Initialize the converter instance. Since the method is stateless regarding input,
        // this setup is safe for repeated use.
        this.converter = new LongConverter();
    }

    @Benchmark
    public void convertNull(Blackhole bh) {
        Long result = converter.convert(null);
        bh.consume(result);
    }

    @Benchmark
    public void convertLong(Blackhole bh) {
        // Test direct Long conversion
        Long result = converter.convert(42L);
        bh.consume(result);
    }

    @Benchmark
    public void convertInteger(Blackhole bh) {
        // Test Number subclass conversion
        Long result = converter.convert(12345);
        bh.consume(result);
    }

    @Benchmark
    public void convertDouble(Blackhole bh) {
        // Test Number subclass conversion
        Long result = converter.convert(123.45);
        bh.consume(result);
    }

    @Benchmark
    public void convertBooleanTrue(Blackhole bh) {
        // Test Boolean conversion (true -> 1L)
        Long result = converter.convert(true);
        bh.consume(result);
    }

    @Benchmark
    public void convertBooleanFalse(Blackhole bh) {
        // Test Boolean conversion (false -> 0L)
        Long result = converter.convert(false);
        bh.consume(result);
    }

    @Benchmark
    public void convertStringPositive(Blackhole bh) {
        // Test string conversion without leading sign
        Long result = converter.convert("98765");
        bh.consume(result);
    }

    @Benchmark
    public void convertStringNegative(Blackhole bh) {
        // Test string conversion with leading sign (should strip it)
        Long result = converter.convert("-12345");
        bh.consume(result);
    }

    @Benchmark
    public void convertStringWithWhitespace(Blackhole bh) {
        // Test trimming functionality
        Long result = converter.convert("  -500  ");
        bh.consume(result);
    }
}
