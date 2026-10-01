package bench.generated.c025;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import jodd.net.URLCoder;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class URLCoderBenchmark {

    // --- Setup Data ---
    // These inputs are constant per benchmark run, suitable for @State fields.
    private String scheme = "https";
    private String userInfo = "user:pass@host.com";
    private String host = "www.example.org";
    private String port = "8080";
    private String path = "/api/resource/item";
    private String query = "q=test&param=value&a=1";
    private String fragment = "section#anchor";
    private String pathSegment = "item_123";
    private String queryParamName = "q";
    private String queryParamValue = "test";

    private Charset utf8 = StandardCharsets.UTF_8;

    // --- Benchmarks for basic components ---

    @Benchmark
    public void encode_unreserved_string(Blackhole bh) {
        String input = "safe_string_123";
        String result = URLCoder.encode(input);
        bh.consume(result);
    }

    @Benchmark
    public void encode_unreserved_string_utf8(Blackhole bh) {
        String input = "safe_string_123";
        String result = URLCoder.encode(input, utf8);
        bh.consume(result);
    }

    @Benchmark
    public void encode_scheme_string(Blackhole bh) {
        String input = "ftp";
        String result = URLCoder.encodeScheme(input);
        bh.consume(result);
    }

    @Benchmark
    public void encode_scheme_string_utf8(Blackhole bh) {
        String input = "ftp";
        String result = URLCoder.encodeScheme(input, utf8);
        bh.consume(result);
    }

    @Benchmark
    public void encode_userinfo_string(Blackhole bh) {
        String input = "user:pass@host.com";
        String result = URLCoder.encodeUserInfo(input);
        bh.consume(result);
    }

    @Benchmark
    public void encode_userinfo_string_utf8(Blackhole bh) {
        String input = "user:pass@host.com";
        String result = URLCoder.encodeUserInfo(input, utf8);
        bh.consume(result);
    }

    @Benchmark
    public void encode_host_string(Blackhole bh) {
        String input = "www.example.org";
        String result = URLCoder.encodeHost(input);
        bh.consume(result);
    }

    @Benchmark
    public void encode_host_string_utf8(Blackhole bh) {
        String input = "www.example.org";
        String result = URLCoder.encodeHost(input, utf8);
        bh.consume(result);
    }

    @Benchmark
    public void encode_port_string(Blackhole bh) {
        String input = "8080";
        String result = URLCoder.encodePort(input);
        bh.consume(result);
    }

    @Benchmark
    public void encode_port_string_utf8(Blackhole bh) {
        String input = "8080";
        String result = URLCoder.encodePort(input, utf8);
        bh.consume(result);
    }

    @Benchmark
    public void encode_path_string(Blackhole bh) {
        String input = "/api/resource/item";
        String result = URLCoder.encodePath(input);
        bh.consume(result);
    }

    @Benchmark
    public void encode_path_string_utf8(Blackhole bh) {
        String input = "/api/resource/item";
        String result = URLCoder.encodePath(input, utf8);
        bh.consume(result);
    }

    @Benchmark
    public void encode_path_segment_string(Blackhole bh) {
        String input = "item_123";
        String result = URLCoder.encodePathSegment(input);
        bh.consume(result);
    }

    @Benchmark
    public void encode_path_segment_string_utf8(Blackhole bh) {
        String input = "item_123";
        String result = URLCoder.encodePathSegment(input, utf8);
        bh.consume(result);
    }

    @Benchmark
    public void encode_query_string(Blackhole bh) {
        String input = "q=test&param=value&a=1";
        String result = URLCoder.encodeQuery(input);
        bh.consume(result);
    }

    @Benchmark
    public void encode_query_string_utf8(Blackhole bh) {
        String input = "q=test&param=value&a=1";
        String result = URLCoder.encodeQuery(input, utf8);
        bh.consume(result);
    }

    @Benchmark
    public void encode_query_param_string(Blackhole bh) {
        String input = "q";
        String result = URLCoder.encodeQueryParam(input);
        bh.consume(result);
    }

    @Benchmark
    public void encode_query_param_string_utf8(Blackhole bh) {
        String input = "q";
        String result = URLCoder.encodeQueryParam(input, utf8);
        bh.consume(result);
    }

    @Benchmark
    public void encode_fragment_string(Blackhole bh) {
        String input = "section#anchor";
        String result = URLCoder.encodeFragment(input);
        bh.consume(result);
    }

    @Benchmark
    public void encode_fragment_string_utf8(Blackhole bh) {
        String input = "section#anchor";
        String result = URLCoder.encodeFragment(input, utf8);
        bh.consume(result);
    }

    // --- Benchmarks for complex URI methods ---

    @Benchmark
    public void encode_full_uri(Blackhole bh) {
        String uri = scheme + "://user@host:port" + path + "?q=" + query + "#" + fragment;
        String result = URLCoder.encodeUri(uri);
        bh.consume(result);
    }

    @Benchmark
    public void encode_full_http_url(Blackhole bh) {
        String httpUrl = scheme + "://user@host:port" + path + "?q=" + query;
        String result = URLCoder.encodeHttpUrl(httpUrl);
        bh.consume(result);
    }

    @Benchmark
    public void encode_full_http_url_utf8(Blackhole bh) {
        String httpUrl = scheme + "://user@host:port" + path + "?q=" + query;
        String result = URLCoder.encodeHttpUrl(httpUrl, utf8);
        bh.consume(result);
    }

    // --- Benchmarks for Builder ---

    @Benchmark
    public void build_url_with_params(Blackhole bh) {
        URLCoder.Builder builder = URLCoder.build(path, true);
        builder.queryParam(queryParamName, queryParamValue);
        String result = builder.get();
        bh.consume(result);
    }

    @Benchmark
    public void build_url_without_encoding(Blackhole bh) {
        URLCoder.Builder builder = URLCoder.build(path, false);
        builder.queryParam(queryParamName, queryParamValue);
        String result = builder.get();
        bh.consume(result);
    }
}
