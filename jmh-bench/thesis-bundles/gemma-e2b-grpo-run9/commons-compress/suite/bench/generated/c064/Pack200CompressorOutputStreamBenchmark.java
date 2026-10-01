package bench.generated.c064;

import org.apache.commons.compress.compressors.pack200.Pack200CompressorOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Pack200CompressorOutputStreamBenchmark {

    // Since Pack200CompressorOutputStream is stateful and relies on internal caching,
    // we instantiate it inside the benchmark method to ensure a clean state for each run.
    // No @State fields are strictly necessary here unless we were reusing a complex,
    // non-mutating factory object.

    @Benchmark
    public void benchmarkFinish(Blackhole bh) {
        try {
            // 1. Setup: Create a temporary output stream to capture the result.
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            
            // 2. Execution: Instantiate and run the compression cycle.
            // We use the default constructor which relies on Pack200Strategy.IN_MEMORY
            Pack200CompressorOutputStream compressor = new Pack200CompressorOutputStream(baos);
            
            // Trigger the compression logic
            compressor.finish();
            
            // 3. Consume result (to prevent dead code elimination)
            bh.consume(baos.toByteArray());
            
            // 4. Cleanup (close is called implicitly or explicitly, but finish() is the core work)
            compressor.close();

        } catch (IOException e) {
            // In a real scenario, we might log this, but for JMH, failing the benchmark
            // due to an exception is acceptable if the operation is expected to succeed.
            // We consume nothing if an exception occurs.
        }
    }

    @Benchmark
    public void benchmarkWrite(Blackhole bh) {
        try {
            // Setup: Create a temporary output stream.
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            
            // Execution: Instantiate and write a small payload.
            Pack200CompressorOutputStream compressor = new Pack200CompressorOutputStream(baos);
            
            // Write a small byte array
            compressor.write("test".getBytes());
            
            // Finish to ensure any internal flushing/cleanup happens (though write might not trigger full compression)
            compressor.finish();
            compressor.close();

            bh.consume(baos.toByteArray());

        } catch (IOException e) {
            // Ignore for benchmark purposes
        }
    }

    @Benchmark
    public void benchmarkWriteWithOffset(Blackhole bh) {
        try {
            // Setup: Create a temporary output stream.
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            
            // Execution: Instantiate and write a byte array with offset/length.
            Pack200CompressorOutputStream compressor = new Pack200CompressorOutputStream(baos);
            
            byte[] data = new byte[1024];
            // Fill data to ensure the write operation is non-trivial
            for (int i = 0; i < data.length; i++) {
                data[i] = (byte) i;
            }
            
            // Write a segment of the data
            compressor.write(data, 0, 100);
            
            compressor.finish();
            compressor.close();

            bh.consume(baos.toByteArray());

        } catch (IOException e) {
            // Ignore for benchmark purposes
        }
    }
}
