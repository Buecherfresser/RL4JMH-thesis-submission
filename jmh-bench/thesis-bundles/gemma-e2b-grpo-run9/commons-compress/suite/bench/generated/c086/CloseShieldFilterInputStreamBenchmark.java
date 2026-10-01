package bench.generated.c086;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.compress.utils.CloseShieldFilterInputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(java.util.concurrent.TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CloseShieldFilterInputStreamBenchmark {

    // Since the class is stateless and we are benchmarking instantiation/method calls,
    // we don't strictly need @State fields, but we keep the class structure clean.

    /**
     * Benchmark for instantiating CloseShieldFilterInputStream.
     * This tests the constructor call.
     */
    @Benchmark
    public void benchmarkConstructor(Blackhole bh) {
        try {
            // Create a simple, in-memory input stream for the constructor
            InputStream dummyStream = new ByteArrayInputStream(new byte[1024]);
            CloseShieldFilterInputStream filterStream = new CloseShieldFilterInputStream(dummyStream);
            // Consume the result to prevent dead code elimination
            bh.consume(filterStream);
        } catch (Exception e) {
            // Ignore exceptions for benchmarking purposes if they are expected to be rare
        }
    }

    /**
     * Benchmark for calling the close() method.
     * This tests the overridden method which should do nothing.
     */
    @Benchmark
    public void benchmarkClose(Blackhole bh) {
        try {
            // Create a simple, in-memory input stream for the close method call
            InputStream dummyStream = new ByteArrayInputStream(new byte[1024]);
            CloseShieldFilterInputStream filterStream = new CloseShieldFilterInputStream(dummyStream);
            
            // Call the method being tested
            filterStream.close();
            
            // Consume the result (void method)
            bh.consume(filterStream);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }
}
