package bench.generated.c027;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import jodd.time.TimeUtil;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class TimeUtilBenchmark {

    // Since TimeUtil is static and stateless, no instance fields are strictly required.
    // We rely on creating fresh, immutable objects inside the benchmark methods.

    @Benchmark
    public void benchmarkToMilliseconds_LocalDateTime_SystemDefault(Blackhole bh) {
        // Test toMilliseconds(LocalDateTime localDateTime)
        LocalDateTime localDateTime = LocalDateTime.now();
        long result = TimeUtil.toMilliseconds(localDateTime);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkToMilliseconds_LocalDateTime_SpecificZone(Blackhole bh) {
        // Test toMilliseconds(LocalDateTime localDateTime, ZoneId zoneId)
        LocalDateTime localDateTime = LocalDateTime.now();
        ZoneId zoneId = ZoneId.of("America/New_York");
        long result = TimeUtil.toMilliseconds(localDateTime, zoneId);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkToMilliseconds_LocalDate(Blackhole bh) {
        // Test toMilliseconds(LocalDate localDate)
        LocalDate localDate = LocalDate.now();
        long result = TimeUtil.toMilliseconds(localDate);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkFormatHttpDate(Blackhole bh) {
        // Test formatHttpDate(long millis)
        long millis = System.currentTimeMillis();
        String result = TimeUtil.formatHttpDate(millis);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkFromMilliseconds_NoZone(Blackhole bh) {
        // Test fromMilliseconds(long milliseconds)
        long milliseconds = System.currentTimeMillis();
        try {
            // This method relies on ZoneId.systemDefault() internally
            java.time.LocalDateTime result = TimeUtil.fromMilliseconds(milliseconds);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions for benchmarking purposes if they are expected to be handled internally
        }
    }

    @Benchmark
    public void benchmarkFromMilliseconds_SpecificZone(Blackhole bh) {
        // Test fromMilliseconds(long milliseconds, ZoneId timeZone)
        long milliseconds = System.currentTimeMillis();
        try {
            ZoneId zoneId = ZoneId.of("Europe/London");
            java.time.LocalDateTime result = TimeUtil.fromMilliseconds(milliseconds, zoneId);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }
}
