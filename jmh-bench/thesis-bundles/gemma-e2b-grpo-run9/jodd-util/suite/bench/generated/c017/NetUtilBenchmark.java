package bench.generated.c017;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.io.IOException;
import java.net.URL;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class NetUtilBenchmark {

    // No instance state needed as NetUtil methods are static.

    @Setup(Level.Trial)
    public void setup() {
        // Setup logic, if any, goes here.
    }

    // --- Tests for resolveIpAddress ---

    @Benchmark
    public String resolveIpAddress_LocalHost(Blackhole bh) {
        try {
            String result = jodd.io.NetUtil.resolveIpAddress(jodd.io.NetUtil.LOCAL_HOST);
            bh.consume(result);
            return null;
        } catch (Exception e) {
            // Ignore exceptions
        }
        return null;
    }

    @Benchmark
    public String resolveIpAddress_UnknownHost(Blackhole bh) {
        try {
            String result = jodd.io.NetUtil.resolveIpAddress("nonexistent.domain.xyz");
            bh.consume(result);
            return null;
        } catch (Exception e) {
            // Ignore exceptions
        }
        return null;
    }

    // --- Tests for getIpAsInt ---

    @Benchmark
    public Integer getIpAsInt_ValidIp(Blackhole bh) {
        // 192.168.1.1 -> 0xC0A80101 (3232235777)
        Integer result = jodd.io.NetUtil.getIpAsInt("192.168.1.1");
        bh.consume(result);
        return null;
    }

    @Benchmark
    public Integer getIpAsInt_LoopbackIp(Blackhole bh) {
        // 127.0.0.1 -> 0x7f000001 (2130706433)
        Integer result = jodd.io.NetUtil.getIpAsInt("127.0.0.1");
        bh.consume(result);
        return null;
    }

    @Benchmark
    public Integer getIpAsInt_InvalidIp(Blackhole bh) {
        // Should throw NumberFormatException or similar if parsing fails, but we test the path
        try {
            jodd.io.NetUtil.getIpAsInt("invalid.ip.format");
        } catch (Exception e) {
            // Expected behavior for invalid input
        }
        return null;
    }

    // --- Tests for getMaskAsInt ---

    @Benchmark
    public Integer getMaskAsInt_ValidMask(Blackhole bh) {
        // Valid mask, should return a value
        Integer result = jodd.io.NetUtil.getMaskAsInt("255.255.255.0");
        bh.consume(result);
        return null;
    }

    @Benchmark
    public Integer getMaskAsInt_InvalidMask(Blackhole bh) {
        // Invalid mask, should fall back to DEFAULT_MASK
        Integer result = jodd.io.NetUtil.getMaskAsInt("999.999.999.999");
        bh.consume(result);
        return null;
    }

    // --- Tests for isSocketAccessAllowed ---

    @Benchmark
    public Boolean isSocketAccessAllowed_Allowed(Blackhole bh) {
        // Test case where localIp & mask == socketIp & mask (e.g., 127.0.0.1)
        Boolean result = jodd.io.NetUtil.isSocketAccessAllowed(
                jodd.io.NetUtil.INT_VALUE_127_0_0_1,
                jodd.io.NetUtil.INT_VALUE_127_0_0_1,
                0); // Mask doesn't matter much here if socketIp is 127.0.0.1
        bh.consume(result);
        return null;
    }

    @Benchmark
    public Boolean isSocketAccessAllowed_Denied(Blackhole bh) {
        // Test case where access is denied
        Boolean result = jodd.io.NetUtil.isSocketAccessAllowed(
                10,
                20,
                255);
        bh.consume(result);
        return null;
    }

    // --- Tests for downloadBytes (Requires network access) ---

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public byte[] downloadBytes_Success(Blackhole bh) {
        try {
            // Use a URL that is likely to succeed quickly or fail fast, depending on environment
            byte[] result = jodd.io.NetUtil.downloadBytes("http://www.google.com");
            bh.consume(result);
            return null;
        } catch (IOException e) {
            // Expected failure if network is down or URL is bad
        }
        return null;
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public byte[] downloadBytes_Failure(Blackhole bh) {
        try {
            // Use a URL that is highly unlikely to exist or be accessible
            jodd.io.NetUtil.downloadBytes("http://definitely.not.a.real.url/nonexistentfile.txt");
        } catch (IOException e) {
            // Expected exception handling
        }
        return null;
    }

    // --- Tests for getRemoteFileSize (Requires network access) ---

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public Long getRemoteFileSize_Success(Blackhole bh) {
        try {
            // Use a URL that is likely to succeed quickly
            Long result = jodd.io.NetUtil.getRemoteFileSize("http://www.google.com");
            bh.consume(result);
            return null;
        } catch (IOException e) {
            // Expected failure
        }
        return null;
    }
}
