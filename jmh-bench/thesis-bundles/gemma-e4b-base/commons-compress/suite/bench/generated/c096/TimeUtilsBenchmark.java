package bench.generated.c096;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Date;
import java.nio.file.attribute.FileTime;
import org.apache.commons.compress.utils.TimeUtils;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class TimeUtilsBenchmark {

    private FileTime validFileTime;
    private FileTime nullFileTime;
    private Date validDate;
    private Date nullDate;
    private long validUnixSeconds;
    private long validNtfsTime;
    private long validJavaTime;

    @Setup(Level.Trial)
    public void setup() {
        // Setup FileTime inputs
        validFileTime = FileTime.fromMillis(1672531200000L); // Jan 1, 2023
        nullFileTime = null;

        // Setup Date inputs
        validDate = new Date(1672531200000L);
        nullDate = null;

        // Setup long inputs
        validUnixSeconds = 1672531200L; // Unix timestamp in seconds
        validNtfsTime = 131107200000000000L; // Example NTFS time
        validJavaTime = 1672531200000L; // Java time in milliseconds
    }

    // --- isUnixTime(FileTime time) ---
    @Benchmark
    public void testIsUnixTimeFileTime(Blackhole bh) {
        bh.consume(TimeUtils.isUnixTime(validFileTime));
        bh.consume(TimeUtils.isUnixTime(nullFileTime));
    }

    // --- isUnixTime(long seconds) ---
    @Benchmark
    public void testIsUnixTimeLong(Blackhole bh) {
        bh.consume(TimeUtils.isUnixTime(validUnixSeconds));
    }

    // --- ntfsTimeToDate(long ntfsTime) ---
    @Benchmark
    public void testNtfsTimeToDate(Blackhole bh) {
        bh.consume(TimeUtils.ntfsTimeToDate(validNtfsTime));
    }

    // --- ntfsTimeToFileTime(long ntfsTime) ---
    @Benchmark
    public void testNtfsTimeToFileTime(Blackhole bh) {
        bh.consume(TimeUtils.ntfsTimeToFileTime(validNtfsTime));
    }

    // --- toDate(FileTime fileTime) ---
    @Benchmark
    public void testToDate(Blackhole bh) {
        bh.consume(TimeUtils.toDate(validFileTime));
        bh.consume(TimeUtils.toDate(nullFileTime));
    }

    // --- toFileTime(Date date) ---
    @Benchmark
    public void testToFileTime(Blackhole bh) {
        bh.consume(TimeUtils.toFileTime(validDate));
        bh.consume(TimeUtils.toFileTime(nullDate));
    }

    // --- toNtfsTime(Date date) ---
    @Benchmark
    public void testToNtfsTimeDate(Blackhole bh) {
        bh.consume(TimeUtils.toNtfsTime(validDate));
        bh.consume(TimeUtils.toNtfsTime(nullDate));
    }

    // --- toNtfsTime(FileTime fileTime) ---
    @Benchmark
    public void testToNtfsTimeFileTime(Blackhole bh) {
        bh.consume(TimeUtils.toNtfsTime(validFileTime));
        bh.consume(TimeUtils.toNtfsTime(nullFileTime));
    }

    // --- toNtfsTime(long javaTime) ---
    @Benchmark
    public void testToNtfsTimeLong(Blackhole bh) {
        bh.consume(TimeUtils.toNtfsTime(validJavaTime));
    }

    // --- toUnixTime(FileTime fileTime) ---
    @Benchmark
    public void testToUnixTime(Blackhole bh) {
        bh.consume(TimeUtils.toUnixTime(validFileTime));
        bh.consume(TimeUtils.toUnixTime(nullFileTime));
    }

    // --- truncateToHundredNanos(FileTime fileTime) ---
    @Benchmark
    public void testTruncateToHundredNanos(Blackhole bh) {
        bh.consume(TimeUtils.truncateToHundredNanos(validFileTime));
    }

    // --- unixTimeToFileTime(long time) ---
    @Benchmark
    public void testUnixTimeToFileTime(Blackhole bh) {
        bh.consume(TimeUtils.unixTimeToFileTime(validUnixSeconds));
    }
}
