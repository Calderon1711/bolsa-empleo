package org.example.bolsa_empleo.controller;

import org.example.bolsa_empleo.entidades.*;
import org.example.bolsa_empleo.service.*;
import org.example.bolsa_empleo.service.LoginService;
import org.example.bolsa_empleo.service.EmpresaService;
import org.example.bolsa_empleo.service.PuestoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class GeneralController {

    private final PuestoService puestoService;
    private final BusquedaService busquedaService;
    private final EmpresaService empresaService;
    private final LoginService loginService;

    public GeneralController(PuestoService puestoService,
                             BusquedaService busquedaService,
                             EmpresaService empresaService,LoginService loginService) {
        this.puestoService = puestoService;
        this.busquedaService = busquedaService;
        this.empresaService = empresaService;
        this.loginService = loginService;
    }

    @GetMapping("/buscar-puestos")
    public String buscarPuestos(Model model) {
        model.addAttribute("caracteristicasRaiz", busquedaService.obtenerCaracteristicasRaiz());
        model.addAttribute("buscado", false);
        return "general/Buscar_Puestos";
    }

    @PostMapping("/buscar-puestos")
    public String buscarPuestosPost(@RequestParam(value = "caracteristicaIds", required = false) List<Long> caracteristicaIds,
                                    Model model) {
        model.addAttribute("caracteristicasRaiz", busquedaService.obtenerCaracteristicasRaiz());
        model.addAttribute("resultados", busquedaService.buscarPorCaracteristicas(caracteristicaIds));
        model.addAttribute("buscado", true);
        return "general/Buscar_Puestos";
    }

    @GetMapping("/registro-empresa")
    public String registroEmpresa(Model model) {
        model.addAttribute("empresa", new Empresa());
        return "empresa/Empresa";
    }

    @PostMapping("/registro-empresa")
    public String registrarEmpresa(@ModelAttribute Empresa empresa, RedirectAttributes ra) {
        empresaService.registrarEmpresa(empresa);
        ra.addFlashAttribute("exitoso", true);
        return "redirect:/registro-empresa";
    }

    @GetMapping("/registro-oferente")
    public String registroOferente() {
        return "oferente/Oferente";
    }

    @GetMapping("/login")
    public String mostrarLogin() {
        return "general/Login";
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam String correo,
                                @RequestParam String password,
                                Model model) {

        Object usuario = loginService.validarLogin(correo, password);

        if (usuario instanceof Administrador) {
            return "redirect:/admin/dashboard";
        }
        else if (usuario instanceof Empresa) {
            return "redirect:/empresa/dashboard";
        }
        else if (usuario instanceof Oferente) {
            return "redirect:/oferente/dashboard";
        }
        else {
            model.addAttribute("error", "Correo o contraseña incorrectos");
            return "general/Login";
        }
    }

    @GetMapping("/")
    public String inicio(Model model) {
        List<Puesto> puestos = puestoService.obtenerUltimosCincoPuestosPublicos();

        // Construye el contenido HTML del popover para cada puesto (se hace aquí porque es lógica de presentación)
        Map<Long, String> popoverContent = new LinkedHashMap<>();
        for (Puesto p : puestos) {
            List<PuestoCaracteristica> chars = puestoService.obtenerCaracteristicasDePuesto(p.getId());
            StringBuilder sb = new StringBuilder("<ul class='mb-0 ps-3'>");
            for (PuestoCaracteristica pc : chars) {
                sb.append("<li>")
                  .append(pc.getCaracteristica().getNombre())
                  .append(" (nivel ").append(pc.getNivelRequerido()).append(")")
                  .append("</li>");
            }
            sb.append("</ul>");
            popoverContent.put(p.getId(), sb.toString());
        }

        model.addAttribute("puestos", puestos);
        model.addAttribute("popoverContent", popoverContent);
        return "general/Inicio";
    }
}