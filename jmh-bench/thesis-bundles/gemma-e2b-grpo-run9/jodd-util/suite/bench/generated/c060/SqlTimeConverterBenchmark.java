package bench.generated.c060;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import jodd.typeconverter.impl.SqlTimeConverter;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SqlTimeConverterBenchmark {

    // The subject under test. Since SqlTimeConverter is stateless (all methods are static
    // or rely on static utilities), we don't need an instance field.
    private SqlTimeConverter converter;

    @Setup
    public void setup() {
        // Initialize the converter once per benchmark run (or trial, depending on JMH configuration)
        this.converter = new SqlTimeConverter();
    }

    @Benchmark
    public void benchmarkStringConversion(Blackhole bh) {
        try {
            // Test case 1: String that should be parsed as milliseconds (hits the string path)
            // This tests Long.parseLong and the subsequent Time constructor call.
            Time result = converter.convert("1678886400000");
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions for benchmarking purposes if they are expected in failure modes
        }
    }

    @Benchmark
    public void benchmarkNullConversion(Blackhole bh) {
        try {
            // Test case 2: Null input (should return null)
            Object result = converter.convert(null);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void benchmarkEmptyStringConversion(Blackhole bh) {
        try {
            // Test case 3: Empty string input (should hit string path, likely fail parsing or throw)
            // We expect this to throw TypeConversionException based on the implementation logic.
            converter.convert("");
        } catch (Exception e) {
            // Expected behavior for invalid input
        }
    }
}
