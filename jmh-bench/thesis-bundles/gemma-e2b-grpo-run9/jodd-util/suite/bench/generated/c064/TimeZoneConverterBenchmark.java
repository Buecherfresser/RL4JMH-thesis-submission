package bench.generated.c064;

import jodd.typeconverter.impl.TimeZoneConverter;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class TimeZoneConverterBenchmark {

    // The subject under test.
    private TimeZoneConverter converter;

    @Setup
    public void setup() {
        // Initialize the converter once per benchmark run
        this.converter = new TimeZoneConverter();
    }

    @Benchmark
    public void testConvertNull(Blackhole bh) {
        // Test case 1: Null input (fast path)
        TimeZone result = converter.convert(null);
        bh.consume(result);
    }

    @Benchmark
    public void testConvertTimeZone(Blackhole bh) {
        // Test case 2: TimeZone object input (fast path)
        TimeZone tz = TimeZone.getTimeZone("UTC");
        TimeZone result = converter.convert(tz);
        bh.consume(result);
    }

    @Benchmark
    public void testConvertString(Blackhole bh) {
        // Test case 3: Standard object input (path involving toString() and TimeZone.getTimeZone())
        // Using a simple String object to test the general conversion path.
        String input = "Some arbitrary string value";
        TimeZone result = converter.convert(input);
        bh.consume(result);
    }
}
