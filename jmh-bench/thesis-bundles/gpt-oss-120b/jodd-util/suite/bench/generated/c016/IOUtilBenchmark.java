package bench.generated.c016;

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
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.io.IOUtil;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class IOUtilBenchmark {

    private byte[] payload;
    private String text;
    private Charset utf8;

    @Setup
    public void setup() {
        int size = 1024;
        payload = new byte[size];
        for (int i = 0; i < size; i++) {
            payload[i] = (byte) (i & 0xFF);
        }
        text = new String(payload, StandardCharsets.UTF_8);
        utf8 = StandardCharsets.UTF_8;
    }

    // ---------------------------------------------------------------- close

    @Benchmark
    public void benchmarkClose(Blackhole bh) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IOUtil.close(baos);
        bh.consume(baos);
    }

    // ---------------------------------------------------------------- copy Reader -> Writer

    @Benchmark
    public int benchmarkCopyReaderWriter() throws Exception {
        Reader in = new StringReader(text);
        Writer out = new StringWriter();
        return IOUtil.copy(in, out);
    }

    @Benchmark
    public int benchmarkCopyReaderWriterCount() throws Exception {
        Reader in = new StringReader(text);
        Writer out = new StringWriter();
        int half = text.length() / 2;
        return IOUtil.copy(in, out, half);
    }

    // ---------------------------------------------------------------- copy InputStream -> OutputStream

    @Benchmark
    public int benchmarkCopyInputStreamOutputStream() throws Exception {
        InputStream in = new ByteArrayInputStream(payload);
        OutputStream out = new ByteArrayOutputStream();
        return IOUtil.copy(in, out);
    }

    @Benchmark
    public int benchmarkCopyInputStreamOutputStreamCount() throws Exception {
        InputStream in = new ByteArrayInputStream(payload);
        OutputStream out = new ByteArrayOutputStream();
        int half = payload.length / 2;
        return IOUtil.copy(in, out, half);
    }

    // ---------------------------------------------------------------- readAvailableBytes

    @Benchmark
    public byte[] benchmarkReadAvailableBytes() throws Exception {
        InputStream in = new ByteArrayInputStream(payload);
        return IOUtil.readAvailableBytes(in);
    }

    // ---------------------------------------------------------------- copy Reader -> OutputStream (various overloads)

    @Benchmark
    public OutputStream benchmarkCopyReaderToOutputStream() throws Exception {
        Reader in = new StringReader(text);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        return IOUtil.copy(in, out);
    }

    @Benchmark
    public OutputStream benchmarkCopyReaderToOutputStreamCount() throws Exception {
        Reader in = new StringReader(text);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int half = text.length() / 2;
        return IOUtil.copy(in, out, half);
    }

    @Benchmark
    public OutputStream benchmarkCopyReaderToOutputStreamCharset() throws Exception {
        Reader in = new StringReader(text);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        return IOUtil.copy(in, out, utf8);
    }

    @Benchmark
    public OutputStream benchmarkCopyReaderToOutputStreamCharsetCount() throws Exception {
        Reader in = new StringReader(text);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int half = text.length() / 2;
        return IOUtil.copy(in, out, utf8, half);
    }

    // ---------------------------------------------------------------- copyToOutputStream (InputStream)

    @Benchmark
    public ByteArrayOutputStream benchmarkCopyToOutputStreamInputStream() throws Exception {
        InputStream in = new ByteArrayInputStream(payload);
        return IOUtil.copyToOutputStream(in);
    }

    @Benchmark
    public ByteArrayOutputStream benchmarkCopyToOutputStreamInputStreamCount() throws Exception {
        InputStream in = new ByteArrayInputStream(payload);
        int half = payload.length / 2;
        return IOUtil.copyToOutputStream(in, half);
    }

    // ---------------------------------------------------------------- copyToOutputStream (Reader)

    @Benchmark
    public ByteArrayOutputStream benchmarkCopyToOutputStreamReader() throws Exception {
        Reader in = new StringReader(text);
        return IOUtil.copyToOutputStream(in);
    }

    @Benchmark
    public ByteArrayOutputStream benchmarkCopyToOutputStreamReaderCharset() throws Exception {
        Reader in = new StringReader(text);
        return IOUtil.copyToOutputStream(in, utf8);
    }

    @Benchmark
    public ByteArrayOutputStream benchmarkCopyToOutputStreamReaderCount() throws Exception {
        Reader in = new StringReader(text);
        int half = text.length() / 2;
        return IOUtil.copyToOutputStream(in, half);
    }

    @Benchmark
    public ByteArrayOutputStream benchmarkCopyToOutputStreamReaderCharsetCount() throws Exception {
        Reader in = new StringReader(text);
        int half = text.length() / 2;
        return IOUtil.copyToOutputStream(in, utf8, half);
    }

    // ---------------------------------------------------------------- copy InputStream -> Writer

    @Benchmark
    public Writer benchmarkCopyInputStreamToWriter() throws Exception {
        InputStream in = new ByteArrayInputStream(payload);
        StringWriter out = new StringWriter();
        return IOUtil.copy(in, out);
    }

    @Benchmark
    public Writer benchmarkCopyInputStreamToWriterCount() throws Exception {
        InputStream in = new ByteArrayInputStream(payload);
        StringWriter out = new StringWriter();
        int half = payload.length / 2;
        return IOUtil.copy(in, out, half);
    }

    @Benchmark
    public Writer benchmarkCopyInputStreamToWriterCharset() throws Exception {
        InputStream in = new ByteArrayInputStream(payload);
        StringWriter out = new StringWriter();
        return IOUtil.copy(in, out, utf8);
    }

    @Benchmark
    public Writer benchmarkCopyInputStreamToWriterCharsetCount() throws Exception {
        InputStream in = new ByteArrayInputStream(payload);
        StringWriter out = new StringWriter();
        int half = payload.length / 2;
        return IOUtil.copy(in, out, utf8, half);
    }

    // ---------------------------------------------------------------- copy InputStream -> CharArrayWriter

    @Benchmark
    public CharArrayWriter benchmarkCopyInputStreamToCharArrayWriter() throws Exception {
        InputStream in = new ByteArrayInputStream(payload);
        return IOUtil.copy(in);
    }

    @Benchmark
    public CharArrayWriter benchmarkCopyInputStreamToCharArrayWriterCount() throws Exception {
        InputStream in = new ByteArrayInputStream(payload);
        int half = payload.length / 2;
        return IOUtil.copy(in, half);
    }

    @Benchmark
    public CharArrayWriter benchmarkCopyInputStreamToCharArrayWriterCharset() throws Exception {
        InputStream in = new ByteArrayInputStream(payload);
        return IOUtil.copy(in, utf8);
    }

    @Benchmark
    public CharArrayWriter benchmarkCopyInputStreamToCharArrayWriterCharsetCount() throws Exception {
        InputStream in = new ByteArrayInputStream(payload);
        int half = payload.length / 2;
        return IOUtil.copy(in, utf8, half);
    }

    // ---------------------------------------------------------------- copy Reader -> CharArrayWriter

    @Benchmark
    public CharArrayWriter benchmarkCopyReaderToCharArrayWriter() throws Exception {
        Reader in = new StringReader(text);
        return IOUtil.copy(in);
    }

    @Benchmark
    public CharArrayWriter benchmarkCopyReaderToCharArrayWriterCount() throws Exception {
        Reader in = new StringReader(text);
        int half = text.length() / 2;
        return IOUtil.copy(in, half);
    }

    // ---------------------------------------------------------------- readBytes (InputStream)

    @Benchmark
    public byte[] benchmarkReadBytesInputStream() throws Exception {
        InputStream in = new ByteArrayInputStream(payload);
        return IOUtil.readBytes(in);
    }

    @Benchmark
    public byte[] benchmarkReadBytesInputStreamCount() throws Exception {
        InputStream in = new ByteArrayInputStream(payload);
        int half = payload.length / 2;
        return IOUtil.readBytes(in, half);
    }

    // ---------------------------------------------------------------- readBytes (Reader)

    @Benchmark
    public byte[] benchmarkReadBytesReader() throws Exception {
        Reader in = new StringReader(text);
        return IOUtil.readBytes(in);
    }

    @Benchmark
    public byte[] benchmarkReadBytesReaderCount() throws Exception {
        Reader in = new StringReader(text);
        int half = text.length() / 2;
        return IOUtil.readBytes(in, half);
    }

    @Benchmark
    public byte[] benchmarkReadBytesReaderCharset() throws Exception {
        Reader in = new StringReader(text);
        return IOUtil.readBytes(in, utf8);
    }

    @Benchmark
    public byte[] benchmarkReadBytesReaderCharsetCount() throws Exception {
        Reader in = new StringReader(text);
        int half = text.length() / 2;
        return IOUtil.readBytes(in, utf8, half);
    }

    // ---------------------------------------------------------------- readChars (Reader)

    @Benchmark
    public char[] benchmarkReadCharsReader() throws Exception {
        Reader in = new StringReader(text);
        return IOUtil.readChars(in);
    }

    @Benchmark
    public char[] benchmarkReadCharsReaderCount() throws Exception {
        Reader in = new StringReader(text);
        int half = text.length() / 2;
        return IOUtil.readChars(in, half);
    }

    // ---------------------------------------------------------------- readChars (InputStream)

    @Benchmark
    public char[] benchmarkReadCharsInputStream() throws Exception {
        InputStream in = new ByteArrayInputStream(payload);
        return IOUtil.readChars(in);
    }

    @Benchmark
    public char[] benchmarkReadCharsInputStreamCount() throws Exception {
        InputStream in = new ByteArrayInputStream(payload);
        int half = payload.length / 2;
        return IOUtil.readChars(in, half);
    }

    @Benchmark
    public char[] benchmarkReadCharsInputStreamCharset() throws Exception {
        InputStream in = new ByteArrayInputStream(payload);
        return IOUtil.readChars(in, utf8);
    }

    @Benchmark
    public char[] benchmarkReadCharsInputStreamCharsetCount() throws Exception {
        InputStream in = new ByteArrayInputStream(payload);
        int half = payload.length / 2;
        return IOUtil.readChars(in, utf8, half);
    }

    // ---------------------------------------------------------------- compare InputStreams

    @Benchmark
    public boolean benchmarkCompareInputStreams() throws Exception {
        InputStream a = new ByteArrayInputStream(payload);
        InputStream b = new ByteArrayInputStream(payload);
        return IOUtil.compare(a, b);
    }

    // ---------------------------------------------------------------- compare Readers

    @Benchmark
    public boolean benchmarkCompareReaders() throws Exception {
        Reader a = new StringReader(text);
        Reader b = new StringReader(text);
        return IOUtil.compare(a, b);
    }

    // ---------------------------------------------------------------- wrappers

    @Benchmark
    public InputStreamReader benchmarkInputStreamReader() throws Exception {
        InputStream in = new ByteArrayInputStream(payload);
        return IOUtil.inputStreamReadeOf(in);
    }

    @Benchmark
    public InputStreamReader benchmarkInputStreamReaderCharset() throws Exception {
        InputStream in = new ByteArrayInputStream(payload);
        return IOUtil.inputStreamReadeOf(in, utf8);
    }

    @Benchmark
    public OutputStreamWriter benchmarkOutputStreamWriter() throws Exception {
        OutputStream out = new ByteArrayOutputStream();
        return IOUtil.outputStreamWriterOf(out);
    }

    @Benchmark
    public OutputStreamWriter benchmarkOutputStreamWriterCharset() throws Exception {
        OutputStream out = new ByteArrayOutputStream();
        return IOUtil.outputStreamWriterOf(out, utf8);
    }
}
