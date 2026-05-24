package ejercicio_faa;

import java.io.RandomAccessFile;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Ejercicio_faa {

    private File f;
    private List<String> campos;
    private List<Integer> camposLength;
    private long longReg;       // Bytes por registro
    private long numReg = 0;    // Número de registros dentro del fichero
    
    Ejercicio_faa(String path, List<String> campos, List<Integer> camposLength) throws IOException {
        this.campos = campos;
        this.camposLength = camposLength;
        this.f = new File(path);
        this.longReg = 0;
        
        for(Integer campo: camposLength){
            this.longReg += campo;
        }
        
        if(f.exists()){
            this.numReg = f.length() / this.longReg;
        }
    }
    
    public long getNumReg() {
        return numReg;
    }
    
    // MÉTODO ORIGINAL DEL PROFESOR: Añadir al final
    public void insertar(Map<String,String> reg) throws IOException {
        insertar(reg, this.numReg++);        
    }
    
    // MÉTODO ORIGINAL DEL PROFESOR: Modificar/Insertar en posición concreta
    public void insertar(Map<String,String> reg, long pos) {
        try (RandomAccessFile rndFile = new RandomAccessFile(this.f, "rws")) {
            
            // POSICIONARNOS PARA ESCRIBIR
            rndFile.seek(pos * this.longReg);
            
            int total = campos.size();
            for(int i = 0; i < total; i++) {
                String nomCampo = campos.get(i);
                Integer longCampo = camposLength.get(i);
                String valorCampo = reg.get(nomCampo);
                        
                if(valorCampo == null) {
                    valorCampo = "";
                }
                
                String valorCampoForm = String.format("%1$-" + longCampo + "s", valorCampo);
                
                // Medida de seguridad extra: Si el texto es más largo que el hueco binario, se recorta.
                if (valorCampoForm.length() > longCampo) {
                    valorCampoForm = valorCampoForm.substring(0, longCampo);
                }
                
                rndFile.write(valorCampoForm.getBytes("UTF-8"), 0, longCampo);
            }  
        } catch(Exception ex) {
            System.out.println("Error de escritura: " + ex.getMessage());
        }
    }
    
    // NUEVO MÉTODO REQUERIDO: Leer un registro completo
    public void leer(long pos) {
        try (RandomAccessFile rndFile = new RandomAccessFile(this.f, "r")) {
            // Verificar si el registro existe
            if (pos * this.longReg >= rndFile.length() || pos < 0) {
                System.out.println("[X] El registro en la posición " + pos + " no existe.");
                return;
            }
            
            // Mover el cursor al byte inicial del registro solicitado
            rndFile.seek(pos * this.longReg);
            
            System.out.println("\n--- DATOS DEL REGISTRO " + pos + " ---");
            int total = campos.size();
            for (int i = 0; i < total; i++) {
                String nomCampo = campos.get(i);
                Integer longCampo = camposLength.get(i);
                
                // Creamos un array de bytes del tamaño exacto del campo
                byte[] bytes = new byte[longCampo];
                rndFile.read(bytes); // Leemos los bytes del fichero a memoria
                
                // Transformamos a String y le quitamos los espacios en blanco sobrantes
                String valor = new String(bytes, "UTF-8").trim();
                System.out.println("-> " + nomCampo + ": " + valor);
            }
            System.out.println("----------------------------");
            
        } catch (Exception ex) {
            System.out.println("Error de lectura: " + ex.getMessage());
        }
    }
    
    public static void main(String args[]) {
        
        List<String> campos = new ArrayList<>();
        List<Integer> camposLength = new ArrayList<>();
        
        // REQUISITO: string, int y fecha como mínimo.
        campos.add("NOMBRE");      // String
        campos.add("EDAD");        // Entero (int) guardado como cadena (ej. "25  ")
        campos.add("FECHA_NAC");   // Fecha guardada como cadena (ej. "15/04/1990")
        
        camposLength.add(32); // 32 bytes
        camposLength.add(4);  // 4 bytes para edad (suficiente para 9999 años)
        camposLength.add(10); // 10 bytes para formato dd/mm/yyyy
        
        Scanner scanner = new Scanner(System.in);
        
        try {
            Ejercicio_faa faa = new Ejercicio_faa("fichero_binario_alumnos.dat", campos, camposLength);
            int opcion = 0;
            
            // REQUISITO: Menú interactivo
            while (opcion != 4) {
                System.out.println("\n=== GESTIÓN BINARIA (Random Access File) ===");
                System.out.println("1. Leer un registro");
                System.out.println("2. Añadir un nuevo registro (Final)");
                System.out.println("3. Modificar un registro (Sobreescribir)");
                System.out.println("4. Salir");
                System.out.println(">>> Total de registros en fichero: " + faa.getNumReg());
                System.out.print("Opción: ");
                
                try {
                    opcion = Integer.parseInt(scanner.nextLine());
                } catch (Exception e) {
                    System.out.println("Elige un número válido.");
                    continue;
                }
                
                switch (opcion) {
                    case 1:
                        System.out.print("Introduce la posición del registro a leer (Ej: 0, 1, 2...): ");
                        long posLeer = Long.parseLong(scanner.nextLine());
                        faa.leer(posLeer);
                        break;
                        
                    case 2:
                        Map<String, String> regNuevo = new HashMap<>();
                        System.out.print("NOMBRE (Texto): ");
                        regNuevo.put("NOMBRE", scanner.nextLine());
                        System.out.print("EDAD (Número): ");
                        regNuevo.put("EDAD", scanner.nextLine());
                        System.out.print("FECHA DE NACIMIENTO (dd/mm/yyyy): ");
                        regNuevo.put("FECHA_NAC", scanner.nextLine());
                        
                        faa.insertar(regNuevo);
                        System.out.println("[✓] Registro añadido con éxito al final del fichero binario.");
                        break;
                        
                    case 3:
                        System.out.print("Introduce la posición del registro a modificar (0 a " + (faa.getNumReg() - 1) + "): ");
                        long posMod = Long.parseLong(scanner.nextLine());
                        if (posMod >= faa.getNumReg() || posMod < 0) {
                            System.out.println("[X] Posición inválida.");
                            break;
                        }
                        
                        Map<String, String> regMod = new HashMap<>();
                        System.out.print("NUEVO NOMBRE (Texto): ");
                        regMod.put("NOMBRE", scanner.nextLine());
                        System.out.print("NUEVA EDAD (Número): ");
                        regMod.put("EDAD", scanner.nextLine());
                        System.out.print("NUEVA FECHA DE NACIMIENTO (dd/mm/yyyy): ");
                        regMod.put("FECHA_NAC", scanner.nextLine());
                        
                        faa.insertar(regMod, posMod);
                        System.out.println("[✓] Registro " + posMod + " modificado (sobreescrito) con éxito.");
                        break;
                        
                    case 4:
                        System.out.println("Saliendo del gestor...");
                        break;
                }
            }
        } catch (IOException e) {
            System.out.println("Excepción de IO: " + e.getMessage());
        }
        scanner.close();
    }
}
