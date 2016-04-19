package net.eviera.latin2utf;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Latin2Utf {

    private static final Logger log = Logger.getLogger(Latin2Utf.class.getName());

    public static void main(String[] args) {


    }


    public void readFile(File file) {

        try {
            byte[] inBytes = Files.readAllBytes(Paths.get(file.toURI()));
            byte[] outBytes = convertBytes(inBytes);

        } catch (IOException e) {
            log.log(Level.SEVERE, "Error al leer el archivo [" + file +"]", e);
        }
    }

    public byte[] convertBytes(byte[] inBytes) {
        for (byte b : inBytes) {
            if (b > 127) {
                System.out.println(b);
            }
        }

        return new byte[0];
    }

}
