package org.example.bolsa_empleo.controller;

import org.example.bolsa_empleo.entidades.*;
import org.example.bolsa_empleo.repository.NacionalidadRepository;
import org.example.bolsa_empleo.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class GeneralController {

    private final PuestoService puestoService;
    private final BusquedaService busquedaService;
    private final EmpresaService empresaService;
    private final LoginService loginService;
    private final OferenteService oferenteService;
    private final NacionalidadRepository nacionalidadRepository;

    public GeneralController(PuestoService puestoService,
                             BusquedaService busquedaService,
                             EmpresaService empresaService,
                             LoginService loginService,
                             OferenteService oferenteService,
                             NacionalidadRepository nacionalidadRepository) {
        this.puestoService = puestoService;
        this.busquedaService = busquedaService;
        this.empresaService = empresaService;
        this.loginService = loginService;
        this.oferenteService = oferenteService;
        this.nacionalidadRepository = nacionalidadRepository;
    }

    @GetMapping("/")
    public String inicio(Model model) {
        List<Puesto> puestos = puestoService.obtenerUltimosCincoPuestosPublicos();

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
        return "empresa/Empresa_Registro";
    }

    @PostMapping("/registro-empresa")
    public String registrarEmpresa(@ModelAttribute Empresa empresa, RedirectAttributes ra) {
        empresaService.registrarEmpresa(empresa);
        ra.addFlashAttribute("exitoso", true);
        return "redirect:/registro-empresa";
    }

    @GetMapping("/registro-oferente")
    public String registroOferente(Model model) {
        model.addAttribute("oferente", new Oferente());
        model.addAttribute("nacionalidades", nacionalidadRepository.findAll());
        return "oferente/Oferente_Registro";
    }

    @PostMapping("/registro-oferente")
    public String registrarOferente(@ModelAttribute Oferente oferente, @RequestParam Long idNacionalidad, RedirectAttributes ra) {
        Nacionalidad nacionalidad = nacionalidadRepository.findById(idNacionalidad).orElse(null);
        oferente.setNacionalidad(nacionalidad);
        oferenteService.registrarOferente(oferente);
        ra.addFlashAttribute("exitoso", true);
        return "redirect:/registro-oferente";
    }

    @GetMapping("/login")
    public String mostrarLogin() {
        return "general/Login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String correo,
                        @RequestParam String password,
                        HttpSession session,
                        RedirectAttributes ra) {

        Object usuario = loginService.validarLogin(correo, password);

        if (usuario == null) {
            ra.addFlashAttribute("error", "Credenciales inválidas");
            return "redirect:/login";
        }

        // Limpiar cualquier sesión anterior
        session.removeAttribute("adminLogueado");
        session.removeAttribute("empresaLogueada");
        session.removeAttribute("oferenteLogueado");

        if (usuario instanceof Administrador admin) {
            session.setAttribute("adminLogueado", admin);
            return "redirect:/admin/dashboard";
        } else if (usuario instanceof Empresa empresa) {
            session.setAttribute("empresaLogueada", empresa);
            return "redirect:/empresa/dashboard";
        } else if (usuario instanceof Oferente oferente) {
            session.setAttribute("oferenteLogueado", oferente);
            return "redirect:/oferente/dashboard";
        }

        ra.addFlashAttribute("error", "No se pudo iniciar sesión");
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}