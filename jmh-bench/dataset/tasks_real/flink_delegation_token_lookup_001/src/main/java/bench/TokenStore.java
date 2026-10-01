package bench;

import java.util.HashMap;
import java.util.Map;

public final class TokenStore {

    private final HashMap<String, String> tokens;

    public TokenStore(Map<String, String> seed) {
        this.tokens = new HashMap<>(seed);
    }

    public String lookup(String key) {
        return tokens.get(key);
    }
}
