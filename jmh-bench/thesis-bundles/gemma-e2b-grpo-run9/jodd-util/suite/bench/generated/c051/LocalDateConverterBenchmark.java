package bench.generated.c051;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Calendar;

// Assuming LocalDateConverter is accessible via its package structure or imported correctly.
// Since we cannot rely on external classpath setup, we must assume the necessary imports
// for jodd.* are resolved by the execution environment.
import jodd.typeconverter.impl.LocalDateConverter;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class LocalDateConverterBenchmark {

    private LocalDateConverter converter;

    @Setup
    public void setup() {
        // Initialize the converter instance. Since the method is non-static,
        // we need an instance.
        this.converter = new LocalDateConverter();
    }

    @Benchmark
    public void convert_LocalDateTime(Blackhole bh) {
        try {
            // Test conversion from LocalDateTime
            LocalDate result = converter.convert(LocalDateTime.now());
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions for benchmarking purposes if they are expected in some paths
        }
    }

    @Benchmark
    public void convert_Date(Blackhole bh) {
        try {
            // Test conversion from java.util.Date
            // Note: Creating a new Date object here avoids potential state issues if Date were mutable,
            // though Date is generally mutable.
            java.util.Date date = new java.util.Date();
            LocalDate result = converter.convert(date);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void convert_Number(Blackhole bh) {
        try {
            // Test conversion from a Number (Long)
            // We use a fixed value to avoid relying on system time/state changes
            Long number = 1672531200000L; // Example timestamp in milliseconds
            LocalDate result = converter.convert(number);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void convert_Null(Blackhole bh) {
        try {
            // Test null handling
            LocalDate result = converter.convert(null);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void convert_String_Digits(Blackhole bh) {
        try {
            // Test conversion from a string that looks like a timestamp (should parse successfully)
            String digitString = "1672531200000";
            LocalDate result = converter.convert(digitString);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void convert_String_Invalid(Blackhole bh) {
        try {
            // Test conversion from a string that should fail parsing (should throw TypeConversionException)
            String invalidString = "not_a_date_format";
            converter.convert(invalidString);
            // If execution reaches here, the benchmark failed to throw the expected exception
        } catch (Exception e) {
            // Expected behavior for invalid input
        }
    }
}
