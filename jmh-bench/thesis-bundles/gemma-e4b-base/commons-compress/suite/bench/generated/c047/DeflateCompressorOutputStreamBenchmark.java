package bench.generated.c047;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import org.apache.commons.compress.compressors.deflate.DeflateCompressorOutputStream;
import org.apache.commons.compress.compressors.deflate.DeflateParameters;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DeflateCompressorOutputStreamBenchmark {

    private byte[] inputData;
    private DeflateParameters defaultParameters;

    @Setup(Level.Trial)
    public void setup() {
        // Create a representative input payload
        inputData = new byte[1024 * 10]; // 10 KB payload
        for (int i = 0; i < inputData.length; i++) {
            inputData[i] = (byte) (i % 256);
        }
        
        // Setup default parameters
        defaultParameters = new DeflateParameters();
    }

    /**
     * Benchmarks the cost of writing a chunk of data to the DeflateCompressorOutputStream.
     * A new stream is created for each invocation to ensure a clean compression state.
     */
    @Benchmark
    public void writeData(Blackhole bh) throws Exception {
        // Setup fresh state for this invocation
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DeflateCompressorOutputStream compressor = new DeflateCompressorOutputStream(baos, defaultParameters);

        try {
            // Perform the unit of work
            compressor.write(inputData, 0, inputData.length);
            
            // Consume the result (though write is void, we use bh to prevent DCE)
            bh.consume(null); 
        } finally {
            // Ensure resources are cleaned up
            compressor.close();
        }
    }

    /**
     * Benchmarks the cost of flushing the DeflateCompressorOutputStream.
     * A stream is initialized, written to, and then flushed.
     */
    @Benchmark
    public void flushStream(Blackhole bh) throws Exception {
        // Setup fresh state for this invocation
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DeflateCompressorOutputStream compressor = new DeflateCompressorOutputStream(baos, defaultParameters);

        try {
            // Write some data first to ensure there is buffered data to flush
            compressor.write(inputData, 0, inputData.length / 2);
            
            // Perform the unit of work
            compressor.flush();
            
            // Consume the result
            bh.consume(null);
        } finally {
            compressor.close();
        }
    }

    /**
     * Benchmarks the cost of finishing the compression process.
     * A stream is initialized, written to, and then finished.
     */
    @Benchmark
    public void finishCompression(Blackhole bh) throws Exception {
        // Setup fresh state for this invocation
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DeflateCompressorOutputStream compressor = new DeflateCompressorOutputStream(baos, defaultParameters);

        try {
            // Write some data first
            compressor.write(inputData, 0, inputData.length);
            
            // Perform the unit of work
            compressor.finish();
            
            // Consume the result
            bh.consume(null);
        } finally {
            compressor.close();
        }
    }

    /**
     * Benchmarks the cost of closing the DeflateCompressorOutputStream.
     * A stream is initialized, written to, and then closed.
     */
    @Benchmark
    public void closeStream(Blackhole bh) throws Exception {
        // Setup fresh state for this invocation
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DeflateCompressorOutputStream compressor = new DeflateCompressorOutputStream(baos, defaultParameters);

        try {
            // Write some data first
            compressor.write(inputData, 0, inputData.length);
            
            // Perform the unit of work
            compressor.close();
            
            // Consume the result
            bh.consume(null);
        } catch (Exception e) {
            // Handle potential exceptions during close if necessary, though JMH handles exceptions
            throw e;
        }
    }
}
