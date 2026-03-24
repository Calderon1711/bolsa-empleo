package org.example.bolsa_empleo.controller;

import jakarta.servlet.http.HttpSession;
import org.example.bolsa_empleo.entidades.Administrador;
import org.example.bolsa_empleo.entidades.Caracteristica;
import org.example.bolsa_empleo.service.AdminService;
import org.example.bolsa_empleo.service.ReporteService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final ReporteService reporteService;
    private final HttpSession session;

    public AdminController(AdminService adminService, ReporteService reporteService, HttpSession session) {
        this.adminService = adminService;
        this.reporteService = reporteService;
        this.session = session;
    }

    private boolean adminNoLogueado() {
        return session.getAttribute("adminLogueado") == null;
    }
    private Administrador getAdminLogueado() {
        return (Administrador) session.getAttribute("adminLogueado");
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        if (adminNoLogueado()) return "redirect:/login";

        Administrador admin = getAdminLogueado();

        model.addAttribute("empresasPendientes", adminService.listarEmpresasPendientes().size());
        model.addAttribute("oferentesPendientes", adminService.listarOferentesPendientes().size());
        model.addAttribute("nombreUsuario", admin.getNombreAdministrador());
        return "admin/Admin";
    }

    @GetMapping("/empresas-pendientes")
    public String empresasPendientes(Model model) {
        if (adminNoLogueado()) return "redirect:/login";
        model.addAttribute("empresas", adminService.listarEmpresasPendientes());
        return "admin/Admin_EmpresasPendientes";
    }

    @PostMapping("/empresas/{id}/aprobar")
    public String aprobarEmpresa(@PathVariable Long id, RedirectAttributes ra) {
        if (adminNoLogueado()) return "redirect:/login";
        adminService.aprobarEmpresa(id);
        ra.addFlashAttribute("exitoso", "Empresa aprobada correctamente");
        return "redirect:/admin/empresas-pendientes";
    }

    @GetMapping("/oferentes-pendientes")
    public String oferentesPendientes(Model model) {
        if (adminNoLogueado()) return "redirect:/login";
        model.addAttribute("oferentes", adminService.listarOferentesPendientes());
        return "admin/Admin_OferentesPendientes";
    }

    @PostMapping("/oferentes/{cedula}/aprobar")
    public String aprobarOferente(@PathVariable String cedula, RedirectAttributes ra) {
        if (adminNoLogueado()) return "redirect:/login";
        adminService.aprobarOferente(cedula);
        ra.addFlashAttribute("exitoso", "Oferente aprobado correctamente");
        return "redirect:/admin/oferentes-pendientes";
    }

    @GetMapping("/caracteristicas")
    public String caracteristicas(@RequestParam(required = false) Long padreId, Model model) {
        if (adminNoLogueado()) return "redirect:/login";
        Caracteristica actual = padreId != null ? adminService.buscarCaracteristica(padreId).orElse(null) : null;
        List<Caracteristica> ruta = new ArrayList<>();
        Caracteristica cursor = actual;
        while (cursor != null) {
            ruta.add(0, cursor);
            cursor = cursor.getPadre();
        }
        model.addAttribute("subcategorias", adminService.listarSubcategorias(padreId));
        model.addAttribute("raices", adminService.listarRaices());
        model.addAttribute("padreId", padreId);
        model.addAttribute("actual", actual);
        model.addAttribute("ruta", ruta);
        return "admin/Admin_Caracteristicas";
    }

    @PostMapping("/caracteristicas")
    public String registrarCaracteristica(@RequestParam String nombre,
                                          @RequestParam(required = false) Long padreId,
                                          RedirectAttributes ra) {
        if (adminNoLogueado()) return "redirect:/login";
        adminService.registrarCaracteristica(nombre, padreId);
        ra.addFlashAttribute("exitoso", "Característica registrada correctamente");
        return "redirect:/admin/caracteristicas" + (padreId != null ? "?padreId=" + padreId : "");
    }

    @GetMapping("/reporte/pdf")
    public ResponseEntity<byte[]> descargarReporte() {
        if (adminNoLogueado()) return ResponseEntity.status(302).header(HttpHeaders.LOCATION, "/login").build();
        byte[] pdf = reporteService.generarReporteAdministrativo();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.inline().filename("reporte-bolsa-empleo.pdf").build());
        return ResponseEntity.ok().headers(headers).body(pdf);
    }
}
