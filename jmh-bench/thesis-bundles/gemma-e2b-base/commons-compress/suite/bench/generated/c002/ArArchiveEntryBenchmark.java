package bench.generated.c002;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import org.apache.commons.compress.archivers.ar.ArArchiveEntry;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ArArchiveEntryBenchmark {

    // Fixed payload data for creating entries
    private byte[] fixedData;
    private String entryName;
    private long entryLength;
    private int userId;
    private int groupId;
    private int mode;
    private long lastModified;

    // The subject under test, initialized in @Setup
    private ArArchiveEntry entry;

    @Setup
    public void setup() {
        // 1. Prepare fixed payload data
        this.fixedData = new byte[1024 * 10]; // 10KB of data

        // 2. Define fixed entry parameters
        this.entryName = "test_file.txt";
        this.entryLength = this.fixedData.length;
        this.userId = 1001;
        this.groupId = 2002;
        this.mode = 0644; // DEFAULT_MODE is 33188 (octal 0100644)
        this.lastModified = System.currentTimeMillis() / 1000;

        // 3. Create the subject instance
        this.entry = new ArArchiveEntry(this.entryName, this.entryLength, this.userId, this.groupId, this.mode, this.lastModified);
    }

    @Benchmark
    public void getLength(Blackhole bh) {
        bh.consume(entry.getLength());
    }

    @Benchmark
    public void getName(Blackhole bh) {
        bh.consume(entry.getName());
    }

    @Benchmark
    public void getUserId(Blackhole bh) {
        bh.consume(entry.getUserId());
    }

    @Benchmark
    public void getGroupId(Blackhole bh) {
        bh.consume(entry.getGroupId());
    }

    @Benchmark
    public void getMode(Blackhole bh) {
        bh.consume(entry.getMode());
    }

    @Benchmark
    public void getLastModified(Blackhole bh) {
        bh.consume(entry.getLastModified());
    }

    @Benchmark
    public void getSize(Blackhole bh) {
        bh.consume(entry.getSize());
    }

    @Benchmark
    public void equals(Blackhole bh) {
        bh.consume(entry.equals(entry));
    }

    @Benchmark
    public void hashCode(Blackhole bh) {
        bh.consume(entry.hashCode());
    }

    @Benchmark
    public void isDirectory(Blackhole bh) {
        bh.consume(entry.isDirectory());
    }
}
