package bench.generated.c061;

import org.apache.commons.compress.compressors.lzma.LZMACompressorOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class LZMACompressorOutputStreamBenchmark {

    // Since LZMACompressorOutputStream is complex and relies on internal state
    // (like LZMAOutputStream), we avoid @State fields and instantiate it
    // within the benchmark method to ensure a clean, isolated test for each run.

    @Benchmark
    public void benchmarkWrite(Blackhole bh) {
        try {
            // 1. Setup: Create a fresh output stream to capture compressed data
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            
            // 2. Action: Instantiate the compressor stream
            // We use the default builder() which uses default LZMA2Options
            LZMACompressorOutputStream compressorStream = new LZMACompressorOutputStream(baos);

            // 3. Action: Write data (the core operation being measured)
            byte[] data = new byte[1024]; // Small, fixed input size
            // Fill data with non-zero values to ensure some work is done
            for (int i = 0; i < data.length; i++) {
                data[i] = (byte) (i % 256);
            }
            
            compressorStream.write(data, 0, data.length);

            // 4. Cleanup/Finalization: Finish the compression process
            compressorStream.finish();

            // 5. Consume result (to prevent dead code elimination)
            bh.consume(baos.toByteArray());

        } catch (IOException e) {
            // In a real scenario, we might log this, but for JMH, we just let the exception propagate
            // or handle it minimally if we expect it to be transient.
            // For this benchmark, we swallow it to ensure the benchmark loop continues.
        }
    }
}
