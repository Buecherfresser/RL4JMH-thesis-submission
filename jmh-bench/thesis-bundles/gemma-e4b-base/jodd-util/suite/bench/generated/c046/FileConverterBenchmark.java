package bench.generated.c046;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.FileConverter;
import jodd.typeconverter.TypeConversionException;
import java.io.File;
import java.io.IOException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FileConverterBenchmark {

    private FileConverter converter;
    private byte[] sampleBytes;
    private String sampleString;
    private File sampleFile;

    @Setup(Level.Trial)
    public void setup() throws TypeConversionException {
        converter = new FileConverter();
        
        // Prepare sample byte array (e.g., 1KB)
        sampleBytes = new byte[1024];
        for (int i = 0; i < 1024; i++) {
            sampleBytes[i] = (byte) (i % 256);
        }

        // Prepare sample string (e.g., 1KB)
        sampleString = "A".repeat(1024);

        // Prepare a sample File object for identity conversion
        try {
            sampleFile = File.createTempFile("test", ".tmp");
            sampleFile.deleteOnExit();
        } catch (IOException e) {
            // Catch IOException from File.createTempFile() and wrap it in TypeConversionException
            throw new TypeConversionException(e);
        }
    }

    @Benchmark
    public File benchmarkConvertBytes(Blackhole bh) throws TypeConversionException {
        File result = converter.convert(sampleBytes);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public File benchmarkConvertString(Blackhole bh) throws TypeConversionException {
        File result = converter.convert(sampleString);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public File benchmarkConvertFileIdentity(Blackhole bh) throws TypeConversionException {
        // Testing the case where the input is already a File
        File result = converter.convert(sampleFile);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public File benchmarkConvertNull(Blackhole bh) throws TypeConversionException {
        // Testing the null input case
        File result = converter.convert(null);
        bh.consume(result);
        return result;
    }
}
