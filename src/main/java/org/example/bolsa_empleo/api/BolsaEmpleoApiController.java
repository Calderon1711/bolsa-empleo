package org.example.bolsa_empleo.api;

import jakarta.servlet.http.HttpSession;
import org.example.bolsa_empleo.entidades.Administrador;
import org.example.bolsa_empleo.entidades.Empresa;
import org.example.bolsa_empleo.entidades.Oferente;
import org.example.bolsa_empleo.service.LoginService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class BolsaEmpleoApiController {

    private final LoginService loginService;

    public BolsaEmpleoApiController(LoginService loginService) {
        this.loginService = loginService;
    }

    private ResponseEntity<Map<String, Object>> ok(Object data) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("ok", true);
        body.put("data", data);
        return ResponseEntity.ok(body);
    }

    private ResponseEntity<Map<String, Object>> mensaje(String mensaje) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("ok", true);
        body.put("mensaje", mensaje);
        return ResponseEntity.ok(body);
    }

    private ResponseEntity<Map<String, Object>> error(HttpStatus status, String mensaje) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("ok", false);
        body.put("mensaje", mensaje);
        return ResponseEntity.status(status).body(body);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestBody Map<String, String> body,
            HttpSession session
    ) {
        String correo = body.get("correo");
        String password = body.get("password");

        Object usuario = loginService.validarLogin(correo, password);

        if (usuario == null) {
            return error(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
        }

        session.removeAttribute("adminLogueado");
        session.removeAttribute("empresaLogueada");
        session.removeAttribute("oferenteLogueado");

        if (usuario instanceof Administrador admin) {
            session.setAttribute("adminLogueado", admin);

            return ok(Map.of(
                    "tipo", "ADMIN",
                    "cedula", admin.getCedulaAdministrador(),
                    "nombre", admin.getNombreAdministrador(),
                    "correo", admin.getCorreoAdministrador()
            ));
        }

        if (usuario instanceof Empresa empresa) {
            session.setAttribute("empresaLogueada", empresa);

            return ok(Map.of(
                    "tipo", "EMPRESA",
                    "id", empresa.getIdEmpresa(),
                    "nombre", empresa.getNombreEmpresa(),
                    "correo", empresa.getCorreoEmpresa()
            ));
        }

        if (usuario instanceof Oferente oferente) {
            session.setAttribute("oferenteLogueado", oferente);

            return ok(Map.of(
                    "tipo", "OFERENTE",
                    "cedula", oferente.getCedulaOferente(),
                    "nombre", oferente.getNombreOferente(),
                    "correo", oferente.getCorreoOferente()
            ));
        }

        return error(HttpStatus.UNAUTHORIZED, "Tipo de usuario no reconocido");
    }

    @GetMapping("/auth/me")
    public ResponseEntity<Map<String, Object>> obtenerUsuarioActual(HttpSession session) {

        Object admin = session.getAttribute("adminLogueado");
        Object empresa = session.getAttribute("empresaLogueada");
        Object oferente = session.getAttribute("oferenteLogueado");

        if (admin instanceof Administrador a) {
            return ok(Map.of(
                    "tipo", "ADMIN",
                    "cedula", a.getCedulaAdministrador(),
                    "nombre", a.getNombreAdministrador(),
                    "correo", a.getCorreoAdministrador()
            ));
        }

        if (empresa instanceof Empresa e) {
            return ok(Map.of(
                    "tipo", "EMPRESA",
                    "id", e.getIdEmpresa(),
                    "nombre", e.getNombreEmpresa(),
                    "correo", e.getCorreoEmpresa()
            ));
        }

        if (oferente instanceof Oferente o) {
            return ok(Map.of(
                    "tipo", "OFERENTE",
                    "cedula", o.getCedulaOferente(),
                    "nombre", o.getNombreOferente(),
                    "correo", o.getCorreoOferente()
            ));
        }

        return error(HttpStatus.UNAUTHORIZED, "No hay sesión activa");
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpSession session) {
        session.invalidate();
        return mensaje("Sesión cerrada correctamente");
    }
}