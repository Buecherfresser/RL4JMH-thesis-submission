package bench.generated.c036;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import jodd.typeconverter.impl.ByteConverter;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ByteConverterBenchmark {

    // Since ByteConverter is stateless, we can instantiate it once per trial
    private ByteConverter converter;

    @Setup
    public void setup() {
        // Initialize the converter instance.
        this.converter = new ByteConverter();
    }

    @Benchmark
    public void testNullInput(Blackhole bh) {
        try {
            Byte result = converter.convert(null);
            if (result != null) {
                bh.consume(result);
            }
        } catch (Exception e) {
            // Ignore exceptions for this specific test if they are expected/handled elsewhere
        }
    }

    @Benchmark
    public void testByteInput(Blackhole bh) {
        try {
            Byte result = converter.convert((byte) 10);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore
        }
    }

    @Benchmark
    public void testIntegerInput(Blackhole bh) {
        try {
            // Test Number conversion path
            Byte result = converter.convert(12345);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore
        }
    }

    @Benchmark
    public void testBooleanTrueInput(Blackhole bh) {
        try {
            // Test Boolean conversion path (true -> 1)
            Byte result = converter.convert(true);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore
        }
    }

    @Benchmark
    public void testBooleanFalseInput(Blackhole bh) {
        try {
            // Test Boolean conversion path (false -> 0)
            Byte result = converter.convert(false);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore
        }
    }

    @Benchmark
    public void testStringInputValid(Blackhole bh) {
        try {
            // Test standard string conversion
            Byte result = converter.convert("12345");
            bh.consume(result);
        } catch (Exception e) {
            // Ignore
        }
    }

    @Benchmark
    public void testStringInputWithPlus(Blackhole bh) {
        try {
            // Test string conversion with leading '+'
            Byte result = converter.convert("+98765");
            bh.consume(result);
        } catch (Exception e) {
            // Ignore
        }
    }

    @Benchmark
    public void testStringInputNull(Blackhole bh) {
        try {
            Byte result = converter.convert(null);
            if (result != null) {
                bh.consume(result);
            }
        } catch (Exception e) {
            // Ignore
        }
    }
}
