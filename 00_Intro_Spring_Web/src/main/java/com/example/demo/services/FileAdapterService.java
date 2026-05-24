package com.example.demo.services;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.*;

@Service
public class FileAdapterService {

    // Estructura interna para almacenar los metadatos de los campos flexibles
    public static class SchemaMetadata implements Serializable {
        public List<String> fields = new ArrayList<>();
        public List<Integer> lengths = new ArrayList<>();
        public long recordLength = 0;
    }

    /**
     * ADAPTADOR 1: XML -> DAT (Acceso Aleatorio)
     * Cumple requisitos A, D y E (Cálculo dinámico de anchos máximos).
     */
    public SchemaMetadata xml2dat(File xmlFile, File datFile) throws Exception {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(xmlFile);
        doc.getDocumentElement().normalize();

        NodeList itemList = doc.getElementsByTagName("item");

        SchemaMetadata schema = new SchemaMetadata();
        Map<String, Integer> maxLengths = new LinkedHashMap<>();

        // PASO 1 (Opcional E): Pre-leer el XML para descubrir campos y anchos máximos
        for (int i = 0; i < itemList.getLength(); i++) {
            Node itemNode = itemList.item(i);
            if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
                NodeList children = itemNode.getChildNodes();
                for (int j = 0; j < children.getLength(); j++) {
                    Node child = children.item(j);
                    if (child.getNodeType() == Node.ELEMENT_NODE) {
                        String fieldName = child.getNodeName();
                        String value = child.getTextContent().trim();
                        int length = value.length();
                        
                        // Opcional D: Tamaño máximo fijo (ej: 250 bytes)
                        if (length == 0) length = 1;
                        if (length > 250) length = 250;
                        
                        maxLengths.put(fieldName, Math.max(maxLengths.getOrDefault(fieldName, 10), length));
                    }
                }
            }
        }

        schema.fields.addAll(maxLengths.keySet());
        schema.lengths.addAll(maxLengths.values());
        for (int len : schema.lengths) {
            schema.recordLength += len;
        }

        // Guardar esquema para usarlo más adelante sin reescanear
        guardarSchema(schema, new File(datFile.getAbsolutePath() + ".schema"));

        // PASO 2: Volcar a fichero binario DAT (Acceso Aleatorio)
        try (RandomAccessFile raf = new RandomAccessFile(datFile, "rws")) {
            raf.setLength(0); // Reiniciamos el archivo por si existía
            for (int i = 0; i < itemList.getLength(); i++) {
                Node itemNode = itemList.item(i);
                if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) itemNode;
                    for (int j = 0; j < schema.fields.size(); j++) {
                        String fieldName = schema.fields.get(j);
                        int len = schema.lengths.get(j);
                        String val = "";
                        NodeList nl = element.getElementsByTagName(fieldName);
                        if (nl.getLength() > 0) {
                            val = nl.item(0).getTextContent().trim();
                        }
                        
                        String formatted = String.format("%1$-" + len + "s", val);
                        if (formatted.length() > len) formatted = formatted.substring(0, len);
                        raf.write(formatted.getBytes("UTF-8"), 0, len);
                    }
                }
            }
        }
        return schema;
    }

    /**
     * ADAPTADOR 2: DAT -> XML
     * Cumple requisitos A y C (Descargar DAT formateado en XML).
     */
    public void dat2xml(File datFile, File xmlFile) throws Exception {
        SchemaMetadata schema = leerSchema(new File(datFile.getAbsolutePath() + ".schema"));
        
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.newDocument();
        
        Element rootElement = doc.createElement("catalogo");
        doc.appendChild(rootElement);

        try (RandomAccessFile raf = new RandomAccessFile(datFile, "r")) {
            long totalRecords = raf.length() / schema.recordLength;
            for (long i = 0; i < totalRecords; i++) {
                Element item = doc.createElement("item");
                
                for (int j = 0; j < schema.fields.size(); j++) {
                    int len = schema.lengths.get(j);
                    byte[] bytes = new byte[len];
                    raf.read(bytes);
                    String val = new String(bytes, "UTF-8").trim();
                    
                    Element field = doc.createElement(schema.fields.get(j));
                    field.appendChild(doc.createTextNode(val));
                    item.appendChild(field);
                }
                rootElement.appendChild(item);
            }
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(xmlFile);
        transformer.transform(source, result);
    }

    /**
     * MÉTODOS DEL EDITOR WEB:
     * Leen y escriben en caliente el fichero DAT (Acceso Aleatorio)
     */
    public List<Map<String, String>> leerDatCompleto(File datFile) throws Exception {
        SchemaMetadata schema = leerSchema(new File(datFile.getAbsolutePath() + ".schema"));
        List<Map<String, String>> registros = new ArrayList<>();
        
        try (RandomAccessFile raf = new RandomAccessFile(datFile, "r")) {
            long totalRecords = raf.length() / schema.recordLength;
            for (long i = 0; i < totalRecords; i++) {
                Map<String, String> row = new LinkedHashMap<>();
                for (int j = 0; j < schema.fields.size(); j++) {
                    int len = schema.lengths.get(j);
                    byte[] bytes = new byte[len];
                    raf.read(bytes);
                    row.put(schema.fields.get(j), new String(bytes, "UTF-8").trim());
                }
                registros.add(row);
            }
        }
        return registros;
    }

    public void updateDatRow(File datFile, int rowIndex, Map<String, String> newData) throws Exception {
        SchemaMetadata schema = leerSchema(new File(datFile.getAbsolutePath() + ".schema"));
        
        try (RandomAccessFile raf = new RandomAccessFile(datFile, "rws")) {
            raf.seek(rowIndex * schema.recordLength);
            for (int j = 0; j < schema.fields.size(); j++) {
                String fieldName = schema.fields.get(j);
                int len = schema.lengths.get(j);
                String val = newData.getOrDefault(fieldName, "");
                String formatted = String.format("%1$-" + len + "s", val);
                if (formatted.length() > len) formatted = formatted.substring(0, len);
                raf.write(formatted.getBytes("UTF-8"), 0, len);
            }
        }
    }

    // Persistencia de los metadatos dinámicos
    private void guardarSchema(SchemaMetadata schema, File file) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(schema);
        }
    }

    public SchemaMetadata leerSchema(File file) throws Exception {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (SchemaMetadata) ois.readObject();
        }
    }
}
