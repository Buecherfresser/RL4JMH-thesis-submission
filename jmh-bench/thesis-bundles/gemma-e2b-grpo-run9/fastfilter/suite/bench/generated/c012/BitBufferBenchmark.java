package bench.generated.c012;

import org.fastfilter.gcs.BitBuffer;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BitBufferBenchmark {

    @Benchmark
    public void testStaticFoldSigned(Blackhole bh) {
        long x = -5L;
        long result = BitBuffer.foldSigned(x);
        bh.consume(result);
    }

    @Benchmark
    public void testStaticUnfoldSigned(Blackhole bh) {
        long x = 10L;
        long result = BitBuffer.unfoldSigned(x);
        bh.consume(result);
    }

    @Benchmark
    public void testWriteBit(Blackhole bh) {
        // Create a fresh buffer instance for each invocation
        BitBuffer buffer = new BitBuffer(1024);
        buffer.writeBit(1L);
        bh.consume(buffer);
    }

    @Benchmark
    public void testReadBit(Blackhole bh) {
        // Create a fresh buffer instance for each invocation
        BitBuffer buffer = new BitBuffer(1024);
        // Write something first to ensure readBit doesn't immediately fail
        buffer.writeBit(0L);
        bh.consume(buffer.readBit());
    }

    @Benchmark
    public void testWriteGolombRiceFast(Blackhole bh) {
        // Create a fresh buffer instance for each invocation
        BitBuffer buffer = new BitBuffer(1024);
        try {
            buffer.writeGolombRiceFast(5, 100L);
        } catch (Exception e) {
            // Ignore exceptions if they occur during benchmarking setup
        }
        bh.consume(buffer);
    }

    @Benchmark
    public void testReadEliasDelta(Blackhole bh) {
        // Create a fresh buffer instance for each invocation
        BitBuffer buffer = new BitBuffer(1024);
        try {
            // Write some data to ensure readBit doesn't immediately fail
            buffer.writeBit(1L);
            buffer.writeBit(0L);
            bh.consume(buffer.readEliasDelta());
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void testWriteNumber(Blackhole bh) {
        // Create a fresh buffer instance for each invocation
        BitBuffer buffer = new BitBuffer(1024);
        try {
            // Write a number that requires two longs (e.g., 64 bits)
            buffer.writeNumber(0xDEADBEEFL, 64);
            bh.consume(buffer);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }
}
