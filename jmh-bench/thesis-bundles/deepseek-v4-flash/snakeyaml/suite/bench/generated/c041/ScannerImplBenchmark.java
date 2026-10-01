package bench.generated.c041;

import org.openjdk.jmh.annotations.*;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.reader.StreamReader;
import org.yaml.snakeyaml.scanner.ScannerImpl;
import org.yaml.snakeyaml.tokens.Token;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ScannerImplBenchmark {

    @State(Scope.Benchmark)
    public static class ScannerImplState {
        LoaderOptions options;
        String yamlSimple;
        String yamlMapping;
        String yamlSequence;
        String yamlFlow;
        String yamlComments;
        String yamlDirective;
        String yamlComplex;

        @Setup(Level.Trial)
        public void setup() {
            options = new LoaderOptions();
            // enable comments to include comment tokens
            options.setProcessComments(true);
            yamlSimple = "hello world";
            yamlMapping = "key: value\nkey2: value2\n";
            yamlSequence = "- a\n- b\n- c\n";
            yamlFlow = "[a, b, c]";
            yamlComments = "# comment\nkey: value # inline\n";
            yamlDirective = "%YAML 1.2\n---\nkey: value\n";
            yamlComplex = "&anchor key: !tag value\n*anchor\n";
        }
    }

    private int scanAll(ScannerImpl scanner) {
        int count = 0;
        while (true) {
            Token token = scanner.getToken();
            count++;
            if (token.getTokenId() == Token.ID.StreamEnd) {
                break;
            }
        }
        return count;
    }

    private ScannerImpl newScanner(String yaml, ScannerImplState state) {
        StreamReader reader = new StreamReader(yaml);
        return new ScannerImpl(reader, state.options);
    }

    @Benchmark
    public int scanSimple(ScannerImplState state) {
        ScannerImpl scanner = newScanner(state.yamlSimple, state);
        return scanAll(scanner);
    }

    @Benchmark
    public int scanMapping(ScannerImplState state) {
        ScannerImpl scanner = newScanner(state.yamlMapping, state);
        return scanAll(scanner);
    }

    @Benchmark
    public int scanSequence(ScannerImplState state) {
        ScannerImpl scanner = newScanner(state.yamlSequence, state);
        return scanAll(scanner);
    }

    @Benchmark
    public int scanFlow(ScannerImplState state) {
        ScannerImpl scanner = newScanner(state.yamlFlow, state);
        return scanAll(scanner);
    }

    @Benchmark
    public int scanComments(ScannerImplState state) {
        ScannerImpl scanner = newScanner(state.yamlComments, state);
        return scanAll(scanner);
    }

    @Benchmark
    public int scanDirective(ScannerImplState state) {
        ScannerImpl scanner = newScanner(state.yamlDirective, state);
        return scanAll(scanner);
    }

    @Benchmark
    public int scanComplex(ScannerImplState state) {
        ScannerImpl scanner = newScanner(state.yamlComplex, state);
        return scanAll(scanner);
    }

    @Benchmark
    public boolean checkTokenSingle(ScannerImplState state) {
        ScannerImpl scanner = newScanner(state.yamlSimple, state);
        return scanner.checkToken(Token.ID.StreamStart);
    }

    @Benchmark
    public boolean checkTokenVarargs(ScannerImplState state) {
        ScannerImpl scanner = newScanner(state.yamlSimple, state);
        return scanner.checkToken(Token.ID.StreamStart, Token.ID.Scalar);
    }

    @Benchmark
    public Token peekToken(ScannerImplState state) {
        ScannerImpl scanner = newScanner(state.yamlSimple, state);
        return scanner.peekToken();
    }

    @Benchmark
    public Token getToken(ScannerImplState state) {
        ScannerImpl scanner = newScanner(state.yamlSimple, state);
        return scanner.getToken();
    }
}
