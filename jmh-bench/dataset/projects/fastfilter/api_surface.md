# FastFilter 1.0.2 - public API surface

FastFilter (`org.fastfilter`) is a dependency-free Java library of *approximate
membership* data structures: Bloom filters, counting and blocked Bloom variants,
Cuckoo filters, Xor and Xor+ filters, and a Golomb-compressed set. All of them
answer "have I seen this 64-bit key?" with a small false-positive rate and no
false negatives.

Everything is CPU- and memory-bound with no I/O, so benchmarks should build a
key set once in `@Setup` and measure either *construction* or *lookup*. Keys are
plain `long`s — use a fixed-seed `java.util.Random` (or `SplittableRandom`) so a
run is reproducible.

## Core abstraction

`org.fastfilter.Filter`
- `boolean mayContain(long key)` — the hot path; never a false negative
- `long getBitCount()` — space used, in bits
- `boolean supportsAdd()` / `void add(long key)`
- `boolean supportsRemove()` / `void remove(long key)`
- `long cardinality()`

`org.fastfilter.FilterType` — an enum over every implementation, each with
- `Filter construct(long[] keys, int setting)`

Constants: `BLOOM`, `COUNTING_BLOOM`, `SUCCINCT_COUNTING_BLOOM`,
`SUCCINCT_COUNTING_BLOOM_RANKED`, `BLOCKED_BLOOM`,
`SUCCINCT_COUNTING_BLOCKED_BLOOM`, `SUCCINCT_COUNTING_BLOCKED_BLOOM_RANKED`,
`XOR_SIMPLE`, `XOR_SIMPLE_2`, `XOR_8`, `XOR_16`, `XOR_PLUS_8`, `CUCKOO_8`,
`CUCKOO_16`, `CUCKOO_PLUS_8`, `CUCKOO_PLUS_16`, `GCS`.

`setting` means bits-per-key for the Bloom family and the GCS, and is ignored by
the Xor and Cuckoo filters. Values of 8–16 are typical.

## Filter families (direct entry points)

- `bloom.Bloom` — `static Bloom construct(long[] keys, double bitsPerKey)`,
  `add(long)`, `mayContain(long)`. Classic Bloom filter; supports `add`.
- `bloom.BlockedBloom` — `static BlockedBloom construct(long[] keys, int bitsPerKey)`;
  cache-line-blocked, faster lookups, slightly worse false-positive rate.
- `bloom.count.CountingBloom`, `bloom.count.SuccinctCountingBloom`,
  `bloom.count.SuccinctCountingBlockedBloom`,
  `bloom.count.SuccinctCountingBloomRanked`,
  `bloom.count.SuccinctCountingBlockedBloomRanked` — same
  `static … construct(long[] keys, int bitsPerKey)` shape; these also support
  `remove(long)`.
- `xor.Xor8`, `xor.Xor16`, `xor.XorSimple`, `xor.XorSimple2`, `xor.XorFuse8` —
  `static … construct(long[] keys)`; immutable, built in one peeling pass over
  the whole key set. `Xor8`/`XorPlus8` also expose `byte[] getData()` and a
  matching `InputStream` constructor for round-tripping. Note that `XorSimple`
  and `XorSimple2` draw a fresh random seed per attempt and throw
  `ArrayIndexOutOfBoundsException` for roughly one seed in twenty, so a
  benchmark that builds them must retry rather than let the iteration abort.
- `xorplus.XorPlus8` — `static XorPlus8 construct(long[] keys)`; Xor filter with
  a rank-select-compressed fingerprint array.
- `xorplus.Rank9` — `Rank9(BitSet set, long bitCount)`, `long rank(long pos)`,
  `long get(long pos)`, `long getAndPartialRank(long pos)`,
  `long remainingRank(long pos)`. Standalone succinct rank structure.
- `cuckoo.Cuckoo8`, `cuckoo.Cuckoo16`, `cuckoo.CuckooPlus8`,
  `cuckoo.CuckooPlus16` — `static … construct(long[] keys)`,
  `Cuckoo8(int capacity)`, `void insert(long key)`, `void remove(long key)`.
  The only family that supports both add and remove. Each table seeds itself
  randomly and `insert` throws `IllegalStateException("Table full")` when the
  cuckoo chain runs out of kicks; the static `construct` handles that by
  rebuilding, and hand-rolled insertion loops need to do the same.
- `gcs.GolombCompressedSet` — `static GolombCompressedSet construct(long[] keys, int fingerprintBits)`;
  the most space-efficient and the slowest to probe.

## Bit-level and hashing utilities

`org.fastfilter.gcs.BitBuffer` — a growable bit vector with an implicit cursor:
- `BitBuffer(long bits)`, `int position()`, `void seek(int pos)`, `void clear()`
- `void writeBit(long x)` / `long readBit()`
- `void writeNumber(long x, int bitCount)` / `long readNumber(int bitCount)` /
  `long readNumber(long pos, int bitCount)`
- `void writeGolombRice(int shift, long value)`,
  `void writeGolombRiceFast(int shift, long value)`,
  `void skipGolombRice(int shift)`, `int skipGolombRice(int pos, int shift)`
- `void writeEliasDelta(long value)` / `long readEliasDelta()`,
  `static int getEliasDeltaSize(long value)`
- `static long foldSigned(long x)` / `static long unfoldSigned(long x)`
- `int readUntilZero(int pos)`, `void write(BitBuffer bits)`

`org.fastfilter.gcs.MonotoneList` — succinct monotone integer sequence:
- `static MonotoneList generate(int[] data, BitBuffer buffer)`
- `static int getSize(int[] data)`, `static MonotoneList load(BitBuffer buffer)`
- `int get(int i)`, `long getPair(int i)`

`org.fastfilter.gcs.Sort`
- `static void sortUnsigned(long[] data)` / `(long[] data, int offset, int len)`

`org.fastfilter.utils.Hash`
- `static long hash64(long x, long seed)`, `static int reduce(int hash, int n)`
- `static long randomSeed()`, `static void setSeed(long seed)`

`org.fastfilter.utils.StringUtils`
- `static int getHex(char c)`
