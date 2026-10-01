package bench;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class RecordCopierTest {
    @Test public void concatenatesRecords() {
        byte[] out = RecordCopier.copyAll(new byte[][]{{1,2,3}, {4,5}}, 5);
        assertArrayEquals(new byte[]{1,2,3,4,5}, out);
    }
    @Test public void emptyInput() {
        assertEquals(0, RecordCopier.copyAll(new byte[0][], 0).length);
    }
    @Test public void singleRecord() {
        byte[] out = RecordCopier.copyAll(new byte[][]{{7,8,9}}, 3);
        assertArrayEquals(new byte[]{7,8,9}, out);
    }
    @Test public void preservesOrder() {
        byte[] out = RecordCopier.copyAll(new byte[][]{{1}, {2}, {3}}, 3);
        assertArrayEquals(new byte[]{1,2,3}, out);
    }
}
