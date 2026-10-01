package bench;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RegexCount {

    private static final Pattern WORD = Pattern.compile("\\w+");

    private RegexCount() {}

    /** Return the number of word matches in the input. */
    public static int countMatches(String text) {
        Matcher m = WORD.matcher(text);
        int count = 0;
        while (m.find()) {
            count++;
        }
        return count;
    }
}
