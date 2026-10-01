package bench.generated.c043;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.DateConverter;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import jodd.time.JulianDate;
import java.time.LocalDateTime;
import java.time.LocalDate;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DateConverterBenchmark {

    private DateConverter converter;

    private Date dateInput;
    private Calendar calendarInput;
    private JulianDate julianDateInput;
    private LocalDateTime localDateTimeInput;
    private LocalDate localDateInput;
    private Number numberInput;
    private String numericStringInput;
    private String isoStringInput;

    @Setup(Level.Trial)
    public void setup() {
        converter = new DateConverter();

        long epochMillis = 1622547800000L; // 2021-06-01T12:30:00Z
        dateInput = new Date(epochMillis);

        calendarInput = new GregorianCalendar();
        calendarInput.setTimeInMillis(epochMillis);

        // JulianDate: year 2021, day 152.5 (approx June 1)
        julianDateInput = new JulianDate(2021, 152.5);

        localDateTimeInput = LocalDateTime.of(2021, 6, 1, 12, 34, 56);
        localDateInput = LocalDate.of(2021, 6, 1);

        numberInput = Long.valueOf(epochMillis);

        numericStringInput = Long.toString(epochMillis);
        isoStringInput = "2021-06-01T12:34:56";
    }

    @Benchmark
    public Date benchmarkNull() {
        return converter.convert(null);
    }

    @Benchmark
    public Date benchmarkDate() {
        return converter.convert(dateInput);
    }

    @Benchmark
    public Date benchmarkCalendar() {
        return converter.convert(calendarInput);
    }

    @Benchmark
    public Date benchmarkJulianDate() {
        return converter.convert(julianDateInput);
    }

    @Benchmark
    public Date benchmarkLocalDateTime() {
        return converter.convert(localDateTimeInput);
    }

    @Benchmark
    public Date benchmarkLocalDate() {
        return converter.convert(localDateInput);
    }

    @Benchmark
    public Date benchmarkNumber() {
        return converter.convert(numberInput);
    }

    @Benchmark
    public Date benchmarkNumericString() {
        return converter.convert(numericStringInput);
    }

    @Benchmark
    public Date benchmarkIsoString() {
        return converter.convert(isoStringInput);
    }
}
