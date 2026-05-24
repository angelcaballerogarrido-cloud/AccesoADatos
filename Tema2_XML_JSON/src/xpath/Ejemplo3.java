package xpath;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import java.io.File;

public class Ejemplo3 {

    public static void main(String[] args) {
        try {
            // 1. CARGA DEL MODELO DOM (Idéntico al tutorial)
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            // ¡Nunca olvidar esto para evitar problemas con Namespaces en XPath!
            factory.setNamespaceAware(true); 
            
            DocumentBuilder builder = factory.newDocumentBuilder();
            // Usamos nuestro XML personalizado en lugar del del tutorial
            Document doc = builder.parse(new File("videogames.xml"));

            // 2. CREACIÓN DEL MOTOR XPATH
            XPathFactory xpathfactory = XPathFactory.newInstance();
            XPath xpath = xpathfactory.newXPath();
            XPathExpression expr;
            Object result;
            NodeList nodes;

            System.out.println("====================================================");
            System.out.println("      CONSULTAS XPATH SOBRE VIDEOGAMES.XML          ");
            System.out.println("====================================================\n");

            // --- CONSULTA 1: Exploración básica ---
            System.out.println("1) Obtener todos los títulos del catálogo (//game/title/text()):");
            expr = xpath.compile("//game/title/text()");
            result = expr.evaluate(doc, XPathConstants.NODESET); // Retorna un Set de Nodos
            nodes = (NodeList) result;
            for (int i = 0; i < nodes.getLength(); i++) {
                System.out.println("   - " + nodes.item(i).getNodeValue());
            }
            System.out.println();

            // --- CONSULTA 2: Filtro condicional por elemento ---
            System.out.println("2) Juegos que cuestan menos de $30 (//game[price<30]/title/text()):");
            expr = xpath.compile("//game[price<30]/title/text()");
            result = expr.evaluate(doc, XPathConstants.NODESET);
            nodes = (NodeList) result;
            for (int i = 0; i < nodes.getLength(); i++) {
                System.out.println("   - " + nodes.item(i).getNodeValue());
            }
            System.out.println();

            // --- CONSULTA 3: Filtro condicional por texto exacto ---
            System.out.println("3) Juegos desarrollados por CD Projekt Red (//game[developer='CD Projekt Red']/title/text()):");
            expr = xpath.compile("//game[developer='CD Projekt Red']/title/text()");
            result = expr.evaluate(doc, XPathConstants.NODESET);
            nodes = (NodeList) result;
            for (int i = 0; i < nodes.getLength(); i++) {
                System.out.println("   - " + nodes.item(i).getNodeValue());
            }
            System.out.println();

            // --- CONSULTA 4: Uso de funciones XPath (count) ---
            System.out.println("4) Contar juegos lanzados DESPUÉS de 2018 (count(//game[@year>2018])):");
            expr = xpath.compile("count(//game[@year>2018])");
            // ATENCIÓN: El resultado ya no es un NODESET, es un NUMBER
            result = expr.evaluate(doc, XPathConstants.NUMBER); 
            Double count = (Double) result;
            System.out.println("   -> Total: " + count.intValue() + " juegos encontrados.");
            System.out.println();

            // --- CONSULTA 5: Extracción de atributos (@) ---
            System.out.println("5) ID del juego lanzado en el año 2022 (//game[@year=2022]/@id):");
            expr = xpath.compile("//game[@year=2022]/@id");
            result = expr.evaluate(doc, XPathConstants.NODESET);
            nodes = (NodeList) result;
            for (int i = 0; i < nodes.getLength(); i++) {
                System.out.println("   -> ID: " + nodes.item(i).getNodeValue());
            }
            System.out.println();

        } catch (Exception e) {
            System.out.println("Error crítico ejecutando XPath: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
