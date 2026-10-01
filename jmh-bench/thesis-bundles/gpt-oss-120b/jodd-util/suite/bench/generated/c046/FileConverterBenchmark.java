package bench.generated.c046;

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
import java.util.concurrent.TimeUnit;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import jodd.io.FileUtil;
import jodd.typeconverter.impl.FileConverter;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FileConverterBenchmark {

    private FileConverter converter;
    private byte[] byteArray;
    private String stringData;
    private File fileData;

    @Setup
    public void setup() {
        converter = new FileConverter();

        byteArray = new byte[1024];
        for (int i = 0; i < byteArray.length; i++) {
            byteArray[i] = (byte) (i & 0xFF);
        }
        stringData = new String(byteArray, StandardCharsets.UTF_8);

        try {
            fileData = FileUtil.createTempFile();
            FileUtil.writeBytes(fileData, byteArray);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Benchmark
    public File convertNull() {
        return converter.convert(null);
    }

    @Benchmark
    public File convertFile() {
        return converter.convert(fileData);
    }

    @Benchmark
    public File convertByteArray() {
        return converter.convert(byteArray);
    }

    @Benchmark
    public File convertString() {
        return converter.convert(stringData);
    }
}
