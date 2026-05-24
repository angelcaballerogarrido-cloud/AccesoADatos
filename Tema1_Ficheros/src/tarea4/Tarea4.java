package tarea4;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Tarea4 {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        // Pedimos los datos al usuario por consola
        System.out.print("Introduce la ruta de la carpeta donde quieres buscar (ej. C:\\Users): ");
        String rutaCarpeta = scanner.nextLine();
        
        System.out.print("Introduce la palabra o texto exacto a buscar: ");
        String textoBuscado = scanner.nextLine();
        
        File carpeta = new File(rutaCarpeta);
        
        if (!carpeta.exists() || !carpeta.isDirectory()) {
            System.out.println("[X] La ruta proporcionada no existe o no es un directorio válido.");
        } else {
            System.out.println("\nIniciando búsqueda de '" + textoBuscado + "' en: " + rutaCarpeta + "\n");
            buscarEnCarpeta(carpeta, textoBuscado);
            System.out.println("\nBúsqueda finalizada.");
        }
        
        scanner.close();
    }
    
    /**
     * Navega por los ficheros de una carpeta. Si encuentra otra carpeta,
     * se llama a sí mismo (recursividad) para buscar también en su interior.
     */
    private static void buscarEnCarpeta(File carpeta, String texto) {
        File[] ficheros = carpeta.listFiles();
        
        if (ficheros != null) {
            for (File f : ficheros) {
                if (f.isFile()) {
                    // Es un archivo, analizamos su contenido
                    buscarTextoEnFichero(f, texto);
                } else if (f.isDirectory()) {
                    // Es una subcarpeta, seguimos profundizando (Recursividad de la Tarea 3)
                    buscarEnCarpeta(f, texto);
                }
            }
        }
    }
    
    /**
     * Lee un archivo línea por línea y busca las ocurrencias de un texto,
     * imprimiendo la línea y la columna exacta.
     */
    private static void buscarTextoEnFichero(File fichero, String textoBuscado) {
        // Usamos try-with-resources para cerrar el fichero automáticamente
        try (BufferedReader br = new BufferedReader(new FileReader(fichero))) {
            String linea;
            int numLinea = 1;
            boolean archivoImpreso = false; // Bandera para imprimir el nombre del archivo solo una vez
            
            while ((linea = br.readLine()) != null) {
                // Buscamos la primera aparición del texto en esta línea (columna)
                int columna = linea.indexOf(textoBuscado);
                
                // Mientras siga encontrando el texto en la misma línea...
                while (columna != -1) {
                    // Si es la primera vez que lo encontramos en este fichero, imprimimos la cabecera
                    if (!archivoImpreso) {
                        System.out.println("📄 Fichero: " + fichero.getAbsolutePath());
                        archivoImpreso = true;
                    }
                    
                    // Sumamos +1 a la columna porque indexOf empieza en 0
                    System.out.println("    -> Encontrado en Línea: " + numLinea + " | Columna: " + (columna + 1));
                    
                    // Seguimos buscando en la misma línea por si la palabra se repite
                    columna = linea.indexOf(textoBuscado, columna + textoBuscado.length());
                }
                numLinea++;
            }
        } catch (IOException e) {
            // Ignoramos ficheros de sistema que estén bloqueados o no tengan permiso de lectura
        }
    }
}
