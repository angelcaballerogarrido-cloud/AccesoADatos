package dom_parser;

import java.io.File;
import java.io.PrintStream;
import java.io.FileNotFoundException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.SAXException;

public class Dom_parser {

    private static final String INDENT_CHAR = "  "; // Usaremos 2 espacios para que se vea claro
    
    public static void muestraNodo (Node nodo, int level, PrintStream ps)
    {
        // Evitamos errores por si llega un nodo nulo
        if (nodo == null) return;
        
        // 1. Generar la indentación (espacios a la izquierda) según el nivel de profundidad
        StringBuilder indent = new StringBuilder();
        for (int i = 0; i < level; i++) {
            indent.append(INDENT_CHAR);
        }
        String tab = indent.toString();

        // 2. Extracción de los metadatos requeridos
        short tipo = nodo.getNodeType();
        String nombre = nodo.getNodeName();
        String valor = nodo.getNodeValue();
        
        // 3. Manejo de Nodos según su Tipo
        
        // TIPO A: El documento principal (la raíz invisible de la que cuelga todo)
        if (tipo == Node.DOCUMENT_NODE) {
            Document doc = (Document) nodo; // Casteamos para poder acceder a los métodos exclusivos
            
            ps.println("=== METADATOS DEL DOCUMENTO XML ===");
            ps.println("Versión XML: " + doc.getXmlVersion());
            ps.println("Encoding: " + doc.getXmlEncoding());
            ps.println("===================================\n");
            
            // Llamada recursiva a sus hijos (Ej: <catalogo>)
            if (nodo.hasChildNodes()) {
                NodeList hijos = nodo.getChildNodes();
                for (int i = 0; i < hijos.getLength(); i++) {
                    muestraNodo(hijos.item(i), level, ps);
                }
            }
        }
        // TIPO B: Nodos de Elemento (las etiquetas normales, ej: <libro>)
        else if (tipo == Node.ELEMENT_NODE) {
            ps.print(tab + "<" + nombre);
            
            // Tratamiento de Atributos (ej: <libro id="1">)
            if (nodo.hasAttributes()) {
                NamedNodeMap atributos = nodo.getAttributes();
                for (int i = 0; i < atributos.getLength(); i++) {
                    Node attr = atributos.item(i);
                    ps.print(" " + attr.getNodeName() + "=\"" + attr.getNodeValue() + "\"");
                }
            }
            ps.println(">");
            
            // Recursividad: Hijos de este elemento (ej: <titulo>, <autor>)
            if (nodo.hasChildNodes()) {
                NodeList hijos = nodo.getChildNodes();
                // Aquí sumamos +1 al nivel (level) para que sus hijos salgan más tabulados a la derecha
                for (int i = 0; i < hijos.getLength(); i++) {
                    muestraNodo(hijos.item(i), level + 1, ps);
                }
            }
            
            // Cierre de la etiqueta (ej: </libro>)
            ps.println(tab + "</" + nombre + ">");
        } 
        // TIPO C: Nodos de Texto (el texto escrito entre dos etiquetas)
        else if (tipo == Node.TEXT_NODE) {
            // Limpiamos espacios en blanco para no imprimir saltos de línea vacíos
            if (valor != null && !valor.trim().isEmpty()) {
                ps.println(tab + valor.trim());
            }
        }
    }
    
    public static void main(String [] args)
    {
        // En Visual Studio Code, si no le pasamos argumentos, args[0] dará error. 
        // Ponemos un fallback para poder probarlo fácilmente sin configuraciones raras.
        String nomFich = "books.xml";
        if (args.length > 0) {
            nomFich = args[0];
        }
        
        System.out.println("Intentando analizar el fichero: " + nomFich + "\n");
        
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setIgnoringComments(true);
        dbf.setIgnoringElementContentWhitespace(true);
        
        try{
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse( new File(nomFich) );
            muestraNodo( doc, 0, System.out );
        }
        catch( FileNotFoundException ex ) {
            System.out.println("No se encuentra el fichero XML. Asegúrate de tenerlo en la carpeta raíz.");
        }
        catch( ParserConfigurationException | SAXException ex )
        {
            System.out.println(ex.getMessage() );
        }
        catch( Exception ex )
        {
            ex.printStackTrace();
        }
    }
}
