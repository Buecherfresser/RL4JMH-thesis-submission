# Apache Commons Compress 1.28.0 - public API surface

Commons Compress (`org.apache.commons.compress`) is a Java library with a unified
API for archive formats (ar, cpio, dump, tar, zip, jar, 7z, arj) and compression
formats (bzip2, gzip, Pack200, LZMA, XZ, Snappy, Unix compress `.Z`, DEFLATE,
DEFLATE64, LZ4, Brotli, Zstandard).

This document lists the most relevant, frequently used entry points so a benchmark
suite can exercise a broad cross-section of the library. Prefer in-memory byte
buffers (`ByteArrayInputStream` / `ByteArrayOutputStream`) over disk I/O so the
measured work is the library's compression/archiving logic, not the filesystem.

## Factories (format-detecting entry points)

`org.apache.commons.compress.compressors.CompressorStreamFactory`
- `CompressorInputStream createCompressorInputStream(String name, InputStream in)`
- `CompressorInputStream createCompressorInputStream(InputStream in)` (auto-detect)
- `CompressorOutputStream createCompressorOutputStream(String name, OutputStream out)`
- name constants: `GZIP`, `BZIP2`, `XZ`, `LZMA`, `SNAPPY_FRAMED`, `SNAPPY_RAW`,
  `LZ4_FRAMED`, `LZ4_BLOCK`, `DEFLATE`, `DEFLATE64`, `Z`, `BROTLI`, `ZSTANDARD`, `PACK200`

`org.apache.commons.compress.archivers.ArchiveStreamFactory`
- `ArchiveInputStream<?> createArchiveInputStream(String name, InputStream in)`
- `ArchiveInputStream<?> createArchiveInputStream(InputStream in)` (auto-detect)
- `ArchiveOutputStream<?> createArchiveOutputStream(String name, OutputStream out)`
- name constants: `AR`, `CPIO`, `DUMP`, `JAR`, `TAR`, `ZIP`, `SEVEN_Z`, `ARJ`

## Compressor streams (read = decompress, write = compress)

Common shape: each has a constructor taking the underlying stream, and the input
streams expose `int read()`, `int read(byte[] b, int off, int len)`, `long getBytesRead()`.
Output streams expose `void write(byte[] b, int off, int len)`, `void close()`, `void finish()`.

- `compressors.gzip.GzipCompressorInputStream` / `GzipCompressorOutputStream`
  (the latter takes an optional `GzipParameters`)
- `compressors.bzip2.BZip2CompressorInputStream` / `BZip2CompressorOutputStream`
  (`BZip2CompressorOutputStream(out, int blockSize)`)
- `compressors.xz.XZCompressorInputStream` / `XZCompressorOutputStream`
- `compressors.lzma.LZMACompressorInputStream` / `LZMACompressorOutputStream`
- `compressors.deflate.DeflateCompressorInputStream` / `DeflateCompressorOutputStream`
- `compressors.deflate64.Deflate64CompressorInputStream`
- `compressors.snappy.SnappyCompressorInputStream`,
  `FramedSnappyCompressorInputStream` / `FramedSnappyCompressorOutputStream`
- `compressors.lz4.BlockLZ4CompressorInputStream` / `BlockLZ4CompressorOutputStream`,
  `FramedLZ4CompressorInputStream` / `FramedLZ4CompressorOutputStream`
- `compressors.z.ZCompressorInputStream`
- `compressors.brotli.BrotliCompressorInputStream` (decompress only)
- `compressors.zstandard.ZstdCompressorInputStream` / `ZstdCompressorOutputStream`

## Archive input streams (iterate entries, read data)

Common shape: `ArchiveEntry getNextEntry()`, `int read(byte[] b, int off, int len)`,
`boolean canReadEntryData(ArchiveEntry e)`.

- `archivers.tar.TarArchiveInputStream(InputStream)` -> `TarArchiveEntry getNextEntry()`
- `archivers.zip.ZipArchiveInputStream(InputStream)` -> `ZipArchiveEntry getNextEntry()`
- `archivers.cpio.CpioArchiveInputStream(InputStream)`
- `archivers.ar.ArArchiveInputStream(InputStream)`
- `archivers.arj.ArjArchiveInputStream(InputStream)`
- `archivers.dump.DumpArchiveInputStream(InputStream)`

Random-access zip (whole-archive in memory or file):
- `archivers.zip.ZipFile` builder: `ZipFile.builder().setByteArray(byte[]).get()`;
  `Enumeration<ZipArchiveEntry> getEntries()`, `InputStream getInputStream(ZipArchiveEntry)`
- `archivers.sevenz.SevenZFile` builder for 7z (in-memory via `setByteArray`/channel)

## Archive output streams (write entries)

Common shape: `void putArchiveEntry(ArchiveEntry e)`, `void write(byte[] b, int off, int len)`,
`void closeArchiveEntry()`, `void finish()`.

- `archivers.tar.TarArchiveOutputStream(OutputStream)`, entry: `new TarArchiveEntry(String name)` then `setSize(long)`
- `archivers.zip.ZipArchiveOutputStream(OutputStream)`, entry: `new ZipArchiveEntry(String name)`;
  `setMethod(ZipEntry.DEFLATED | STORED)`, `setLevel(int)`
- `archivers.cpio.CpioArchiveOutputStream(OutputStream)`
- `archivers.ar.ArArchiveOutputStream(OutputStream)`

## Useful helpers

- `utils.IOUtils.toByteArray(InputStream)`, `IOUtils.copy(InputStream, OutputStream)`
- `compressors.gzip.GzipParameters`, `archivers.zip.ZipArchiveEntry`,
  `archivers.tar.TarArchiveEntry`

## Suggested benchmarking recipe

For a given format, build a representative compressed/archived payload once in a
`@Setup` method (into a `byte[]`), then in the `@Benchmark` method wrap it in a
`ByteArrayInputStream`, run it through the relevant input stream, and drain it with
`IOUtils.toByteArray(...)` (consume the result so it is not dead-code eliminated).
For compression/writing benchmarks, compress a fixed in-memory payload into a
`ByteArrayOutputStream`. Cover as many distinct formats and both directions
(compress and decompress, archive read and archive write) as you reasonably can.
