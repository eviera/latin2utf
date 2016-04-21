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

    private Map<Byte, byte[]> conversionDict;

    public Latin2Utf() {
        populateConversionDictionary();
    }

    private void populateConversionDictionary() {
        conversionDict = new HashMap<Byte, byte[]>();

        // http://www.fileformat.info/info/unicode/category/Ll/list.htm
        // https://es.wikipedia.org/wiki/ISO_8859-1

        int[] conversionLookup = {
                //minusculas acentuadas
                0xE1,   0xC3,0xA1,      //LATIN SMALL LETTER A WITH ACUTE
                0xE9,   0xC3,0xA9,      //LATIN SMALL LETTER E WITH ACUTE
                0xED,   0xC3,0xAD,      //LATIN SMALL LETTER I WITH ACUTE
        };

        for (int i=0; i < conversionLookup.length; i++) {
            conversionDict.put((byte)conversionLookup[i], new byte[] {(byte) conversionLookup[++i], (byte) conversionLookup[++i]});
        }
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
                byte[] conversion = conversionDict.get(b);
                if (conversion == null) {
                    throw new RuntimeException("No hay conversion para el byte [" + (b & 0xFF) + "]");
                }
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
