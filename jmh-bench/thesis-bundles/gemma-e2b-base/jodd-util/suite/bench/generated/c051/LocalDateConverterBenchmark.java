package bench.generated.c051;

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
import jodd.time.TimeUtil;
import jodd.typeconverter.impl.LocalDateConverter;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class LocalDateConverterBenchmark {

    private LocalDateConverter converter;

    // --- Setup State ---
    // Inputs for different branches of the convert method

    // 1. LocalDateTime input
    private LocalDateTime localDateTimeInput;

    // 2. Date input
    private Date dateInput;

    // 3. Timestamp input
    private Timestamp timestampInput;

    // 4. Calendar input
    private Calendar calendarInput;

    // 5. Number input (Long)
    private Long numberInput;

    // 6. String input (Valid date format, relies on LocalDate.parse)
    private String validDateStringInput;

    // 7. String input (Numeric string, relies on Long.parseLong)
    private String numericStringInput;

    // 8. LocalTime input (Triggers exception)
    private LocalTime localTimeInput;

    // 9. Null input
    private Object nullInput;


    @Setup
    public void setup() {
        converter = new LocalDateConverter();

        // Setup 1: LocalDateTime
        this.localDateTimeInput = LocalDateTime.of(2023, 10, 27, 10, 30);

        // Setup 2: Date
        this.dateInput = new Date(1698364800000L); // Example timestamp converted to Date

        // Setup 3: Timestamp
        this.timestampInput = new Timestamp(1698364800000L);

        // Setup 4: Calendar
        this.calendarInput = Calendar.getInstance();
        this.calendarInput.set(2023, Calendar.OCTOBER, 27, 10, 30, 0);

        // Setup 5: Number (Long)
        this.numberInput = 1698364800000L;

        // Setup 6: Valid Date String
        this.validDateStringInput = "2023-10-27";

        // Setup 7: Numeric String
        this.numericStringInput = "1698364800000";

        // Setup 8: LocalTime (Triggers exception)
        this.localTimeInput = LocalTime.of(10, 30);

        // Setup 9: Null
        this.nullInput = null;
    }

    @Benchmark
    public void convert_LocalDateTime(Blackhole bh) {
        LocalDate result = converter.convert(localDateTimeInput);
        bh.consume(result);
    }

    @Benchmark
    public void convert_Date(Blackhole bh) {
        LocalDate result = converter.convert(dateInput);
        bh.consume(result);
    }

    @Benchmark
    public void convert_Timestamp(Blackhole bh) {
        LocalDate result = converter.convert(timestampInput);
        bh.consume(result);
    }

    @Benchmark
    public void convert_Calendar(Blackhole bh) {
        LocalDate result = converter.convert(calendarInput);
        bh.consume(result);
    }

    @Benchmark
    public void convert_Number(Blackhole bh) {
        // Testing Number branch (using Long)
        LocalDate result = converter.convert(numberInput);
        bh.consume(result);
    }

    @Benchmark
    public void convert_ValidDateString(Blackhole bh) {
        // Testing String branch (parseable date)
        LocalDate result = converter.convert(validDateStringInput);
        bh.consume(result);
    }

    @Benchmark
    public void convert_NumericString(Blackhole bh) {
        // Testing String branch (numeric parsing)
        LocalDate result = converter.convert(numericStringInput);
        bh.consume(result);
    }

    @Benchmark
    public void convert_LocalTime_ThrowsException(Blackhole bh) {
        // Testing the path that throws TypeConversionException
        try {
            converter.convert(localTimeInput);
        } catch (TypeConversionException e) {
            // Expected path, consume nothing if exception is thrown
        }
        bh.consume(null); // Consume null as the return value is not applicable here
    }

    @Benchmark
    public void convert_Null(Blackhole bh) {
        // Testing null handling
        LocalDate result = converter.convert(nullInput);
        bh.consume(result);
    }
}
