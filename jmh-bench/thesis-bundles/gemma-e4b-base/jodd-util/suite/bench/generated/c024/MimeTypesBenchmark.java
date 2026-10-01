package bench.generated.c024;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.net.MimeTypes;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MimeTypesBenchmark {

    private String commonExtension;
    private String unknownExtension;
    private String mimeTypeToFind;
    private String wildcardPattern;
    private String[] mimeTypesForSearch;

    @Setup(Level.Trial)
    public void setup() {
        // Representative inputs
        commonExtension = "txt";
        unknownExtension = "xyz";
        mimeTypeToFind = "text/plain";
        wildcardPattern = "image/*";
        mimeTypesForSearch = new String[]{"text/html", "image/jpeg"};
    }

    @Benchmark
    public void lookupMimeType_CommonExtension(Blackhole bh) {
        String result = MimeTypes.lookupMimeType(commonExtension);
        bh.consume(result);
    }

    @Benchmark
    public void lookupMimeType_UnknownExtension(Blackhole bh) {
        String result = MimeTypes.lookupMimeType(unknownExtension);
        bh.consume(result);
    }

    @Benchmark
    public void getMimeType_CommonExtension(Blackhole bh) {
        String result = MimeTypes.getMimeType(commonExtension);
        bh.consume(result);
    }

    @Benchmark
    public void getMimeType_UnknownExtension(Blackhole bh) {
        String result = MimeTypes.getMimeType(unknownExtension);
        bh.consume(result);
    }

    @Benchmark
    public void isRegisteredExtension_True(Blackhole bh) {
        boolean result = MimeTypes.isRegisteredExtension(commonExtension);
        bh.consume(result);
    }

    @Benchmark
    public void isRegisteredExtension_False(Blackhole bh) {
        boolean result = MimeTypes.isRegisteredExtension(unknownExtension);
        bh.consume(result);
    }

    @Benchmark
    public void registerMimeType_NewEntry(Blackhole bh) {
        // Note: This modifies static state. Use with caution in real benchmarks.
        String newExt = "custom";
        String newMime = "application/custom";
        MimeTypes.registerMimeType(newExt, newMime);
        bh.consume(newMime);
    }

    @Benchmark
    public void findExtensionsByMimeTypes_Wildcard(Blackhole bh) {
        String[] extensions = MimeTypes.findExtensionsByMimeTypes(wildcardPattern, true);
        bh.consume(extensions);
    }

    @Benchmark
    public void findExtensionsByMimeTypes_ExactMatch(Blackhole bh) {
        String[] extensions = MimeTypes.findExtensionsByMimeTypes(mimeTypeToFind, false);
        bh.consume(extensions);
    }

    @Benchmark
    public void findExtensionsByMimeTypes_MultipleExactMatches(Blackhole bh) {
        // Test searching for multiple types simultaneously
        String multiType = String.join(", ", mimeTypesForSearch);
        String[] extensions = MimeTypes.findExtensionsByMimeTypes(multiType, false);
        bh.consume(extensions);
    }
}
