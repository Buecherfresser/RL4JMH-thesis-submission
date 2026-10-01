package bench.generated.c057;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;
import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorOutputStream;
import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorOutputStream.Parameters;
import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorOutputStream.BlockSize;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FramedLZ4CompressorOutputStreamBenchmark {

    private byte[] inputData;
    private static final int INPUT_SIZE = 1024 * 1024; // 1MB input payload

    @Setup(Level.Trial)
    public void setup() {
        inputData = new byte[INPUT_SIZE];
        new Random().nextBytes(inputData);
    }

    /**
     * Benchmarks compression using default parameters (M4, Content Checksum=T, Block Checksum=F, Dependency=F).
     */
    @Benchmark
    public void benchmarkDefaultParameters(Blackhole bh) throws IOException {
        Parameters params = Parameters.DEFAULT;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        // Create and use the compressor stream
        try (FramedLZ4CompressorOutputStream compressor = new FramedLZ4CompressorOutputStream(baos, params)) {
            // Write input data
            compressor.write(inputData, 0, inputData.length);
            // Finish compression
            compressor.finish();
        }
        
        // Consume the compressed output
        bh.consume(baos.toByteArray());
    }

    /**
     * Benchmarks compression using a small block size (K64).
     */
    @Benchmark
    public void benchmarkSmallBlockSize(Blackhole bh) throws IOException {
        Parameters params = new Parameters(BlockSize.K64);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try (FramedLZ4CompressorOutputStream compressor = new FramedLZ4CompressorOutputStream(baos, params)) {
            compressor.write(inputData, 0, inputData.length);
            compressor.finish();
        }
        
        bh.consume(baos.toByteArray());
    }

    /**
     * Benchmarks compression using a medium block size (M1).
     */
    @Benchmark
    public void benchmarkMediumBlockSize(Blackhole bh) throws IOException {
        Parameters params = new Parameters(BlockSize.M1);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try (FramedLZ4CompressorOutputStream compressor = new FramedLZ4CompressorOutputStream(baos, params)) {
            compressor.write(inputData, 0, inputData.length);
            compressor.finish();
        }
        
        bh.consume(baos.toByteArray());
    }

    /**
     * Benchmarks compression with both content and block checksums enabled (M4).
     */
    @Benchmark
    public void benchmarkWithBlockChecksum(Blackhole bh) throws IOException {
        Parameters params = new Parameters(BlockSize.M4, true, true, false);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try (FramedLZ4CompressorOutputStream compressor = new FramedLZ4CompressorOutputStream(baos, params)) {
            compressor.write(inputData, 0, inputData.length);
            compressor.finish();
        }
        
        bh.consume(baos.toByteArray());
    }

    /**
     * Benchmarks compression with block dependency enabled (M4).
     */
    @Benchmark
    public void benchmarkWithBlockDependency(Blackhole bh) throws IOException {
        Parameters params = new Parameters(BlockSize.M4, true, false, true);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try (FramedLZ4CompressorOutputStream compressor = new FramedLZ4CompressorOutputStream(baos, params)) {
            compressor.write(inputData, 0, inputData.length);
            compressor.finish();
        }
        
        bh.consume(baos.toByteArray());
    }
}
