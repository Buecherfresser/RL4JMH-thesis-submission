package bench;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class TypeCache {

    public static final class Key {
        final String a;
        final String b;
        public Key(String a, String b) {
            this.a = a;
            this.b = b;
        }
        @Override public int hashCode() {
            return Objects.hash(a, b);
        }
        @Override public boolean equals(Object o) {
            if (!(o instanceof Key)) return false;
            Key k = (Key) o;
            return a.equals(k.a) && b.equals(k.b);
        }
    }

    private final HashMap<Key, Integer> map;

    public TypeCache(Map<Key, Integer> seed) {
        this.map = new HashMap<>(seed);
    }

    public Integer lookup(Key key) {
        return map.get(key);
    }
}
