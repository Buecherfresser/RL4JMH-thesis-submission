package bench.generated.c035;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntryRequest;
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

    private ZipArchiveEntry zipArchiveEntry;
    private InputStreamSupplier payloadSupplier;
    private ZipArchiveEntryRequest request;

    @Setup(Level.Trial)
    public void setup() {
        // 1. Setup ZipArchiveEntry
        zipArchiveEntry = new ZipArchiveEntry("test_file.txt");
        zipArchiveEntry.setMethod(ZipArchiveEntry.DEFLATED);

        // 2. Setup InputStreamSupplier
        byte[] payload = "This is a test payload for the ZIP entry.".getBytes();
        InputStream payloadStream = new ByteArrayInputStream(payload);

        // Implement InputStreamSupplier
        payloadSupplier = () -> payloadStream;

        // 3. Setup the Request object
        request = ZipArchiveEntryRequest.createZipArchiveEntryRequest(zipArchiveEntry, payloadSupplier);
    }

    @Benchmark
    public void benchmark_Creation(Blackhole bh) {
        // Benchmarking the static factory method itself
        ZipArchiveEntry newEntry = new ZipArchiveEntry("temp.zip");
        newEntry.setMethod(ZipArchiveEntry.STORED);
        InputStreamSupplier newSupplier = () -> new ByteArrayInputStream("data".getBytes());
        ZipArchiveEntryRequest newRequest = ZipArchiveEntryRequest.createZipArchiveEntryRequest(newEntry, newSupplier);
        bh.consume(newRequest);
    }

    @Benchmark
    public void benchmark_GetMethod(Blackhole bh) {
        int method = request.getMethod();
        bh.consume(method);
    }

    @Benchmark
    public void benchmark_GetPayloadStream(Blackhole bh) {
        InputStream stream = request.getPayloadStream();
        bh.consume(stream);
    }
}
