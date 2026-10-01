package bench.generated.c017;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

import jodd.io.NetUtil;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class NetUtilBenchmark {

    private String validIp;
    private String invalidIp;
    private String defaultMask;
    private int localIpInt;
    private int socketIpInt;
    private int maskInt;

    @Setup(Level.Trial)
    public void setup() {
        validIp = "192.168.1.1";
        invalidIp = "999.999.999.999";
        defaultMask = "255.255.255.0";
        localIpInt = NetUtil.getIpAsInt(validIp);
        socketIpInt = NetUtil.getIpAsInt("192.168.1.100");
        maskInt = NetUtil.getMaskAsInt(defaultMask);
    }

    @Benchmark
    public int getIpAsInt() {
        return NetUtil.getIpAsInt(validIp);
    }

    @Benchmark
    public int getMaskAsIntValid() {
        return NetUtil.getMaskAsInt(defaultMask);
    }

    @Benchmark
    public int getMaskAsIntInvalid() {
        return NetUtil.getMaskAsInt(invalidIp);
    }

    @Benchmark
    public boolean validateIPv4Valid() {
        return NetUtil.validateIPv4(validIp);
    }

    @Benchmark
    public boolean validateIPv4Invalid() {
        return NetUtil.validateIPv4(invalidIp);
    }

    @Benchmark
    public boolean isSocketAccessAllowed() {
        return NetUtil.isSocketAccessAllowed(localIpInt, socketIpInt, maskInt);
    }
}
