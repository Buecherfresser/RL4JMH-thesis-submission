package bench.generated.c052;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.sql.Timestamp;
import java.util.Date;

import jodd.typeconverter.impl.LocalDateTimeConverter;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class LocalDateTimeConverterBenchmark {

    private LocalDateTimeConverter converter;

    private LocalDate localDate;
    private Calendar calendar;
    private Timestamp timestamp;
    private Date date;
    private Long numberLong;
    private String isoString;
    private String millisString;

    @Setup
    public void setUp() {
        converter = new LocalDateTimeConverter();

        localDate = LocalDate.of(2023, 4, 5);

        calendar = new GregorianCalendar();
        calendar.set(2023, Calendar.APRIL, 5, 12, 34, 56);
        calendar.set(Calendar.MILLISECOND, 0);

        timestamp = new Timestamp(calendar.getTimeInMillis());

        date = new Date(calendar.getTimeInMillis());

        numberLong = calendar.getTimeInMillis();

        isoString = "2023-04-05T12:34:56";

        millisString = Long.toString(calendar.getTimeInMillis());
    }

    @Benchmark
    public LocalDateTime convertFromLocalDate() {
        return converter.convert(localDate);
    }

    @Benchmark
    public LocalDateTime convertFromCalendar() {
        return converter.convert(calendar);
    }

    @Benchmark
    public LocalDateTime convertFromTimestamp() {
        return converter.convert(timestamp);
    }

    @Benchmark
    public LocalDateTime convertFromDate() {
        return converter.convert(date);
    }

    @Benchmark
    public LocalDateTime convertFromNumber() {
        return converter.convert(numberLong);
    }

    @Benchmark
    public LocalDateTime convertFromIsoString() {
        return converter.convert(isoString);
    }

    @Benchmark
    public LocalDateTime convertFromMillisString() {
        return converter.convert(millisString);
    }
}
