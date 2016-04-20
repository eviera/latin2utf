package net.eviera.latin2utf;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Latin2Utf {

    private static final Logger log = Logger.getLogger(Latin2Utf.class.getName());

    private Map<Byte, byte[]> conversionTable;

    public Latin2Utf() {
        conversionTable = new HashMap<Byte, byte[]>();
        conversionTable.put((byte) 0xE1, new byte[] {(byte) 0xC3, (byte) 0xA1});
    }

    public void converFile(File inFile) {

        try {
            byte[] inBytes = Files.readAllBytes(Paths.get(inFile.toURI()));
            byte[] outBytes = convertBytes(inBytes);


        } catch (IOException e) {
            log.log(Level.SEVERE, "Error al leer el archivo [" + inFile + "]", e);
        }
    }

    public byte[] convertBytes(byte[] inBytes) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        for (byte b : inBytes) {
            if ((b & 0xFF) > 0x7F) {
                System.out.printf("b=%X  ", b);
                byte[] conversion = conversionTable.get(b);
                System.out.printf("conv=%X%X", conversion[0], conversion[1]);
                bos.write(conversion);
            } else {
                bos.write(b);
            }

        }
        bos.close();
        return bos.toByteArray();
    }


    public static void main(String[] args) {
        Latin2Utf latin2Utf = new Latin2Utf();
        String borrar = Latin2Utf.class.getResource("/borrar.txt").getFile();
        latin2Utf.converFile(new File(borrar));
    }

}
