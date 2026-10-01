package bench.generated.c037;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.CalendarConverter;
import java.util.Calendar;
import java.util.Date;
import jodd.time.JulianDate;
import java.time.LocalDateTime;
import java.time.LocalDate;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CalendarConverterBenchmark {

    private CalendarConverter converter;

    private Calendar calendarInput;
    private Date dateInput;
    private JulianDate julianDateInput;
    private LocalDateTime localDateTimeInput;
    private LocalDate localDateInput;
    private Long numberInput;
    private String numericStringInput;
    private String isoStringInput;

    @Setup(Level.Trial)
    public void setup() {
        converter = new CalendarConverter();

        long now = System.currentTimeMillis();

        calendarInput = Calendar.getInstance();
        calendarInput.setTimeInMillis(now);

        dateInput = new Date(now);

        localDateTimeInput = LocalDateTime.now();
        localDateInput = LocalDate.now();

        julianDateInput = JulianDate.of(localDateTimeInput);

        numberInput = now;
        numericStringInput = Long.toString(now);
        isoStringInput = localDateTimeInput.toString();
    }

    @Benchmark
    public Calendar benchmarkConvertFromCalendar() {
        return converter.convert(calendarInput);
    }

    @Benchmark
    public Calendar benchmarkConvertFromDate() {
        return converter.convert(dateInput);
    }

    @Benchmark
    public Calendar benchmarkConvertFromJulianDate() {
        return converter.convert(julianDateInput);
    }

    @Benchmark
    public Calendar benchmarkConvertFromLocalDateTime() {
        return converter.convert(localDateTimeInput);
    }

    @Benchmark
    public Calendar benchmarkConvertFromLocalDate() {
        return converter.convert(localDateInput);
    }

    @Benchmark
    public Calendar benchmarkConvertFromNumber() {
        return converter.convert(numberInput);
    }

    @Benchmark
    public Calendar benchmarkConvertFromNumericString() {
        return converter.convert(numericStringInput);
    }

    @Benchmark
    public Calendar benchmarkConvertFromIsoString() {
        return converter.convert(isoStringInput);
    }

    @Benchmark
    public Calendar benchmarkConvertNull() {
        return converter.convert(null);
    }
}
