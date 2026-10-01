package bench.generated.c042;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import org.apache.commons.compress.compressors.brotli.BrotliCompressorInputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BrotliCompressorInputStreamBenchmark {

    // Placeholder for compressed data.
    private byte[] compressedData;
    private static final int DATA_SIZE = 1024 * 10; // 10 KB of compressed data

    @Setup(Level.Trial)
    public void setup() {
        // Simulate loading a compressed payload
        compressedData = new byte[DATA_SIZE];
        // Fill with dummy data
        for (int i = 0; i < DATA_SIZE; i++) {
            compressedData[i] = (byte) (i % 256);
        }
    }

    /**
     * Tests the basic read operation (single byte).
     */
    @Benchmark
    public void readSingleByte(Blackhole bh) {
        try (InputStream is = new ByteArrayInputStream(compressedData);
             BrotliCompressorInputStream bcis = new BrotliCompressorInputStream(is)) {
            int result = bcis.read();
            bh.consume(result);
        } catch (IOException e) {
            // Ignore IOExceptions during measurement
        }
    }

    /**
     * Tests reading a buffer of bytes.
     */
    @Benchmark
    public void readBuffer(Blackhole bh) {
        final byte[] buffer = new byte[1024];
        // Must create a fresh stream for each invocation
        try (InputStream is = new ByteArrayInputStream(compressedData);
             BrotliCompressorInputStream bcis = new BrotliCompressorInputStream(is)) {
            int bytesRead = bcis.read(buffer);
            bh.consume(bytesRead);
        } catch (IOException e) {
            // Ignore IOExceptions during measurement
        }
    }

    /**
     * Tests reading a specific segment of a buffer.
     */
    @Benchmark
    public void readSegment(Blackhole bh) {
        final byte[] buffer = new byte[2048];
        final int offset = 512;
        final int length = 512;
        // Must create a fresh stream for each invocation
        try (InputStream is = new ByteArrayInputStream(compressedData);
             BrotliCompressorInputStream bcis = new BrotliCompressorInputStream(is)) {
            int bytesRead = bcis.read(buffer, offset, length);
            bh.consume(bytesRead);
        } catch (IOException e) {
            // Ignore IOExceptions during measurement
        }
    }

    /**
     * Tests available() method.
     */
    @Benchmark
    public void available(Blackhole bh) {
        // Must create a fresh stream for each invocation
        try (InputStream is = new ByteArrayInputStream(compressedData);
             BrotliCompressorInputStream bcis = new BrotliCompressorInputStream(is)) {
            int available = bcis.available();
            bh.consume(available);
        } catch (IOException e) {
            // Ignore IOExceptions during measurement
        }
    }

    /**
     * Tests skip(long n) method.
     */
    @Benchmark
    public void skipBytes(Blackhole bh) {
        final long skipAmount = 100;
        // Must create a fresh stream for each invocation
        try (InputStream is = new ByteArrayInputStream(compressedData);
             BrotliCompressorInputStream bcis = new BrotliCompressorInputStream(is)) {
            long skipped = bcis.skip(skipAmount);
            bh.consume(skipped);
        } catch (IOException e) {
            // Ignore IOExceptions during measurement
        }
    }

    /**
     * Tests mark() functionality.
     */
    @Benchmark
    public void markStream(Blackhole bh) {
        final int readLimit = 1024;
        // Must create a fresh stream for each invocation
        try (InputStream is = new ByteArrayInputStream(compressedData);
             BrotliCompressorInputStream bcis = new BrotliCompressorInputStream(is)) {
            bcis.mark(readLimit);
            bh.consume(bcis.markSupported());
        } catch (IOException e) {
            // Ignore IOExceptions during measurement
        }
    }

    /**
     * Tests reset() functionality.
     */
    @Benchmark
    public void resetStream(Blackhole bh) {
        // Must create a fresh stream for each invocation
        try (InputStream is = new ByteArrayInputStream(compressedData);
             BrotliCompressorInputStream bcis = new BrotliCompressorInputStream(is)) {
            // Read a bit to move the pointer
            bcis.read();
            bcis.reset();
            bh.consume(true);
        } catch (IOException e) {
            // Ignore IOExceptions during measurement
        }
    }

    /**
     * Tests getCompressedCount() method.
     */
    @Benchmark
    public void getCompressedCount(Blackhole bh) {
        // Must create a fresh stream for each invocation
        try (InputStream is = new ByteArrayInputStream(compressedData);
             BrotliCompressorInputStream bcis = new BrotliCompressorInputStream(is)) {
            long count = bcis.getCompressedCount();
            bh.consume(count);
        } catch (IOException e) {
            // Ignore IOExceptions during measurement
        }
    }

    /**
     * Tests markSupported() method.
     */
    @Benchmark
    public void markSupported(Blackhole bh) {
        // Must create a fresh stream for each invocation
        try (InputStream is = new ByteArrayInputStream(compressedData);
             BrotliCompressorInputStream bcis = new BrotliCompressorInputStream(is)) {
            boolean supported = bcis.markSupported();
            bh.consume(supported);
        } catch (IOException e) {
            // Ignore IOExceptions during measurement
        }
    }

    /**
     * Tests close() method.
     */
    @Benchmark
    public void closeStream(Blackhole bh) {
        // Must create a fresh stream for each invocation
        try (InputStream is = new ByteArrayInputStream(compressedData);
             BrotliCompressorInputStream bcis = new BrotliCompressorInputStream(is)) {
            bcis.close();
            bh.consume(true);
        } catch (IOException e) {
            // Ignore IOExceptions during measurement
        }
    }
}
