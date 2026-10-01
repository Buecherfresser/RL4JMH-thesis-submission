package bench.generated.c004;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.commons.compress.archivers.ar.ArArchiveOutputStream;
import org.apache.commons.compress.archivers.ar.ArArchiveEntry;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ArArchiveOutputStreamBenchmark {

    @State(Scope.Benchmark)
    public static class Data {
        byte[] smallPayload;
        byte[] largePayload;
        ArArchiveEntry shortEntry;
        ArArchiveEntry longEntry;

        @Setup(Level.Trial)
        public void setup() {
            smallPayload = new byte[1024];
            largePayload = new byte[1024 * 1024];
            for (int i = 0; i < smallPayload.length; i++) {
                smallPayload[i] = (byte) (i % 127);
            }
            for (int i = 0; i < largePayload.length; i++) {
                largePayload[i] = (byte) (i % 127);
            }
            shortEntry = new ArArchiveEntry("file.txt", smallPayload.length);
            longEntry = new ArArchiveEntry("aVeryLongFileNameThatExceedsSixteenCharacters.txt", smallPayload.length);
        }
    }

    @Benchmark
    public byte[] writeSingleEntry(Data data) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ArArchiveOutputStream ar = new ArArchiveOutputStream(baos)) {
            ar.putArchiveEntry(data.shortEntry);
            ar.write(data.smallPayload, 0, data.smallPayload.length);
            ar.closeArchiveEntry();
            ar.finish();
        }
        return baos.toByteArray();
    }

    @Benchmark
    public byte[] writeSingleEntryClose(Data data) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ArArchiveOutputStream ar = new ArArchiveOutputStream(baos);
        ar.putArchiveEntry(data.shortEntry);
        ar.write(data.smallPayload, 0, data.smallPayload.length);
        ar.closeArchiveEntry();
        ar.close();
        return baos.toByteArray();
    }

    @Benchmark
    public byte[] writeMultipleEntries(Data data) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ArArchiveOutputStream ar = new ArArchiveOutputStream(baos)) {
            for (int i = 0; i < 10; i++) {
                ar.putArchiveEntry(data.shortEntry);
                ar.write(data.smallPayload, 0, data.smallPayload.length);
                ar.closeArchiveEntry();
            }
            ar.finish();
        }
        return baos.toByteArray();
    }

    @Benchmark
    public byte[] writeLargePayload(Data data) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ArArchiveOutputStream ar = new ArArchiveOutputStream(baos)) {
            ar.putArchiveEntry(data.shortEntry);
            ar.write(data.largePayload, 0, data.largePayload.length);
            ar.closeArchiveEntry();
            ar.finish();
        }
        return baos.toByteArray();
    }

    @Benchmark
    public byte[] writeLongFileNameBSD(Data data) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ArArchiveOutputStream ar = new ArArchiveOutputStream(baos)) {
            ar.setLongFileMode(ArArchiveOutputStream.LONGFILE_BSD);
            ar.putArchiveEntry(data.longEntry);
            ar.write(data.smallPayload, 0, data.smallPayload.length);
            ar.closeArchiveEntry();
            ar.finish();
        }
        return baos.toByteArray();
    }
}
