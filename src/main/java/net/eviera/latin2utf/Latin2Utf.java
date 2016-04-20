package net.eviera.latin2utf;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Latin2Utf {

    private static final Logger log = Logger.getLogger(Latin2Utf.class.getName());

    public static void main(String[] args) {
        Latin2Utf latin2Utf = new Latin2Utf();
        String borrar = Latin2Utf.class.getResource("/borrar.txt").getFile();
        latin2Utf.converFile(new File(borrar));
}


    public void converFile(File file) {

        try {
            byte[] inBytes = Files.readAllBytes(Paths.get(file.toURI()));
            byte[] outBytes = convertBytes(inBytes);

        } catch (IOException e) {
            log.log(Level.SEVERE, "Error al leer el archivo [" + file +"]", e);
        }
    }

    public byte[] convertBytes(byte[] inBytes) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        for (byte b : inBytes) {
            System.out.println(b & 0xFF);
            bos.write(b);
        }
        bos.close();
        return bos.toByteArray();
    }

}
