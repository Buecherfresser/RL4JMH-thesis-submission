package bench.generated.c027;

import jodd.time.TimeUtil;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
@Threads(1) // SimpleDateFormat in TimeUtil is not thread-safe
public class TimeUtilBenchmark {

    private LocalDate localDate;
    private LocalDateTime localDateTime;
    private Date date;
    private Calendar calendar;
    private long millis;
    private String httpDate;
    private ZoneId zoneId;

    @Setup(Level.Trial)
    public void setup() {
        localDate = LocalDate.of(2023, 5, 15);
        localDateTime = LocalDateTime.of(2023, 5, 15, 10, 30, 45);
        date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        calendar = GregorianCalendar.from(ZonedDateTime.of(localDateTime, ZoneId.systemDefault()));
        millis = localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        httpDate = "Mon, 15 May 2023 10:30:45 GMT";
        zoneId = ZoneId.of("America/New_York");
    }

    @Benchmark
    public Date toDateLocalDate() {
        return TimeUtil.toDate(localDate);
    }

    @Benchmark
    public Date toDateLocalDateTime() {
        return TimeUtil.toDate(localDateTime);
    }

    @Benchmark
    public Calendar toCalendarLocalDateTime() {
        return TimeUtil.toCalendar(localDateTime);
    }

    @Benchmark
    public Calendar toCalendarLocalDate() {
        return TimeUtil.toCalendar(localDate);
    }

    @Benchmark
    public long toMillisecondsLocalDateTime() {
        return TimeUtil.toMilliseconds(localDateTime);
    }

    @Benchmark
    public long toMillisecondsLocalDateTimeZone() {
        return TimeUtil.toMilliseconds(localDateTime, zoneId);
    }

    @Benchmark
    public long toMillisecondsLocalDate() {
        return TimeUtil.toMilliseconds(localDate);
    }

    @Benchmark
    public LocalDateTime fromCalendar() {
        return TimeUtil.fromCalendar(calendar);
    }

    @Benchmark
    public LocalDateTime fromDate() {
        return TimeUtil.fromDate(date);
    }

    @Benchmark
    public LocalDateTime fromMilliseconds() {
        return TimeUtil.fromMilliseconds(millis);
    }

    @Benchmark
    public LocalDateTime fromMillisecondsZone() {
        return TimeUtil.fromMilliseconds(millis, zoneId);
    }

    @Benchmark
    public String formatHttpDate() {
        return TimeUtil.formatHttpDate(millis);
    }

    @Benchmark
    public long parseHttpTime() {
        return TimeUtil.parseHttpTime(httpDate);
    }
}
