package bench.generated.c033;

import org.apache.commons.compress.archivers.zip.X0015_CertificateIdForFile;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class X0015_CertificateIdForFileBenchmark {

    // Since X0015_CertificateIdForFile is stateless (or its state is internal and not mutated by public calls),
    // we don't strictly need a @State field, but we keep the class structure clean.

    @Benchmark
    public void benchmarkGetHashAlgorithm(Blackhole bh) {
        // Instantiate the object for each benchmark run.
        X0015_CertificateIdForFile instance = new X0015_CertificateIdForFile();
        
        // Call the method and consume the result.
        bh.consume(instance.getHashAlgorithm());
    }

    @Benchmark
    public void benchmarkGetRecordCount(Blackhole bh) {
        // Instantiate the object for each benchmark run.
        X0015_CertificateIdForFile instance = new X0015_CertificateIdForFile();
        
        // Call the method and consume the result.
        bh.consume(instance.getRecordCount());
    }
}
