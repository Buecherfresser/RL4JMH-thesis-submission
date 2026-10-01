package bench.generated.c096;

import org.apache.commons.compress.utils.TimeUtils;
import java.util.Date;
import java.nio.file.attribute.FileTime;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
public class TimeUtilsBenchmark {

    // Input data prepared in @Setup
    private Date setupDate;
    private FileTime setupFileTime;
    private long setupNTFSTime;
    private long setupUnixSeconds;

    @Setup
    public void setup() {
        // Setup representative inputs. Using values that are not trivially zero or epoch start.

        // 1. Date setup (e.g., a specific point in time)
        setupDate = new Date(1678886400000L); // March 15, 2023, 00:00:00 UTC

        // 2. FileTime setup (derived from the Date)
        setupFileTime = TimeUtils.toFileTime(setupDate);

        // 3. NTFS Time setup (derived from FileTime)
        setupNTFSTime = TimeUtils.toNtfsTime(setupFileTime);

        // 4. Unix Time setup (derived from FileTime)
        setupUnixSeconds = TimeUtils.toUnixTime(setupFileTime);
    }

    // --- Benchmarks for FileTime <-> Date conversions ---

    @Benchmark
    public void testFileTimeToDate(Blackhole bh) {
        Date result = TimeUtils.toDate(setupFileTime);
        bh.consume(result);
    }

    @Benchmark
    public void testDateToFileTime(Blackhole bh) {
        FileTime result = TimeUtils.toFileTime(setupDate);
        bh.consume(result);
    }

    // --- Benchmarks for FileTime <-> Unix Time conversions ---

    @Benchmark
    public void testFileTimeToUnixTime(Blackhole bh) {
        long result = TimeUtils.toUnixTime(setupFileTime);
        bh.consume(result);
    }

    @Benchmark
    public void testUnixTimeToFileTime(Blackhole bh) {
        FileTime result = TimeUtils.unixTimeToFileTime(setupUnixSeconds);
        bh.consume(result);
    }

    // --- Benchmarks for FileTime <-> NTFS Time conversions ---

    @Benchmark
    public void testFileTimeToNTFS(Blackhole bh) {
        long result = TimeUtils.toNtfsTime(setupFileTime);
        bh.consume(result);
    }

    @Benchmark
    public void testNTFSTimeToFileTime(Blackhole bh) {
        FileTime result = TimeUtils.ntfsTimeToFileTime(setupNTFSTime);
        bh.consume(result);
    }

    // --- Benchmarks for Date <-> NTFS Time conversions ---

    @Benchmark
    public void testDateToNTFS(Blackhole bh) {
        long result = TimeUtils.toNtfsTime(setupDate);
        bh.consume(result);
    }

    // --- Benchmarks for long (seconds) <-> NTFS Time conversions ---

    @Benchmark
    public void testSecondsToNTFS(Blackhole bh) {
        long result = TimeUtils.toNtfsTime(setupUnixSeconds);
        bh.consume(result);
    }

    @Benchmark
    public void testNTFSToDate(Blackhole bh) {
        // Fixed: ntfsTimeToDate returns Date, must consume Date
        Date result = TimeUtils.ntfsTimeToDate(setupNTFSTime);
        bh.consume(result);
    }

    // --- Benchmarks for long (seconds) <-> FileTime conversions ---

    @Benchmark
    public void testSecondsToFileTime(Blackhole bh) {
        FileTime result = TimeUtils.unixTimeToFileTime(setupUnixSeconds);
        bh.consume(result);
    }

    // --- Benchmarks for Truncation ---

    @Benchmark
    public void testTruncateToHundredNanos(Blackhole bh) {
        FileTime result = TimeUtils.truncateToHundredNanos(setupFileTime);
        bh.consume(result);
    }
}
