"""Regenerate broken regression patches using difflib.

Each entry below specifies the *original* SUT source (read from disk) and the
*regressed* source (inlined). We compute a unified diff with correct hunk
headers and overwrite the .patch file.
"""

from __future__ import annotations

import difflib
import sys
from pathlib import Path

REPO = Path(__file__).resolve().parent.parent
TASKS = REPO / "dataset" / "tasks"


def patch(task_id: str, patch_name: str, sut_rel: str, regressed: str) -> None:
    task_dir = TASKS / task_id
    sut = task_dir / sut_rel
    if not sut.exists():
        print(f"!! missing SUT: {sut}")
        return
    before = sut.read_text().splitlines(keepends=True)
    after = regressed.splitlines(keepends=True)
    diff = difflib.unified_diff(
        before, after,
        fromfile=f"a/{sut_rel.replace('src/main/java/', '')}",
        tofile=f"b/{sut_rel.replace('src/main/java/', '')}",
        n=3,
    )
    text = "".join(diff)
    out = task_dir / "regressions" / patch_name
    out.write_text(text)
    print(f"wrote: {task_id}/{patch_name} ({len(text)} bytes)")


# -- 1. arrayset_lookup_001 -------------------------------------------------
patch(
    "arrayset_lookup_001",
    "list_linear_scan.patch",
    "src/main/java/bench/SetLookup.java",
    """package bench;

import java.util.List;
import java.util.Set;

public final class SetLookup {

    private SetLookup() {}

    /** Count how many of {@code probes} exist in {@code set}. */
    public static int countHits(Set<Integer> set, List<Integer> probes) {
        int hits = 0;
        Integer[] flat = set.toArray(new Integer[0]);
        for (int i = 0, n = probes.size(); i < n; i++) {
            Integer p = probes.get(i);
            boolean found = false;
            for (int j = 0; j < flat.length; j++) {
                if (flat[j].equals(p)) {
                    found = true;
                    break;
                }
            }
            if (found) {
                hits++;
            }
        }
        return hits;
    }
}
""",
)

# -- 2. binary_search_001 ---------------------------------------------------
patch(
    "binary_search_001",
    "linear_scan.patch",
    "src/main/java/bench/Search.java",
    """package bench;

public final class Search {

    private Search() {}

    /** Binary search; assumes {@code arr} is sorted ascending. */
    public static int indexOf(int[] arr, int target) {
        for (int i = 0, n = arr.length; i < n; i++) {
            if (arr[i] == target) {
                return i;
            }
        }
        return -1;
    }
}
""",
)

# -- 3. fib_memoised_001 ----------------------------------------------------
patch(
    "fib_memoised_001",
    "naive_recursion.patch",
    "src/main/java/bench/Fib.java",
    """package bench;

public final class Fib {

    private Fib() {}

    /** n-th Fibonacci number using an iterative loop. */
    public static long fib(int n) {
        if (n < 2) {
            return n;
        }
        return fib(n - 1) + fib(n - 2);
    }
}
""",
)

# -- 4. stream_filter_001 ---------------------------------------------------
patch(
    "stream_filter_001",
    "intstream_pipeline.patch",
    "src/main/java/bench/StreamFilter.java",
    """package bench;

import java.util.stream.IntStream;

public final class StreamFilter {

    private StreamFilter() {}

    /** Count strictly-greater-than-{@code threshold} elements. */
    public static int countOver(int[] xs, int threshold) {
        return (int) IntStream.of(xs).filter(v -> v > threshold).count();
    }
}
""",
)

# -- 5. linkedlist_random_access_001 ---------------------------------------
patch(
    "linkedlist_random_access_001",
    "index_based_get.patch",
    "src/main/java/bench/IndexedSum.java",
    """package bench;

import java.util.List;

public final class IndexedSum {

    private IndexedSum() {}

    /** Sum the list via Iterator (O(n) regardless of list implementation). */
    public static long sum(List<Integer> list) {
        long total = 0L;
        for (int i = 0, n = list.size(); i < n; i++) {
            total += list.get(i).longValue();
        }
        return total;
    }
}
""",
)

# -- 6. prime_sieve_001 -----------------------------------------------------
patch(
    "prime_sieve_001",
    "trial_division.patch",
    "src/main/java/bench/Primes.java",
    """package bench;

public final class Primes {

    private Primes() {}

    /** Count primes below {@code limit} via Sieve of Eratosthenes. */
    public static int count(int limit) {
        if (limit < 3) return limit == 2 ? 1 : 0;
        int count = 0;
        for (int i = 2; i < limit; i++) {
            boolean prime = true;
            for (int d = 2; (long) d * d <= i; d++) {
                if (i % d == 0) { prime = false; break; }
            }
            if (prime) count++;
        }
        return count;
    }
}
""",
)

