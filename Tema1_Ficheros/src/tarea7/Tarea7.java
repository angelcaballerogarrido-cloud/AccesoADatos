package tarea7;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Scanner;

public class Tarea7 {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("======================================");
        System.out.println("     CONVERSOR DE ENCODING JAVA       ");
        System.out.println("======================================");
        
        // REQUISITO: Solicitar los 4 parámetros
        System.out.print("1. Path fichero de ENTRADA: ");
        String pathEntrada = scanner.nextLine();
        
        System.out.print("2. Encoding de ENTRADA (ASCII, UTF-8, UTF-16, ISO-8859-1): ");
        String encEntrada = scanner.nextLine().toUpperCase();
        
        System.out.print("3. Path fichero de SALIDA (se creará nuevo): ");
        String pathSalida = scanner.nextLine();
        
        System.out.print("4. Encoding de SALIDA (ASCII, UTF-8, UTF-16, ISO-8859-1): ");
        String encSalida = scanner.nextLine().toUpperCase();
        
        scanner.close();

        System.out.println("\nProcesando conversión...");

        try {
            // Llamamos a la función de conversión que puede lanzar las excepciones
            convertirEncoding(pathEntrada, encEntrada, pathSalida, encSalida);
            System.out.println("[✓] El fichero ha sido convertido y guardado con éxito.");
            
        // REQUISITO: Capturando las excepciones solicitadas
        } catch (FileNotFoundException e) {
            System.out.println("[EXCEPCIÓN - FileNotFoundException] Fichero de entrada no encontrado. Comprueba la ruta.");
            System.out.println("Detalles: " + e.getMessage());
            
        } catch (IOException e) {
            // Aquí entrarán, entre otros, los fallos de lectura/escritura y el UnsupportedEncodingException
            System.out.println("[EXCEPCIÓN - IOException] Error de entrada/salida o Encoding no soportado.");
            System.out.println("Detalles: " + e.getMessage());
            
        } catch (Exception e) {
            // Captura de excepción genérica requerida en el enunciado
            System.out.println("[EXCEPCIÓN - Genérica] Se produjo un error inesperado durante el proceso.");
            System.out.println("Detalles: " + e.getMessage());
        }
    }
    
    /**
     * Función que aplica las clases requeridas en la práctica para la conversión
     */
    private static void convertirEncoding(String pEntrada, String eEntrada, String pSalida, String eSalida) 
            throws FileNotFoundException, IOException, Exception {
            
        /* 
         * REQUISITO: Utilizando las clases para LEER (InputStreamReader, BufferedReader)
         * REQUISITO: Utilizando las clases para ESCRIBIR (BufferedWriter, OutputStreamWriter, FileOutputStream)
         * 
         * Usamos el formato Try-With-Resources para garantizar el cierre de flujos.
         */
        try (
            // Bloque de Lectura
            FileInputStream fis = new FileInputStream(pEntrada);
            InputStreamReader isr = new InputStreamReader(fis, eEntrada); // Aquí aplicamos Encoding Origen
            BufferedReader br = new BufferedReader(isr);
            
            // Bloque de Escritura
            FileOutputStream fos = new FileOutputStream(pSalida);
            OutputStreamWriter osw = new OutputStreamWriter(fos, eSalida); // Aquí aplicamos Encoding Destino
            BufferedWriter bw = new BufferedWriter(osw)
        ) {
            String linea;
            
            // Leemos línea a línea y escribimos en el nuevo fichero
            while ((linea = br.readLine()) != null) {
                bw.write(linea);
                bw.newLine();
            }
        }
    }
}
