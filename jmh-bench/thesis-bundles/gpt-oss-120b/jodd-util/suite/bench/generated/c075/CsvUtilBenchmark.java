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

    private Object[] smallElements;
    private Object[] specialElements;
    private Object[] largeElements;

    private String simpleCsv;
    private String quotedCsv;
    private String largeCsv;

    @Setup(Level.Trial)
    public void setup() {
        // Small fixed array
        smallElements = new Object[] {"alpha", "beta", "gamma", "delta", "epsilon"};

        // Array with special characters and nulls
        specialElements = new Object[] {
                "simple",
                "with,comma",
                "with\"quote",
                " leadingSpace",
                "trailingSpace ",
                "line\r\nbreak",
                null
        };

        // Large array of 100 elements
        largeElements = new Object[100];
        for (int i = 0; i < largeElements.length; i++) {
            largeElements[i] = "value" + i;
        }

        // Simple CSV line without quotes
        simpleCsv = "a,b,c";

        // CSV line with quoted fields and escaped quotes
        quotedCsv = "\"a\",\"b,with,comma\",\"c\"";

        // Large CSV line (100 fields, no quoting)
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < largeElements.length; i++) {
            sb.append(largeElements[i]);
            if (i < largeElements.length - 1) {
                sb.append(',');
            }
        }
        largeCsv = sb.toString();
    }

    // Benchmarks for CsvUtil.toCsvString

    @Benchmark
    public String toCsvStringSmall() {
        return CsvUtil.toCsvString(smallElements);
    }

    @Benchmark
    public String toCsvStringSpecial() {
        return CsvUtil.toCsvString(specialElements);
    }

    @Benchmark
    public String toCsvStringLarge() {
        return CsvUtil.toCsvString(largeElements);
    }

    // Benchmarks for CsvUtil.toStringArray

    @Benchmark
    public String[] parseSimpleCsv() {
        return CsvUtil.toStringArray(simpleCsv);
    }

    @Benchmark
    public String[] parseQuotedCsv() {
        return CsvUtil.toStringArray(quotedCsv);
    }

    @Benchmark
    public String[] parseLargeCsv() {
        return CsvUtil.toStringArray(largeCsv);
    }
}
