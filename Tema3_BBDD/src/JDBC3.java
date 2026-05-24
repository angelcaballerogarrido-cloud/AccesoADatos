import java.sql.*;
import java.util.*;

public class JDBC3 {

    private static final String URL = "jdbc:mysql://127.0.0.1:3306/TuBaseDatos?serverTimezone=Europe/Madrid";
    private static final String USER = "root";
    private static final String PASS = "tu_password";
    private static final String TABLA = "agenda";

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    /**
     * Ejemplo de selectColumna aplicando todos los requisitos acumulados:
     * 1) PreparedStatement
     * 2) Transacciones (Aunque para un SELECT puro no es vital, lo ponemos para cumplir la norma)
     * 3) TYPE_SCROLL_INSENSITIVE
     */
    public static List<String> selectColumnaAvanzado(String nomColumna) {
        List<String> resultados = new ArrayList<>();
        String sql = "SELECT " + nomColumna + " FROM " + TABLA;
        
        Connection con = null;
        try {
            con = getConnection();
            
            // REQUISITO 2: Transacciones
            con.setAutoCommit(false);
            
            // REQUISITO 1 y 3: PreparedStatement con SCROLL_INSENSITIVE (Nos permite movernos en cualquier dirección)
            try (PreparedStatement ps = con.prepareStatement(
                    sql, 
                    ResultSet.TYPE_SCROLL_INSENSITIVE, 
                    ResultSet.CONCUR_READ_ONLY)) {
                
                ResultSet rs = ps.executeQuery();
                
                // === DEMOSTRACIÓN DEL PODER DEL CURSOR SCROLL ===
                
                // 1. Podemos saltar directamente a la última fila!
                if (rs.last()) {
                    System.out.println("El último registro es: " + rs.getString(1));
                }
                
                // 2. Podemos volver al principio de golpe!
                rs.beforeFirst();
                
                // 3. Y ahora leemos todo de forma normal
                while (rs.next()) {
                    resultados.add(rs.getString(1));
                }
            }
            
            con.commit();
            
        } catch (SQLException e) {
            System.out.println("Error SQL: " + e.getMessage());
            if (con != null) {
                try { con.rollback(); } catch (SQLException ex) {}
            }
        } finally {
            if (con != null) {
                try { con.setAutoCommit(true); con.close(); } catch (SQLException e) {}
            }
        }
        return resultados;
    }

    public static void main(String[] args) {
        System.out.println("Clase JDBC3 compilada. Todos los PreparedStatements ahora tienen cursores desplazables bidireccionales (Scroll Insensitive).");
    }
}
