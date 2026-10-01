package bench.generated.c033;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.archivers.zip.X0015_CertificateIdForFile;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class X0015_CertificateIdForFileBenchmark {

    private X0015_CertificateIdForFile parsedInstance;
    private byte[] data;

    @Setup(Level.Trial)
    public void setUp() {
        int rcount = 0x1234;   // example record count
        int hashCode = 0x0001; // example hash algorithm code

        data = new byte[4];
        data[0] = (byte) (rcount & 0xFF);
        data[1] = (byte) ((rcount >> 8) & 0xFF);
        data[2] = (byte) (hashCode & 0xFF);
        data[3] = (byte) ((hashCode >> 8) & 0xFF);

        parsedInstance = new X0015_CertificateIdForFile();
        try {
            parsedInstance.parseFromCentralDirectoryData(data, 0, data.length);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Benchmark
    public int parse() {
        X0015_CertificateIdForFile hdr = new X0015_CertificateIdForFile();
        try {
            hdr.parseFromCentralDirectoryData(data, 0, data.length);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return hdr.getRecordCount();
    }

    @Benchmark
    public int getRecordCount() {
        return parsedInstance.getRecordCount();
    }
}
