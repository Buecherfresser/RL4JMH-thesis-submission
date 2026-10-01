package bench.generated.c037;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import jodd.typeconverter.impl.CalendarConverter;

import java.util.Calendar;
import java.util.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import jodd.time.JulianDate;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CalendarConverterBenchmark {

    private CalendarConverter converter;
    private Calendar calendar;
    private Date date;
    private JulianDate julianDate;
    private LocalDateTime localDateTime;
    private LocalDate localDate;
    private Number number;
    private String digitString;
    private String dateTimeString;

    @Setup(Level.Trial)
    public void setUp() {
        converter = new CalendarConverter();
        calendar = Calendar.getInstance();
        date = new Date(1700000000000L);
        localDateTime = LocalDateTime.of(2023, 6, 15, 10, 30, 0);
        localDate = LocalDate.of(2023, 6, 15);
        julianDate = JulianDate.of(localDateTime);
        number = Long.valueOf(1700000000000L);
        digitString = "1700000000000";
        dateTimeString = "2023-06-15T10:30:00";
    }

    @Benchmark
    public void convertNull(Blackhole bh) {
        bh.consume(converter.convert(null));
    }

    @Benchmark
    public Calendar convertCalendar() {
        return converter.convert(calendar);
    }

    @Benchmark
    public Calendar convertDate() {
        return converter.convert(date);
    }

    @Benchmark
    public Calendar convertJulianDate() {
        return converter.convert(julianDate);
    }

    @Benchmark
    public Calendar convertLocalDateTime() {
        return converter.convert(localDateTime);
    }

    @Benchmark
    public Calendar convertLocalDate() {
        return converter.convert(localDate);
    }

    @Benchmark
    public Calendar convertNumber() {
        return converter.convert(number);
    }

    @Benchmark
    public Calendar convertStringDigits() {
        return converter.convert(digitString);
    }

    @Benchmark
    public Calendar convertStringDateTime() {
        return converter.convert(dateTimeString);
    }
}
