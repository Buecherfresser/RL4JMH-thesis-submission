package bench.generated.c038;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import jodd.typeconverter.impl.CharacterArrayConverter;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CharacterArrayConverterBenchmark {

    private CharacterArrayConverter converter;

    @Setup
    public void setup() {
        // Initialize the converter. Passing null for TypeConverterManager
        // as we are testing the conversion logic path itself.
        this.converter = new CharacterArrayConverter(null);
    }

    @Benchmark
    public void convert_Null(Blackhole bh) {
        // Test null input path
        bh.consume(converter.convert(null));
    }

    @Benchmark
    public void convert_CharSequence(Blackhole bh) {
        // Test CharSequence path (direct char array creation)
        try {
            // Use a simple string as CharSequence
            bh.consume(converter.convert("Hello World"));
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void convert_Collection(Blackhole bh) {
        // Test Collection path (e.g., List<String>)
        try {
            // Use a simple collection of Strings
            bh.consume(converter.convert(Arrays.asList("a", "b", "c")));
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void convert_PrimitiveArray_Int(Blackhole bh) {
        // Test the path where the input is an array of primitives (int[])
        try {
            // Create a new array instance for each benchmark run
            bh.consume(converter.convert(new int[]{1, 2, 3}));
        } catch (Exception e) {
            // Ignore
        }
    }

    @Benchmark
    public void convert_ObjectArray(Blackhole bh) {
        // Test the path where the input is an Object array (non-primitive)
        try {
            // Create a new array instance for each benchmark run
            bh.consume(converter.convert(new Object[]{1, "two", 3.0}));
        } catch (Exception e) {
            // Ignore
        }
    }
}
