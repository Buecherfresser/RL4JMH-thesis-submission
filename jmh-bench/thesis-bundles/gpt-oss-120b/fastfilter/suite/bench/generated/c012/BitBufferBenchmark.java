package bench.generated.c012;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.fastfilter.gcs.BitBuffer;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BitBufferBenchmark {

    private BitBuffer buffer;
    private BitBuffer sourceBuffer;
    private BitBuffer destBuffer;

    private int shift;
    private long golombValue;
    private long eliasValue;
    private long numberValue;
    private int numberBits;

    @Setup(Level.Trial)
    public void setup() {
        int bits = 1024;
        buffer = new BitBuffer(bits);
        sourceBuffer = new BitBuffer(bits);
        destBuffer = new BitBuffer(bits);

        shift = 5;
        golombValue = 123456L;
        eliasValue = 987654321L;
        numberValue = 0xABCDEF12345L;
        numberBits = 40; // <=63

        // Prepare sourceBuffer for copy benchmark
        sourceBuffer.clear();
        sourceBuffer.seek(0);
        for (int i = 0; i < 64; i++) {
            sourceBuffer.writeBit(i % 2);
        }

        // Prepare buffer for readNumber benchmark
        buffer.clear();
        buffer.seek(0);
        buffer.writeNumber(numberValue, numberBits);

        // Prepare buffer for readEliasDelta benchmark
        buffer.clear();
        buffer.seek(0);
        buffer.writeEliasDelta(eliasValue);

        // Prepare buffer for readUntilZero benchmark: 10 ones followed by a zero
        buffer.clear();
        buffer.seek(0);
        for (int i = 0; i < 10; i++) {
            buffer.writeBit(1L);
        }
        buffer.writeBit(0L);
        buffer.seek(0);
    }

    @Benchmark
    public int benchmarkWriteBit() {
        buffer.clear();
        buffer.seek(0);
        buffer.writeBit(1L);
        return buffer.position();
    }

    @Benchmark
    public long benchmarkReadBit() {
        buffer.clear();
        buffer.seek(0);
        buffer.writeBit(1L);
        buffer.seek(0);
        return buffer.readBit();
    }

    @Benchmark
    public int benchmarkWriteNumber() {
        buffer.clear();
        buffer.seek(0);
        buffer.writeNumber(numberValue, numberBits);
        return buffer.position();
    }

    @Benchmark
    public long benchmarkReadNumber() {
        buffer.seek(0);
        return buffer.readNumber(numberBits);
    }

    @Benchmark
    public int benchmarkWriteGolombRice() {
        buffer.clear();
        buffer.seek(0);
        buffer.writeGolombRice(shift, golombValue);
        return buffer.position();
    }

    @Benchmark
    public int benchmarkWriteGolombRiceFast() {
        buffer.clear();
        buffer.seek(0);
        buffer.writeGolombRiceFast(shift, golombValue);
        return buffer.position();
    }

    @Benchmark
    public int benchmarkWriteEliasDelta() {
        buffer.clear();
        buffer.seek(0);
        buffer.writeEliasDelta(eliasValue);
        return buffer.position();
    }

    @Benchmark
    public long benchmarkReadEliasDelta() {
        buffer.seek(0);
        return buffer.readEliasDelta();
    }

    @Benchmark
    public int benchmarkSkipGolombRice() {
        buffer.clear();
        buffer.seek(0);
        buffer.writeGolombRice(shift, golombValue);
        buffer.skipGolombRice(shift);
        return buffer.position();
    }

    @Benchmark
    public int benchmarkReadUntilZero() {
        buffer.seek(0);
        return buffer.readUntilZero(0);
    }

    @Benchmark
    public int benchmarkWriteCopy() {
        destBuffer.clear();
        destBuffer.seek(0);
        destBuffer.write(sourceBuffer);
        return destBuffer.position();
    }

    @Benchmark
    public int benchmarkClear() {
        buffer.clear();
        return (int) buffer.data[0];
    }

    @Benchmark
    public int benchmarkPosition() {
        buffer.seek(123);
        return buffer.position();
    }

    @Benchmark
    public int benchmarkSeek() {
        buffer.seek(456);
        return buffer.position();
    }

    @Benchmark
    public long benchmarkFoldSigned() {
        return BitBuffer.foldSigned(-12345L);
    }

    @Benchmark
    public long benchmarkUnfoldSigned() {
        return BitBuffer.unfoldSigned(24690L);
    }

    @Benchmark
    public int benchmarkGetEliasDeltaSize() {
        return BitBuffer.getEliasDeltaSize(eliasValue);
    }
}
