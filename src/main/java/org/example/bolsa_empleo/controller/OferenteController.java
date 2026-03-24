package org.example.bolsa_empleo.controller;

import org.example.bolsa_empleo.entidades.Caracteristica;
import org.example.bolsa_empleo.service.OferenteService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/oferente")
public class OferenteController {

    private final OferenteService oferenteService;

    public OferenteController(OferenteService oferenteService) {
        this.oferenteService = oferenteService;
    }

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam String cedula, Model model) {
        var oferente = oferenteService.buscarOferente(cedula).orElse(null);
        model.addAttribute("oferente", oferente);
        model.addAttribute("cedula", cedula);
        return "oferente/Oferente";
    }

    @GetMapping("/habilidades")
    public String habilidades(@RequestParam String cedula,
                              @RequestParam(required = false) Long padreId,
                              Model model) {
        var oferente = oferenteService.buscarOferente(cedula).orElse(null);
        Caracteristica actual = padreId != null ? oferenteService.buscarCaracteristica(padreId).orElse(null) : null;

        List<Caracteristica> ruta = new ArrayList<>();
        Caracteristica cursor = actual;
        while (cursor != null) {
            ruta.add(0, cursor);
            cursor = cursor.getPadre();
        }

        model.addAttribute("oferente", oferente);
        model.addAttribute("cedula", cedula);
        model.addAttribute("habilidades", oferenteService.listarHabilidades(cedula));
        model.addAttribute("subcategorias", oferenteService.obtenerSubcategorias(padreId));
        model.addAttribute("caracteristicasRaiz", oferenteService.obtenerCaracteristicasRaiz());
        model.addAttribute("ruta", ruta);
        model.addAttribute("actual", actual);
        model.addAttribute("padreId", padreId);
        return "oferente/Oferente_Habilidades";
    }

    @PostMapping("/habilidades/agregar")
    public String agregarHabilidad(@RequestParam String cedula,
                                   @RequestParam Long caracteristicaId,
                                   @RequestParam Integer nivel,
                                   RedirectAttributes ra) {
        oferenteService.guardarHabilidad(cedula, caracteristicaId, nivel);
        ra.addFlashAttribute("exitoso", "Habilidad agregada correctamente");
        return "redirect:/oferente/habilidades?cedula=" + cedula;
    }

    @GetMapping("/postulaciones")
    public String postulaciones(@RequestParam String cedula, Model model) {
        model.addAttribute("cedula", cedula);
        model.addAttribute("postulaciones", oferenteService.listarPostulaciones(cedula));
        model.addAttribute("puestos", oferenteService.listarPuestosPublicos(cedula));
        return "oferente/Oferente_Postulaciones";
    }

    @PostMapping("/postular")
    public String postular(@RequestParam String cedula,
                           @RequestParam Long puestoId,
                           RedirectAttributes ra) {
        oferenteService.postular(cedula, puestoId);
        ra.addFlashAttribute("exitoso", "Postulación registrada correctamente");
        return "redirect:/oferente/postulaciones?cedula=" + cedula;
    }

    @GetMapping("/cv")
    public String cv(@RequestParam String cedula, Model model) {
        model.addAttribute("cedula", cedula);
        model.addAttribute("cv", oferenteService.obtenerCv(cedula).orElse(null));
        return "oferente/Oferente_CV";
    }

    @PostMapping("/cv")
    public String subirCv(@RequestParam String cedula,
                          @RequestParam String descripcion,
                          @RequestParam MultipartFile archivo,
                          RedirectAttributes ra) {
        try {
            oferenteService.guardarCv(cedula, descripcion, archivo);
            ra.addFlashAttribute("exitoso", "CV cargado correctamente");
        } catch (IllegalArgumentException | IOException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/oferente/cv?cedula=" + cedula;
    }

    @GetMapping("/cv/ver/{idCv}")
    public ResponseEntity<Resource> verCv(@PathVariable Long idCv) throws IOException {
        var ruta = oferenteService.obtenerRutaCv(idCv).orElseThrow();
        Resource recurso = new FileSystemResource(ruta);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + ruta.getFileName() + "\"")
                .contentLength(Files.size(ruta))
                .body(recurso);
    }
}
