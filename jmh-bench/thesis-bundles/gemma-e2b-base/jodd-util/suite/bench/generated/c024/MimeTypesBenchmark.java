package bench.generated.c024;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import jodd.net.MimeTypes;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
public class MimeTypesBenchmark {

    // --- State Fields ---
    // Known extensions that should be registered
    private String knownExtension1;
    private String knownExtension2;
    // Extension that is not registered
    private String unknownExtension;
    // Known MIME type for testing findExtensionsByMimeTypes
    private String knownMimeType;

    @Setup
    public void setup() {
        // Initialize inputs once in @Setup
        this.knownExtension1 = "html";
        this.knownExtension2 = "json";
        this.unknownExtension = "unknown_file";
        this.knownMimeType = "text/html";
    }

    @Benchmark
    public void getMimeType_KnownExtension(Blackhole bh) {
        String result = MimeTypes.getMimeType(knownExtension1);
        bh.consume(result);
    }

    @Benchmark
    public void getMimeType_UnknownExtension(Blackhole bh) {
        String result = MimeTypes.getMimeType(unknownExtension);
        bh.consume(result);
    }

    @Benchmark
    public void isRegisteredExtension_True(Blackhole bh) {
        boolean result = MimeTypes.isRegisteredExtension(knownExtension1);
        bh.consume(result);
    }

    @Benchmark
    public void isRegisteredExtension_False(Blackhole bh) {
        boolean result = MimeTypes.isRegisteredExtension(unknownExtension);
        bh.consume(result);
    }

    @Benchmark
    public void findExtensionsByMimeTypes_ExactMatch(Blackhole bh) {
        // Test finding extensions for a specific MIME type without wildcard
        String[] result = MimeTypes.findExtensionsByMimeTypes(knownMimeType, false);
        bh.consume(result);
    }

    @Benchmark
    public void findExtensionsByMimeTypes_WildcardMatch(Blackhole bh) {
        // Test finding extensions using a wildcard pattern
        String[] result = MimeTypes.findExtensionsByMimeTypes("text/*", true);
        bh.consume(result);
    }
}
