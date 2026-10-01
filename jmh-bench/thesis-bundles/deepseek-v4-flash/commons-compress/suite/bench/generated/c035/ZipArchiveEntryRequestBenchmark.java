package bench.generated.c035;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntryRequest;
import org.apache.commons.compress.parallel.InputStreamSupplier;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ZipArchiveEntryRequestBenchmark {

    private byte[] payload;
    private ZipArchiveEntry zipEntry;
    private InputStreamSupplier supplier;
    private ZipArchiveEntryRequest request;

    @Setup(Level.Trial)
    public void setup() {
        // Representative payload – 1 KiB of data.
        payload = new byte[1024];
        for (int i = 0; i < payload.length; i++) {
            payload[i] = (byte) (i % 256);
        }
        zipEntry = new ZipArchiveEntry("benchmark-entry");
        zipEntry.setMethod(ZipArchiveEntry.DEFLATED);
        supplier = () -> new ByteArrayInputStream(payload);
        request = ZipArchiveEntryRequest.createZipArchiveEntryRequest(zipEntry, supplier);
    }

    @Benchmark
    public ZipArchiveEntryRequest createRequest() {
        return ZipArchiveEntryRequest.createZipArchiveEntryRequest(zipEntry, supplier);
    }

    @Benchmark
    public int getMethod() {
        return request.getMethod();
    }

    @Benchmark
    public InputStream getPayloadStream() {
        return request.getPayloadStream();
    }
}
