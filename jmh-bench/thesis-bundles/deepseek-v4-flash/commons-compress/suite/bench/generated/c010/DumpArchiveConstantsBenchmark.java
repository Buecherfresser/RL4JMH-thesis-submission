package bench.generated.c010;

import org.apache.commons.compress.archivers.dump.DumpArchiveConstants;
import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DumpArchiveConstantsBenchmark {

    @State(Scope.Benchmark)
    public static class Codes {
        int compressionValid;
        int compressionInvalid;
        int segmentValid;
        int segmentInvalid;

        @Setup(Level.Trial)
        public void setup() {
            compressionValid = 0;   // ZLIB
            compressionInvalid = 99;
            segmentValid = 1;       // TAPE
            segmentInvalid = 99;
        }
    }

    @Benchmark
    public DumpArchiveConstants.COMPRESSION_TYPE compressionFindValid(Codes c) {
        return DumpArchiveConstants.COMPRESSION_TYPE.find(c.compressionValid);
    }

    @Benchmark
    public DumpArchiveConstants.COMPRESSION_TYPE compressionFindInvalid(Codes c) {
        return DumpArchiveConstants.COMPRESSION_TYPE.find(c.compressionInvalid);
    }

    @Benchmark
    public DumpArchiveConstants.SEGMENT_TYPE segmentFindValid(Codes c) {
        return DumpArchiveConstants.SEGMENT_TYPE.find(c.segmentValid);
    }

    @Benchmark
    public DumpArchiveConstants.SEGMENT_TYPE segmentFindInvalid(Codes c) {
        return DumpArchiveConstants.SEGMENT_TYPE.find(c.segmentInvalid);
    }
}
