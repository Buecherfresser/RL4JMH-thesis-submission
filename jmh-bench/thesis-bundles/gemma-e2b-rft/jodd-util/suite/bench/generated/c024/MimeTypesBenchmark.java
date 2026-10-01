package bench.generated.c024;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import jodd.net.MimeTypes;
import jodd.util.StringUtil;
import jodd.util.StringPool;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MimeTypesBenchmark {

    // --- State Fields ---
    private String knownExtension;
    private String unknownExtension;
    private String knownMimeType;
    private String defaultMimeType;
    private String mimeTypeForWildcardSearch;
    private List<String> knownExtensionsList;
    private List<String> unknownExtensionsList;
    private String[] wildcardMimeTypes;

    @Setup
    public void setup() {
        // Setup known data
        this.knownExtension = "html";
        this.knownMimeType = MimeTypes.MIME_TEXT_HTML;
        this.defaultMimeType = MimeTypes.MIME_APPLICATION_OCTET_STREAM;
        this.mimeTypeForWildcardSearch = MimeTypes.MIME_APPLICATION_XML;

        // Setup unknown data
        this.unknownExtension = "xyz";

        // Setup lists for findExtensionsByMimeTypes
        this.knownExtensionsList = new ArrayList<>(Arrays.asList("html", "json", "css"));
        this.unknownExtensionsList = new ArrayList<>(Arrays.asList("xyz", "pdf"));
        this.wildcardMimeTypes = new String[]{"application/xml", "image/jpeg"};
    }

    @Benchmark
    public void getMimeType_KnownExtension(Blackhole bh) {
        String result = MimeTypes.getMimeType(knownExtension);
        bh.consume(result);
    }

    @Benchmark
    public void getMimeType_UnknownExtension(Blackhole bh) {
        String result = MimeTypes.getMimeType(unknownExtension);
        bh.consume(result);
    }

    @Benchmark
    public void isRegisteredExtension_True(Blackhole bh) {
        boolean result = MimeTypes.isRegisteredExtension(knownExtension);
        bh.consume(result);
    }

    @Benchmark
    public void isRegisteredExtension_False(Blackhole bh) {
        boolean result = MimeTypes.isRegisteredExtension(unknownExtension);
        bh.consume(result);
    }

    @Benchmark
    public void findExtensionsByMimeTypes_ExactMatch(Blackhole bh) {
        String[] result = MimeTypes.findExtensionsByMimeTypes(mimeTypeForWildcardSearch, false);
        bh.consume(result);
    }

    @Benchmark
    public void findExtensionsByMimeTypes_WildcardMatch(Blackhole bh) {
        String[] result = MimeTypes.findExtensionsByMimeTypes(mimeTypeForWildcardSearch, true);
        bh.consume(result);
    }
}
