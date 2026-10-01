package bench.generated.c010;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.archivers.dump.DumpArchiveConstants;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DumpArchiveConstantsBenchmark {

    // Inputs for COMPRESSION_TYPE.find(int code)
    private int knownCompressionCode;
    private int unknownCompressionCode;

    // Inputs for SEGMENT_TYPE.find(int code)
    private int knownSegmentCode;
    private int unknownSegmentCode;

    @Setup
    public void setup() {
        // Known codes: ZLIB is 0, TAPE is 1.
        // We use literals here as there is no public API to retrieve the package-private 'code' field.
        knownCompressionCode = 0;
        knownSegmentCode = 1;

        // Unknown codes
        unknownCompressionCode = 999;
        unknownSegmentCode = 999;
    }

    @Benchmark
    public void findCompressionType_Known(Blackhole bh) {
        bh.consume(DumpArchiveConstants.COMPRESSION_TYPE.find(knownCompressionCode));
    }

    @Benchmark
    public void findCompressionType_Unknown(Blackhole bh) {
        bh.consume(DumpArchiveConstants.COMPRESSION_TYPE.find(unknownCompressionCode));
    }

    @Benchmark
    public void findSegmentType_Known(Blackhole bh) {
        bh.consume(DumpArchiveConstants.SEGMENT_TYPE.find(knownSegmentCode));
    }

    @Benchmark
    public void findSegmentType_Unknown(Blackhole bh) {
        bh.consume(DumpArchiveConstants.SEGMENT_TYPE.find(unknownSegmentCode));
    }
}
