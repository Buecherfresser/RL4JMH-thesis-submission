package bench.generated.c009;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.constructor.SafeConstructor;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SafeConstructorBenchmark {

    private Yaml yaml;
    private String yamlNull;
    private String yamlBoolTrue;
    private String yamlBoolFalse;
    private String yamlIntDecimal;
    private String yamlIntHex;
    private String yamlIntOctal;
    private String yamlIntBinary;
    private String yamlIntSexagesimal;
    private String yamlFloatSimple;
    private String yamlFloatInfinity;
    private String yamlFloatNaN;
    private String yamlBinary;
    private String yamlTimestampDate;
    private String yamlTimestampFull;
    private String yamlOmap;
    private String yamlPairs;
    private String yamlSet;
    private String yamlStr;
    private String yamlSeq;
    private String yamlMap;

    @Setup(Level.Trial)
    public void setup() {
        LoaderOptions loaderOptions = new LoaderOptions();
        yaml = new Yaml(new SafeConstructor(loaderOptions));

        yamlNull = "null";
        yamlBoolTrue = "yes";
        yamlBoolFalse = "no";
        yamlIntDecimal = "12345";
        yamlIntHex = "0x1A2B";
        yamlIntOctal = "0755";
        yamlIntBinary = "0b101010";
        yamlIntSexagesimal = "1:2:3"; // 1*60^2 + 2*60 + 3 = 3723
        yamlFloatSimple = "3.14159";
        yamlFloatInfinity = ".inf";
        yamlFloatNaN = ".nan";
        yamlBinary = "!!binary |\n  R0lGODdhAQABAIAAAAUEBA=="; // base64 for GIF header
        yamlTimestampDate = "2023-01-15";
        yamlTimestampFull = "2023-01-15T12:34:56.789+02:00";
        yamlOmap = "- - a: 1\n  - b: 2";
        yamlPairs = "- - key1: val1\n  - key2: val2";
        yamlSet = "!!set {a, b, c}";
        yamlStr = "\"Hello, World!\"";
        yamlSeq = "- one\n- two\n- three";
        yamlMap = "{name: John, age: 30}";
    }

    @Benchmark
    public Object benchmarkNull() {
        return yaml.load(yamlNull);
    }

    @Benchmark
    public Object benchmarkBoolTrue() {
        return yaml.load(yamlBoolTrue);
    }

    @Benchmark
    public Object benchmarkBoolFalse() {
        return yaml.load(yamlBoolFalse);
    }

    @Benchmark
    public Object benchmarkIntDecimal() {
        return yaml.load(yamlIntDecimal);
    }

    @Benchmark
    public Object benchmarkIntHex() {
        return yaml.load(yamlIntHex);
    }

    @Benchmark
    public Object benchmarkIntOctal() {
        return yaml.load(yamlIntOctal);
    }

    @Benchmark
    public Object benchmarkIntBinary() {
        return yaml.load(yamlIntBinary);
    }

    @Benchmark
    public Object benchmarkIntSexagesimal() {
        return yaml.load(yamlIntSexagesimal);
    }

    @Benchmark
    public Object benchmarkFloatSimple() {
        return yaml.load(yamlFloatSimple);
    }

    @Benchmark
    public Object benchmarkFloatInfinity() {
        return yaml.load(yamlFloatInfinity);
    }

    @Benchmark
    public Object benchmarkFloatNaN() {
        return yaml.load(yamlFloatNaN);
    }

    @Benchmark
    public Object benchmarkBinary() {
        return yaml.load(yamlBinary);
    }

    @Benchmark
    public Object benchmarkTimestampDate() {
        return yaml.load(yamlTimestampDate);
    }

    @Benchmark
    public Object benchmarkTimestampFull() {
        return yaml.load(yamlTimestampFull);
    }

    @Benchmark
    public Object benchmarkOmap() {
        return yaml.load(yamlOmap);
    }

    @Benchmark
    public Object benchmarkPairs() {
        return yaml.load(yamlPairs);
    }

    @Benchmark
    public Object benchmarkSet() {
        return yaml.load(yamlSet);
    }

    @Benchmark
    public Object benchmarkStr() {
        return yaml.load(yamlStr);
    }

    @Benchmark
    public Object benchmarkSeq() {
        return yaml.load(yamlSeq);
    }

    @Benchmark
    public Object benchmarkMap() {
        return yaml.load(yamlMap);
    }
}
