package bench.generated.c064;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.compressors.pack200.Pack200CompressorOutputStream;
import org.apache.commons.compress.compressors.pack200.Pack200Strategy;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Pack200CompressorOutputStreamBenchmark {

    private byte[] inputData;
    private Map<String, String> properties;
    private final int DATA_SIZE = 4096; // 4KB input payload

    @Setup(Level.Trial)
    public void setup() throws IOException {
        // 1. Setup input data (Fixed payload)
        inputData = new byte[DATA_SIZE];
        for (int i = 0; i < DATA_SIZE; i++) {
            inputData[i] = (byte) (i % 256);
        }

        // 2. Setup properties map
        properties = new HashMap<>();
        properties.put("key1", "value1");
    }

    /**
     * Benchmarks writing a full byte array using the default Pack200Strategy.
     */
    @Benchmark
    public void writeFullArrayDefaultStrategy(Blackhole bh) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (Pack200CompressorOutputStream compressor = new Pack200CompressorOutputStream(baos)) {
            compressor.write(inputData);
            compressor.finish();
        }
        bh.consume(baos.toByteArray());
    }

    /**
     * Benchmarks writing a full byte array using a specific Pack200Strategy (e.g., IN_MEMORY).
     */
    @Benchmark
    public void writeFullArraySpecificStrategy(Blackhole bh) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (Pack200CompressorOutputStream compressor = new Pack200CompressorOutputStream(baos, Pack200Strategy.IN_MEMORY)) {
            compressor.write(inputData);
            compressor.finish();
        }
        bh.consume(baos.toByteArray());
    }

    /**
     * Benchmarks writing a full byte array using a specific strategy and custom properties.
     */
    @Benchmark
    public void writeFullArrayWithProperties(Blackhole bh) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (Pack200CompressorOutputStream compressor = new Pack200CompressorOutputStream(baos, Pack200Strategy.IN_MEMORY, properties)) {
            compressor.write(inputData);
            compressor.finish();
        }
        bh.consume(baos.toByteArray());
    }

    /**
     * Benchmarks writing a partial byte array (slice) using the default strategy.
     */
    @Benchmark
    public void writePartialArrayDefaultStrategy(Blackhole bh) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (Pack200CompressorOutputStream compressor = new Pack200CompressorOutputStream(baos)) {
            int length = DATA_SIZE / 2;
            compressor.write(inputData, 0, length);
            compressor.finish();
        }
        bh.consume(baos.toByteArray());
    }

    /**
     * Benchmarks writing a single byte using the default strategy.
     */
    @Benchmark
    public void writeSingleByteDefaultStrategy(Blackhole bh) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (Pack200CompressorOutputStream compressor = new Pack200CompressorOutputStream(baos)) {
            compressor.write(inputData[0]);
            compressor.finish();
        }
        bh.consume(baos.toByteArray());
    }

    /**
     * Benchmarks writing a small chunk of data (e.g., 16 bytes) using the default strategy.
     */
    @Benchmark
    public void writeSmallChunkDefaultStrategy(Blackhole bh) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (Pack200CompressorOutputStream compressor = new Pack200CompressorOutputStream(baos)) {
            int length = 16;
            compressor.write(inputData, 0, length);
            compressor.finish();
        }
        bh.consume(baos.toByteArray());
    }
}
