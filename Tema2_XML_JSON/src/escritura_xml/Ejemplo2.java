package escritura_xml;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.OutputKeys;

public class Ejemplo2 {
    public static void main(String[] args) {
        try {
            // 1. LEER EL FICHERO ORIGINAL (Lectura como en el Ejemplo 1)
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document docOriginal = db.parse(new File("books.xml"));
            
            // REQUISITO 1: Muestra por pantalla una lista de títulos de sus libros
            System.out.println("=== LISTADO DE TÍTULOS ORIGINALES ===");
            NodeList titulos = docOriginal.getElementsByTagName("Title");
            for (int i = 0; i < titulos.getLength(); i++) {
                System.out.println("📖 " + titulos.item(i).getTextContent());
            }
            System.out.println("=====================================\n");

            // 2. CREACIÓN DEL NUEVO DOCUMENTO XML CON DOM (Requisito 2)
            DOMImplementation implementation = db.getDOMImplementation();
            
            // Creamos el documento nuevo y le inyectamos de paso la etiqueta raíz "Catalogo"
            Document docNuevo = implementation.createDocument(null, "Catalogo", null);
            
            // Asignamos metadatos obligatorios
            docNuevo.setXmlVersion("1.0");
            docNuevo.setXmlStandalone(true);
            
            Element raizOriginal = docOriginal.getDocumentElement(); // <Catalog>
            Element raizNueva = docNuevo.getDocumentElement();       // <Catalogo>
            
            // Recorrer los hijos de Catalog (los <Book>)
            NodeList librosOriginales = raizOriginal.getChildNodes();
            for (int i = 0; i < librosOriginales.getLength(); i++) {
                Node nodoL = librosOriginales.item(i);
                
                if (nodoL.getNodeType() == Node.ELEMENT_NODE && nodoL.getNodeName().equals("Book")) {
                    // Creamos el nodo "Libro"
                    Element nuevoLibro = docNuevo.createElement("Libro");
                    
                    // Conservamos el atributo ID del libro original (ej. id="bk101")
                    if (nodoL.hasAttributes()) {
                        for(int a = 0; a < nodoL.getAttributes().getLength(); a++) {
                            Node attr = nodoL.getAttributes().item(a);
                            nuevoLibro.setAttribute(attr.getNodeName(), attr.getNodeValue());
                        }
                    }
                    
                    // Recorremos los atributos/hijos de Book (<Title>, <Author>...)
                    NodeList hijosLibro = nodoL.getChildNodes();
                    for (int j = 0; j < hijosLibro.getLength(); j++) {
                        Node hijo = hijosLibro.item(j);
                        if (hijo.getNodeType() == Node.ELEMENT_NODE) {
                            
                            // Traducimos el nombre de la etiqueta
                            String nombreTraducido = traducirEtiqueta(hijo.getNodeName());
                            
                            // Creamos la nueva etiqueta traducida y le metemos el texto dentro
                            Element nuevoHijo = docNuevo.createElement(nombreTraducido);
                            nuevoHijo.setTextContent(hijo.getTextContent());
                            
                            // Lo enganchamos al libro
                            nuevoLibro.appendChild(nuevoHijo);
                        }
                    }
                    // Enganchamos el libro al catálogo final
                    raizNueva.appendChild(nuevoLibro);
                }
            }
            
            // 3. TRANSFORMACIÓN Y VOLCADO A DISCO
            DOMSource source = new DOMSource(docNuevo);
            StreamResult result = new StreamResult(new File("libros.xml"));
            
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            
            // Le decimos al Transformer que aplique indentación y saltos de línea para que sea bonito de leer
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            
            // Ejecutamos la transformación final a fichero
            transformer.transform(source, result);
            System.out.println("[✓] El archivo 'libros.xml' se ha generado y traducido correctamente en la raíz del proyecto.");

        } catch (Exception e) {
            System.out.println("Error en la ejecución del parser DOM: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Función que funciona a modo de diccionario para traducir las etiquetas solicitadas
     */
    private static String traducirEtiqueta(String original) {
        switch (original) {
            case "Title": return "Titulo";
            case "Genre": return "Genero";
            case "Price": return "Precio";
            case "Author": return "Autor";
            case "Description": return "Descripcion";
            // NOTA: Un tag en XML no puede llevar espacios por norma de la W3C.
            // Poner "Fecha de publicación" causaría DOMException INVALID_CHARACTER_ERR. Usamos guiones bajos.
            case "Public_date": return "Fecha_de_publicacion"; 
            default: return original;
        }
    }
}
