package bench.generated.c051;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.LocalDateConverter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.sql.Timestamp;
import java.util.Date;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class LocalDateConverterBenchmark {

    private LocalDateConverter converter;

    private LocalDateTime ldtInput;
    private Calendar calendarInput;
    private Timestamp timestampInput;
    private Date dateInput;
    private Long numberInput;
    private String isoStringInput;
    private String numericStringInput;

    @Setup(Level.Trial)
    public void setUp() {
        converter = new LocalDateConverter();

        // 2023-08-12T15:30:45
        ldtInput = LocalDateTime.of(2023, 8, 12, 15, 30, 45);

        // Calendar set to epoch milliseconds for 2023-08-12
        calendarInput = new GregorianCalendar();
        calendarInput.setTimeInMillis(1691846400000L);

        // Timestamp for the same instant
        timestampInput = new Timestamp(1691846400000L);

        // Date for the same instant
        dateInput = new Date(1691846400000L);

        // Numeric milliseconds
        numberInput = 1691846400000L;

        // ISO-8601 date string
        isoStringInput = "2023-08-12";

        // Numeric string representing milliseconds
        numericStringInput = "1691846400000";
    }

    @Benchmark
    public LocalDate benchmarkConvertFromLocalDateTime() {
        return converter.convert(ldtInput);
    }

    @Benchmark
    public LocalDate benchmarkConvertFromCalendar() {
        return converter.convert(calendarInput);
    }

    @Benchmark
    public LocalDate benchmarkConvertFromTimestamp() {
        return converter.convert(timestampInput);
    }

    @Benchmark
    public LocalDate benchmarkConvertFromDate() {
        return converter.convert(dateInput);
    }

    @Benchmark
    public LocalDate benchmarkConvertFromNumber() {
        return converter.convert(numberInput);
    }

    @Benchmark
    public LocalDate benchmarkConvertFromIsoString() {
        return converter.convert(isoStringInput);
    }

    @Benchmark
    public LocalDate benchmarkConvertFromNumericString() {
        return converter.convert(numericStringInput);
    }
}
