package bench;

public final class MailboxProcessor {

    private MailboxProcessor() {}

    public static long process(int[] items) {
        long s = 0;
        for (int i = 0; i < items.length; i++) {
            s += items[i] * 31L;
        }
        return s;
    }
}
