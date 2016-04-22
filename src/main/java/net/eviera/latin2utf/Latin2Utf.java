package net.eviera.latin2utf;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Latin2Utf {

    private Map<Byte, byte[]> conversionDict;
    private String directory;
    private String[] extensions;
    private boolean convert;


    public Latin2Utf(String directory, String[] extensions, boolean convert) {

        if (StringUtils.isEmpty(directory)) {
            directory = ".";
        }

        this.extensions = extensions;
        this.directory = directory;
        this.convert = convert;

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

    private void convertFiles() {
        File directoryPath = new File(directory);
        if (!directoryPath.isDirectory()) {
            throw new RuntimeException("El parametro [" + directory + "] no es un directorio");
        }

        System.out.println("Procesando con directorio [" + directory + "], mascara " + Arrays.toString(extensions) + ", conversion [" + convert + "]");

        Collection<File> files = FileUtils.listFiles(directoryPath, extensions, true);
        for (File file : files) {
            converFile(file, convert);
        }

    }

    public void converFile(File file, boolean convert) {

        try {
            byte[] inBytes = Files.readAllBytes(Paths.get(file.toURI()));
            byte[] outBytes = analyze(file.getPath(), inBytes);

        } catch (IOException e) {
            throw new RuntimeException("Error al leer el archivo [" + file + "]", e);
        }
    }

    public byte[] analyze(String filePath, byte[] inBytes) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        int line = 1;
        int col = 0;
        for (byte b : inBytes) {
            //Incrementa el contador de lineas al encontrar un LF
            if ((b & 0xFF) == 0x0A) {
                line++;
                col = 0;
            }

            //Encontro un byte arriba de 127 (0x7F)
            if ((b & 0xFF) > 0x7F) {
                System.out.printf("%s: [%03d][%03d] = ", filePath, line, col);

                byte[] conversion = conversionDict.get(b);
                if (conversion == null) {
                    throw new RuntimeException("No hay conversion para el byte [" + (b & 0xFF) + "]");
                }
                bos.write(conversion);
            } else {
                bos.write(b);
            }

            //Incremento la columna
            col++;
        }
        bos.close();
        return bos.toByteArray();
    }


    public static void main(String[] args) {
        try {
            String folderArgument = null;
            String[] extensions = null;
            boolean convert = false;

            //Parseo los argumentos
            int argCount = 0;
            for (String arg : args) {
                if (arg.startsWith("--")) {
                    //Estos argumentos no suman para el contador
                    switch (arg) {
                        case "--convert":
                            convert = true;
                            break;
                        case "--help":
                            printHelpAndExit(0);
                            break;
                        default:
                            System.out.println("Argumento [" + arg + "] no reconocido");
                            printHelpAndExit(1);
                            break;
                    }
                }
                switch (argCount++) {
                    case 0:
                        //Primer argumento: extensiones
                        extensions = arg.split("[,]");
                        break;
                    case 1:
                        //Segundo argumento: directorio
                        folderArgument = arg;
                        break;
                    default:
                        System.out.println("Argumento [" + arg + "] erroneo");
                        printHelpAndExit(1);
                }
            }

            Latin2Utf latin2Utf = new Latin2Utf(folderArgument, extensions, convert);
            latin2Utf.convertFiles();

        } catch (Exception e) {
            System.err.println("Ha ocurrido un error: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private static void printHelpAndExit(int status) {
        System.out.println("Uso:");
        System.out.println("Latin2Utf [opciones] extensiones [directorio]");
        System.out.println("opciones: ");
        System.out.println("   --help: imprime esta ayuda");
        System.out.println("   --convert: efectua la conversion (si no se especifica, solo lista los cambios)");
        System.out.println("extensiones: (requerido) extensiones de los distintos tipos de archivos a convertir separados por comas (ej: xml,java,jsp)");
        System.out.println("directorio: si no se especifica usa el directorio actual");
        System.exit(status);
    }

}
