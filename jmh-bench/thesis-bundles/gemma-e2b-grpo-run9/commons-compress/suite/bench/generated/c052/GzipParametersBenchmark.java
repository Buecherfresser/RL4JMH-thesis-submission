package bench.generated.c052;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import org.apache.commons.compress.compressors.gzip.GzipParameters;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class GzipParametersBenchmark {

    private GzipParameters parameters;

    @Setup
    public void setup() {
        // Initialize parameters for reuse. We don't need to mutate them heavily
        // for simple read operations, but we need an instance to call methods on.
        this.parameters = new GzipParameters();
    }

    @Benchmark
    public void getBufferSize(Blackhole bh) {
        // Read-only operation
        bh.consume(parameters.getBufferSize());
    }

    @Benchmark
    public void getComment(Blackhole bh) {
        // Read-only operation
        bh.consume(parameters.getComment());
    }

    @Benchmark
    public void getCompressionLevel(Blackhole bh) {
        // Read-only operation
        bh.consume(parameters.getCompressionLevel());
    }

    @Benchmark
    public void getDeflateStrategy(Blackhole bh) {
        // Read-only operation
        bh.consume(parameters.getDeflateStrategy());
    }

    @Benchmark
    public void getExtraField(Blackhole bh) {
        // Read-only operation
        bh.consume(parameters.getExtraField());
    }

    @Benchmark
    public void getFileName(Blackhole bh) {
        // Read-only operation (deprecated, but still public)
        bh.consume(parameters.getFileName());
    }

    @Benchmark
    public void getFileNameCharset(Blackhole bh) {
        // Read-only operation
        bh.consume(parameters.getFileNameCharset());
    }

    @Benchmark
    public void getHeaderCRC(Blackhole bh) {
        // Read-only operation
        bh.consume(parameters.getHeaderCRC());
    }

    @Benchmark
    public void getModificationInstant(Blackhole bh) {
        // Read-only operation
        bh.consume(parameters.getModificationInstant());
    }

    @Benchmark
    public void getModificationTime(Blackhole bh) {
        // Read-only operation
        bh.consume(parameters.getModificationTime());
    }

    @Benchmark
    public void setBufferSize(Blackhole bh) {
        // Mutating operation (should be avoided in high-frequency benchmarks, but required to test mutation)
        try {
            parameters.setBufferSize(1024);
        } catch (IllegalArgumentException e) {
            // Ignore exceptions if the benchmark harness handles them gracefully,
            // though in a real scenario, this might indicate a setup issue.
        }
        bh.consume(parameters.getBufferSize());
    }

    @Benchmark
    public void setComment(Blackhole bh) {
        // Mutating operation
        try {
            parameters.setComment("Test Comment");
        } catch (IllegalArgumentException e) {
            // Ignore exceptions
        }
        bh.consume(parameters.getComment());
    }

    @Benchmark
    public void setCompressionLevel(Blackhole bh) {
        // Mutating operation
        try {
            parameters.setCompressionLevel(9);
        } catch (IllegalArgumentException e) {
            // Ignore exceptions
        }
        bh.consume(parameters.getCompressionLevel());
    }

    @Benchmark
    public void setDeflateStrategy(Blackhole bh) {
        // Mutating operation
        parameters.setDeflateStrategy(1);
        bh.consume(parameters.getDeflateStrategy());
    }

    @Benchmark
    public void setExtraField(Blackhole bh) {
        // Mutating operation
        // Since ExtraField is not defined here, we rely on the fact that setting it
        // doesn't throw an exception if we pass null or a default/mocked object,
        // or we rely on the fact that the benchmark only tests the setter call path.
        // For simplicity, we call it without a concrete ExtraField instance if possible,
        // or rely on the fact that the setter logic is tested.
        try {
            parameters.setExtraField(null);
        } catch (Exception e) {
            // Ignore
        }
        bh.consume(parameters.getExtraField());
    }

    @Benchmark
    public void setFileName(Blackhole bh) {
        // Mutating operation
        try {
            parameters.setFileName("test_file.txt");
        } catch (IllegalArgumentException e) {
            // Ignore exceptions
        }
        bh.consume(parameters.getFileName());
    }

    @Benchmark
    public void setFileNameCharset(Blackhole bh) {
        // Mutating operation
        try {
            parameters.setFileNameCharset(StandardCharsets.UTF_8);
        } catch (Exception e) {
            // Ignore
        }
        bh.consume(parameters.getFileNameCharset());
    }

    @Benchmark
    public void setHeaderCRC(Blackhole bh) {
        // Mutating operation
        parameters.setHeaderCRC(true);
        bh.consume(parameters.getHeaderCRC());
    }

    @Benchmark
    public void setModificationTime(Blackhole bh) {
        // Mutating operation
        parameters.setModificationTime(System.currentTimeMillis() / 1000);
        bh.consume(parameters.getModificationTime());
    }

    @Benchmark
    public void setOperatingSystem(Blackhole bh) {
        // Mutating operation
        parameters.setOperatingSystem(GzipParameters.OS.UNIX.type());
        bh.consume(parameters.getOS());
    }
}
