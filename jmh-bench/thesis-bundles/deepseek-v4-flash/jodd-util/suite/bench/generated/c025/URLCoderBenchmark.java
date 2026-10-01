package bench.generated.c025;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import jodd.net.URLCoder;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class URLCoderBenchmark {

    private String simple;
    private String special;
    private String fullUri;
    private String httpUrl;
    private String path;
    private String pathSegment;
    private String query;
    private String queryValue;
    private String fragment;
    private String host;
    private String port;
    private String scheme;
    private String userInfo;
    private String queryName;
    private String langName;
    private String langValue;
    private String countName;
    private Integer countValue;
    private Charset utf8;

    @Setup(Level.Trial)
    public void setUp() {
        simple = "hello";
        special = "hello world & more/ü?=ä";
        fullUri = "https://user:pass@www.example.com:8080/path;param?query=value&x=1#fragment";
        httpUrl = "http://www.example.com/a b?q=hello world&x=1";
        path = "/path with spaces/ümlaut";
        pathSegment = "file name";
        query = "q=hello world&lang=de";
        queryValue = "hello world";
        fragment = "section 1";
        host = "www.exämple.com";
        port = "8080";
        scheme = "https";
        userInfo = "user name:pass";
        queryName = "q";
        langName = "lang";
        langValue = "de";
        countName = "count";
        countValue = Integer.valueOf(42);
        utf8 = StandardCharsets.UTF_8;
    }

    @Benchmark
    public String encode() {
        return URLCoder.encode(special);
    }

    @Benchmark
    public String encodeWithCharset() {
        return URLCoder.encode(special, utf8);
    }

    @Benchmark
    public String encodeScheme() {
        return URLCoder.encodeScheme(scheme);
    }

    @Benchmark
    public String encodeSchemeWithCharset() {
        return URLCoder.encodeScheme(scheme, utf8);
    }

    @Benchmark
    public String encodeUserInfo() {
        return URLCoder.encodeUserInfo(userInfo);
    }

    @Benchmark
    public String encodeUserInfoWithCharset() {
        return URLCoder.encodeUserInfo(userInfo, utf8);
    }

    @Benchmark
    public String encodeHost() {
        return URLCoder.encodeHost(host);
    }

    @Benchmark
    public String encodeHostWithCharset() {
        return URLCoder.encodeHost(host, utf8);
    }

    @Benchmark
    public String encodePort() {
        return URLCoder.encodePort(port);
    }

    @Benchmark
    public String encodePortWithCharset() {
        return URLCoder.encodePort(port, utf8);
    }

    @Benchmark
    public String encodePath() {
        return URLCoder.encodePath(path);
    }

    @Benchmark
    public String encodePathWithCharset() {
        return URLCoder.encodePath(path, utf8);
    }

    @Benchmark
    public String encodePathSegment() {
        return URLCoder.encodePathSegment(pathSegment);
    }

    @Benchmark
    public String encodePathSegmentWithCharset() {
        return URLCoder.encodePathSegment(pathSegment, utf8);
    }

    @Benchmark
    public String encodeQuery() {
        return URLCoder.encodeQuery(query);
    }

    @Benchmark
    public String encodeQueryWithCharset() {
        return URLCoder.encodeQuery(query, utf8);
    }

    @Benchmark
    public String encodeQueryParam() {
        return URLCoder.encodeQueryParam(queryValue);
    }

    @Benchmark
    public String encodeQueryParamWithCharset() {
        return URLCoder.encodeQueryParam(queryValue, utf8);
    }

    @Benchmark
    public String encodeFragment() {
        return URLCoder.encodeFragment(fragment);
    }

    @Benchmark
    public String encodeFragmentWithCharset() {
        return URLCoder.encodeFragment(fragment, utf8);
    }

    @Benchmark
    public String encodeUri() {
        return URLCoder.encodeUri(fullUri);
    }

    @Benchmark
    public String encodeUriWithCharset() {
        return URLCoder.encodeUri(fullUri, utf8);
    }

    @Benchmark
    public String encodeHttpUrl() {
        return URLCoder.encodeHttpUrl(httpUrl);
    }

    @Benchmark
    public String encodeHttpUrlWithCharset() {
        return URLCoder.encodeHttpUrl(httpUrl, utf8);
    }

    @Benchmark
    public String buildPath() {
        return URLCoder.build(path).get();
    }

    @Benchmark
    public String buildNoEncode() {
        return URLCoder.build(path, false).get();
    }

    @Benchmark
    public String buildWithQueryParams() {
        return URLCoder.build(path, false)
                .queryParam(queryName, queryValue)
                .queryParam(langName, langValue)
                .get();
    }

    @Benchmark
    public String buildWithQueryParamObject() {
        return URLCoder.build(path, false)
                .queryParam(countName, countValue)
                .get();
    }
}
