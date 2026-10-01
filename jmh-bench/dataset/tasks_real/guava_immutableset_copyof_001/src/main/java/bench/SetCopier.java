package bench;

import java.util.Collection;
import java.util.HashSet;

/**
 * Builds a {@link HashSet} from an existing collection.
 */
public final class SetCopier {

    private SetCopier() {}

    public static HashSet<Integer> copyOf(Collection<Integer> src) {
        int capacity = Math.max(16, (int) (src.size() / 0.75f) + 1);
        HashSet<Integer> out = new HashSet<>(capacity);
        for (Integer e : src) {
            out.add(e);
        }
        return out;
    }
}
