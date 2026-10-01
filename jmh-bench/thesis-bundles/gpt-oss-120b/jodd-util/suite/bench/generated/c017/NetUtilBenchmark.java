package bench.generated.c017;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.io.NetUtil;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class NetUtilBenchmark {

    // Input data prepared once per trial
    private String localhost;
    private String nullHost;
    private String sampleIp;
    private String validMask;
    private String invalidMask;
    private int sampleIpInt;
    private int validMaskInt;
    private int localIpForSubnet;
    private int sameSubnetIp;
    private int maskForSubnet;
    private int loopbackIp;
    private byte[] loopbackBytes;

    @Setup(Level.Trial)
    public void setUp() {
        localhost = "localhost";
        nullHost = null;
        sampleIp = "192.168.1.1";
        validMask = "255.255.255.0";
        invalidMask = "invalid.mask";
        sampleIpInt = NetUtil.getIpAsInt(sampleIp);
        validMaskInt = NetUtil.getMaskAsInt(validMask);
        // Subnet test data
        localIpForSubnet = NetUtil.getIpAsInt("192.168.1.10");
        sameSubnetIp = NetUtil.getIpAsInt("192.168.1.20");
        maskForSubnet = NetUtil.getMaskAsInt("255.255.255.0");
        // Loopback data
        loopbackIp = NetUtil.INT_VALUE_127_0_0_1;
        loopbackBytes = new byte[] {127, 0, 0, 1};
    }

    @Benchmark
    public String resolveIpAddressLocalhost() {
        return NetUtil.resolveIpAddress(localhost);
    }

    @Benchmark
    public String resolveIpAddressNull() {
        return NetUtil.resolveIpAddress(nullHost);
    }

    @Benchmark
    public int getIpAsIntSample() {
        return NetUtil.getIpAsInt(sampleIp);
    }

    @Benchmark
    public int getMaskAsIntValid() {
        return NetUtil.getMaskAsInt(validMask);
    }

    @Benchmark
    public int getMaskAsIntInvalid() {
        return NetUtil.getMaskAsInt(invalidMask);
    }

    @Benchmark
    public boolean isSocketAccessAllowedSameSubnet() {
        return NetUtil.isSocketAccessAllowed(localIpForSubnet, sameSubnetIp, maskForSubnet);
    }

    @Benchmark
    public boolean isSocketAccessAllowedLoopback() {
        return NetUtil.isSocketAccessAllowed(localIpForSubnet, loopbackIp, maskForSubnet);
    }

    @Benchmark
    public boolean validateIPv4Valid() {
        return NetUtil.validateIPv4("10.0.0.1");
    }

    @Benchmark
    public boolean validateIPv4Invalid() {
        return NetUtil.validateIPv4("999.999.999.999");
    }

    @Benchmark
    public String resolveHostNameLoopback() {
        return NetUtil.resolveHostName(loopbackBytes);
    }
}
