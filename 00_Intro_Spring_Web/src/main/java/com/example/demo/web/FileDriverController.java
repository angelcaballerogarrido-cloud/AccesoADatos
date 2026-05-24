package com.example.demo.web;

import com.example.demo.services.FileAdapterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/filedriver")
public class FileDriverController {

    @Autowired
    private FileAdapterService adapterService;

    // Directorio donde guardaremos los archivos temporales (XML subido, el DAT y el SCHEMA)
    private final String WORK_DIR = "uploads_filedriver/";

    public FileDriverController() {
        // Asegurarnos de que el directorio exista al arrancar el controlador
        new File(WORK_DIR).mkdirs();
    }

    // PANTALLA PRINCIPAL: Mostrar formulario de subida
    @GetMapping
    public String index() {
        return "filedriver/index";
    }

    // PROCESAMIENTO: Recibe el XML, llama al adaptador y redirige al editor
    @PostMapping("/upload")
    public String uploadXml(@RequestParam("file") MultipartFile file, Model model) {
        try {
            File xmlFile = new File(WORK_DIR + "temp.xml");
            file.transferTo(xmlFile);
            
            File datFile = new File(WORK_DIR + "temp.dat");
            
            // Requisito A: Genera el DAT usando la función adaptadora (esto creará también temp.dat.schema)
            adapterService.xml2dat(xmlFile, datFile);
            
            return "redirect:/filedriver/editor";
        } catch (Exception e) {
            model.addAttribute("error", "Error procesando XML: " + e.getMessage());
            return "filedriver/index";
        }
    }

    // PANTALLA DEL EDITOR: Requisito B (Integrar en WEB FileDrive para EDITAR ficheros)
    @GetMapping("/editor")
    public String editor(Model model) {
        try {
            File datFile = new File(WORK_DIR + "temp.dat");
            if (!datFile.exists()) {
                return "redirect:/filedriver"; // Si no hay datos, volvemos a la subida
            }
            
            // Leemos el fichero binario en caliente y extraemos los metadatos de las columnas dinámicas
            List<Map<String, String>> registros = adapterService.leerDatCompleto(datFile);
            FileAdapterService.SchemaMetadata schema = adapterService.leerSchema(new File(datFile.getAbsolutePath() + ".schema"));
            
            model.addAttribute("registros", registros);
            model.addAttribute("campos", schema.fields);
            
            return "filedriver/editor";
        } catch (Exception e) {
            model.addAttribute("error", "Error cargando el editor: " + e.getMessage());
            return "filedriver/index";
        }
    }

    // ACCIÓN: Guardar una fila modificada en el binario
    @PostMapping("/saveRow")
    public String saveRow(@RequestParam("rowIndex") int rowIndex, @RequestParam Map<String, String> allParams) {
        try {
            File datFile = new File(WORK_DIR + "temp.dat");
            
            // Retiramos el parámetro interno de fila para no inyectarlo en el DAT
            allParams.remove("rowIndex");
            
            // Sobrescribimos usando RandomAccessFile
            adapterService.updateDatRow(datFile, rowIndex, allParams);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/filedriver/editor";
    }

    // ACCIÓN: Requisito C (Descargar ficheros DAT en formato XML)
    @GetMapping("/download")
    public ResponseEntity<FileSystemResource> downloadXml() {
        try {
            File datFile = new File(WORK_DIR + "temp.dat");
            File xmlFileOut = new File(WORK_DIR + "exportado.xml");
            
            // Convertimos de DAT a XML (Función Adaptadora)
            adapterService.dat2xml(datFile, xmlFileOut);
            
            FileSystemResource resource = new FileSystemResource(xmlFileOut);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"fichero_adaptado.xml\"")
                    .contentType(MediaType.APPLICATION_XML)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
