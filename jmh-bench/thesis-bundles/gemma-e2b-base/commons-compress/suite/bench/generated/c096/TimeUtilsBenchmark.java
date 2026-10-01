package bench.generated.c096;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.commons.compress.utils.TimeUtils;
import org.apache.commons.io.file.attribute.FileTimes;
import java.nio.file.attribute.FileTime;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
public class TimeUtilsBenchmark {

    // Input data prepared in Setup
    private Date setupDate;
    private FileTime setupFileTime;
    private long setupUnixTimeSeconds;
    private long setupNtfsTime100Nano;

    @Setup
    public void setup() {
        // Setup a representative time point.
        // Let's use a date far from the epoch for robust testing.
        this.setupDate = new Date(1678886400000L); // March 15, 2023, 00:00:00 UTC
        this.setupFileTime = TimeUtils.toFileTime(setupDate);
        this.setupUnixTimeSeconds = setupDate.getTime() / 1000;
        
        // Calculate a corresponding NTFS time for testing conversion paths
        this.setupNtfsTime100Nano = TimeUtils.toNtfsTime(setupFileTime);
    }

    // --- Benchmarks for Date <-> FileTime conversions ---

    @Benchmark
    public void convertDateToFileTime(Blackhole bh) {
        FileTime result = TimeUtils.toFileTime(setupDate);
        bh.consume(result);
    }

    @Benchmark
    public void convertFileTimeToDate(Blackhole bh) {
        Date result = TimeUtils.toDate(setupFileTime);
        bh.consume(result);
    }

    // --- Benchmarks for Long (Unix/NTFS) <-> FileTime conversions ---

    @Benchmark
    public void convertUnixTimeToFileTime(Blackhole bh) {
        FileTime result = TimeUtils.unixTimeToFileTime(setupUnixTimeSeconds);
        bh.consume(result);
    }

    @Benchmark
    public void convertNtfsTimeToFileTime(Blackhole bh) {
        FileTime result = TimeUtils.ntfsTimeToFileTime(setupNtfsTime100Nano);
        bh.consume(result);
    }

    @Benchmark
    public void convertFileTimeToNtfsTime(Blackhole bh) {
        long result = TimeUtils.toNtfsTime(setupFileTime);
        bh.consume(result);
    }

    @Benchmark
    public void convertDateToNtfsTime(Blackhole bh) {
        long result = TimeUtils.toNtfsTime(setupDate);
        bh.consume(result);
    }

    // --- Benchmarks for Boolean checks ---

    @Benchmark
    public void isUnixTime_FileTime(Blackhole bh) {
        boolean result = TimeUtils.isUnixTime(setupFileTime);
        bh.consume(result);
    }

    @Benchmark
    public void isUnixTime_Long(Blackhole bh) {
        boolean result = TimeUtils.isUnixTime(setupUnixTimeSeconds);
        bh.consume(result);
    }

    // --- Benchmarks for Truncation ---

    @Benchmark
    public void truncateToHundredNanos(Blackhole bh) {
        FileTime result = TimeUtils.truncateToHundredNanos(setupFileTime);
        bh.consume(result);
    }
}
