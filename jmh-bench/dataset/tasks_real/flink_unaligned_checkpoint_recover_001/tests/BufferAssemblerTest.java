package bench;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class BufferAssemblerTest {
    @Test public void joinsTwoSlices() {
        byte[] out = BufferAssembler.assemble(new byte[][]{{1,2}, {3,4,5}});
        assertArrayEquals(new byte[]{1,2,3,4,5}, out);
    }
    @Test public void emptyInput() {
        assertEquals(0, BufferAssembler.assemble(new byte[0][]).length);
    }
    @Test public void preservesOrder() {
        byte[] out = BufferAssembler.assemble(new byte[][]{{9}, {8}, {7}});
        assertArrayEquals(new byte[]{9,8,7}, out);
    }
    @Test public void skipsEmptySlices() {
        byte[] out = BufferAssembler.assemble(new byte[][]{{}, {1}, {}, {2}});
        assertArrayEquals(new byte[]{1,2}, out);
    }
}
