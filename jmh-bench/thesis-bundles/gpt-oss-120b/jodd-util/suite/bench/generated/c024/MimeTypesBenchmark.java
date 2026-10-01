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

    // Known extensions that exist in the default mime types map
    private String knownExtension;
    // An extension that does not exist
    private String unknownExtension;
    // Data for registerMimeType benchmark (overwrites existing entry)
    private String regExtension;
    private String regMime;
    // Data for findExtensionsByMimeTypes benchmark
    private String mimeTypesExact;
    private String mimeTypesWildcard;

    @Setup(Level.Trial)
    public void setUp() {
        knownExtension = "txt"; // typically maps to text/plain
        unknownExtension = "unknownext123";

        regExtension = "txt";
        regMime = "text/plain";

        mimeTypesExact = "text/plain,text/html";
        mimeTypesWildcard = "text/*";
    }

    @Benchmark
    public String benchGetMimeTypeKnown() {
        return MimeTypes.getMimeType(knownExtension);
    }

    @Benchmark
    public String benchGetMimeTypeUnknown() {
        return MimeTypes.getMimeType(unknownExtension);
    }

    @Benchmark
    public String benchLookupMimeTypeKnown() {
        return MimeTypes.lookupMimeType(knownExtension);
    }

    @Benchmark
    public String benchLookupMimeTypeUnknown() {
        return MimeTypes.lookupMimeType(unknownExtension);
    }

    @Benchmark
    public void benchRegisterMimeType(Blackhole bh) {
        MimeTypes.registerMimeType(regExtension, regMime);
        // Verify the registration by reading back the value
        String result = MimeTypes.getMimeType(regExtension);
        bh.consume(result);
    }

    @Benchmark
    public String[] benchFindExtensionsExact() {
        return MimeTypes.findExtensionsByMimeTypes(mimeTypesExact, false);
    }

    @Benchmark
    public String[] benchFindExtensionsWildcard() {
        return MimeTypes.findExtensionsByMimeTypes(mimeTypesWildcard, true);
    }

    @Benchmark
    public boolean benchIsRegisteredExtensionTrue() {
        return MimeTypes.isRegisteredExtension(knownExtension);
    }

    @Benchmark
    public boolean benchIsRegisteredExtensionFalse() {
        return MimeTypes.isRegisteredExtension(unknownExtension);
    }
}
