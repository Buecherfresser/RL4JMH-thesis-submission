package bench.generated.c065;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.net.URI;
import java.net.URL;
import java.io.File;
import java.net.URISyntaxException;

import jodd.typeconverter.impl.URIConverter;
import jodd.typeconverter.TypeConversionException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class URIConverterBenchmark {

    private URIConverter converter;

    @Setup
    public void setup() {
        // Initialize the converter instance once per benchmark run
        this.converter = new URIConverter();
    }

    @Benchmark
    public void testNull(Blackhole bh) {
        try {
            URI result = converter.convert(null);
            if (result != null) {
                bh.consume(result);
            }
        } catch (Exception e) {
            // Ignore exceptions for this simple null test
        }
    }

    @Benchmark
    public void testExistingURI(Blackhole bh) {
        // Create a valid URI instance for testing the instance branch
        URI existingUri = null;
        try {
            existingUri = new URI("http://example.com");
        } catch (URISyntaxException e) {
            // Should not happen for a simple string
        }

        try {
            URI result = converter.convert(existingUri);
            if (result != null) {
                bh.consume(result);
            }
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void testStringConversion(Blackhole bh) {
        // Test the fallback path: new URI(String)
        String input = "http://test.com/path?q=1";
        try {
            URI result = converter.convert(input);
            if (result != null) {
                bh.consume(result);
            }
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void testGenericObjectConversion(Blackhole bh) {
        // Test the fallback path for a non-URI/File/URL object
        // Using a simple String that won't trigger specific type checks
        String input = "some arbitrary data";
        try {
            URI result = converter.convert(input);
            if (result != null) {
                bh.consume(result);
            }
        } catch (Exception e) {
            // Ignore exceptions
        }
    }
}
