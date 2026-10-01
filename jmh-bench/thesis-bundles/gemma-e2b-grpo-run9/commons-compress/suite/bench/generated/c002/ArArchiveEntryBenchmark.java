package bench.generated.c002;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.commons.compress.archivers.ar.ArArchiveEntry;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ArArchiveEntryBenchmark {

    // State field for the benchmark. Since ArArchiveEntry is immutable,
    // we can reuse instances safely.
    private ArArchiveEntry entry;

    @Setup
    public void setup() {
        // Initialize a default entry.
        try {
            // Create a dummy entry. We use a simple name and length.
            this.entry = new ArArchiveEntry("test_file.txt", 1024L);
        } catch (Exception e) {
            // Ignore setup exceptions
        }
    }

    @Benchmark
    public void getName(Blackhole bh) {
        // Test a read-only method
        bh.consume(entry.getName());
    }

    @Benchmark
    public void getSize(Blackhole bh) {
        // Test a read-only method
        bh.consume(entry.getSize());
    }

    @Benchmark
    public void getLength(Blackhole bh) {
        // Test a read-only method
        bh.consume(entry.getLength());
    }

    @Benchmark
    public void getMode(Blackhole bh) {
        // Test a read-only method
        bh.consume(entry.getMode());
    }

    @Benchmark
    public void getUserId(Blackhole bh) {
        // Test a read-only method
        bh.consume(entry.getUserId());
    }

    @Benchmark
    public void getGroupId(Blackhole bh) {
        // Test a read-only method
        bh.consume(entry.getGroupId());
    }

    @Benchmark
    public void getLastModified(Blackhole bh) {
        // Test a read-only method
        bh.consume(entry.getLastModified());
    }

    @Benchmark
    public void getLastModifiedDate(Blackhole bh) {
        // Test a read-only method
        bh.consume(entry.getLastModifiedDate());
    }

    @Benchmark
    public void equals(Blackhole bh) {
        // Test equals method
        bh.consume(entry.equals(entry));
    }

    @Benchmark
    public void hashCode(Blackhole bh) {
        // Test hashCode method
        bh.consume(entry.hashCode());
    }

    @Benchmark
    public void isDirectory(Blackhole bh) {
        // Test a method that should always return false for this implementation
        bh.consume(entry.isDirectory());
    }
}
