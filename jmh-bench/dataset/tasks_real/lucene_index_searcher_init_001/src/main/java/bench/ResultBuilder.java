package bench;

import java.util.ArrayList;
import java.util.List;

public final class ResultBuilder {

    private ResultBuilder() {}

    public static List<Integer> build(int n) {
        ArrayList<Integer> out = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            out.add(i * 7);
        }
        return out;
    }
}
