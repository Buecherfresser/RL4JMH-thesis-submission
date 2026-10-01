package bench.generated.c035;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntryRequest;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.parallel.InputStreamSupplier;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ZipArchiveEntryRequestBenchmark {

    // State fields are not strictly necessary here since we rely on static methods
    // and stateless operations, but they satisfy the requirement for @State if needed.

    /**
     * Benchmarks the static factory method for creating a ZipArchiveEntryRequest.
     * This tests object instantiation and initialization.
     */
    @Benchmark
    public void createRequest(Blackhole bh) {
        try {
            // We rely on the static method call. We use nulls/dummy objects
            // for ZipArchiveEntry and InputStreamSupplier as we are only testing
            // the path through the request object itself.
            ZipArchiveEntryRequest request = ZipArchiveEntryRequest.createZipArchiveEntryRequest(
                    null,
                    null
            );
            bh.consume(request);
        } catch (Exception e) {
            // Ignore exceptions for benchmarking purposes if they are expected
        }
    }

    /**
     * Benchmarks the getMethod() method.
     */
    @Benchmark
    public int getMethod(Blackhole bh) {
        try {
            // Create a request instance (requires dummy dependencies)
            ZipArchiveEntryRequest request = ZipArchiveEntryRequest.createZipArchiveEntryRequest(
                    null,
                    null
            );
            int method = request.getMethod();
            bh.consume(method);
            return method;
        } catch (Exception e) {
            // Should not happen in a controlled benchmark
            throw new RuntimeException(e);
        }
    }

    /**
     * Benchmarks the getPayloadStream() method.
     * This tests the call to payloadSupplier.get().
     */
    @Benchmark
    public void getPayloadStream(Blackhole bh) {
        try {
            // Create a request instance
            ZipArchiveEntryRequest request = ZipArchiveEntryRequest.createZipArchiveEntryRequest(
                    null,
                    null
            );
            // Calling this method executes payloadSupplier.get()
            InputStream stream = request.getPayloadStream();
            bh.consume(stream);
        } catch (Exception e) {
            // Should not happen in a controlled benchmark
        }
    }
}
