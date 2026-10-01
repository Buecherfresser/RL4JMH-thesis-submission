package bench.generated.c053;

import jodd.typeconverter.impl.LocalTimeConverter;
import jodd.typeconverter.TypeConverter;
import jodd.typeconverter.TypeConversionException;
import jodd.time.TimeUtil;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class LocalTimeConverterBenchmark {

    private LocalTimeConverter converter;

    // --- Setup State Fields ---

    // Path 1: LocalDateTime input
    private LocalDateTime localDateTimeInput;

    // Path 2: Calendar input
    private Calendar calendarInput;

    // Path 3: Timestamp input
    private Timestamp timestampInput;

    // Path 4: Date input
    private Date dateInput;

    // Path 5: Number input (Long)
    private Long numberInput;

    // Path 6: String input (Valid millisecond string)
    private String validMillisStringInput;

    // Path 7: String input (Invalid format, should default to LocalTime.parse)
    private String invalidStringInput;

    // Path 8: LocalDate input (Expected to throw exception)
    private LocalDate localDateInput;

    // Path 9: Null input
    private Object nullInput;

    @Setup
    public void setup() {
        converter = new LocalTimeConverter();

        // Path 1: LocalDateTime
        localDateTimeInput = LocalDateTime.of(2023, 10, 27, 14, 30);

        // Path 2: Calendar
        calendarInput = Calendar.getInstance();
        calendarInput.set(2023, Calendar.OCTOBER, 27, 14, 30, 0);

        // Path 3: Timestamp (FIXED: Using Instant.from() to create Timestamp)
        Instant instant = Instant.ofEpochSecond(1678886400L);
        timestampInput = Timestamp.from(instant);

        // Path 4: Date
        dateInput = new Date(1678886400000L);

        // Path 5: Number (Long)
        numberInput = 1678886400000L;

        // Path 6: Valid String (milliseconds)
        validMillisStringInput = "1678886400000";

        // Path 7: Invalid String (should trigger LocalTime.parse)
        invalidStringInput = "2023-10-27";

        // Path 8: LocalDate
        localDateInput = LocalDate.of(2023, 10, 27);

        // Path 9: Null
        nullInput = null;
    }

    @Benchmark
    public void convert_LocalDateTime(Blackhole bh) {
        LocalTime result = converter.convert(localDateTimeInput);
        bh.consume(result);
    }

    @Benchmark
    public void convert_Calendar(Blackhole bh) {
        LocalTime result = converter.convert(calendarInput);
        bh.consume(result);
    }

    @Benchmark
    public void convert_Timestamp(Blackhole bh) {
        LocalTime result = converter.convert(timestampInput);
        bh.consume(result);
    }

    @Benchmark
    public void convert_Date(Blackhole bh) {
        LocalTime result = converter.convert(dateInput);
        bh.consume(result);
    }

    @Benchmark
    public void convert_Number(Blackhole bh) {
        LocalTime result = converter.convert(numberInput);
        bh.consume(result);
    }

    @Benchmark
    public void convert_String_Valid(Blackhole bh) {
        LocalTime result = converter.convert(validMillisStringInput);
        bh.consume(result);
    }

    @Benchmark
    public void convert_String_Invalid(Blackhole bh) {
        LocalTime result = converter.convert(invalidStringInput);
        bh.consume(result);
    }

    @Benchmark
    public void convert_LocalDate_ThrowsException(Blackhole bh) {
        try {
            converter.convert(localDateInput);
        } catch (TypeConversionException e) {
            // Expected behavior
        }
        bh.consume(null);
    }

    @Benchmark
    public void convert_Null(Blackhole bh) {
        LocalTime result = converter.convert(nullInput);
        bh.consume(result);
    }
}
