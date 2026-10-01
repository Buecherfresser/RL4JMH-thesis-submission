package bench.generated.c046;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import jodd.typeconverter.TypeConversionException;
import jodd.typeconverter.TypeConverter;
import jodd.typeconverter.impl.FileConverter;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FileConverterBenchmark {

    private byte[] testBytes;
    private String testString;

    @Setup(Level.Trial)
    public void setup() {
        // Prepare fixed payloads in memory
        // Test data size chosen to be large enough to measure I/O overhead but small enough to run quickly.
        int dataSize = 1024 * 1024; // 1 MB

        this.testBytes = new byte[dataSize];
        for (int i = 0; i < dataSize; i++) {
            testBytes[i] = (byte) (i % 256);
        }

        // FIX: Correctly create String from byte array using the specified charset.
        this.testString = new String(testBytes, StandardCharsets.UTF_8);
    }

    @Benchmark
    public void convert_NullValue(Blackhole bh) {
        FileConverter converter = new FileConverter();
        File result = converter.convert(null);
        bh.consume(result);
    }

    @Benchmark
    public void convert_ExistingFile(Blackhole bh) {
        FileConverter converter = new FileConverter();
        // Note: Using a dummy file path. The actual I/O performance depends on the OS/filesystem.
        File existingFile = new File("/tmp/dummy_benchmark_file.txt");
        File result = converter.convert(existingFile);
        bh.consume(result);
    }

    @Benchmark
    public void convert_ByteArray(Blackhole bh) {
        FileConverter converter = new FileConverter();
        File result = converter.convert(testBytes);
        bh.consume(result);
    }

    @Benchmark
    public void convert_String(Blackhole bh) {
        FileConverter converter = new FileConverter();
        File result = converter.convert(testString);
        bh.consume(result);
    }
}
