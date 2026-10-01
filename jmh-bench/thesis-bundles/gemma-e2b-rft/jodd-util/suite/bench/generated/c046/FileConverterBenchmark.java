package bench.generated.c046;

import jodd.io.FileUtil;
import jodd.typeconverter.TypeConversionException;
import jodd.typeconverter.TypeConverter;
import jodd.typeconverter.impl.FileConverter;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FileConverterBenchmark {

    private FileConverter converter;
    private byte[] testBytes;
    private String testString;
    private File testFile;

    @Setup
    public void setup() throws IOException {
        converter = new FileConverter();

        // Setup byte array input
        String content = "This is a test string for byte conversion.";
        testBytes = content.getBytes(StandardCharsets.UTF_8);

        // Setup string input
        testString = "Another string test for file conversion.";

        // Setup File input (requires creating a temporary file for the setup)
        testFile = FileUtil.createTempFile();
        FileUtil.writeBytes(testFile, testBytes);
    }

    @Benchmark
    public void convertNull(Blackhole bh) {
        File result = converter.convert(null);
        bh.consume(result);
    }

    @Benchmark
    public void convertByteArray(Blackhole bh) {
        File result = converter.convert(testBytes);
        bh.consume(result);
    }

    @Benchmark
    public void convertString(Blackhole bh) {
        File result = converter.convert(testString);
        bh.consume(result);
    }

    @Benchmark
    public void convertFile(Blackhole bh) {
        File result = converter.convert(testFile);
        bh.consume(result);
    }
}
