package bench.generated.c025;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.nio.charset.StandardCharsets;
import jodd.net.URLCoder;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class URLCoderBenchmark {

    private String sampleString;
    private String scheme;
    private String userInfo;
    private String host;
    private String port;
    private String path;
    private String pathSegment;
    private String query;
    private String queryParam;
    private String fragment;
    private String fullUri;
    private String httpUrl;

    private String basePath;
    private String paramName;
    private String paramValue;

    @Setup
    public void setup() {
        sampleString = "Hello World! @#%&*()=+[]{}|;:',<>/?`~";

        scheme = "https";
        userInfo = "user:pa ss";
        host = "www.example.com";
        port = "8080";
        path = "/path with spaces/segment";
        pathSegment = "segment/with?special=chars";
        query = "name=John Doe&age=30";
        queryParam = "name=John Doe";
        fragment = "section 2";

        fullUri = "https://jodd:ddoj@www.jodd.org:8080/file;p=1?q=2#third";

        httpUrl = "http://www.example.com/path/to resource?param=val";

        basePath = "/api/v1/resource";
        paramName = "search";
        paramValue = "value with spaces & symbols";
    }

    @Benchmark
    public String encodeString() {
        return URLCoder.encode(sampleString);
    }

    @Benchmark
    public String encodeStringUtf8() {
        return URLCoder.encode(sampleString, StandardCharsets.UTF_8);
    }

    @Benchmark
    public String encodeScheme() {
        return URLCoder.encodeScheme(scheme);
    }

    @Benchmark
    public String encodeSchemeUtf8() {
        return URLCoder.encodeScheme(scheme, StandardCharsets.UTF_8);
    }

    @Benchmark
    public String encodeUserInfo() {
        return URLCoder.encodeUserInfo(userInfo);
    }

    @Benchmark
    public String encodeUserInfoUtf8() {
        return URLCoder.encodeUserInfo(userInfo, StandardCharsets.UTF_8);
    }

    @Benchmark
    public String encodeHost() {
        return URLCoder.encodeHost(host);
    }

    @Benchmark
    public String encodeHostUtf8() {
        return URLCoder.encodeHost(host, StandardCharsets.UTF_8);
    }

    @Benchmark
    public String encodePort() {
        return URLCoder.encodePort(port);
    }

    @Benchmark
    public String encodePortUtf8() {
        return URLCoder.encodePort(port, StandardCharsets.UTF_8);
    }

    @Benchmark
    public String encodePath() {
        return URLCoder.encodePath(path);
    }

    @Benchmark
    public String encodePathUtf8() {
        return URLCoder.encodePath(path, StandardCharsets.UTF_8);
    }

    @Benchmark
    public String encodePathSegment() {
        return URLCoder.encodePathSegment(pathSegment);
    }

    @Benchmark
    public String encodePathSegmentUtf8() {
        return URLCoder.encodePathSegment(pathSegment, StandardCharsets.UTF_8);
    }

    @Benchmark
    public String encodeQuery() {
        return URLCoder.encodeQuery(query);
    }

    @Benchmark
    public String encodeQueryUtf8() {
        return URLCoder.encodeQuery(query, StandardCharsets.UTF_8);
    }

    @Benchmark
    public String encodeQueryParam() {
        return URLCoder.encodeQueryParam(queryParam);
    }

    @Benchmark
    public String encodeQueryParamUtf8() {
        return URLCoder.encodeQueryParam(queryParam, StandardCharsets.UTF_8);
    }

    @Benchmark
    public String encodeFragment() {
        return URLCoder.encodeFragment(fragment);
    }

    @Benchmark
    public String encodeFragmentUtf8() {
        return URLCoder.encodeFragment(fragment, StandardCharsets.UTF_8);
    }

    @Benchmark
    public String encodeUri() {
        return URLCoder.encodeUri(fullUri);
    }

    @Benchmark
    public String encodeUriUtf8() {
        return URLCoder.encodeUri(fullUri, StandardCharsets.UTF_8);
    }

    @Benchmark
    public String encodeHttpUrl() {
        return URLCoder.encodeHttpUrl(httpUrl);
    }

    @Benchmark
    public String encodeHttpUrlUtf8() {
        return URLCoder.encodeHttpUrl(httpUrl, StandardCharsets.UTF_8);
    }

    @Benchmark
    public String builderCreate() {
        URLCoder.Builder b = URLCoder.build(basePath, false);
        return b.get();
    }

    @Benchmark
    public void builderAddQueryParam(Blackhole bh) {
        URLCoder.Builder b = URLCoder.build(basePath, false);
        b.queryParam(paramName, paramValue);
        bh.consume(b);
    }
}
