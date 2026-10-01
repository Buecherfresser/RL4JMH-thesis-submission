package bench.generated.c088;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.utils.CountingOutputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CountingOutputStreamBenchmark {

    // Since CountingOutputStream is not thread-safe and relies on an underlying stream,
    // we instantiate it locally in the benchmark methods to ensure isolation.

    @Benchmark
    public void testWriteByteArray(Blackhole bh) {
        try (OutputStream underlyingOut = new ByteArrayOutputStream()) {
            CountingOutputStream cos = new CountingOutputStream(underlyingOut);
            byte[] data = new byte[1024];
            
            // Test write(byte[] b)
            cos.write(data);
            
            // Consume the result to prevent dead code elimination
            bh.consume(cos.getBytesWritten());
        } catch (IOException e) {
            // Should not happen with ByteArrayOutputStream
        }
    }

    @Benchmark
    public void testWriteSingleByte(Blackhole bh) {
        try (OutputStream underlyingOut = new ByteArrayOutputStream()) {
            CountingOutputStream cos = new CountingOutputStream(underlyingOut);
            
            // Test write(int b) repeatedly
            for (int i = 0; i < 100; i++) {
                cos.write((byte) i);
            }
            
            bh.consume(cos.getBytesWritten());
        } catch (IOException e) {
            // Should not happen with ByteArrayOutputStream
        }
    }

    @Benchmark
    public void testWriteWithOffsetAndLength(Blackhole bh) {
        try (OutputStream underlyingOut = new ByteArrayOutputStream()) {
            CountingOutputStream cos = new CountingOutputStream(underlyingOut);
            byte[] data = new byte[2048];
            
            // Test write(byte[] b, int off, int len)
            cos.write(data, 100, 500);
            
            bh.consume(cos.getBytesWritten());
        } catch (IOException e) {
            // Should not happen with ByteArrayOutputStream
        }
    }
    
    @Benchmark
    public void testGetBytesWritten(Blackhole bh) {
        try (OutputStream underlyingOut = new ByteArrayOutputStream()) {
            CountingOutputStream cos = new CountingOutputStream(underlyingOut);
            
            // Perform a write operation to ensure the counter is incremented
            cos.write(new byte[10]);
            
            // Check the result
            bh.consume(cos.getBytesWritten());
        } catch (IOException e) {
            // Should not happen with ByteArrayOutputStream
        }
    }
}
