package tarea9;

import java.io.RandomAccessFile;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Ejercicio_faa_Avanzado {
    private File f;
    private List<String> campos;
    private List<Integer> camposLength;
    private long longReg;       
    private long numReg = 0;    
    
    public Ejercicio_faa_Avanzado(String path, List<String> campos, List<Integer> camposLength) throws IOException {
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
    
    /**
     * MÉTODO AUXILIAR (Motor matemático)
     * Calcula en qué byte exacto empieza una columna dentro de un registro.
     */
    private long getOffsetColumna(String nomColumna) throws Exception {
        int index = campos.indexOf(nomColumna);
        if (index == -1) throw new Exception("La columna '" + nomColumna + "' no existe.");
        
        long offset = 0;
        for (int i = 0; i < index; i++) {
            offset += camposLength.get(i);
        }
        return offset;
    }

    private int getLongitudColumna(String nomColumna) {
        int index = campos.indexOf(nomColumna);
        return index == -1 ? 0 : camposLength.get(index);
    }

    // =========================================================
    // 1) selectCampo(int numRegistro, String nomColumna)
    // =========================================================
    public String selectCampo(int numRegistro, String nomColumna) {
        if (numRegistro >= numReg || numRegistro < 0) return null;
        
        try (RandomAccessFile rndFile = new RandomAccessFile(this.f, "r")) {
            long offsetColumna = getOffsetColumna(nomColumna);
            int longitud = getLongitudColumna(nomColumna);
            
            // Viajamos al inicio del registro + los bytes hasta llegar a la columna
            rndFile.seek((numRegistro * this.longReg) + offsetColumna);
            byte[] bytes = new byte[longitud];
            rndFile.read(bytes);
            return new String(bytes, "UTF-8").trim();
            
        } catch (Exception e) {
            System.out.println("Error en selectCampo: " + e.getMessage());
            return null;
        }
    }
    
    // =========================================================
    // 2) selectColumna(String nomColumna)
    // =========================================================
    public List<String> selectColumna(String nomColumna) {
        List<String> resultados = new ArrayList<>();
        // Reutilizamos el motor base, iterando por todas las filas
        for (int i = 0; i < this.numReg; i++) {
            resultados.add(selectCampo(i, nomColumna));
        }
        return resultados;
    }
    
    // =========================================================
    // 3) selectRowList(int numRegistro)
    // =========================================================
    public List<String> selectRowList(int numRegistro) {
        List<String> resultados = new ArrayList<>();
        if (numRegistro >= numReg || numRegistro < 0) return resultados;
        
        // Reutilizamos el motor base, iterando por todas las columnas de esta fila
        for (String col : campos) {
            resultados.add(selectCampo(numRegistro, col));
        }
        return resultados;
    }
    
    // =========================================================
    // 4) selectRowMap(int numRegistro)
    // =========================================================
    public Map<String, String> selectRowMap(int numRegistro) {
        Map<String, String> resultados = new HashMap<>();
        if (numRegistro >= numReg || numRegistro < 0) return resultados;
        
        for (String col : campos) {
            resultados.put(col, selectCampo(numRegistro, col));
        }
        return resultados;
    }
    
    // =========================================================
    // METODO AUXILIAR: Insertar / Sobrescribir Fila Completa
    // =========================================================
    private void insertarBase(Map<String,String> reg, long pos) {
        try (RandomAccessFile rndFile = new RandomAccessFile(this.f, "rws")) {
            rndFile.seek(pos * this.longReg);
            
            for(int i = 0; i < campos.size(); i++) {
                String nomCampo = campos.get(i);
                Integer longCampo = camposLength.get(i);
                String valorCampo = reg.get(nomCampo);
                if(valorCampo == null) valorCampo = "";
                
                String valorForm = String.format("%1$-" + longCampo + "s", valorCampo);
                if (valorForm.length() > longCampo) valorForm = valorForm.substring(0, longCampo);
                
                rndFile.write(valorForm.getBytes("UTF-8"), 0, longCampo);
            }  
            if (pos >= this.numReg) this.numReg = pos + 1;
        } catch(Exception ex) {}
    }

    // =========================================================
    // 5.1) update(int row, Map)
    // =========================================================
    public void update(int row, Map<String, String> reg) {
        // Aprovechamos que la inserción de Tarea 8 ya sobrescribe registros si le damos la 'pos'
        insertarBase(reg, row);
    }
    
    // =========================================================
    // 5.2) update(int row, String campo, String valor)
    // =========================================================
    public void update(int row, String campo, String valor) {
        if (row >= numReg || row < 0) return;
        
        try (RandomAccessFile rndFile = new RandomAccessFile(this.f, "rws")) {
            long offsetColumna = getOffsetColumna(campo);
            int longCampo = getLongitudColumna(campo);
            
            rndFile.seek((row * this.longReg) + offsetColumna);
            
            if (valor == null) valor = "";
            String valorFormateado = String.format("%1$-" + longCampo + "s", valor);
            if (valorFormateado.length() > longCampo) valorFormateado = valorFormateado.substring(0, longCampo);
            
            rndFile.write(valorFormateado.getBytes("UTF-8"), 0, longCampo);
            
        } catch (Exception e) {
            System.out.println("Error en update campo: " + e.getMessage());
        }
    }
    
    // =========================================================
    // 6) delete(int row)
    // =========================================================
    public void delete(int row) {
        if (row >= numReg || row < 0) return;
        
        try (RandomAccessFile rndFile = new RandomAccessFile(this.f, "rws")) {
            rndFile.seek(row * this.longReg);
            
            // Creamos una cadena de espacios en blanco del tamaño exacto del registro completo
            String emptyRecord = String.format("%1$-" + this.longReg + "s", "");
            
            // Sobrescribimos el registro, "limpiando" sus datos
            rndFile.write(emptyRecord.getBytes("UTF-8"), 0, (int)this.longReg);
            
        } catch (Exception e) {
            System.out.println("Error en delete: " + e.getMessage());
        }
    }
}
