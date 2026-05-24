package javaapplication3;

import java.io.File;
import java.util.Date;

public class JavaApplication3 {

    public static void main(String[] args)
    {
        
        String ruta = "/media";
        if( args.length > 0 ) ruta = args[0];
        
        File fich = new File( ruta );
        
        if( !fich.exists() )
        {
            // Mantenemos el texto del profesor (corrigiendo la pequeña errata 'direcotio' a 'directorio')
            System.out.println("No existe el fichero o directorio " + ruta );
        }
        else
        {
            // --- CÓDIGO AÑADIDO: Mayor número de métodos posibles ---
            System.out.println("--- INFORMACIÓN EXTRA ---");
            System.out.println("Ruta absoluta: " + fich.getAbsolutePath());
            System.out.println("Nombre: " + fich.getName());
            System.out.println("¿Es oculto?: " + (fich.isHidden() ? "Sí" : "No"));
            System.out.println("Permisos - Lectura: " + fich.canRead() + " | Escritura: " + fich.canWrite() + " | Ejecución: " + fich.canExecute());
            System.out.println("Última modificación: " + new Date(fich.lastModified()));
            System.out.println("-------------------------");
            
            if( fich.isFile() )
            {
                // Código base del profesor para ficheros
                System.out.println(ruta+" es un fichero.");
                
                // --- CÓDIGO AÑADIDO ---
                System.out.println("Tamaño del fichero: " + fich.length() + " bytes");
                System.out.println("Ruta del directorio padre: " + fich.getParent());
            }
            else
            {
                // Código base del profesor para directorios
                System.out.println(" " + ruta + " es un directorio. Contenidos:");
                File[] ficheros = fich.listFiles();
                
                // Añadimos comprobación de null para evitar errores si no hay permisos
                if (ficheros != null) {
                    for( File f: ficheros )
                    {
                        String texto = f.isDirectory() ? "/" : f.isFile() ? "_":"?";
                        
                        // CÓDIGO AÑADIDO: También usamos .length() y .getName() aquí dentro
                        String extraInfo = f.isFile() ? " (" + f.length() + " bytes)" : "";
                        System.out.println(texto + " " + f.getName() + extraInfo);
                    }
                }
            }
        }       
    }   
}
