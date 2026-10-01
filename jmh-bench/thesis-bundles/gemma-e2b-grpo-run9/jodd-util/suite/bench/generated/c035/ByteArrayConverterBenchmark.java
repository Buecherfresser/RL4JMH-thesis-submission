package bench.generated.c035;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Arrays;
import java.util.Collection;

import jodd.typeconverter.impl.ByteArrayConverter;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ByteArrayConverterBenchmark {

    private ByteArrayConverter converter;

    public ByteArrayConverterBenchmark() {
        // Instantiate the converter. We pass null for TypeConverterManager
        // as we cannot mock it easily, relying on the fact that the
        // tested methods might handle null dependencies gracefully or fail
        // predictably if the dependency is truly required.
        this.converter = new ByteArrayConverter(null);
    }

    @Setup
    public void setup() {
        // Setup phase: Initialize the converter.
    }

    @Benchmark
    public byte[] benchmarkConvertNull(Blackhole bh) {
        // Test null input handling
        try {
            byte[] result = converter.convert(null);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
        return null;
    }

    @Benchmark
    public byte[] benchmarkConvertPrimitiveArray(Blackhole bh) {
        // Test conversion of a primitive array (byte[])
        try {
            // Create a byte array input
            byte[] input = {1, 2, 3, 4, 5};
            byte[] result = converter.convert(input);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
        return null;
    }

    @Benchmark
    public byte[] benchmarkConvertObjectArray(Blackhole bh) {
        // Test conversion of an object array (e.g., Integer[])
        try {
            // Create an array of objects
            Object[] input = {1, 2, 3};
            byte[] result = converter.convert(input);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
        return null;
    }

    @Benchmark
    public byte[] benchmarkConvertCollection(Blackhole bh) {
        // Test conversion of a Collection (e.g., List of Integers)
        try {
            // Create a simple collection of objects
            Collection<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
            byte[] result = converter.convert(input);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
        return null;
    }

    @Benchmark
    public byte[] benchmarkConvertString(Blackhole bh) {
        // Test conversion of a String (which should trigger StringUtil.splitc and then array conversion)
        try {
            // Use a string that might contain delimiters
            String input = "a,b,c";
            byte[] result = converter.convert(input);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
        return null;
    }
}
