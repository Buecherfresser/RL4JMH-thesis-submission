package bench.generated.c063;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Arrays;
import java.sql.Clob;
import java.sql.SQLException;

import jodd.typeconverter.impl.StringConverter;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class StringConverterBenchmark {

    private StringConverter converter;

    @Setup
    public void setup() {
        // Initialize the subject under test. Since it's stateless,
        // creating an instance here is fine for benchmarking.
        this.converter = new StringConverter();
    }

    @Benchmark
    public String convert_Null(Blackhole bh) {
        // Test null handling
        return converter.convert(null);
    }

    @Benchmark
    public String convert_String(Blackhole bh) {
        // Test CharSequence handling (String)
        return converter.convert("hello world");
    }

    @Benchmark
    public String convert_Class(Blackhole bh) {
        // Test Class handling
        return converter.convert(String.class);
    }

    @Benchmark
    public String convert_IntArray(Blackhole bh) {
        // Test int[] array conversion
        int[] data = {1, 2, 3, 4, 5};
        return converter.convert(data);
    }

    @Benchmark
    public String convert_LongArray(Blackhole bh) {
        // Test long[] array conversion
        long[] data = {100L, 200L, 300L};
        return converter.convert(data);
    }

    @Benchmark
    public String convert_ByteArray(Blackhole bh) {
        // Test byte[] array conversion
        byte[] data = {1, 2, 3, 4, 5};
        return converter.convert(data);
    }

    @Benchmark
    public String convert_BooleanArray(Blackhole bh) {
        // Test boolean[] array conversion
        boolean[] data = {true, false, true};
        return converter.convert(data);
    }

    @Benchmark
    public String convert_Object(Blackhole bh) {
        // Test fallback path (Object -> toString())
        try {
            // Use an object that doesn't match specific types
            return converter.convert(new Object());
        } catch (Exception e) {
            // Ignore exceptions for benchmark stability if they occur during setup/teardown
            return null;
        }
    }
}
