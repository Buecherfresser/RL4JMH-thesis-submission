package bench.generated.c041;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.reader.StreamReader;
import org.yaml.snakeyaml.scanner.ScannerImpl;
import org.yaml.snakeyaml.tokens.Token;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class ScannerImplBenchmark {

    private ScannerImpl scanner;
    private StreamReader reader;
    private String yamlDocument;
    private LoaderOptions loaderOptions;

    @Setup
    public void setup() throws IOException {
        // 1. Define a complex YAML document for benchmarking
        this.yamlDocument = """
            ---
            # This is a comment
            name: Test Document
            version: 1.0
            tags:
              - important
            settings:
              timeout: 30
              enabled: true
            anchor: &A
              value: 123
            reference: *A
            list:
              - item1
              - item2
            flow_map:
              key1: value1
              key2: value2
            """;

        // 2. Prepare the input stream using StringReader to satisfy StreamReader constructor requirements
        StringReader stringReader = new StringReader(this.yamlDocument);
        this.reader = new StreamReader(stringReader);

        // 3. Prepare LoaderOptions
        this.loaderOptions = new LoaderOptions();
        // Ensure comments are processed for a realistic test
        this.loaderOptions.setProcessComments(true);

        // 4. Initialize the ScannerImpl
        this.scanner = new ScannerImpl(this.reader, this.loaderOptions);
    }

    @Benchmark
    public void benchmarkTokenization(Blackhole bh) {
        int tokenCount = 0;
        Token token;
        
        // Consume tokens until the stream is done
        while ((token = scanner.getToken()) != null) {
            tokenCount++;
            bh.consume(token);
        }
    }
}
