package tarea5;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Tarea5 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int opcion = 0;

        // Bucle infinito hasta que el usuario decida salir (opción 4)
        while (opcion != 4) {
            System.out.println("\n==============================================");
            System.out.println("    MENÚ: ESCRITURA SECUENCIAL DE FICHEROS    ");
            System.out.println("==============================================");
            System.out.println("1. Crear fichero nuevo y escribir contenido");
            System.out.println("2. Añadir texto al FINAL del fichero");
            System.out.println("3. Añadir texto al PRINCIPIO del fichero");
            System.out.println("4. Salir");
            System.out.print("Elige una opción: ");
            
            try {
                opcion = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("[X] Por favor, introduce un número válido.");
                continue;
            }

            switch (opcion) {
                case 1:
                    crearFichero(scanner);
                    break;
                case 2:
                    anadirFinal(scanner);
                    break;
                case 3:
                    anadirPrincipio(scanner);
                    break;
                case 4:
                    System.out.println("Saliendo del programa...");
                    break;
                default:
                    System.out.println("[X] Opción incorrecta.");
            }
        }
        scanner.close();
    }

    /**
     * OP 1: Crea un fichero nuevo (o lo sobreescribe si existe) y escribe contenido.
     */
    private static void crearFichero(Scanner scanner) {
        System.out.print("-> Introduce el nombre del fichero a crear (ej. apuntes.txt): ");
        String nombreFichero = scanner.nextLine();
        
        System.out.print("-> Escribe el contenido a introducir: ");
        String contenido = scanner.nextLine();

        // FileWriter con false (o sin segundo argumento) indica que se crea o sobreescribe
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(nombreFichero, false))) {
            bw.write(contenido);
            bw.newLine(); // Añadimos salto de línea para futuras escrituras
            System.out.println("[✓] Fichero creado y contenido escrito con éxito.");
        } catch (IOException e) {
            System.out.println("[ERROR] No se pudo crear el fichero: " + e.getMessage());
        }
    }

    /**
     * OP 2: Añade texto al final de un archivo existente.
     */
    private static void anadirFinal(Scanner scanner) {
        System.out.print("-> Introduce el nombre del fichero existente: ");
        String nombreFichero = scanner.nextLine();
        
        System.out.print("-> Escribe el contenido a añadir al final: ");
        String contenido = scanner.nextLine();

        // FileWriter con el flag a TRUE activa el modo 'Append' (Añadir al final)
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(nombreFichero, true))) {
            bw.write(contenido);
            bw.newLine();
            System.out.println("[✓] Contenido insertado al FINAL con éxito.");
        } catch (IOException e) {
            System.out.println("[ERROR] No se pudo modificar el fichero: " + e.getMessage());
        }
    }

    /**
     * OP 3: Añade texto al comienzo de un fichero.
     * Como Java no soporta esto de forma nativa, primero leemos todo,
     * escribimos lo nuevo y luego volvemos a volcar lo antiguo.
     */
    private static void anadirPrincipio(Scanner scanner) {
        System.out.print("-> Introduce el nombre del fichero existente: ");
        String nombreFichero = scanner.nextLine();
        
        File fichero = new File(nombreFichero);
        if (!fichero.exists()) {
            System.out.println("[X] El fichero no existe. Créalo primero con la opción 1.");
            return;
        }

        System.out.print("-> Escribe el contenido a añadir al principio: ");
        String nuevoContenido = scanner.nextLine();

        StringBuilder contenidoAntiguo = new StringBuilder();
        
        // 1. Leemos y almacenamos todo el contenido existente con BufferedReader
        try (BufferedReader br = new BufferedReader(new FileReader(fichero))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                contenidoAntiguo.append(linea).append(System.lineSeparator());
            }
        } catch (IOException e) {
            System.out.println("[ERROR] Fallo al leer el fichero: " + e.getMessage());
            return;
        }

        // 2. Sobreescribimos el archivo completo poniendo el nuevo contenido arriba
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fichero, false))) {
            bw.write(nuevoContenido);
            bw.newLine(); // Salto de línea debajo de nuestro nuevo texto
            bw.write(contenidoAntiguo.toString()); // Volcamos el texto antiguo
            System.out.println("[✓] Contenido insertado al PRINCIPIO con éxito.");
        } catch (IOException e) {
            System.out.println("[ERROR] Fallo al escribir en el fichero: " + e.getMessage());
        }
    }
}
