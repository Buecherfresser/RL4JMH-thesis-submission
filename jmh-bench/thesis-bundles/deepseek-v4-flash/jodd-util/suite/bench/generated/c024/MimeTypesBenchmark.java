package bench.generated.c024;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;
import jodd.net.MimeTypes;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MimeTypesBenchmark {

    private String ext;
    private String mimeType;
    private String wildcardPattern;

    @Setup(Level.Trial)
    public void setup() {
        ext = "html";
        mimeType = "text/html";
        wildcardPattern = "text/*";
    }

    @Benchmark
    public String lookupMimeType() {
        return MimeTypes.lookupMimeType(ext);
    }

    @Benchmark
    public String getMimeType() {
        return MimeTypes.getMimeType(ext);
    }

    @Benchmark
    public boolean isRegisteredExtension() {
        return MimeTypes.isRegisteredExtension(ext);
    }

    @Benchmark
    public String[] findExtensionsByMimeTypes() {
        return MimeTypes.findExtensionsByMimeTypes(mimeType, false);
    }

    @Benchmark
    public String[] findExtensionsByMimeTypesWildcard() {
        return MimeTypes.findExtensionsByMimeTypes(wildcardPattern, true);
    }
}
