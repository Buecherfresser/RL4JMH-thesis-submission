package bench.generated.c041;

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
import org.yaml.snakeyaml.scanner.ScannerImpl;
import org.yaml.snakeyaml.reader.StreamReader;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.tokens.Token;
import org.yaml.snakeyaml.tokens.Token.ID;

@State(org.openjdk.jmh.annotations.Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ScannerImplBenchmark {

    private String yamlDocument;
    private LoaderOptions loaderOptions;

    @Setup(org.openjdk.jmh.annotations.Level.Trial)
    public void setUp() {
        // A modest YAML document exercising various token types.
        yamlDocument = ""
                + "key: value\n"
                + "list:\n"
                + "  - item1\n"
                + "  - item2\n"
                + "map: {a: 1, b: 2}\n"
                + "block: |\n"
                + "  line1\n"
                + "  line2\n"
                + "folded: >\n"
                + "  folded line1\n"
                + "  folded line2\n"
                + "# comment line\n"
                + "...";
        loaderOptions = new LoaderOptions();
    }

    @Benchmark
    public boolean checkTokenSingle() {
        ScannerImpl scanner = new ScannerImpl(new StreamReader(yamlDocument), loaderOptions);
        return scanner.checkToken(ID.StreamStart);
    }

    @Benchmark
    public boolean checkTokenMultiple() {
        ScannerImpl scanner = new ScannerImpl(new StreamReader(yamlDocument), loaderOptions);
        return scanner.checkToken(ID.StreamStart, ID.StreamEnd);
    }

    @Benchmark
    public ID peekTokenId() {
        ScannerImpl scanner = new ScannerImpl(new StreamReader(yamlDocument), loaderOptions);
        return scanner.peekToken().getTokenId();
    }

    @Benchmark
    public ID getTokenId() {
        ScannerImpl scanner = new ScannerImpl(new StreamReader(yamlDocument), loaderOptions);
        return scanner.getToken().getTokenId();
    }

    @Benchmark
    public void resetDocumentIndex(Blackhole bh) {
        ScannerImpl scanner = new ScannerImpl(new StreamReader(yamlDocument), loaderOptions);
        scanner.resetDocumentIndex();
        // Consume a simple check to prevent dead‑code elimination.
        bh.consume(scanner.checkToken(ID.StreamStart));
    }
}
