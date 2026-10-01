package bench.generated.c015;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import jodd.io.FileUtil;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FileUtilBenchmark {

    // State fields are avoided for I/O heavy classes,
    // relying on FileUtil methods handling their own temporary file creation/deletion.

    @Setup
    public void setup() throws IOException {
        // Setup is empty as FileUtil methods handle their own I/O setup/teardown.
    }

    // --- Benchmarks for File/Directory Manipulation (I/O Bound) ---

    @Benchmark
    public void benchmark_mkdir(Blackhole bh) {
        try {
            // Attempt to create a directory.
            FileUtil.mkdir("temp_dir_benchmark_123");
        } catch (IOException e) {
            // Ignore expected IO exceptions during benchmarking setup/teardown
        }
        bh.consume(null);
    }

    @Benchmark
    public void benchmark_deleteFile(Blackhole bh) {
        try {
            // Attempt to delete a non-existent file.
            FileUtil.deleteFile("non_existent_file_xyz");
        } catch (Exception e) {
            // Expected exceptions during benchmarking
        }
        bh.consume(null);
    }

    // --- Benchmarks for String/Byte Operations (I/O Mix) ---

    @Benchmark
    public void benchmark_readString(Blackhole bh) {
        try {
            // Reading a string from a non-existent file should throw an exception.
            FileUtil.readString("non_existent_file_for_read");
        } catch (Exception e) {
            // Expected exceptions
        }
        bh.consume(null);
    }

    @Benchmark
    public void benchmark_writeString(Blackhole bh) {
        try {
            // Writing to a non-existent file. This will create a temp file.
            FileUtil.writeString("temp_output_file", "test data");
        } catch (IOException e) {
            // Expected exceptions
        }
        bh.consume(null);
    }

    @Benchmark
    public void benchmark_readBytes(Blackhole bh) {
        try {
            // Reading bytes from a non-existent file.
            FileUtil.readBytes("non_existent_file_for_bytes");
        } catch (Exception e) {
            // Expected exceptions
        }
        bh.consume(null);
    }

    @Benchmark
    public void benchmark_writeBytes(Blackhole bh) {
        try {
            // Writing bytes to a non-existent file.
            FileUtil.writeBytes("temp_output_file_bytes", new byte[0]);
        } catch (IOException e) {
            // Expected exceptions
        }
        bh.consume(null);
    }
}
