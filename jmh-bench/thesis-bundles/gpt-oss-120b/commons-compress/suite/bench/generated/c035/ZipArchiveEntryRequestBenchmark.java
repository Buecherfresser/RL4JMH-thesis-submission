package bench.generated.c035;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntryRequest;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.parallel.InputStreamSupplier;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;

@State(org.openjdk.jmh.annotations.Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ZipArchiveEntryRequestBenchmark {

    private static final int PAYLOAD_SIZE = 1024; // 1 KiB

    private ZipArchiveEntry entry;
    private InputStreamSupplier supplier;
    private ZipArchiveEntryRequest request;
    private byte[] payload;

    @Setup(org.openjdk.jmh.annotations.Level.Trial)
    public void setUp() {
        // Prepare a deterministic payload
        payload = new byte[PAYLOAD_SIZE];
        for (int i = 0; i < PAYLOAD_SIZE; i++) {
            payload[i] = (byte) (i & 0xFF);
        }

        // Create a ZipArchiveEntry (default method = STORED)
        entry = new ZipArchiveEntry("test.txt");
        entry.setSize(PAYLOAD_SIZE);
        entry.setMethod(ZipArchiveEntry.STORED);

        // Supplier that returns a fresh InputStream each call
        supplier = new InputStreamSupplier() {
            @Override
            public InputStream get() {
                return new ByteArrayInputStream(payload);
            }
        };

        // Build the request once for read‑only benchmarks
        request = ZipArchiveEntryRequest.createZipArchiveEntryRequest(entry, supplier);
    }

    @Benchmark
    public ZipArchiveEntryRequest benchmarkCreateRequest() {
        // Create a new request each invocation
        return ZipArchiveEntryRequest.createZipArchiveEntryRequest(entry, supplier);
    }

    @Benchmark
    public int benchmarkGetMethod() {
        return request.getMethod();
    }

    @Benchmark
    public int benchmarkGetPayloadStream() throws IOException {
        InputStream is = request.getPayloadStream();
        try {
            byte[] buffer = new byte[256];
            int total = 0;
            int read;
            while ((read = is.read(buffer)) != -1) {
                total += read;
            }
            return total;
        } finally {
            is.close();
        }
    }

    // Optional: consume the payload via Blackhole to avoid dead‑code elimination
    @Benchmark
    public void benchmarkConsumePayload(Blackhole bh) throws IOException {
        InputStream is = request.getPayloadStream();
        try {
            byte[] buffer = new byte[256];
            int read;
            while ((read = is.read(buffer)) != -1) {
                bh.consume(read);
            }
        } finally {
            is.close();
        }
    }
}
