package bench.generated.c027;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.time.TimeUtil;
import java.time.*;
import java.util.*;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class TimeUtilBenchmark {

    private LocalDate localDate;
    private LocalDateTime localDateTime;
    private ZoneId zoneId;
    private Calendar calendar;
    private Date javaDate;
    private long epochMillis;
    private String httpDateString;
    private String invalidHttpDateString;

    @Setup(Level.Trial)
    public void setUp() {
        zoneId = ZoneId.of("UTC");
        localDate = LocalDate.of(2020, 5, 15);
        localDateTime = LocalDateTime.of(2020, 5, 15, 12, 34, 56, 123_000_000);
        calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.set(2020, Calendar.MAY, 15, 12, 34, 56);
        calendar.set(Calendar.MILLISECOND, 123);
        javaDate = new Date(calendar.getTimeInMillis());
        epochMillis = javaDate.getTime();
        httpDateString = TimeUtil.formatHttpDate(epochMillis);
        invalidHttpDateString = "invalid-date";
    }

    @Benchmark
    public Date benchToDateFromLocalDate() {
        return TimeUtil.toDate(localDate);
    }

    @Benchmark
    public Date benchToDateFromLocalDateTime() {
        return TimeUtil.toDate(localDateTime);
    }

    @Benchmark
    public Calendar benchToCalendarFromLocalDateTime() {
        return TimeUtil.toCalendar(localDateTime);
    }

    @Benchmark
    public Calendar benchToCalendarFromLocalDate() {
        return TimeUtil.toCalendar(localDate);
    }

    @Benchmark
    public long benchToMillisecondsFromLocalDateTime() {
        return TimeUtil.toMilliseconds(localDateTime);
    }

    @Benchmark
    public long benchToMillisecondsFromLocalDateTimeWithZone() {
        return TimeUtil.toMilliseconds(localDateTime, zoneId);
    }

    @Benchmark
    public long benchToMillisecondsFromLocalDate() {
        return TimeUtil.toMilliseconds(localDate);
    }

    @Benchmark
    public LocalDateTime benchFromCalendar() {
        return TimeUtil.fromCalendar(calendar);
    }

    @Benchmark
    public LocalDateTime benchFromDate() {
        return TimeUtil.fromDate(javaDate);
    }

    @Benchmark
    public LocalDateTime benchFromMilliseconds() {
        return TimeUtil.fromMilliseconds(epochMillis);
    }

    @Benchmark
    public LocalDateTime benchFromMillisecondsWithZone() {
        return TimeUtil.fromMilliseconds(epochMillis, zoneId);
    }

    @Benchmark
    public String benchFormatHttpDate() {
        return TimeUtil.formatHttpDate(epochMillis);
    }

    @Benchmark
    public long benchParseHttpTimeValid() {
        return TimeUtil.parseHttpTime(httpDateString);
    }

    @Benchmark
    public long benchParseHttpTimeInvalid() {
        return TimeUtil.parseHttpTime(invalidHttpDateString);
    }
}
