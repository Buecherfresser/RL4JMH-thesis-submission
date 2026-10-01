package bench.generated.c053;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.LocalTimeConverter;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.LocalDate;
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
public class LocalTimeConverterBenchmark {

    private LocalTimeConverter converter;
    private LocalDateTime localDateTime;
    private Calendar calendar;
    private Timestamp timestamp;
    private Date date;
    private Long number;
    private String numericString;
    private String timeString;
    private LocalDate localDate;

    @Setup(Level.Trial)
    public void setup() {
        converter = new LocalTimeConverter();
        localDateTime = LocalDateTime.of(2023, 1, 2, 12, 34, 56);
        calendar = new GregorianCalendar();
        calendar.setTimeInMillis(123456789L);
        timestamp = new Timestamp(123456789L);
        date = new Date(123456789L);
        number = 123456789L;
        numericString = "123456789";
        timeString = "12:34:56";
        localDate = LocalDate.of(2023, 1, 2);
    }

    @Benchmark
    public LocalTime benchmarkFromLocalDateTime() {
        return converter.convert(localDateTime);
    }

    @Benchmark
    public LocalTime benchmarkFromCalendar() {
        return converter.convert(calendar);
    }

    @Benchmark
    public LocalTime benchmarkFromTimestamp() {
        return converter.convert(timestamp);
    }

    @Benchmark
    public LocalTime benchmarkFromDate() {
        return converter.convert(date);
    }

    @Benchmark
    public LocalTime benchmarkFromNumber() {
        return converter.convert(number);
    }

    @Benchmark
    public LocalTime benchmarkFromNumericString() {
        return converter.convert(numericString);
    }

    @Benchmark
    public LocalTime benchmarkFromTimeString() {
        return converter.convert(timeString);
    }

    @Benchmark
    public void benchmarkFromLocalDate(Blackhole bh) {
        try {
            bh.consume(converter.convert(localDate));
        } catch (Exception e) {
            bh.consume(e);
        }
    }
}
