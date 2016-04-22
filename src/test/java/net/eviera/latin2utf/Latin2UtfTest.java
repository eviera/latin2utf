package net.eviera.latin2utf;

import junit.framework.TestCase;
import org.apache.commons.io.Charsets;

import java.io.IOException;
import java.util.Arrays;

/**
 * Unit test for simple App.
 */
public class Latin2UtfTest extends TestCase {

    public void testConvertBytesSimple() throws IOException {
        Latin2Utf l2u = new Latin2Utf(null, null, false);
        String simple = "abc";
        byte[] result = l2u.analyze("", simple.getBytes(Charsets.ISO_8859_1));
        assertTrue("comparacion simple", Arrays.equals(new byte[]{(byte) 0x61, (byte) 0x62, (byte) 0x63}, result));
    }

    public void testConvertBytesAcute() throws IOException {
        Latin2Utf l2u = new Latin2Utf(null, null, false);
        byte[] result = l2u.analyze("", ("d" + (char) 237 + "a").getBytes(Charsets.ISO_8859_1));
        assertTrue("comparacion con acentos", Arrays.equals(new byte[]{(byte) 0x64, (byte) 0xC3, (byte) 0xAD, (byte) 0x61}, result));
    }

}
