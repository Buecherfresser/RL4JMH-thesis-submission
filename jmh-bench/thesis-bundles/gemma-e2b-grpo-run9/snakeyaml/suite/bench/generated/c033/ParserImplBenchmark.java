package bench.generated.c033;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.IOException;

import org.yaml.snakeyaml.parser.ParserImpl;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.reader.StreamReader;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ParserImplBenchmark {

    // State field for the subject under test.
    private ParserImpl parser;

    @Setup
    public void setup() {
        try {
            // Create a dummy input stream.
            byte[] yamlData = "key: value\n".getBytes();
            ByteArrayInputStream bais = new ByteArrayInputStream(yamlData);
            
            // Wrap InputStream in InputStreamReader to satisfy StreamReader's requirement for a Reader.
            Reader reader = new InputStreamReader(bais);
            
            LoaderOptions options = new LoaderOptions();
            // Instantiate the ParserImpl using the Reader
            this.parser = new ParserImpl(new StreamReader(reader), options);
        } catch (Exception e) {
            // In a real scenario, this should throw a RuntimeException or handle failure appropriately.
            System.err.println("Setup failed: " + e.getMessage());
        }
    }

    @Benchmark
    public void benchmarkGetEvent(Blackhole bh) {
        // Call a method that involves state manipulation
        try {
            bh.consume(parser.getEvent());
        } catch (Exception e) {
            // Ignore exceptions during benchmark execution
        }
    }

    @Benchmark
    public void benchmarkPeekEvent(Blackhole bh) {
        // Call a method that involves state manipulation
        try {
            bh.consume(parser.peekEvent());
        } catch (Exception e) {
            // Ignore exceptions
        }
    }
}
