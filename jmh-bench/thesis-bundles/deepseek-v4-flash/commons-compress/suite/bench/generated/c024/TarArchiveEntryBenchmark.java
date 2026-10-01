package bench.generated.c024;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.io.IOException;
import java.nio.file.attribute.FileTime;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveStructSparse;
import org.apache.commons.compress.archivers.zip.ZipEncoding;
import org.apache.commons.compress.archivers.zip.ZipEncodingHelper;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class TarArchiveEntryBenchmark {

    private static final int BLOCK_SIZE = 512;
    private static final ZipEncoding ENCODING = ZipEncodingHelper.getZipEncoding(StandardCharsets.UTF_8);

    private byte[] header;
    private byte[] headerOut;
    private TarArchiveEntry entry;
    private TarArchiveEntry dirEntry;
    private TarArchiveEntry fileEntry;
    private TarArchiveEntry linkEntry;
    private TarArchiveEntry symlinkEntry;
    private TarArchiveEntry otherEntry;
    private TarArchiveEntry paxEntry;
    private TarArchiveEntry sparseEntry;
    private TarArchiveEntry[] pool;
    private int poolIndex;
    private List<TarArchiveStructSparse> sparseHeaders;
    private Map<String, String> globalPaxHeaders;

    private String name;
    private String otherName;
    private String linkName;
    private String userName;
    private String groupName;
    private String paxKey;
    private String paxValue;
    private String paxPathKey;
    private String paxPathValue;
    private long sizeValue;
    private int modeValue;
    private long userIdValue;
    private long groupIdValue;
    private int userIdIntValue;
    private int groupIdIntValue;
    private int devMajorValue;
    private int devMinorValue;
    private long dataOffsetValue;
    private long modTimeValue;
    private FileTime timeValue;
    private Date dateValue;

    @Setup(Level.Trial)
    public void setup() throws Exception {
        name = "some/dir/file.txt";
        otherName = "other/name";
        linkName = "target";
        userName = "user";
        groupName = "group";
        paxKey = "custom.key";
        paxValue = "custom.value";
        paxPathKey = "path";
        paxPathValue = "new/name";
        sizeValue = 12345L;
        modeValue = 0644;
        userIdValue = 1000L;
        groupIdValue = 1000L;
        userIdIntValue = 1000;
        groupIdIntValue = 1000;
        devMajorValue = 3;
        devMinorValue = 1;
        dataOffsetValue = 512L;
        modTimeValue = 1234567890000L;
        timeValue = FileTime.fromMillis(modTimeValue);
        dateValue = new Date(modTimeValue);

        entry = new TarArchiveEntry(name);
        entry.setSize(sizeValue);
        entry.setMode(modeValue);
        entry.setUserId(userIdValue);
        entry.setGroupId(groupIdValue);
        entry.setUserName(userName);
        entry.setGroupName(groupName);
        entry.setLinkName(linkName);
        entry.setLastModifiedTime(timeValue);

        header = new byte[BLOCK_SIZE];
        entry.writeEntryHeader(header);
        headerOut = new byte[BLOCK_SIZE];

        dirEntry = new TarArchiveEntry("some/dir/", TarArchiveEntry.LF_DIR);
        fileEntry = new TarArchiveEntry("some/dir/file.txt", TarArchiveEntry.LF_NORMAL);
        linkEntry = new TarArchiveEntry("link", TarArchiveEntry.LF_LINK);
        symlinkEntry = new TarArchiveEntry("symlink", TarArchiveEntry.LF_SYMLINK);
        otherEntry = new TarArchiveEntry(otherName);

        paxEntry = new TarArchiveEntry("pax");
        paxEntry.addPaxHeader(paxKey, paxValue);
        paxEntry.addPaxHeader(paxPathKey, paxPathValue);

        sparseHeaders = new ArrayList<>();
        sparseHeaders.add(new TarArchiveStructSparse(0L, 1024L));
        sparseHeaders.add(new TarArchiveStructSparse(4096L, 2048L));
        sparseEntry = new TarArchiveEntry("sparse", TarArchiveEntry.LF_GNUTYPE_SPARSE);
        sparseEntry.addPaxHeader("GNU.sparse.size", "6144");
        sparseEntry.setSparseHeaders(sparseHeaders);
        sparseEntry.setSize(6144L);

        globalPaxHeaders = new HashMap<>();

        pool = new TarArchiveEntry[1024];
        for (int i = 0; i < pool.length; i++) {
            pool[i] = new TarArchiveEntry("pool/" + i);
        }
        poolIndex = 0;
    }

    private TarArchiveEntry nextPoolEntry() {
        return pool[poolIndex++ & (pool.length - 1)];
    }

    @Benchmark
    public TarArchiveEntry constructorFromName() {
        return new TarArchiveEntry(name);
    }

    @Benchmark
    public TarArchiveEntry constructorFromNameAndLinkFlag() {
        return new TarArchiveEntry(name, TarArchiveEntry.LF_DIR);
    }

    @Benchmark
    public TarArchiveEntry constructorFromHeader() throws IOException {
        return new TarArchiveEntry(header);
    }

    @Benchmark
    public TarArchiveEntry constructorFromHeaderWithEncoding() throws IOException {
        return new TarArchiveEntry(header, ENCODING, false);
    }

    @Benchmark
    public TarArchiveEntry constructorFromHeaderWithGlobalPaxHeaders() throws IOException {
        return new TarArchiveEntry(globalPaxHeaders, header, ENCODING, false);
    }

    @Benchmark
    public TarArchiveEntry constructorFromHeaderWithDataOffset() throws IOException {
        return new TarArchiveEntry(header, ENCODING, false, dataOffsetValue);
    }

    @Benchmark
    public TarArchiveEntry parseTarHeader() {
        entry.parseTarHeader(header);
        return entry;
    }

    @Benchmark
    public byte[] writeEntryHeader() {
        entry.writeEntryHeader(headerOut);
        return headerOut;
    }

    @Benchmark
    public byte[] writeEntryHeaderStarMode() throws IOException {
        entry.writeEntryHeader(headerOut, ENCODING, true);
        return headerOut;
    }

    @Benchmark
    public String getName() {
        return entry.getName();
    }

    @Benchmark
    public long getSize() {
        return entry.getSize();
    }

    @Benchmark
    public int getMode() {
        return entry.getMode();
    }

    @Benchmark
    public int getUserId() {
        return entry.getUserId();
    }

    @Benchmark
    public long getLongUserId() {
        return entry.getLongUserId();
    }

    @Benchmark
    public int getGroupId() {
        return entry.getGroupId();
    }

    @Benchmark
    public long getLongGroupId() {
        return entry.getLongGroupId();
    }

    @Benchmark
    public String getUserName() {
        return entry.getUserName();
    }

    @Benchmark
    public String getGroupName() {
        return entry.getGroupName();
    }

    @Benchmark
    public Date getModTime() {
        return entry.getModTime();
    }

    @Benchmark
    public FileTime getLastModifiedTime() {
        return entry.getLastModifiedTime();
    }

    @Benchmark
    public Date getLastModifiedDate() {
        return entry.getLastModifiedDate();
    }

    @Benchmark
    public boolean isDirectory() {
        return entry.isDirectory();
    }

    @Benchmark
    public boolean isFile() {
        return entry.isFile();
    }

    @Benchmark
    public boolean isSymbolicLink() {
        return entry.isSymbolicLink();
    }

    @Benchmark
    public boolean isLink() {
        return entry.isLink();
    }

    @Benchmark
    public boolean isSparse() {
        return entry.isSparse();
    }

    @Benchmark
    public boolean isPaxHeader() {
        return entry.isPaxHeader();
    }

    @Benchmark
    public boolean isGlobalPaxHeader() {
        return entry.isGlobalPaxHeader();
    }

    @Benchmark
    public boolean isGNULongNameEntry() {
        return entry.isGNULongNameEntry();
    }

    @Benchmark
    public boolean isGNULongLinkEntry() {
        return entry.isGNULongLinkEntry();
    }

    @Benchmark
    public boolean isGNUSparse() {
        return entry.isGNUSparse();
    }

    @Benchmark
    public boolean isOldGNUSparse() {
        return entry.isOldGNUSparse();
    }

    @Benchmark
    public boolean isPaxGNUSparse() {
        return entry.isPaxGNUSparse();
    }

    @Benchmark
    public boolean isStarSparse() {
        return entry.isStarSparse();
    }

    @Benchmark
    public boolean isExtended() {
        return entry.isExtended();
    }

    @Benchmark
    public boolean isCheckSumOK() {
        return entry.isCheckSumOK();
    }

    @Benchmark
    public boolean isDescendent() {
        return entry.isDescendent(otherEntry);
    }

    @Benchmark
    public String getLinkName() {
        return entry.getLinkName();
    }

    @Benchmark
    public byte getLinkFlag() {
        return entry.getLinkFlag();
    }

    @Benchmark
    public long getRealSize() {
        return entry.getRealSize();
    }

    @Benchmark
    public long getDataOffset() {
        return entry.getDataOffset();
    }

    @Benchmark
    public boolean isStreamContiguous() {
        return entry.isStreamContiguous();
    }

    @Benchmark
    public int getDevMajor() {
        return entry.getDevMajor();
    }

    @Benchmark
    public int getDevMinor() {
        return entry.getDevMinor();
    }

    @Benchmark
    public FileTime getCreationTime() {
        return entry.getCreationTime();
    }

    @Benchmark
    public FileTime getLastAccessTime() {
        return entry.getLastAccessTime();
    }

    @Benchmark
    public FileTime getStatusChangeTime() {
        return entry.getStatusChangeTime();
    }

    @Benchmark
    public String getExtraPaxHeader() {
        return entry.getExtraPaxHeader(paxKey);
    }

    @Benchmark
    public Map<String, String> getExtraPaxHeaders() {
        return entry.getExtraPaxHeaders();
    }

    @Benchmark
    public List<TarArchiveStructSparse> getSparseHeaders() {
        return entry.getSparseHeaders();
    }

    @Benchmark
    public List<TarArchiveStructSparse> getOrderedSparseHeaders() throws IOException {
        return entry.getOrderedSparseHeaders();
    }

    @Benchmark
    public int hashCode() {
        return entry.hashCode();
    }

    @Benchmark
    public boolean equals() {
        return entry.equals(otherEntry);
    }
}
