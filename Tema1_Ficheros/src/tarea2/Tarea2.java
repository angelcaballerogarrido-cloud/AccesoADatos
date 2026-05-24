package tarea2;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Tarea2 {

    public static void main(String[] args) {
        
        // 1. CREACIÓN DE LA ESTRUCTURA USANDO BUCLES
        String[] nivel1 = {"Abuelo", "Abuela"};
        String[] nivel2 = {"Padre", "Madre"};
        
        // Usamos una carpeta base para que no se mezcle todo suelto
        String rutaBase = "familia_directorios"; 
        
        int contador = 1;
        
        try {
            System.out.println("Generando estructura de carpetas y ficheros...");
            for (String n1 : nivel1) {
                for (String n2 : nivel2) {
                    // Ejemplo: familia_directorios/Abuelo/Padre
                    File directorio = new File(rutaBase + "/" + n1 + "/" + n2);
                    
                    // mkdirs() crea los directorios padres si no existen
                    directorio.mkdirs(); 
                    
                    // Creando los 2 ficheros por cada carpeta
                    File hijo = new File(directorio, "Hijo" + contador + ".txt");
                    hijo.createNewFile();
                    contador++;
                    
                    File hija = new File(directorio, "Hija" + contador + ".txt");
                    hija.createNewFile();
                    contador++;
                }
            }
            System.out.println("[OK] Estructura creada con éxito en la carpeta '" + rutaBase + "'.\n");
            
            // 2. PREGUNTAR AL USUARIO EL ARCHIVO A BORRAR (TIP 1)
            Scanner scanner = new Scanner(System.in);
            System.out.print("Introduce el nombre exacto del fichero a borrar (ej. Hijo3.txt): ");
            String nombreABorrar = scanner.nextLine();
            
            // 3. RECORRIDO RECURSIVO Y BORRADO
            File raiz = new File(rutaBase);
            boolean borrado = buscarYBorrarRecursivo(raiz, nombreABorrar);
            
            System.out.println("-------------------------------------------------");
            if (borrado) {
                System.out.println("[ÉXITO] El fichero '" + nombreABorrar + "' fue encontrado y ELIMINADO.");
            } else {
                System.out.println("[ERROR] No se encontró ningún fichero llamado '" + nombreABorrar + "'.");
            }
            
            scanner.close();
            
        } catch (IOException e) {
            System.out.println("Error de Entrada/Salida: " + e.getMessage());
        }
    }
    
    /**
     * Función recursiva que navega por todos los directorios buscando un archivo concreto.
     * Si lo encuentra, lo elimina y devuelve true.
     */
    public static boolean buscarYBorrarRecursivo(File directorio, String nombreObjetivo) {
        // Caso base de seguridad
        if (directorio == null || !directorio.exists()) {
            return false;
        }
        
        File[] contenidos = directorio.listFiles();
        if (contenidos != null) {
            for (File f : contenidos) {
                if (f.isDirectory()) {
                    // Si es una carpeta, nos llamamos a nosotros mismos (RECURSIVIDAD)
                    boolean encontrado = buscarYBorrarRecursivo(f, nombreObjetivo);
                    if (encontrado) {
                        return true; // Propagamos el éxito hacia arriba para parar la búsqueda
                    }
                } else if (f.isFile() && f.getName().equalsIgnoreCase(nombreObjetivo)) {
                    // Hemos encontrado el archivo que nos pidieron
                    System.out.println("-> ¡Archivo localizado en: " + f.getAbsolutePath() + "!");
                    return f.delete(); // Devuelve true si el borrado tiene éxito
                }
            }
        }
        return false;
    }
}
