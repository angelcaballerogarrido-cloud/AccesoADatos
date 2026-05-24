package javaapplication3;

import java.io.File;
import java.util.Date;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

public class JavaApplication3 {

    public static void main(String[] args)
    {
        
        String ruta = "/media";
        if( args.length > 0 ) ruta = args[0];
        
        File fich = new File( ruta );
        
        if( !fich.exists() )
        {
            // Mantenemos el texto del profesor (corrigiendo la pequeña errata 'direcotio' a 'directorio')
            System.out.println("No existe el fichero o directorio " + ruta );
        }
        else
        {
            // --- CÓDIGO AÑADIDO: Mayor número de métodos posibles ---
            System.out.println("--- INFORMACIÓN EXTRA ---");
            System.out.println("Ruta absoluta: " + fich.getAbsolutePath());
            System.out.println("Nombre: " + fich.getName());
            System.out.println("¿Es oculto?: " + (fich.isHidden() ? "Sí" : "No"));
            System.out.println("Permisos - Lectura: " + fich.canRead() + " | Escritura: " + fich.canWrite() + " | Ejecución: " + fich.canExecute());
            System.out.println("Última modificación: " + new Date(fich.lastModified()));
            System.out.println("-------------------------");
            
            if( fich.isFile() )
            {
                // Código base del profesor para ficheros
                System.out.println(ruta+" es un fichero.");
                
                // --- CÓDIGO AÑADIDO ---
                System.out.println("Tamaño del fichero: " + fich.length() + " bytes");
                System.out.println("Ruta del directorio padre: " + fich.getParent());
                
                // --- NUEVO: Conteo de vocales (Ambas implementaciones) ---
                System.out.println("Vocales (usando FileReader): " + contarVocalesFileReader(fich));
                System.out.println("Vocales (usando BufferedReader): " + contarVocalesBufferedReader(fich));
            }
            else
            {
                // Código base del profesor para directorios
                System.out.println(" " + ruta + " es un directorio. Contenidos:");
                File[] ficheros = fich.listFiles();
                
                // Añadimos comprobación de null para evitar errores si no hay permisos
                if (ficheros != null) {
                    for( File f: ficheros )
                    {
                        String texto = f.isDirectory() ? "/" : f.isFile() ? "_":"?";
                        
                        String extraInfo = "";
                        if (f.isFile()) {
                            // CÓDIGO AÑADIDO: Imprimir el conteo de ambas implementaciones
                            int vFR = contarVocalesFileReader(f);
                            int vBR = contarVocalesBufferedReader(f);
                            extraInfo = " (" + f.length() + " bytes) -> Vocales [FileReader: " + vFR + " | BufferedReader: " + vBR + "]";
                        }
                        
                        System.out.println(texto + " " + f.getName() + extraInfo);
                    }
                }
            }
        }       
    }   

    /**
     * Implementación 1: Usando FileReader carácter a carácter
     */
    public static int contarVocalesFileReader(File miFichero) {
        int vocales = 0;
        // El try-with-resources cierra automáticamente el flujo (no hace falta .close())
        try (FileReader fil = new FileReader(miFichero)) {
            int letra = fil.read();
            while (letra != -1) {
                char c = Character.toLowerCase((char) letra);
                if (esVocal(c)) {
                    vocales++;
                }
                letra = fil.read();
            }
        } catch (IOException e) {
            // Capturamos el error si algún fichero está bloqueado por el sistema o no hay permisos
        }
        return vocales;
    }

    /**
     * Implementación 2: Usando BufferedReader línea a línea
     */
    public static int contarVocalesBufferedReader(File miFichero) {
        int vocales = 0;
        try (BufferedReader brf = new BufferedReader(new FileReader(miFichero))) {
            String line = brf.readLine();
            while (line != null) {
                String lowerLine = line.toLowerCase();
                for (int i = 0; i < lowerLine.length(); i++) {
                    if (esVocal(lowerLine.charAt(i))) {
                        vocales++;
                    }
                }
                line = brf.readLine();
            }
        } catch (IOException e) {
            // Capturamos el error si algún fichero está bloqueado por el sistema
        }
        return vocales;
    }

    /**
     * Función auxiliar para identificar vocales de forma limpia
     */
    private static boolean esVocal(char c) {
        return c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u' ||
               c == 'á' || c == 'é' || c == 'í' || c == 'ó' || c == 'ú';
    }
}
