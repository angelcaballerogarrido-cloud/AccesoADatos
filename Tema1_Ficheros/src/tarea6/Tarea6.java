package tarea6;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Tarea6 {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Introduce la ruta exacta del fichero de texto a corregir (ej. texto.txt): ");
        String rutaOriginal = scanner.nextLine();
        scanner.close();

        File archivoOriginal = new File(rutaOriginal);
        
        // Comprobación de seguridad
        if (!archivoOriginal.exists() || !archivoOriginal.isFile()) {
            System.out.println("[X] El fichero introducido no existe o no es válido.");
            return;
        }

        File archivoTemporal = null;
        
        try {
            // 1. Crear un archivo temporal (la función nos garantiza un nombre único que no pise nada)
            // Se creará en el mismo directorio que el archivo original para facilitar luego el renombrado
            archivoTemporal = File.createTempFile("temp_correccion_", ".txt", archivoOriginal.getParentFile());
            
            // 2. Abrir flujos (usamos try-with-resources para que se cierren automáticamente al terminar)
            try (BufferedReader br = new BufferedReader(new FileReader(archivoOriginal));
                 BufferedWriter bw = new BufferedWriter(new FileWriter(archivoTemporal))) {
                
                String linea;
                // Inicializamos a 'true' para que la primerísima letra del texto siempre sea mayúscula
                boolean forzarMayuscula = true; 
                
                while ((linea = br.readLine()) != null) {
                    
                    // REQUISITO 2: Eliminar dobles/triples espacios en blanco usando Regex
                    // "\\s+" busca cualquier secuencia de espacios/tabulaciones de tamaño 1 o más y lo cambia por " "
                    linea = linea.replaceAll("\\s+", " ");
                    
                    // REQUISITO 1: Forzar mayúscula después del punto
                    StringBuilder lineaCorregida = new StringBuilder();
                    for (int i = 0; i < linea.length(); i++) {
                        char c = linea.charAt(i);
                        
                        // Si nos exigen mayúscula y el carácter actual es una letra...
                        if (forzarMayuscula && Character.isLetter(c)) {
                            c = Character.toUpperCase(c);
                            forzarMayuscula = false; // Ya hemos aplicado la mayúscula, apagamos la bandera
                        }
                        
                        // Si encontramos un punto, encendemos la bandera para la próxima letra que aparezca
                        if (c == '.') {
                            forzarMayuscula = true;
                        }
                        
                        lineaCorregida.append(c);
                    }
                    
                    // Escribimos la línea terminada al fichero temporal y añadimos el salto de línea
                    bw.write(lineaCorregida.toString());
                    bw.newLine();
                }
            } // IMPORTANTE: Aquí se cierran los flujos automáticamente. Si no se cerrasen, Windows no nos dejaría renombrar el archivo en el paso 3.

            // 3. Reemplazar el archivo
            // Primero tenemos que borrar el original para dejar el hueco (el nombre) libre
            if (archivoOriginal.delete()) {
                // Ahora renombramos el temporal con el nombre del original
                if (archivoTemporal.renameTo(archivoOriginal)) {
                    System.out.println("[✓] El archivo ha sido corregido y sobreescrito con éxito.");
                } else {
                    System.out.println("[ERROR] No se pudo renombrar el archivo temporal al original.");
                }
            } else {
                System.out.println("[ERROR] No se pudo borrar el archivo original por problemas de permisos.");
            }
            
        } catch (IOException e) {
            System.out.println("[ERROR] Fallo de Entrada/Salida: " + e.getMessage());
        } finally {
            // Medida de seguridad: Si el programa falló a medias, intentamos borrar el archivo temporal basura
            if (archivoTemporal != null && archivoTemporal.exists() && archivoTemporal.getName().startsWith("temp_correccion_")) {
                archivoTemporal.delete();
            }
        }
    }
}
