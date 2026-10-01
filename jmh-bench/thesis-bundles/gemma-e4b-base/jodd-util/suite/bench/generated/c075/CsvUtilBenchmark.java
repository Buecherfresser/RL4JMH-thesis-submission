package bench.generated.c075;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.util.CsvUtil;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CsvUtilBenchmark {

    private Object[] csvElements;
    private String csvLine;

    @Setup(Level.Trial)
    public void setup() {
        // Input for toCsvString: A mix of types, nulls, and fields requiring quoting/escaping
        csvElements = new Object[]{
                "Simple Field",
                "Field, with comma",
                null,
                "Field with \"\" quote",
                "Another field",
                12345
        };

        // Input for toStringArray: A complex CSV line including quoted fields, separators, and escaped quotes
        csvLine = "Header1, \"Field 2, with comma\", null, \"Field 4 with \"\" quote\", 12345";
    }

    @Benchmark
    public void benchmarkToCsvString(Blackhole bh) {
        String result = CsvUtil.toCsvString(csvElements);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkToStringArray(Blackhole bh) {
        String[] result = CsvUtil.toStringArray(csvLine);
        bh.consume(result);
    }
}
