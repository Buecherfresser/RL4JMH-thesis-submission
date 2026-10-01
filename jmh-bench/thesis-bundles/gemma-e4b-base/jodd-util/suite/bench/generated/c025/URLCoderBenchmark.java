package bench.generated.c025;

import org.openjdk.jmh.annotations.*;
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

    // Input data fields
    private String simpleString;
    private String complexString;
    private String scheme;
    private String userInfo;
    private String host;
    private String port;
    private String path;
    private String query;
    private String fragment;
    private String fullUri;
    private String httpUrl;
    private String builderPath;
    private String builderParamName;
    private String builderParamValue;

    @Setup(Level.Trial)
    public void setup() {
        // Simple, unreserved string
        simpleString = "hello world";

        // Complex string containing reserved characters: /, ?, =, &, #, %
        complexString = "a/b?c=d&e#f%g";

        // URI components
        scheme = "https";
        userInfo = "user:pass@domain";
        host = "www.example-host.com";
        port = "8080";
        path = "/path/to/resource";
        query = "key1=value1&key2=value2";
        fragment = "section#1";

        // Full URI and HTTP URL
        fullUri = "https://" + userInfo + "@" + host + ":" + port + path + "?" + query + "#" + fragment;
        httpUrl = "http://" + host + path + "?" + query;

        // Builder inputs
        builderPath = "/test/path";
        builderParamName = "key";
        builderParamValue = "value";
    }

    // --- Basic Encoding Tests (UNRESERVED) ---

    @Benchmark
    public void encode_SimpleString(Blackhole bh) {
        String result = URLCoder.encode(simpleString);
        bh.consume(result);
    }

    @Benchmark
    public void encode_ComplexString(Blackhole bh) {
        String result = URLCoder.encode(complexString);
        bh.consume(result);
    }

    // --- Component Encoding Tests ---

    @Benchmark
    public void encodeScheme_Simple(Blackhole bh) {
        String result = URLCoder.encodeScheme(scheme);
        bh.consume(result);
    }

    @Benchmark
    public void encodeUserInfo_Complex(Blackhole bh) {
        String result = URLCoder.encodeUserInfo(userInfo);
        bh.consume(result);
    }

    @Benchmark
    public void encodeHost_Complex(Blackhole bh) {
        String result = URLCoder.encodeHost(host);
        bh.consume(result);
    }

    @Benchmark
    public void encodePort_Simple(Blackhole bh) {
        String result = URLCoder.encodePort(port);
        bh.consume(result);
    }

    @Benchmark
    public void encodePath_Complex(Blackhole bh) {
        String result = URLCoder.encodePath(path);
        bh.consume(result);
    }

    @Benchmark
    public void encodePathSegment_Complex(Blackhole bh) {
        String result = URLCoder.encodePathSegment("segment/with/chars");
        bh.consume(result);
    }

    @Benchmark
    public void encodeQuery_Complex(Blackhole bh) {
        String result = URLCoder.encodeQuery(query);
        bh.consume(result);
    }

    @Benchmark
    public void encodeQueryParam_Complex(Blackhole bh) {
        String result = URLCoder.encodeQueryParam("key=value");
        bh.consume(result);
    }

    @Benchmark
    public void encodeFragment_Complex(Blackhole bh) {
        String result = URLCoder.encodeFragment(fragment);
        bh.consume(result);
    }

    // --- Full URI Encoding Tests ---

    @Benchmark
    public void encodeUri_Full(Blackhole bh) {
        String result = URLCoder.encodeUri(fullUri);
        bh.consume(result);
    }

    @Benchmark
    public void encodeHttpUrl_Full(Blackhole bh) {
        String result = URLCoder.encodeHttpUrl(httpUrl);
        bh.consume(result);
    }

    // --- Builder Tests ---

    @Benchmark
    public void builder_QueryParam_Append(Blackhole bh) {
        // Simulate appending a query parameter
        URLCoder.Builder builder = new URLCoder.Builder(builderPath, true, StandardCharsets.UTF_8);
        URLCoder.Builder result = builder.queryParam(builderParamName, builderParamValue);
        bh.consume(result);
    }

    @Benchmark
    public void builder_Get_Finalize(Blackhole bh) {
        // Simulate building the final URL
        URLCoder.Builder builder = new URLCoder.Builder(builderPath, true, StandardCharsets.UTF_8);
        String result = builder.queryParam(builderParamName, builderParamValue).get();
        bh.consume(result);
    }
}
