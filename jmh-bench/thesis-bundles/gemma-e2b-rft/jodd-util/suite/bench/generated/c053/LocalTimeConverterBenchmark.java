package bench.generated.c053;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import jodd.typeconverter.TypeConversionException;
import jodd.typeconverter.TypeConverter;
import jodd.typeconverter.impl.LocalTimeConverter;
import jodd.time.TimeUtil;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class LocalTimeConverterBenchmark {

    private LocalTimeConverter converter;

    // --- State Fields for Inputs ---

    // 1. LocalDateTime input
    private LocalDateTime localDateTimeInput;

    // 2. Calendar input
    private Calendar calendarInput;

    // 3. Timestamp input
    private Timestamp timestampInput;

    // 4. Date input
    private Date dateInput;

    // 5. Number input (Long)
    private Long numberInput;

    // 6. String input (Numeric, parsable by Long.parseLong)
    private String numericStringInput;

    // 7. String input (Standard LocalTime format, parsable by LocalTime.parse)
    private String timeStringInput;

    // 8. LocalDate input (to test exception path)
    private LocalDate dateInputForException;

    // --- Setup ---

    @Setup
    public void setup() {
        converter = new LocalTimeConverter();

        // Setup 1: LocalDateTime
        this.localDateTimeInput = LocalDateTime.of(2023, 10, 27, 14, 30, 0);

        // Setup 2: Calendar
        this.calendarInput = Calendar.getInstance();
        this.calendarInput.set(2023, Calendar.OCTOBER, 27, 14, 30, 0);

        // Setup 3: Timestamp
        long timestampMillis = System.currentTimeMillis();
        this.timestampInput = new Timestamp(timestampMillis);

        // Setup 4: Date
        this.dateInput = new Date(System.currentTimeMillis());

        // Setup 5: Number
        this.numberInput = 1678886400000L; // A large millisecond value

        // Setup 6: Numeric String (milliseconds)
        this.numericStringInput = "1678886400000";

        // Setup 7: Time String (LocalTime format)
        this.timeStringInput = "14:30:00";

        // Setup 8: LocalDate (for exception testing)
        this.dateInputForException = LocalDate.of(2023, 10, 27);
    }

    // --- Benchmarks ---

    @Benchmark
    public void benchmarkConvert_LocalDateTime(Blackhole bh) {
        LocalTime result = converter.convert(localDateTimeInput);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkConvert_Calendar(Blackhole bh) {
        LocalTime result = converter.convert(calendarInput);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkConvert_Timestamp(Blackhole bh) {
        LocalTime result = converter.convert(timestampInput);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkConvert_Date(Blackhole bh) {
        LocalTime result = converter.convert(dateInput);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkConvert_Number(Blackhole bh) {
        LocalTime result = converter.convert(numberInput);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkConvert_NumericString(Blackhole bh) {
        LocalTime result = converter.convert(numericStringInput);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkConvert_TimeString(Blackhole bh) {
        LocalTime result = converter.convert(timeStringInput);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkConvert_LocalDate_ThrowsException(Blackhole bh) {
        // This benchmark tests the path that throws TypeConversionException
        try {
            converter.convert(dateInputForException);
        } catch (TypeConversionException e) {
            // Expected behavior
        }
        bh.consume(null); // Consume null as the return value is not applicable here
    }
}
