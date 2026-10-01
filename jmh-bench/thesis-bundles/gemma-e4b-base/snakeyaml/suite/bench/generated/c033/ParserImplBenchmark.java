package bench.generated.c033;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import org.yaml.snakeyaml.parser.ParserImpl;
import org.yaml.snakeyaml.reader.StreamReader;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.events.Event;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ParserImplBenchmark {

    private ByteArrayInputStream inputStream;
    private StreamReader reader;
    private LoaderOptions options;
    private ParserImpl parser;

    // A complex YAML document to test various parsing paths (scalars, sequences, mappings, comments)
    private static final String COMPLEX_YAML =
            "--- # Document start\n" +
            "metadata:\n" +
            "  version: 1.1\n" +
            "  tags:\n" +
            "    - alpha\n" +
            "    - beta # Comment here\n" +
            "items:\n" +
            "  - name: Item A\n" +
            "    value: 100\n" +
            "  - name: Item B\n" +
            "    value: 200\n" +
            "settings:\n" +
            "  enabled: true\n" +
            "  limit: 50\n" +
            "empty_field: \n" +
            "";

    @Setup(Level.Trial)
    public void setupTrial() {
        // Build input once per trial
        inputStream = new ByteArrayInputStream(COMPLEX_YAML.getBytes());
        options = new LoaderOptions();
        // FIX: Wrap ByteArrayInputStream in InputStreamReader to satisfy StreamReader(Reader) constructor
        reader = new StreamReader(new InputStreamReader(inputStream));
    }

    @Setup(Level.Iteration)
    public void setupIteration() {
        // Initialize a fresh parser instance for each iteration to ensure clean state
        parser = new ParserImpl(reader, options);
    }

    /**
     * Benchmarks the core event retrieval mechanism (getEvent).
     * This forces the parser to advance through the stream state machine.
     */
    @Benchmark
    public Event benchmarkGetEvent(Blackhole bh) {
        Event event = parser.getEvent();
        bh.consume(event);
        return event;
    }

    /**
     * Benchmarks peeking at the next event without consuming it.
     * This tests the internal state management of the parser.
     */
    @Benchmark
    public Event benchmarkPeekEvent(Blackhole bh) {
        Event event = parser.peekEvent();
        bh.consume(event);
        return event;
    }

    /**
     * Benchmarks checking if the next event matches a specific ID.
     * This tests the internal state machine's lookahead capability.
     */
    @Benchmark
    public boolean benchmarkCheckEvent(Blackhole bh) {
        // We check for a common event type (e.g., ScalarEvent)
        boolean matches = parser.checkEvent(Event.ID.Scalar);
        bh.consume(matches);
        return matches;
    }
}
