package bench.generated.c025;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import jodd.net.URLCoder;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class URLCoderBenchmark {

    // --- Input Data Setup ---
    private String fullUri;
    private String query;
    private String host;
    private String path;
    private String scheme;
    private String userInfo;
    private String fragment;
    private String queryParam;
    private String httpUrl;
    private String port; // Field to hold the port value

    @Setup
    public void setup() {
        // Setup complex URI components
        this.scheme = "https";
        this.userInfo = "user:pass@host.com";
        this.host = "www.example.com";
        this.port = "8080"; // Defined port
        this.path = "/api/resource/item/123";
        this.query = "q=search&limit=10&sort=date";
        this.fragment = "section#details";
        this.queryParam = "key=value&param2=test";

        this.fullUri = scheme + "://" + userInfo + "@" + host + ":" + port + path + "?" + query + "#" + fragment;
        this.httpUrl = "https://user@host.com:8080/path?q=test";
    }

    // --- Benchmarks for basic component encoding ---

    @Benchmark
    public void encodeFullUri(Blackhole bh) {
        String result = URLCoder.encodeUri(fullUri);
        bh.consume(result);
    }

    @Benchmark
    public void encodeHttpUrl(Blackhole bh) {
        String result = URLCoder.encodeHttpUrl(httpUrl);
        bh.consume(result);
    }

    @Benchmark
    public void encodeQuery(Blackhole bh) {
        String result = URLCoder.encodeQuery(query);
        bh.consume(result);
    }

    @Benchmark
    public void encodeQueryParam(Blackhole bh) {
        String result = URLCoder.encodeQueryParam(queryParam);
        bh.consume(result);
    }

    @Benchmark
    public void encodeHost(Blackhole bh) {
        String result = URLCoder.encodeHost(host);
        bh.consume(result);
    }

    @Benchmark
    public void encodePath(Blackhole bh) {
        String result = URLCoder.encodePath(path);
        bh.consume(result);
    }

    @Benchmark
    public void encodeScheme(Blackhole bh) {
        String result = URLCoder.encodeScheme(scheme);
        bh.consume(result);
    }

    @Benchmark
    public void encodeFragment(Blackhole bh) {
        String result = URLCoder.encodeFragment(fragment);
        bh.consume(result);
    }

    // --- Benchmarks for individual component encoding (testing specific URIPart logic) ---

    @Benchmark
    public void encodeUserInfo(Blackhole bh) {
        String result = URLCoder.encodeUserInfo(userInfo);
        bh.consume(result);
    }

    @Benchmark
    public void encodePort(Blackhole bh) {
        // Test encoding the string "8080" using the port encoder
        String encodedPort = URLCoder.encodePort(this.port);
        bh.consume(encodedPort);
    }
    
    @Benchmark
    public void encodePathSegment(Blackhole bh) {
        String segment = "segment/with/slashes";
        String result = URLCoder.encodePathSegment(segment);
        bh.consume(result);
    }
}
