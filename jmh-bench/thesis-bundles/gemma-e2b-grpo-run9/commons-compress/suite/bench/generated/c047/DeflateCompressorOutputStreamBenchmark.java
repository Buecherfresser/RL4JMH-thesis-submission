package bench.generated.c047;

import org.apache.commons.compress.compressors.deflate.DeflateCompressorOutputStream;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DeflateCompressorOutputStreamBenchmark {

    // We don't need complex state since we create new streams in each benchmark method
    // to avoid state pollution, adhering to the anti-pattern avoidance rules.

    @Benchmark
    public void benchmarkWrite(Blackhole bh) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             DeflateCompressorOutputStream dos = new DeflateCompressorOutputStream(baos)) {

            byte[] data = "This is a test string for compression benchmarking.".getBytes();
            dos.write(data, 0, data.length);
            dos.flush(); // Ensure data is written out before closing/finishing
        } catch (IOException e) {
            // Ignore exceptions for benchmarking purposes if they occur during stream operations
        }
    }

    @Benchmark
    public void benchmarkFinish(Blackhole bh) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             DeflateCompressorOutputStream dos = new DeflateCompressorOutputStream(baos)) {

            byte[] data = "This is a test string for finishing benchmarking.".getBytes();
            dos.write(data, 0, data.length);
            dos.finish();
        } catch (IOException e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void benchmarkClose(Blackhole bh) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             DeflateCompressorOutputStream dos = new DeflateCompressorOutputStream(baos)) {

            byte[] data = "This is a test string for closing benchmarking.".getBytes();
            dos.write(data, 0, data.length);
            dos.close();
        } catch (IOException e) {
            // Ignore exceptions
        }
    }
}
