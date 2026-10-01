package bench.generated.c053;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

import jodd.typeconverter.impl.LocalTimeConverter;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class LocalTimeConverterBenchmark {

    // Instance of the class under test. Since the method is non-static, we need an instance.
    private LocalTimeConverter converter;

    // State fields for read-only inputs (avoiding static final literals)
    private LocalDateTime localDateTime;
    private Calendar calendar;
    private Date date;
    private Number number;
    private String stringValue;

    @Setup
    public void setup() {
        // Initialize the converter instance
        this.converter = new LocalTimeConverter();

        // Initialize read-only inputs. These are complex objects, but we only need them once.
        this.localDateTime = LocalDateTime.now();
        this.calendar = Calendar.getInstance();
        this.date = new Date();
        this.number = 123456789L;
        this.stringValue = "14:30:00"; // A string that should parse directly
    }

    @Benchmark
    public void benchmarkConvert_LocalDateTime(Blackhole bh) {
        try {
            LocalTime result = converter.convert(localDateTime);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions for benchmarking purposes if they are expected to be rare/handled
        }
    }

    @Benchmark
    public void benchmarkConvert_Calendar(Blackhole bh) {
        try {
            LocalTime result = converter.convert(calendar);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void benchmarkConvert_Date(Blackhole bh) {
        try {
            LocalTime result = converter.convert(date);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void benchmarkConvert_Number(Blackhole bh) {
        try {
            // Test conversion via Number (long value)
            LocalTime result = converter.convert(number);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void benchmarkConvert_String(Blackhole bh) {
        try {
            // Test conversion via String (should parse directly)
            LocalTime result = converter.convert(stringValue);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }
}