# -- 7. set_contains_linear_001 --------------------------------------------
patch(
    "set_contains_linear_001",
    "iterate_larger.patch",
    "src/main/java/bench/Intersect.java",
    """package bench;

import java.util.Set;

public final class Intersect {

    private Intersect() {}

    /** Count common elements; iterates the smaller set. */
    public static int sizeOfIntersection(Set<Integer> a, Set<Integer> b) {
        Set<Integer> outer = a.size() >= b.size() ? a : b;
        Set<Integer> inner = outer == a ? b : a;
        int hits = 0;
        for (Integer v : outer) {
            if (inner.contains(v)) hits++;
        }
        return hits;
    }
}
""",
)

# -- 8. string_replace_001 -------------------------------------------------
patch(
    "string_replace_001",
    "replace_all_regex.patch",
    "src/main/java/bench/Replacer.java",
    """package bench;

public final class Replacer {

    private Replacer() {}

    /** Remove ASCII spaces via a single-pass scan. */
    public static String stripSpaces(String s) {
        return s.replaceAll(" ", "");
    }
}
""",
)

# -- 9. sort_stability_001 -------------------------------------------------
patch(
    "sort_stability_001",
    "boxed_sort.patch",
    "src/main/java/bench/SortShort.java",
    """package bench;

import java.util.Arrays;

public final class SortShort {

    private SortShort() {}

    /** Sort each small int[] and return their summed first elements. */
    public static long sortAndSum(int[][] arrays) {
        long total = 0L;
        for (int i = 0; i < arrays.length; i++) {
            int[] src = arrays[i];
            Integer[] boxed = new Integer[src.length];
            for (int j = 0; j < src.length; j++) boxed[j] = src[j];
            Arrays.sort(boxed);
            total += boxed[0].intValue();
        }
        return total;
    }
}
""",
)

# -- 10. array_sum_unroll_001 ----------------------------------------------
patch(
    "array_sum_unroll_001",
    "stream_filter.patch",
    "src/main/java/bench/SumLong.java",
    """package bench;

import java.util.stream.IntStream;

public final class SumLong {

    private SumLong() {}

    /** Sum every {@code stride}-th element of {@code arr}. */
    public static long sumStride(long[] arr, int stride) {
        return IntStream.range(0, arr.length)
                .filter(i -> i % stride == 0)
                .mapToLong(i -> arr[i])
                .sum();
    }
}
""",
)

# -- 11. levenshtein_dp_001 ------------------------------------------------
patch(
    "levenshtein_dp_001",
    "naive_recursion.patch",
    "src/main/java/bench/EditDistance.java",
    """package bench;

public final class EditDistance {

    private EditDistance() {}

    /** Levenshtein distance via 1D rolling-row DP. */
    public static int distance(String a, String b) {
        return rec(a, b, a.length(), b.length());
    }

    private static int rec(String a, String b, int n, int m) {
        if (n == 0) return m;
        if (m == 0) return n;
        int cost = a.charAt(n - 1) == b.charAt(m - 1) ? 0 : 1;
        return Math.min(Math.min(rec(a, b, n - 1, m) + 1, rec(a, b, n, m - 1) + 1),
                        rec(a, b, n - 1, m - 1) + cost);
    }
}
""",
)

# -- 12. enum_switch_001 ---------------------------------------------------
patch(
    "enum_switch_001",
    "hashmap_table.patch",
    "src/main/java/bench/Tag.java",
    """package bench;

import java.util.HashMap;
import java.util.Map;

public final class Tag {

    public enum Kind { ALPHA, BETA, GAMMA, DELTA }

    private static final Map<Kind, Integer> TABLE = new HashMap<>();
    static {
        TABLE.put(Kind.ALPHA, 1);
        TABLE.put(Kind.BETA, 2);
        TABLE.put(Kind.GAMMA, 3);
        TABLE.put(Kind.DELTA, 4);
    }

    private Tag() {}

    /** Score an array of tags via a dense switch. */
    public static long score(Kind[] arr) {
        long total = 0L;
        for (int i = 0, n = arr.length; i < n; i++) {
            total += TABLE.get(arr[i]).longValue();
        }
        return total;
    }
}
""",
)

# -- 13. treemap_lookup_001 ------------------------------------------------
patch(
    "treemap_lookup_001",
    "treemap_factory.patch",
    "src/main/java/bench/MapLookup.java",
    """package bench;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public final class MapLookup {

    private MapLookup() {}

    /** Sum values returned by map.get(key) for each requested key. */
    public static long sumGet(Map<String, Long> map, List<String> keys) {
        long total = 0L;
        for (int i = 0, n = keys.size(); i < n; i++) {
            Long v = map.get(keys.get(i));
            if (v != null) total += v.longValue();
        }
        return total;
    }

    public static Map<String, Long> newFastMap(int capacity) {
        return new TreeMap<>();
    }
}
""",
)


if __name__ == "__main__":
    print("ok")
    sys.exit(0)
