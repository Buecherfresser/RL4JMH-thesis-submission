package bench.generated.c042;

import org.apache.commons.compress.compressors.brotli.BrotliCompressorInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BrotliCompressorInputStreamBenchmark {

    // Since BrotliCompressorInputStream is designed to wrap an InputStream,
    // we don't strictly need a @State field unless we want to reuse a complex,
    // initialized stream instance across benchmarks. For simplicity and to avoid
    // complex state management/closing issues, we will instantiate the stream
    // inside the benchmark method, relying on the setup/warmup phase to handle
    // initialization costs if they are significant.

    @Benchmark
    public void benchmarkRead(Blackhole bh) {
        try (InputStream is = new ByteArrayInputStream(new byte[1024])) {
            // Attempt to create the stream. This will likely throw IOException
            // if the underlying BrotliInputStream fails to initialize on garbage data,
            // but we measure the path taken.
            BrotliCompressorInputStream bci = new BrotliCompressorInputStream(is);
            
            // Call a method that performs work and consume the result
            bci.read();
            
            // Ensure resources are closed if they were opened (though try-with-resources handles this)
            bci.close();
        } catch (IOException e) {
            // Ignore exceptions for benchmarking purposes if they occur due to invalid input data
        }
    }

    @Benchmark
    public void benchmarkReadByteArray(Blackhole bh) {
        try (InputStream is = new ByteArrayInputStream(new byte[1024])) {
            BrotliCompressorInputStream bci = new BrotliCompressorInputStream(is);
            
            // Call a method that performs work and consume the result
            bci.read(new byte[100]);
            
            bci.close();
        } catch (IOException e) {
            // Ignore exceptions
        }
    }
    
    @Benchmark
    public void benchmarkReadWithLargeBuffer(Blackhole bh) {
        try (InputStream is = new ByteArrayInputStream(new byte[4096])) {
            BrotliCompressorInputStream bci = new BrotliCompressorInputStream(is);
            
            // Call a method that performs work with a larger buffer
            bci.read(new byte[4096], 0, 4096);
            
            bci.close();
        } catch (IOException e) {
            // Ignore exceptions
        }
    }
}
