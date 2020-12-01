package lab2;

import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class ErrorChecking {
//error checking class using Cyclic redundancy check
    public static long getCRC32(byte[] bytes) {
        Checksum crc32 = new CRC32();
        crc32.update(bytes, 0, bytes.length);
        return crc32.getValue();
    }

}
