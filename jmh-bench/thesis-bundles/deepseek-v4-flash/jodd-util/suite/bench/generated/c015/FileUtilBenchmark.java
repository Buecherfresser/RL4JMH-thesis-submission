package bench.generated.c015;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.io.FileUtil;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FileUtilBenchmark {

    private File tempDir;
    private File textFile;
    private File binaryFile;
    private File destFile;
    private File destDir;
    private File srcDir;
    private File existingDir;
    private File referenceFile;
    private File ancestor;
    private File descendant;
    private File nonExistingFile;
    private String payloadString;
    private byte[] payloadBytes;
    private char[] payloadChars;
    private String homePath;
    private String tempPrefix;
    private String tempSuffix;
    private Charset charset;
    private URL fileURL;
    private FileFilter acceptAllFilter;
    private long referenceTime;

    @Setup(Level.Trial)
    public void setup() throws IOException {
        tempDir = Files.createTempDirectory("jmh-fileutil").toFile();
        tempDir.deleteOnExit();
        textFile = new File(tempDir, "text.txt");
        binaryFile = new File(tempDir, "binary.dat");
        destFile = new File(tempDir, "dest.txt");
        destDir = new File(tempDir, "destDir");
        srcDir = new File(tempDir, "srcDir");
        existingDir = new File(tempDir, "existingDir");
        referenceFile = new File(tempDir, "reference.txt");
        ancestor = tempDir;
        descendant = textFile;
        nonExistingFile = new File(tempDir, "does-not-exist.txt");

        destDir.mkdirs();
        srcDir.mkdirs();
        existingDir.mkdirs();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            sb.append("line ").append(i).append(" - some text to make it non-trivial\n");
        }
        payloadString = sb.toString();
        payloadBytes = payloadString.getBytes(StandardCharsets.UTF_8);
        payloadChars = payloadString.toCharArray();

        FileUtil.writeString(textFile, payloadString);
        FileUtil.writeBytes(binaryFile, payloadBytes);
        FileUtil.writeString(destFile, payloadString);
        FileUtil.writeString(new File(srcDir, "a.txt"), payloadString);
        FileUtil.writeString(new File(srcDir, "b.txt"), payloadString);
        FileUtil.writeString(referenceFile, "reference");
        referenceFile.setLastModified(System.currentTimeMillis() - 100_000);

        fileURL = textFile.toURI().toURL();
        homePath = "~/some/path";
        tempPrefix = "jmh-";
        tempSuffix = ".tmp";
        charset = StandardCharsets.UTF_8;
        acceptAllFilter = file -> true;
        referenceTime = System.currentTimeMillis() - 1000;
    }

    @Benchmark
    public File file() {
        return FileUtil.file(homePath);
    }

    @Benchmark
    public boolean equalsString() {
        return FileUtil.equals(textFile.getPath(), textFile.getPath());
    }

    @Benchmark
    public boolean equalsFile() {
        return FileUtil.equals(textFile, textFile);
    }

    @Benchmark
    public File toFile() {
        return FileUtil.toFile(fileURL);
    }

    @Benchmark
    public URL toURL() throws IOException {
        return FileUtil.toURL(textFile);
    }

    @Benchmark
    public String toFileName() {
        return FileUtil.toFileName(fileURL);
    }

    @Benchmark
    public File toContainerFile() {
        return FileUtil.toContainerFile(fileURL);
    }

    @Benchmark
    public boolean isExistingFile() {
        return FileUtil.isExistingFile(textFile);
    }

    @Benchmark
    public boolean isExistingFolder() {
        return FileUtil.isExistingFolder(existingDir);
    }

    @Benchmark
    public File mkdirsExisting() throws IOException {
        return FileUtil.mkdirs(existingDir);
    }

    @Benchmark
    public File mkdirExisting() throws IOException {
        return FileUtil.mkdir(existingDir);
    }

    @Benchmark
    public void touch(Blackhole bh) throws IOException {
        FileUtil.touch(textFile);
        bh.consume(textFile.lastModified());
    }

    @Benchmark
    public void copyFile(Blackhole bh) throws IOException {
        FileUtil.copyFile(textFile, destFile);
        bh.consume(destFile.length());
    }

    @Benchmark
    public File copyFileToDir() throws IOException {
        return FileUtil.copyFileToDir(textFile, destDir);
    }

    @Benchmark
    public void copyDir(Blackhole bh) throws IOException {
        FileUtil.copyDir(srcDir, destDir);
        bh.consume(destDir.lastModified());
    }

    @Benchmark
    public char[] readUTFCharsFile() throws IOException {
        return FileUtil.readUTFChars(textFile);
    }

    @Benchmark
    public char[] readUTFCharsString() throws IOException {
        return FileUtil.readUTFChars(textFile.getPath());
    }

    @Benchmark
    public char[] readCharsFile() throws IOException {
        return FileUtil.readChars(textFile, charset);
    }

    @Benchmark
    public char[] readCharsFileDefault() throws IOException {
        return FileUtil.readChars(textFile);
    }

    @Benchmark
    public char[] readCharsString() throws IOException {
        return FileUtil.readChars(textFile.getPath());
    }

    @Benchmark
    public void writeChars(Blackhole bh) throws IOException {
        FileUtil.writeChars(destFile, payloadChars, charset);
        bh.consume(destFile.length());
    }

    @Benchmark
    public void writeCharsString(Blackhole bh) throws IOException {
        FileUtil.writeChars(destFile.getPath(), payloadChars);
        bh.consume(destFile.length());
    }

    @Benchmark
    public String readUTFStringFile() throws IOException {
        return FileUtil.readUTFString(textFile);
    }

    @Benchmark
    public String readUTFStringStream() throws IOException {
        return FileUtil.readUTFString(new ByteArrayInputStream(payloadBytes));
    }

    @Benchmark
    public String readStringFile() throws IOException {
        return FileUtil.readString(textFile, charset);
    }

    @Benchmark
    public String readStringFileDefault() throws IOException {
        return FileUtil.readString(textFile);
    }

    @Benchmark
    public String readStringString() throws IOException {
        return FileUtil.readString(textFile.getPath());
    }

    @Benchmark
    public void writeString(Blackhole bh) throws IOException {
        FileUtil.writeString(destFile, payloadString, charset);
        bh.consume(destFile.length());
    }

    @Benchmark
    public void writeStringDefault(Blackhole bh) throws IOException {
        FileUtil.writeString(destFile, payloadString);
        bh.consume(destFile.length());
    }

    @Benchmark
    public void writeStream(Blackhole bh) throws IOException {
        FileUtil.writeStream(destFile, new ByteArrayInputStream(payloadBytes));
        bh.consume(destFile.length());
    }

    @Benchmark
    public String[] readLines() throws IOException {
        return FileUtil.readLines(textFile, charset);
    }

    @Benchmark
    public String[] readLinesString() throws IOException {
        return FileUtil.readLines(textFile.getPath());
    }

    @Benchmark
    public byte[] readBytes() throws IOException {
        return FileUtil.readBytes(textFile);
    }

    @Benchmark
    public byte[] readBytesString() throws IOException {
        return FileUtil.readBytes(textFile.getPath());
    }

    @Benchmark
    public byte[] readBytesCount() throws IOException {
        return FileUtil.readBytes(textFile, 128);
    }

    @Benchmark
    public void writeBytes(Blackhole bh) throws IOException {
        FileUtil.writeBytes(destFile, payloadBytes);
        bh.consume(destFile.length());
    }

    @Benchmark
    public void writeBytesString(Blackhole bh) throws IOException {
        FileUtil.writeBytes(destFile.getPath(), payloadBytes);
        bh.consume(destFile.length());
    }

    @Benchmark
    public boolean compare() throws IOException {
        return FileUtil.compare(textFile, textFile);
    }

    @Benchmark
    public boolean isOlderFileFile() {
        return FileUtil.isOlder(textFile, referenceFile);
    }

    @Benchmark
    public boolean isNewerFileFile() {
        return FileUtil.isNewer(textFile, referenceFile);
    }

    @Benchmark
    public boolean isNewerFileLong() {
        return FileUtil.isNewer(textFile, referenceTime);
    }

    @Benchmark
    public boolean isOlderFileLong() {
        return FileUtil.isOlder(textFile, referenceTime);
    }

    @Benchmark
    public void copySmart(Blackhole bh) throws IOException {
        FileUtil.copy(textFile, destFile);
        bh.consume(destFile.length());
    }

    @Benchmark
    public boolean isAncestor() {
        return FileUtil.isAncestor(ancestor, descendant, true);
    }

    @Benchmark
    public File getParentFile() {
        return FileUtil.getParentFile(textFile);
    }

    @Benchmark
    public boolean isFilePathAcceptable() {
        return FileUtil.isFilePathAcceptable(textFile, acceptAllFilter);
    }

    @Benchmark
    public File createTempFileNoCreate() throws IOException {
        return FileUtil.createTempFile(tempPrefix, tempSuffix, tempDir, false);
    }

    @Benchmark
    public boolean isSymlink() {
        return FileUtil.isSymlink(textFile);
    }

    @Benchmark
    public String md5() throws IOException {
        return FileUtil.md5(textFile);
    }

    @Benchmark
    public String sha256() throws IOException {
        return FileUtil.sha256(textFile);
    }

    @Benchmark
    public String sha512() throws IOException {
        return FileUtil.sha512(textFile);
    }

    @Benchmark
    public boolean isBinary() throws IOException {
        return FileUtil.isBinary(textFile);
    }
}
