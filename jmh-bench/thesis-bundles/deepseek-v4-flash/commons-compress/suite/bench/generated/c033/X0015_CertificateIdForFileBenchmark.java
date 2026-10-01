package bench.generated.c033;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipException;
import org.apache.commons.compress.archivers.zip.X0015_CertificateIdForFile;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class X0015_CertificateIdForFileBenchmark {

    @State(Scope.Benchmark)
    public static class DataState {
        byte[] data;
        X0015_CertificateIdForFile parsedObject;

        @Setup(Level.Trial)
        public void setup() {
            // Build a valid 4-byte extra field: rcount = 1234, hashAlg = 2 (SHA1)
            data = new byte[4];
            data[0] = (byte) (1234 & 0xFF);
            data[1] = (byte) ((1234 >> 8) & 0xFF);
            data[2] = (byte) (2 & 0xFF);
            data[3] = (byte) ((2 >> 8) & 0xFF);

            // Pre-parse an object for getter benchmarks
            parsedObject = new X0015_CertificateIdForFile();
            try {
                parsedObject.parseFromCentralDirectoryData(data, 0, data.length);
            } catch (ZipException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Benchmark
    public int parseAndGetRecordCount(DataState state) {
        X0015_CertificateIdForFile obj = new X0015_CertificateIdForFile();
        try {
            obj.parseFromCentralDirectoryData(state.data, 0, state.data.length);
        } catch (ZipException e) {
            throw new RuntimeException(e);
        }
        return obj.getRecordCount();
    }

    @Benchmark
    public void parseAndGetHashAlgorithm(DataState state, Blackhole bh) {
        X0015_CertificateIdForFile obj = new X0015_CertificateIdForFile();
        try {
            obj.parseFromCentralDirectoryData(state.data, 0, state.data.length);
        } catch (ZipException e) {
            throw new RuntimeException(e);
        }
        bh.consume(obj.getHashAlgorithm());
    }

    @Benchmark
    public int getRecordCount(DataState state) {
        return state.parsedObject.getRecordCount();
    }

    @Benchmark
    public void getHashAlgorithm(DataState state, Blackhole bh) {
        bh.consume(state.parsedObject.getHashAlgorithm());
    }

    @Benchmark
    public void parseOnly(DataState state, Blackhole bh) {
        X0015_CertificateIdForFile obj = new X0015_CertificateIdForFile();
        try {
            obj.parseFromCentralDirectoryData(state.data, 0, state.data.length);
        } catch (ZipException e) {
            throw new RuntimeException(e);
        }
        bh.consume(obj);
    }
}
