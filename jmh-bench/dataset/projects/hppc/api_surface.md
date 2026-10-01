# Carrot Search HPPC 0.10.0 - public API surface

HPPC (`com.carrotsearch.hppc`) is a collections library for Java primitives: maps,
sets, lists, deques, stacks and a bit set that store `int`/`long`/`char`/`short`/
`byte`/`float`/`double` (and `Object`) directly, with no boxing. Every container
is generated for each primitive combination, so the same handful of shapes repeat
across ~280 classes in a single flat package.

Everything is CPU- and memory-bound. Benchmarks should fill a container in
`@Setup` with a fixed-seed `java.util.Random` and measure a single operation kind
per method (insert, look up, iterate, remove, resize), since the interesting
comparisons are against the boxed `java.util` equivalents.

## Naming scheme

`<KType><VType>HashMap` — e.g. `IntIntHashMap`, `LongObjectHashMap`,
`CharFloatHashMap`, `ObjectIntHashMap`. `<KType>HashSet` — `IntHashSet`,
`ObjectHashSet`. `<Type>ArrayList`, `<Type>ArrayDeque`, `<Type>Stack`.
`Object…IdentityHashMap` / `ObjectIdentityHashSet` use reference identity rather
than `equals`. `SortedIteration<K><V>HashMap` is a read-only view with a sorted
iteration order.

## Hash maps — `<K><V>HashMap`

Constructors: `IntIntHashMap()`, `IntIntHashMap(int expectedElements)`,
`IntIntHashMap(int expectedElements, double loadFactor)`,
`IntIntHashMap(IntIntAssociativeContainer container)`.
Static factory: `static IntIntHashMap from(int[] keys, int[] values)`
(and `Object…IdentityHashMap.from(KType[] keys, …[] values)`).

- `int put(int key, int value)`, `int get(int key)`, `int getOrDefault(int, int)`
- `boolean containsKey(int key)`, `int remove(int key)`
- `int putOrAdd(int key, int putValue, int incrementValue)`,
  `int addTo(int key, int additionValue)`
- `int putAll(IntIntAssociativeContainer)` / `putAll(Iterable<? extends IntIntCursor>)`
- `int indexOf(int key)`, `boolean indexExists(int)`, `int indexGet(int)`,
  `void indexInsert(int, int, int)`, `int indexReplace(int, int)`
- `void clear()`, `void release()`, `void ensureCapacity(int expectedElements)`,
  `int size()`, `boolean isEmpty()`, `long ramBytesUsed()`
- `keys()`, `values()`, and iteration via `for (IntIntCursor c : map)`
- `<T extends IntIntProcedure> T forEach(T procedure)` and
  `<T extends IntIntPredicate> T forEach(T predicate)` — the predicate form stops
  early when it returns `false`. Key-only containers take `IntPredicate` /
  `IntProcedure`.

`clear()` keeps the buffers; `release()` drops them back to the initial capacity.

## Hash sets — `<K>HashSet`

`IntHashSet()`, `IntHashSet(int expectedElements[, double loadFactor])`,
`static IntHashSet from(int... elements)`;
`boolean add(int)`, `int addAll(...)`, `boolean contains(int)`,
`boolean remove(int)`, `int removeAll(...)`, `int retainAll(...)`,
`void ensureCapacity(int)`, `clear()`, `release()`, `toArray()`,
`forEach(procedure|predicate)`.

`ObjectIdentityHashSet.from(KType... elements)` is the identity-comparing variant.

## Lists, deques and stacks

`IntArrayList()`, `IntArrayList(int expectedElements)`,
`static IntArrayList from(int... elements)`; `add(int)`, `add(int...)`,
`addAll(...)`, `insert(int index, int)`, `get(int index)`, `set(int index, int)`,
`removeAt(int)`, `removeFirst(int)`, `removeLast(int)`, `removeAll(int)`,
`indexOf(int)`, `contains(int)`, `resize(int)`, `ensureCapacity(int)`,
`trimToSize()`, `clear()`, `release()`, `toArray()`, `ramBytesUsed()`.

`IntArrayDeque` adds `addFirst`/`addLast`, `removeFirst`/`removeLast`,
`getFirst`/`getLast`, `descendingIterator()`, `ramBytesUsed()`.

`IntStack extends IntArrayList` — `static IntStack from(int... elements)`,
`void push(int)`, `void push(int... elements)`, `int pop()`, `int peek()`,
`void discard(int count)`.

## Bit set — `BitSet`

`BitSet()`, `BitSet(long numBits)`, `BitSet(long[] bits, int numWords)`;
`set(long)`, `set(long start, long end)`, `clear(long)`, `get(long)`,
`getAndSet(long)`, `flip(long)`, `cardinality()`, `capacity()`,
`intersect(BitSet)`, `union(BitSet)`, `remove(BitSet)`, `xor(BitSet)`,
`intersectCount(BitSet, BitSet)`, `nextSetBit(long)`, `iterator()`,
`static BitSet newInstance()`. `BitSetIterator` walks set bits.

## Sorting — `com.carrotsearch.hppc.sorting`

- `IndirectSort.mergesort(int start, int length, IntBinaryOperator comparator)` —
  returns a permutation of indices, leaving the data untouched.
- `IndirectSort.mergesort(int[] orderArray, IntBinaryOperator comparator)`,
  `IndirectSort.mergesort(T[] input, int start, int length, Comparator<? super T>)`.
- `QuickSort.sort(int[] array, IntBinaryOperator comparator)`,
  `QuickSort.sort(int[] array, int fromIndex, int toIndex, IntBinaryOperator)`.

## Sorted-iteration views

`new SortedIterationIntCharHashMap(IntCharHashMap delegate, IntComparator)` or
`(delegate, IntCharComparator)`; the `Object`-keyed variants take
`Comparator<KType>` / `ObjectIntComparator<KType>`. The view exposes the same
read API as its delegate plus `clear()` / `release()`, which forward to it.

## Support types

`com.carrotsearch.hppc.cursors` — `IntCursor`, `IntIntCursor`, `ObjectCursor`, …
each with public `index`, `key`, `value` fields, reused across iterations.
`…predicates` — `IntPredicate`, `IntIntPredicate`, `ObjectPredicate<KType>`, …
(`boolean apply(...)`). `…procedures` — `IntProcedure`, `IntIntProcedure`, …
(`void apply(...)`). `…comparators` — `IntComparator`, `IntIntComparator`, …

`Containers` — `randomSeed64()`, `DEFAULT_EXPECTED_ELEMENTS`;
`HashContainers` — `DEFAULT_LOAD_FACTOR`, capacity maths;
`BufferAllocationException`, `Accountable.ramBytesUsed()`.
