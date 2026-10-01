package bench.generated.c075;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;
import jodd.util.CsvUtil;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CsvUtilBenchmark {

    private Object[] simpleFields;
    private Object[] complexFields;
    private Object[] nullContainingFields;

    private String simpleCsv;
    private String complexCsv;
    private String emptyFieldCsv;
    private String quotedOnlyCsv;

    @Setup(Level.Trial)
    public void setup() {
        simpleFields = new Object[] {"alpha", "beta", "gamma"};
        simpleCsv = "alpha,beta,gamma";

        complexFields = new Object[] {
            "a,b",
            "he said \"hi\"",
            "line\nbreak",
            " lead",
            "trail ",
            ""
        };
        complexCsv = CsvUtil.toCsvString(complexFields);

        nullContainingFields = new Object[] {"x", null, "z"};
        emptyFieldCsv = "alpha,,gamma";
        quotedOnlyCsv = "\"alpha\",\"beta\"";
    }

    @Benchmark
    public String toCsvSimple() {
        return CsvUtil.toCsvString(simpleFields);
    }

    @Benchmark
    public String toCsvComplex() {
        return CsvUtil.toCsvString(complexFields);
    }

    @Benchmark
    public String toCsvWithNull() {
        return CsvUtil.toCsvString(nullContainingFields);
    }

    @Benchmark
    public String[] parseSimple() {
        return CsvUtil.toStringArray(simpleCsv);
    }

    @Benchmark
    public String[] parseComplex() {
        return CsvUtil.toStringArray(complexCsv);
    }

    @Benchmark
    public String[] parseEmptyFields() {
        return CsvUtil.toStringArray(emptyFieldCsv);
    }

    @Benchmark
    public String[] parseQuotedOnly() {
        return CsvUtil.toStringArray(quotedOnlyCsv);
    }
}
