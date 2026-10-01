package bench;

import java.util.List;

public final class BatchIterator {

    private BatchIterator() {}

    public static long sum(List<List<Integer>> batches) {
        long s = 0;
        for (int b = 0, nb = batches.size(); b < nb; b++) {
            List<Integer> batch = batches.get(b);
            for (int i = 0, n = batch.size(); i < n; i++) {
                s += batch.get(i);
            }
        }
        return s;
    }
}
