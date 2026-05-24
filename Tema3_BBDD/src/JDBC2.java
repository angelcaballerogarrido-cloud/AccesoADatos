import java.sql.*;
import java.util.ArrayList;

public class JDBC2 {

    // Constantes de conexión
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/TuBaseDatos?serverTimezone=Europe/Madrid";
    private static final String USER = "root";
    private static final String PASS = "tu_password";

    /*
     * SCRIPT DE CREACIÓN DE LAS TABLAS (PARA EJECUTAR EN MYSQL/PHPMYADMIN)
     * 
     * CREATE TABLE usuarios (
     *     id INT AUTO_INCREMENT PRIMARY KEY,
     *     dni VARCHAR(20) UNIQUE,
     *     nombre VARCHAR(100),
     *     direccion VARCHAR(200),
     *     cp VARCHAR(10)
     * );
     * 
     * CREATE TABLE licencias (
     *     id_usuario INT,
     *     tipo VARCHAR(2),
     *     expedicion DATETIME,
     *     caducidad DATETIME,
     *     FOREIGN KEY (id_usuario) REFERENCES usuarios(id)
     * );
     */

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    /**
     * Requisito 5: Permite INSERTAR 1 USUARIO + VARIAS LICENCIAS con 1 sola llamada usando TRANSACCIONES.
     */
    public static boolean insertLicencias(String dni, String direccion, String cp, String nombre, ArrayList<ArrayList<String>> licencias) {
        Connection con = null;
        
        try {
            con = getConnection();
            
            // Requisito 4: Iniciamos la transacción congelando el guardado automático
            con.setAutoCommit(false);

            // PASO 1: Insertar el Usuario
            String sqlUser = "INSERT INTO usuarios (dni, direccion, cp, nombre) VALUES (?, ?, ?, ?)";
            
            // Requisito 3: PreparedStatement en TODOS los statements.
            // Statement.RETURN_GENERATED_KEYS nos permite recuperar el ID (AutoIncrement) que MySQL le asigne.
            try (PreparedStatement psUser = con.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS)) {
                psUser.setString(1, dni);
                psUser.setString(2, direccion);
                psUser.setString(3, cp);
                psUser.setString(4, nombre);
                psUser.executeUpdate();
                
                // Extraemos el ID recién creado
                ResultSet rs = psUser.getGeneratedKeys();
                int idUsuarioNuevo = -1;
                if (rs.next()) {
                    idUsuarioNuevo = rs.getInt(1);
                } else {
                    throw new SQLException("No se pudo obtener el ID autogenerado del usuario.");
                }
                
                // PASO 2: Insertar sus Licencias asociadas a ese ID
                if (licencias != null && !licencias.isEmpty()) {
                    String sqlLic = "INSERT INTO licencias (id_usuario, tipo, expedicion, caducidad) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement psLic = con.prepareStatement(sqlLic)) {
                        for (ArrayList<String> lic : licencias) {
                            psLic.setInt(1, idUsuarioNuevo);
                            psLic.setString(2, lic.get(0)); // TIPO (Ej: A1)
                            psLic.setString(3, lic.get(1)); // EXPEDICION (YYYY-MM-DD HH:MM:SS)
                            psLic.setString(4, lic.get(2)); // CADUCIDAD (YYYY-MM-DD HH:MM:SS)
                            psLic.executeUpdate();
                        }
                    }
                }
            }
            
            // Requisito 4: Si hemos llegado hasta aquí sin excepciones, confirmamos todas las operaciones
            con.commit();
            System.out.println("[✓] Transacción Exitosa: Usuario y licencias registrados.");
            return true;

        // Requisito 6: Gestión correcta de Excepciones SQL y Genéricas
        } catch (SQLException e) {
            System.out.println("[X] Error SQL detectado: " + e.getMessage());
            System.out.println(">>> Ejecutando ROLLBACK para deshacer cambios a medias...");
            if (con != null) {
                try {
                    con.rollback();
                    System.out.println("<<< ROLLBACK COMPLETADO. La base de datos está a salvo.");
                } catch (Exception ex) {
                    // Requisito 6: Exception al RollBack ejecutada dentro de un try catch
                    System.out.println("[FALTAL] Error grave al intentar hacer Rollback: " + ex.getMessage());
                }
            }
            return false;
            
        } catch (Exception e) {
            System.out.println("[X] Error Genérico en la lógica: " + e.getMessage());
            return false;
            
        } finally {
            // Buenas prácticas: Restaurar el autocommit y cerrar la conexión
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException e) {}
            }
        }
    }

    /**
     * Requisito 5: Elimina todas las Licencias de un DNI concreto usando TRANSACCIONES.
     */
    public static boolean eliminarLicencias(String dni) {
        Connection con = null;
        
        try {
            con = getConnection();
            con.setAutoCommit(false);
            
            // 1. Averiguar qué ID tiene el usuario que posee este DNI
            String sqlFind = "SELECT id FROM usuarios WHERE dni = ?";
            int userId = -1;
            
            try (PreparedStatement psFind = con.prepareStatement(sqlFind)) {
                psFind.setString(1, dni);
                ResultSet rs = psFind.executeQuery();
                if (rs.next()) {
                    userId = rs.getInt(1);
                } else {
                    throw new SQLException("El usuario con DNI " + dni + " no existe en la base de datos.");
                }
            }
            
            // 2. Eliminar todas sus licencias asociadas
            String sqlDel = "DELETE FROM licencias WHERE id_usuario = ?";
            try (PreparedStatement psDel = con.prepareStatement(sqlDel)) {
                psDel.setInt(1, userId);
                int afectadas = psDel.executeUpdate();
                System.out.println("[✓] Operación Exitosa. Se han eliminado " + afectadas + " licencias del DNI " + dni);
            }
            
            // Confirmamos
            con.commit();
            return true;
            
        } catch (SQLException e) {
            System.out.println("[X] Error SQL al intentar eliminar: " + e.getMessage());
            if (con != null) {
                try {
                    con.rollback();
                } catch (Exception ex) {
                    System.out.println("[FATAL] Error en Rollback: " + ex.getMessage());
                }
            }
            return false;
        } catch (Exception e) {
            System.out.println("[X] Error Genérico: " + e.getMessage());
            return false;
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException e) {}
            }
        }
    }

    public static void main(String[] args) {
        // Ejemplo de uso para probarlo cuando tengas la BBDD creada:
        /*
        ArrayList<ArrayList<String>> listaLicencias = new ArrayList<>();
        
        ArrayList<String> lic1 = new ArrayList<>();
        lic1.add("B1");
        lic1.add("2020-05-10 10:00:00");
        lic1.add("2030-05-10 10:00:00");
        listaLicencias.add(lic1);
        
        ArrayList<String> lic2 = new ArrayList<>();
        lic2.add("A2");
        lic2.add("2022-01-15 12:30:00");
        lic2.add("2032-01-15 12:30:00");
        listaLicencias.add(lic2);

        // Prueba de Inserción
        insertLicencias("12345678Z", "Av. Programadores 4", "28080", "Juan Antonio", listaLicencias);
        
        // Prueba de Borrado
        // eliminarLicencias("12345678Z");
        */
        System.out.println("Clase JDBC2 compilada y lista para ejecutar transacciones SQL.");
    }
}
