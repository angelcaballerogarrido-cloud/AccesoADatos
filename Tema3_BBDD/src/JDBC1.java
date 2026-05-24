import java.sql.*;
import java.util.*;

public class JDBC1 {

    // Constantes de conexión (Deberás poner las de tu servidor MySQL local)
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/TuBaseDatos?serverTimezone=Europe/Madrid";
    private static final String USER = "root";
    private static final String PASS = "tu_password";
    
    // Asumimos que operamos sobre una tabla llamada 'agenda' donde la Primary Key es 'id'
    private static final String TABLA = "agenda";
    private static final String PK = "id";

    /**
     * Devuelve una nueva conexión a la base de datos
     */
    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    // =========================================================
    // 1) selectCampo(int numRegistro, String nomColumna)
    // =========================================================
    public static String selectCampo(int numRegistro, String nomColumna) {
        // En consultas dinámicas donde el nombre de la columna cambia, no se puede usar '?' para la columna,
        // hay que concatenarlo en el String de forma segura.
        String sql = "SELECT " + nomColumna + " FROM " + TABLA + " WHERE " + PK + " = ?";
        
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, numRegistro);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                // JDBC empieza en el índice 1, no en el 0
                return rs.getString(1);
            }
        } catch (SQLException e) {
            System.out.println("Error selectCampo: " + e.getMessage());
        }
        return null;
    }

    // =========================================================
    // 2) selectColumna(String nomColumna)
    // =========================================================
    public static List<String> selectColumna(String nomColumna) {
        List<String> resultados = new ArrayList<>();
        String sql = "SELECT " + nomColumna + " FROM " + TABLA;
        
        try (Connection con = getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            
            while (rs.next()) {
                resultados.add(rs.getString(1));
            }
        } catch (SQLException e) {
            System.out.println("Error selectColumna: " + e.getMessage());
        }
        return resultados;
    }

    // =========================================================
    // 3) selectRowList(int numRegistro)
    // =========================================================
    public static List<String> selectRowList(int numRegistro) {
        List<String> fila = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLA + " WHERE " + PK + " = ?";
        
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, numRegistro);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                // Sacamos los metadatos para saber cuántas columnas ha devuelto el SELECT *
                ResultSetMetaData metaData = rs.getMetaData();
                int numColumnas = metaData.getColumnCount();
                
                for (int i = 1; i <= numColumnas; i++) {
                    fila.add(rs.getString(i));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error selectRowList: " + e.getMessage());
        }
        return fila;
    }

    // =========================================================
    // 4) selectRowMap(int numRegistro)
    // =========================================================
    public static Map<String, String> selectRowMap(int numRegistro) {
        Map<String, String> fila = new LinkedHashMap<>();
        String sql = "SELECT * FROM " + TABLA + " WHERE " + PK + " = ?";
        
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, numRegistro);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                ResultSetMetaData metaData = rs.getMetaData();
                int numColumnas = metaData.getColumnCount();
                
                for (int i = 1; i <= numColumnas; i++) {
                    // metaData.getColumnName(i) nos da el nombre real de la columna en la BBDD
                    fila.put(metaData.getColumnName(i), rs.getString(i));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error selectRowMap: " + e.getMessage());
        }
        return fila;
    }

    // =========================================================
    // 5.1) update(int row, Map<String, String> reg)
    // =========================================================
    public static void update(int row, Map<String, String> reg) {
        if (reg == null || reg.isEmpty()) return;

        // Construir la consulta dinámicamente: UPDATE agenda SET col1 = ?, col2 = ? WHERE id = ?
        StringBuilder sql = new StringBuilder("UPDATE " + TABLA + " SET ");
        List<String> columnas = new ArrayList<>(reg.keySet());
        
        for (int i = 0; i < columnas.size(); i++) {
            sql.append(columnas.get(i)).append(" = ?");
            if (i < columnas.size() - 1) {
                sql.append(", ");
            }
        }
        sql.append(" WHERE ").append(PK).append(" = ?");

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            
            // Rellenar las interrogaciones con los valores del MAP
            int index = 1;
            for (String columna : columnas) {
                ps.setString(index++, reg.get(columna));
            }
            // Por último, la interrogación del WHERE id = ?
            ps.setInt(index, row);
            
            int filasAfectadas = ps.executeUpdate();
            System.out.println("[UPDATE MAP] Filas modificadas: " + filasAfectadas);
            
        } catch (SQLException e) {
            System.out.println("Error update (Map): " + e.getMessage());
        }
    }

    // =========================================================
    // 5.2) update(int row, String campo, String valor)
    // =========================================================
    public static void update(int row, String campo, String valor) {
        String sql = "UPDATE " + TABLA + " SET " + campo + " = ? WHERE " + PK + " = ?";
        
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, valor);
            ps.setInt(2, row);
            
            int filasAfectadas = ps.executeUpdate();
            System.out.println("[UPDATE CAMPO] Filas modificadas: " + filasAfectadas);
            
        } catch (SQLException e) {
            System.out.println("Error update (Campo): " + e.getMessage());
        }
    }

    // =========================================================
    // 6) delete(int row)
    // =========================================================
    public static void delete(int row) {
        String sql = "DELETE FROM " + TABLA + " WHERE " + PK + " = ?";
        
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, row);
            
            int filasAfectadas = ps.executeUpdate();
            System.out.println("[DELETE] Filas eliminadas: " + filasAfectadas);
            
        } catch (SQLException e) {
            System.out.println("Error delete: " + e.getMessage());
        }
    }

    // =========================================================
    // PROGRAMA PRINCIPAL (TEST)
    // =========================================================
    public static void main(String[] args) {
        System.out.println("--- PRUEBAS DEL GESTOR JDBC1 ---");
        System.out.println("Asegúrate de tener una tabla 'agenda' en MySQL y modificar URL, USER y PASS al inicio de la clase.\n");
        
        /* 
        EJEMPLOS DE CÓMO LLAMAR A LAS FUNCIONES CUANDO TENGAS LA BBDD:
        
        // 1. Mostrar un campo de la ID 1
        String nombre = JDBC1.selectCampo(1, "nombre");
        System.out.println("Nombre del registro 1: " + nombre);
        
        // 4. Mostrar toda la fila 2 en un Map
        Map<String, String> fila = JDBC1.selectRowMap(2);
        System.out.println("Datos del registro 2: " + fila);
        
        // 5.2. Actualizar solo la dirección de la ID 3
        JDBC1.update(3, "direccion", "Calle Nueva 123");
        
        // 6. Borrar la ID 4
        JDBC1.delete(4);
        */
    }
}
