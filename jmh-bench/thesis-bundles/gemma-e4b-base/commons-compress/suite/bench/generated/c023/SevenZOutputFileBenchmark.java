package bench.generated.c023;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SevenZOutputFileBenchmark {

    private byte[] inputData;
    private SevenZArchiveEntry entry;

    @Setup(Level.Trial)
    public void setup() throws IOException {
        // Prepare a fixed input payload
        inputData = new byte[1024 * 10]; // 10 KB payload
        for (int i = 0; i < inputData.length; i++) {
            inputData[i] = (byte) (i % 256);
        }

        // Prepare a representative archive entry
        entry = new SevenZArchiveEntry();
        entry.setName("test_file.bin");
        entry.setDirectory(false);
    }

    /**
     * Benchmarks the process of writing a single entry and finalizing the 7z archive.
     * This measures the overhead of setting up the output stream, writing data,
     * closing the entry, and writing the final archive header/footer.
     */
    @Benchmark
    public void benchmarkWriteSingleEntryAndFinish(Blackhole bh) throws IOException {
        // 1. Setup in-memory channel
        SeekableInMemoryByteChannel channel = new SeekableInMemoryByteChannel();

        // 2. Initialize SUT
        SevenZOutputFile output = new SevenZOutputFile(channel);

        // 3. Add entry
        output.putArchiveEntry(entry);

        // 4. Write data
        output.write(inputData);

        // 5. Close entry (finalizes entry metadata and CRC)
        output.closeArchiveEntry();

        // 6. Finish archive (writes header/footer)
        output.finish();

        // 7. Close SUT and channel
        output.close();
        channel.close();

        // Consume the resulting bytes to prevent dead code elimination
        bh.consume(channel.position());
    }

    /**
     * Benchmarks the process of writing multiple entries and finalizing the 7z archive.
     */
    @Benchmark
    public void benchmarkWriteMultipleEntriesAndFinish(Blackhole bh) throws IOException {
        // 1. Setup in-memory channel
        SeekableInMemoryByteChannel channel = new SeekableInMemoryByteChannel();

        // 2. Initialize SUT
        SevenZOutputFile output = new SevenZOutputFile(channel);

        // 3. Prepare multiple entries
        SevenZArchiveEntry entry1 = new SevenZArchiveEntry();
        entry1.setName("file1.bin");
        entry1.setDirectory(false);

        SevenZArchiveEntry entry2 = new SevenZArchiveEntry();
        entry2.setName("file2.bin");
        entry2.setDirectory(false);

        // 4. Add entries
        output.putArchiveEntry(entry1);
        output.write(inputData);
        output.closeArchiveEntry();

        output.putArchiveEntry(entry2);
        output.write(inputData);
        output.closeArchiveEntry();

        // 5. Finish archive
        output.finish();

        // 6. Close SUT and channel
        output.close();
        channel.close();

        // Consume the resulting bytes
        bh.consume(channel.position());
    }
}
