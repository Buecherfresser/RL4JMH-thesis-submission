package bench.generated.c096;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.utils.TimeUtils;
import java.nio.file.attribute.FileTime;
import java.util.Date;
import java.time.Instant;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class TimeUtilsBenchmark {

    private FileTime fileTime;
    private Date date;
    private long ntfsTime;
    private long javaTime;
    private long unixSeconds;

    @Setup(Level.Trial)
    public void setUp() {
        javaTime = System.currentTimeMillis();
        fileTime = FileTime.from(Instant.ofEpochMilli(javaTime));
        date = new Date(javaTime);
        // NTFS time = (javaTime + offset) * 10_000 (100‑nanosecond units)
        ntfsTime = (javaTime + 11644473600000L) * 10_000L;
        unixSeconds = javaTime / 1000L;
    }

    @Benchmark
    public boolean benchmarkIsUnixTimeFileTime() {
        return TimeUtils.isUnixTime(fileTime);
    }

    @Benchmark
    public boolean benchmarkIsUnixTimeLong() {
        return TimeUtils.isUnixTime(unixSeconds);
    }

    @Benchmark
    public Date benchmarkNtfsTimeToDate() {
        return TimeUtils.ntfsTimeToDate(ntfsTime);
    }

    @Benchmark
    public FileTime benchmarkNtfsTimeToFileTime() {
        return TimeUtils.ntfsTimeToFileTime(ntfsTime);
    }

    @Benchmark
    public Date benchmarkToDate() {
        return TimeUtils.toDate(fileTime);
    }

    @Benchmark
    public FileTime benchmarkToFileTime() {
        return TimeUtils.toFileTime(date);
    }

    @Benchmark
    public long benchmarkToNtfsTimeFromDate() {
        return TimeUtils.toNtfsTime(date);
    }

    @Benchmark
    public long benchmarkToNtfsTimeFromFileTime() {
        return TimeUtils.toNtfsTime(fileTime);
    }

    @Benchmark
    public long benchmarkToNtfsTimeFromLong() {
        return TimeUtils.toNtfsTime(javaTime);
    }

    @Benchmark
    public long benchmarkToUnixTime() {
        return TimeUtils.toUnixTime(fileTime);
    }

    @Benchmark
    public FileTime benchmarkTruncateToHundredNanos() {
        return TimeUtils.truncateToHundredNanos(fileTime);
    }

    @Benchmark
    public FileTime benchmarkUnixTimeToFileTime() {
        return TimeUtils.unixTimeToFileTime(unixSeconds);
    }
}
