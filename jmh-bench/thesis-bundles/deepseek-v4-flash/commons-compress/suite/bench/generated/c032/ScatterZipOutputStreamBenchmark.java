package bench.generated.c032;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.*;
import java.util.concurrent.TimeUnit;
import java.util.zip.Deflater;
import org.apache.commons.compress.archivers.zip.*;
import org.apache.commons.compress.parallel.ScatterGatherBackingStore;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ScatterZipOutputStreamBenchmark {

    private static final int NUM_ENTRIES = 10;
    private byte[] payload;

    @Setup(Level.Trial)
    public void setup() {
        payload = new byte[1024];
        for (int i = 0; i < payload.length; i++) {
            payload[i] = (byte) (i % 256);
        }
    }

    private ScatterZipOutputStream createScatterStream(int numEntries) throws IOException {
        InMemoryScatterGatherBackingStore backingStore = new InMemoryScatterGatherBackingStore();
        StreamCompressor streamCompressor = StreamCompressor.create(Deflater.DEFAULT_COMPRESSION, backingStore);
        ScatterZipOutputStream scatterStream = new ScatterZipOutputStream(backingStore, streamCompressor);
        for (int i = 0; i < numEntries; i++) {
            ZipArchiveEntry entry = new ZipArchiveEntry("entry" + i);
            entry.setMethod(ZipMethod.DEFLATED.getCode());
            ZipArchiveEntryRequest request = ZipArchiveEntryRequest.createZipArchiveEntryRequest(entry, () -> new ByteArrayInputStream(payload));
            scatterStream.addArchiveEntry(request);
        }
        return scatterStream;
    }

    @Benchmark
    public int addSingleEntry() throws IOException {
        try (ScatterZipOutputStream scatterStream = createScatterStream(0)) {
            ZipArchiveEntry entry = new ZipArchiveEntry("entry");
            entry.setMethod(ZipMethod.DEFLATED.getCode());
            ZipArchiveEntryRequest request = ZipArchiveEntryRequest.createZipArchiveEntryRequest(entry, () -> new ByteArrayInputStream(payload));
            scatterStream.addArchiveEntry(request);
            return 1;
        }
    }

    @Benchmark
    public int addMultipleEntries() throws IOException {
        try (ScatterZipOutputStream scatterStream = createScatterStream(0)) {
            for (int i = 0; i < NUM_ENTRIES; i++) {
                ZipArchiveEntry entry = new ZipArchiveEntry("entry" + i);
                entry.setMethod(ZipMethod.DEFLATED.getCode());
                ZipArchiveEntryRequest request = ZipArchiveEntryRequest.createZipArchiveEntryRequest(entry, () -> new ByteArrayInputStream(payload));
                scatterStream.addArchiveEntry(request);
            }
            return NUM_ENTRIES;
        }
    }

    @Benchmark
    public byte[] writeTo() throws IOException {
        try (ScatterZipOutputStream scatterStream = createScatterStream(NUM_ENTRIES);
             ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipArchiveOutputStream target = new ZipArchiveOutputStream(baos)) {
            scatterStream.writeTo(target);
            target.finish();
            return baos.toByteArray();
        }
    }

    @Benchmark
    public byte[] zipEntryWriter() throws IOException {
        try (ScatterZipOutputStream scatterStream = createScatterStream(NUM_ENTRIES);
             ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipArchiveOutputStream target = new ZipArchiveOutputStream(baos);
             ScatterZipOutputStream.ZipEntryWriter writer = scatterStream.zipEntryWriter()) {
            for (int i = 0; i < NUM_ENTRIES; i++) {
                writer.writeNextZipEntry(target);
            }
            target.finish();
            return baos.toByteArray();
        }
    }

    private static class InMemoryScatterGatherBackingStore implements ScatterGatherBackingStore {
        private final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        private boolean closedForWriting = false;
        private InputStream inputStream;

        @Override
        public void writeOut(byte[] data, int offset, int length) throws IOException {
            if (closedForWriting) {
                throw new IOException("Backing store is closed for writing");
            }
            baos.write(data, offset, length);
        }

        @Override
        public InputStream getInputStream() throws IOException {
            if (!closedForWriting) {
                throw new IOException("Backing store not closed for writing yet");
            }
            if (inputStream == null) {
                inputStream = new ByteArrayInputStream(baos.toByteArray());
            }
            return inputStream;
        }

        @Override
        public void closeForWriting() throws IOException {
            closedForWriting = true;
        }

        @Override
        public void close() throws IOException {
            // nothing to do
        }
    }
}
