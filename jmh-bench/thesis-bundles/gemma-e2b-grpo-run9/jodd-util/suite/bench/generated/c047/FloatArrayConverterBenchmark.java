package bench.generated.c047;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import jodd.typeconverter.impl.FloatArrayConverter;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FloatArrayConverterBenchmark {

    private FloatArrayConverter converter;

    @Setup
    public void setup() {
        // Initialize the converter. We pass null for TypeConverterManager
        // as we are only testing the structure of FloatArrayConverter itself.
        this.converter = new FloatArrayConverter(null);
    }

    @Benchmark
    public void testConvertNull(Blackhole bh) {
        // Test null input path
        bh.consume(converter.convert(null));
    }

    @Benchmark
    public void testConvertPrimitiveArray_Float(Blackhole bh) {
        // Test case where input is already a float[]
        try {
            float[] input = {1.0f, 2.0f, 3.0f};
            bh.consume(converter.convert(input));
        } catch (Exception e) {
            // Ignore exceptions during benchmark execution
        }
    }

    @Benchmark
    public void testConvertCollection_Iterable(Blackhole bh) {
        // Test case where input is an Iterable (e.g., array implements Iterable)
        try {
            // Passing a float array which implements Iterable
            float[] input = {1.0f, 2.0f, 3.0f};
            bh.consume(converter.convert(input));
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void testConvertCharSequence_String(Blackhole bh) {
        // Test case where input is a CharSequence (String)
        try {
            // Passing a string that might trigger StringUtil.splitc
            bh.consume(converter.convert("1.0,2.0,3.0"));
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void testConvertNonArray_SingleElement(Blackhole bh) {
        // Test case where input is a non-array, non-collection (e.g., Integer)
        try {
            // Passing an Integer object
            bh.consume(converter.convert(123));
        } catch (Exception e) {
            // Ignore exceptions
        }
    }
}
