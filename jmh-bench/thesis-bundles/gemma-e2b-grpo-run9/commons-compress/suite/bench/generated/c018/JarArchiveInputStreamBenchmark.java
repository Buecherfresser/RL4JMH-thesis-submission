package bench.generated.c018;

import org.apache.commons.compress.archivers.jar.JarArchiveInputStream;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class JarArchiveInputStreamBenchmark {

    // Since the method being tested is static and requires no state,
    // we don't strictly need @State fields, but we include the required annotations.

    /**
     * Benchmarks the static method that checks if a byte signature matches a JAR archive stream.
     * This tests the delegation path to ZipArchiveInputStream.matches.
     *
     * @param bh Blackhole to consume the result.
     */
    @Benchmark
    public void testMatches(Blackhole bh) {
        try {
            // Use a dummy byte array for the signature check.
            // The actual content doesn't matter for this static check, only the method call path.
            byte[] signature = new byte[10];
            
            // Call the static method under test
            boolean result = JarArchiveInputStream.matches(signature, 10);
            
            bh.consume(result);
        } catch (Exception e) {
            // Catch exceptions that might occur during stream operations if the underlying
            // implementation throws them on invalid input, ensuring the benchmark doesn't fail.
        }
    }
}
