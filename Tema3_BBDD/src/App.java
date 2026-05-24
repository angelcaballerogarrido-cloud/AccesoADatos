import java.sql.*;

public class App {
    public static void main(String[] args) {
        // Cargar el DRIVER MYSQL manualmente
        // NO ES NECESARIO DESDE JAVA SE 6
        try {
            // Desde MySQL 8+ el driver viejo "com.mysql.jdbc.Driver" está obsoleto. 
            // El correcto es el que ha puesto tu profesor con ".cj."
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            System.out.println("==================================================");
            System.out.println("[✓] ÉXITO: El DRIVER de MySQL se ha cargado correctamente.");
            System.out.println("==================================================");
        } 
        catch(Exception ex) {
            System.out.println("[X] ERROR AL CARGAR EL DRIVER: No se encuentra el archivo .jar o la clase no existe.");
            ex = ex; // Tal cual lo puso el profesor
        }
    } // End Main()
} // End Class
