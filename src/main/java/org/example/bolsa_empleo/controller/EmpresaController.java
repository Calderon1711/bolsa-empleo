package org.example.bolsa_empleo.controller;

import jakarta.servlet.http.HttpSession;
import org.example.bolsa_empleo.entidades.Empresa;
import org.example.bolsa_empleo.entidades.OferenteCaracteristica;
import org.example.bolsa_empleo.entidades.Puesto;
import org.example.bolsa_empleo.service.AdminService;
import org.example.bolsa_empleo.service.EmpresaService;
import org.example.bolsa_empleo.service.OferenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/empresa")
public class EmpresaController {

    private final EmpresaService empresaService;
    private final AdminService adminService;
    private final OferenteService oferenteService;

    @Autowired
    private HttpSession session;

    public EmpresaController(EmpresaService empresaService,
                             AdminService adminService,
                             OferenteService oferenteService) {
        this.empresaService = empresaService;
        this.adminService = adminService;
        this.oferenteService = oferenteService;
    }

    /** Obtiene la empresa logueada de la sesión, o null si no hay sesión activa. */
    private Empresa getEmpresaLogueada() {
        return (Empresa) session.getAttribute("empresaLogueada");
    }

    // ── Dashboard ────────────────────────────────────────────────────────────────

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Empresa empresa = getEmpresaLogueada();
        if (empresa == null) return "redirect:/login";
        model.addAttribute("empresa", empresa);
        return "empresa/Empresa";
    }

    // ── Mis puestos ──────────────────────────────────────────────────────────────

    @GetMapping("/puestos")
    public String misPuestos(Model model) {
        Empresa empresa = getEmpresaLogueada();
        if (empresa == null) return "redirect:/login";
        model.addAttribute("puestos", empresaService.listarPuestos(empresa.getIdEmpresa()));
        model.addAttribute("tipoCambio", empresaService.obtenerTipoCambio());
        return "empresa/Empresa_MisPuestos";
    }

    @PostMapping("/puestos/desactivar/{id}")
    public String desactivarPuesto(@PathVariable Long id) {
        if (getEmpresaLogueada() == null) return "redirect:/login";
        empresaService.desactivarPuesto(id);
        return "redirect:/empresa/puestos";
    }

    // ── Publicar puesto ──────────────────────────────────────────────────────────

    @GetMapping("/puestos/publicar")
    public String mostrarFormPublicar(Model model) {
        if (getEmpresaLogueada() == null) return "redirect:/login";
        model.addAttribute("puesto", new Puesto());
        model.addAttribute("caracteristicas", adminService.listarRaices());
        return "empresa/Empresa_PublicarPuesto";
    }

    @PostMapping("/puestos/publicar")
    public String publicarPuesto(@ModelAttribute Puesto puesto,
                                 @RequestParam(required = false) List<Long> caracteristicaIds,
                                 @RequestParam(required = false) List<Integer> niveles) {
        Empresa empresa = getEmpresaLogueada();
        if (empresa == null) return "redirect:/login";
        empresaService.publicarPuesto(puesto, empresa.getIdEmpresa(), caracteristicaIds, niveles);
        return "redirect:/empresa/puestos";
    }

    // ── Buscar candidatos ────────────────────────────────────────────────────────

    @GetMapping("/candidatos/buscar")
    public String buscarCandidatos(@RequestParam Long puestoId, Model model) {
        Empresa empresa = getEmpresaLogueada();
        if (empresa == null) return "redirect:/login";

        List<EmpresaService.ResultadoCandidato> candidatos = empresaService.buscarCandidatos(puestoId);

        Puesto puesto = empresaService.listarPuestos(empresa.getIdEmpresa()).stream()
                .filter(p -> p.getId().equals(puestoId))
                .findFirst().orElseThrow();

        model.addAttribute("candidatos", candidatos);
        model.addAttribute("puesto", puesto);
        return "empresa/Empresa_Candidatos";
    }

    // ── Detalle de oferente ──────────────────────────────────────────────────────

    @GetMapping("/candidatos/{cedula}")
    public String detalleOferente(@PathVariable String cedula, Model model) {
        if (getEmpresaLogueada() == null) return "redirect:/login";
        oferenteService.buscarOferente(cedula).ifPresent(o -> model.addAttribute("oferente", o));
        List<OferenteCaracteristica> habilidades = oferenteService.listarHabilidades(cedula);
        model.addAttribute("habilidades", habilidades);
        return "empresa/Detalle_Empleado";
    }
}
