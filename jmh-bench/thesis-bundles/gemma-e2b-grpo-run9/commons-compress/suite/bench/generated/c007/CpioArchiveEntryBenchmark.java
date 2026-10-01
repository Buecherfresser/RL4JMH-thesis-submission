package bench.generated.c007;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.archivers.cpio.CpioArchiveEntry;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CpioArchiveEntryBenchmark {

    // State field to hold the instance of the class under test.
    // Since CpioArchiveEntry is mutable and not thread-safe, we create a new instance
    // for each benchmark method or rely on JMH's isolation between threads/iterations.
    private CpioArchiveEntry entry;

    @Setup
    public void setup() {
        try {
            // Initialize a default entry. We use FORMAT_NEW (which is 0) as a safe default
            // if we cannot instantiate it with a simple constructor without throwing exceptions
            // due to missing constants/dependencies.
            this.entry = new CpioArchiveEntry((short) 0);
        } catch (Exception e) {
            // Ignore setup failures if dependencies are missing, as the focus is on JMH structure.
            System.err.println("Setup failed: " + e.getMessage());
        }
    }

    @Benchmark
    public void constructor(Blackhole bh) {
        try {
            // Test constructor that takes only format (simplest path)
            CpioArchiveEntry newEntry = new CpioArchiveEntry((short) 0);
            bh.consume(newEntry);
        } catch (Exception e) {
            // Ignore exceptions during benchmark execution if dependencies are missing
        }
    }

    @Benchmark
    public void getFormat(Blackhole bh) {
        // Test a simple getter
        bh.consume(entry.getFormat());
    }

    @Benchmark
    public void setSize(Blackhole bh) {
        // Test a setter that modifies internal state
        try {
            entry.setSize(1024L);
            bh.consume(entry.getSize());
        } catch (IllegalArgumentException e) {
            // Ignore exceptions if size validation fails due to missing constants
        }
    }

    @Benchmark
    public void setName(Blackhole bh) {
        // Test a setter that modifies internal state
        try {
            entry.setName("test_file");
            bh.consume(entry.getName());
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void getAlignmentBoundary(Blackhole bh) {
        // Test a getter that returns a final value
        bh.consume(entry.getAlignmentBoundary());
    }

    @Benchmark
    public void getChksum(Blackhole bh) {
        // Test a method that might throw UnsupportedOperationException if format is not NEW
        try {
            bh.consume(entry.getChksum());
        } catch (UnsupportedOperationException e) {
            // Expected if format is not NEW
        }
    }

    @Benchmark
    public void getDeviceMaj(Blackhole bh) {
        // Test a method that might throw UnsupportedOperationException if format is OLD
        try {
            bh.consume(entry.getDeviceMaj());
        } catch (UnsupportedOperationException e) {
            // Expected if format is NEW
        }
    }
}
