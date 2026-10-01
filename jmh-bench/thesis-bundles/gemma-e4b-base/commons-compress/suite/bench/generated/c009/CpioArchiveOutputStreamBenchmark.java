package bench.generated.c009;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.archivers.cpio.CpioArchiveOutputStream;
import org.apache.commons.compress.archivers.cpio.CpioArchiveEntry;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CpioArchiveOutputStreamBenchmark {

    private ByteArrayOutputStream baos;
    private byte[] sampleData;
    private CpioArchiveEntry sampleEntry;
    private File dummyFile;

    @Setup(Level.Trial)
    public void setup() throws IOException {
        // Setup common resources once per trial
        baos = new ByteArrayOutputStream();
        
        // 1. Create a dummy file/path required by createArchiveEntry
        dummyFile = File.createTempFile("dummy", ".txt");
        dummyFile.deleteOnExit();
        Path dummyPath = dummyFile.toPath();

        // 2. Use a temporary stream instance to create a valid CpioArchiveEntry
        // We use a minimal stream setup just to call the factory method.
        CpioArchiveOutputStream tempStream = new CpioArchiveOutputStream(baos);
        
        // Use the public API to create the entry
        sampleEntry = tempStream.createArchiveEntry(dummyFile, "testfile.txt");
        
        // Manually set properties that are usually set by the file system/metadata.
        // FIX: Replaced setFileSize(int) with setSize(long) to match expected API signature.
        sampleEntry.setSize(1024L);
        sampleEntry.setMode(CpioArchiveEntry.C_ISREG);
        
        // Create sample data payload
        sampleData = new byte[1024];
        for (int i = 0; i < 1024; i++) {
            sampleData[i] = (byte) (i % 256);
        }
    }

    /**
     * Benchmarks the creation of a new CpioArchiveEntry.
     * Note: This tests the entry creation logic, not the stream interaction.
     */
    @Benchmark
    public CpioArchiveEntry benchmarkCreateEntry() throws IOException {
        // Since CpioArchiveEntry creation requires a File/Path, we must use the stream's factory method.
        // We reuse the dummy file created in setup.
        CpioArchiveOutputStream tempStream = new CpioArchiveOutputStream(baos);
        return tempStream.createArchiveEntry(dummyFile, "testfile.txt");
    }

    /**
     * Benchmarks starting a new CPIO entry using putArchiveEntry.
     * Requires recreating the stream for isolation.
     */
    @Benchmark
    public void benchmarkPutArchiveEntry(Blackhole bh) throws IOException {
        // Setup stream for this invocation
        ByteArrayOutputStream invocationBaos = new ByteArrayOutputStream();
        CpioArchiveOutputStream stream = new CpioArchiveOutputStream(invocationBaos);
        
        try {
            // Action
            stream.putArchiveEntry(sampleEntry);
            
            // Consume result
            bh.consume(stream);
        } finally {
            stream.close();
        }
    }

    /**
     * Benchmarks writing data into the current CPIO entry using write(byte[], int, int).
     * Requires recreating the stream and putting an entry first.
     */
    @Benchmark
    public void benchmarkWriteData(Blackhole bh) throws IOException {
        // Setup stream for this invocation
        ByteArrayOutputStream invocationBaos = new ByteArrayOutputStream();
        CpioArchiveOutputStream stream = new CpioArchiveOutputStream(invocationBaos);
        
        try {
            // Setup state: put entry
            stream.putArchiveEntry(sampleEntry);
            
            // Action
            stream.write(sampleData, 0, sampleData.length);
            
            // Consume result
            bh.consume(stream);
        } finally {
            stream.closeArchiveEntry();
            stream.close();
        }
    }

    /**
     * Benchmarks finalizing the current CPIO entry using closeArchiveEntry().
     * Requires recreating the stream and writing data first.
     */
    @Benchmark
    public void benchmarkCloseArchiveEntry(Blackhole bh) throws IOException {
        // Setup stream for this invocation
        ByteArrayOutputStream invocationBaos = new ByteArrayOutputStream();
        CpioArchiveOutputStream stream = new CpioArchiveOutputStream(invocationBaos);
        
        try {
            // Setup state: put entry and write data
            stream.putArchiveEntry(sampleEntry);
            stream.write(sampleData, 0, sampleData.length);
            
            // Action
            stream.closeArchiveEntry();
            
            // Consume result
            bh.consume(stream);
        } finally {
            stream.close();
        }
    }

    /**
     * Benchmarks finalizing the entire CPIO archive using finish().
     * Requires recreating the stream and closing at least one entry.
     */
    @Benchmark
    public void benchmarkFinishArchive(Blackhole bh) throws IOException {
        // Setup stream for this invocation
        ByteArrayOutputStream invocationBaos = new ByteArrayOutputStream();
        CpioArchiveOutputStream stream = new CpioArchiveOutputStream(invocationBaos);
        
        try {
            // Setup state: put entry and close it
            stream.putArchiveEntry(sampleEntry);
            stream.write(sampleData, 0, sampleData.length);
            stream.closeArchiveEntry();
            
            // Action
            stream.finish();
            
            // Consume result
            bh.consume(stream);
        } finally {
            stream.close();
        }
    }
}
