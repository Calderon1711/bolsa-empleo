package org.example.bolsa_empleo.api;

import org.example.bolsa_empleo.entidades.Administrador;
import org.example.bolsa_empleo.entidades.Empresa;
import org.example.bolsa_empleo.entidades.Oferente;
import org.example.bolsa_empleo.entidades.Nacionalidad;
import org.example.bolsa_empleo.entidades.Caracteristica;
import org.example.bolsa_empleo.entidades.Puesto;
import org.example.bolsa_empleo.entidades.PuestoCaracteristica;
import org.example.bolsa_empleo.security.JwtService;
import org.example.bolsa_empleo.service.LoginService;
import org.example.bolsa_empleo.service.EmpresaService;
import org.example.bolsa_empleo.service.OferenteService;
import org.example.bolsa_empleo.service.AdminService;
import org.example.bolsa_empleo.repository.NacionalidadRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class BolsaEmpleoApiController {

    private final LoginService loginService;
    private final JwtService jwtService;
    private final EmpresaService empresaService;
    private final OferenteService oferenteService;
    private final NacionalidadRepository nacionalidadRepository;
    private final AdminService adminService;


    public BolsaEmpleoApiController(LoginService loginService, JwtService jwtService, EmpresaService empresaService,
                                    OferenteService oferenteService, NacionalidadRepository nacionalidadRepository,
                                    AdminService adminService)
    {
        this.loginService = loginService;
        this.jwtService = jwtService;
        this.empresaService = empresaService;
        this.oferenteService = oferenteService;
        this.nacionalidadRepository = nacionalidadRepository;
        this.adminService = adminService;
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

    private String valor(Map<String, String> body, String... llaves) {
        for (String llave : llaves) {
            String valor = body.get(llave);

            if (valor != null && !valor.isBlank()) {
                return valor.trim();
            }
        }

        return null;
    }

    private String extraerToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }

        return authHeader.substring(7);
    }

    private Map<String, Object> caracteristicaDTO(Caracteristica caracteristica) {
        Map<String, Object> dto = new LinkedHashMap<>();

        dto.put("id", caracteristica.getId());
        dto.put("nombre", caracteristica.getNombre());

        if (caracteristica.getPadre() != null) {
            dto.put("padreId", caracteristica.getPadre().getId());
            dto.put("padreNombre", caracteristica.getPadre().getNombre());
        } else {
            dto.put("padreId", null);
            dto.put("padreNombre", null);
        }

        return dto;
    }

    private Map<String, Object> requisitoPuestoDTO(PuestoCaracteristica requisito) {
        Map<String, Object> dto = new LinkedHashMap<>();

        dto.put("id", requisito.getId());
        dto.put("caracteristicaId", requisito.getCaracteristica().getId());
        dto.put("caracteristicaNombre", requisito.getCaracteristica().getNombre());
        dto.put("nivelRequerido", requisito.getNivelRequerido());

        return dto;
    }

    private Map<String, Object> puestoDTO(Puesto puesto) {
        Map<String, Object> dto = new LinkedHashMap<>();

        dto.put("id", puesto.getId());
        dto.put("titulo", puesto.getTitulo());
        dto.put("descripcion", puesto.getDescripcion());
        dto.put("salario", puesto.getSalario());
        dto.put("estado", puesto.getEstado());
        dto.put("tipoPublicacion", puesto.getTipoPublicacion());
        dto.put("fechaRegistro", puesto.getFechaRegistro() != null
                ? puesto.getFechaRegistro().toString()
                : "");

        if (puesto.getEmpresa() != null) {
            dto.put("empresaId", puesto.getEmpresa().getIdEmpresa());
            dto.put("empresaNombre", puesto.getEmpresa().getNombreEmpresa());
        }

        dto.put("requisitos", empresaService.obtenerRequisitosPuesto(puesto.getId())
                .stream()
                .map(this::requisitoPuestoDTO)
                .toList()
        );

        return dto;
    }

    private String texto(Object valor) {
        return valor != null ? valor.toString().trim() : null;
    }

    private Long numeroLong(Object valor) {
        if (valor == null || valor.toString().isBlank()) {
            return null;
        }

        return Long.parseLong(valor.toString());
    }

    private Integer numeroInteger(Object valor) {
        if (valor == null || valor.toString().isBlank()) {
            return null;
        }

        return Integer.parseInt(valor.toString());
    }

    private Double numeroDouble(Object valor) {
        if (valor == null || valor.toString().isBlank()) {
            return null;
        }

        return Double.parseDouble(valor.toString());
    }

    private List<Map<String, Object>> construirRutaCaracteristica(Caracteristica actual) {
        List<Map<String, Object>> ruta = new ArrayList<>();

        Caracteristica cursor = actual;

        while (cursor != null) {
            ruta.add(0, caracteristicaDTO(cursor));
            cursor = cursor.getPadre();
        }

        return ruta;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestBody Map<String, String> body
    ) {
        String usuarioLogin = valor(body, "usuario", "correo", "cedula", "identificacion");
        String password = valor(body, "password", "clave");

        Object usuario = loginService.validarLogin(usuarioLogin, password);

        if (usuario == null) {
            return error(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
        }

        Map<String, Object> data = new LinkedHashMap<>();

        if (usuario instanceof Administrador admin) {
            data.put("tipo", "ADMIN");
            data.put("cedula", admin.getCedulaAdministrador());
            data.put("nombre", admin.getNombreAdministrador());
            data.put("correo", admin.getCorreoAdministrador());

            String token = jwtService.generarToken(admin.getCedulaAdministrador(), data);
            data.put("token", token);

            return ok(data);
        }

        if (usuario instanceof Empresa empresa) {
            data.put("tipo", "EMPRESA");
            data.put("id", empresa.getIdEmpresa());
            data.put("nombre", empresa.getNombreEmpresa());
            data.put("correo", empresa.getCorreoEmpresa());

            String token = jwtService.generarToken(
                    String.valueOf(empresa.getIdEmpresa()),
                    data
            );

            data.put("token", token);

            return ok(data);
        }

        if (usuario instanceof Oferente oferente) {
            data.put("tipo", "OFERENTE");
            data.put("cedula", oferente.getCedulaOferente());
            data.put("nombre", oferente.getNombreOferente());
            data.put("correo", oferente.getCorreoOferente());

            String token = jwtService.generarToken(oferente.getCedulaOferente(), data);
            data.put("token", token);

            return ok(data);
        }

        return error(HttpStatus.UNAUTHORIZED, "Tipo de usuario no reconocido");
    }

    @GetMapping("/auth/me")
    public ResponseEntity<Map<String, Object>> obtenerUsuarioActual(
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        String token = extraerToken(authHeader);

        if (token == null || !jwtService.tokenValido(token)) {
            return error(HttpStatus.UNAUTHORIZED, "No hay sesión activa");
        }

        var claims = jwtService.extraerClaims(token);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("tipo", claims.get("tipo"));
        data.put("nombre", claims.get("nombre"));
        data.put("correo", claims.get("correo"));

        if (claims.get("cedula") != null) {
            data.put("cedula", claims.get("cedula"));
        }

        if (claims.get("id") != null) {
            data.put("id", claims.get("id"));
        }

        return ok(data);
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<Map<String, Object>> logout() {
        return mensaje("Sesión cerrada correctamente");
    }

    @PostMapping("/public/registro-empresa")
    public ResponseEntity<Map<String, Object>> registrarEmpresa(
            @RequestBody Map<String, String> body
    ) {
        try {
            Empresa empresa = new Empresa();

            empresa.setNombreEmpresa(body.get("nombreEmpresa"));
            empresa.setCorreoEmpresa(body.get("correoEmpresa"));
            empresa.setPasswordEmpresa(body.get("passwordEmpresa"));
            empresa.setTelefono(body.get("telefono"));
            empresa.setLocalizacion(body.get("localizacion"));
            empresa.setDescripcionEmpresa(body.get("descripcionEmpresa"));

            empresaService.registrarEmpresa(empresa);

            return mensaje("Registro enviado correctamente. La empresa queda pendiente de aprobación.");
        } catch (Exception e) {
            return error(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/public/nacionalidades")
    public ResponseEntity<Map<String, Object>> listarNacionalidades() {
        return ok(
                nacionalidadRepository.findAll()
                        .stream()
                        .map(n -> Map.of(
                                "idNacionalidad", n.getIdNacionalidad(),
                                "nombreNacionalidad", n.getNombreNacionalidad()
                        ))
                        .toList()
        );
    }

    @PostMapping("/public/registro-oferente")
    public ResponseEntity<Map<String, Object>> registrarOferente(
            @RequestBody Map<String, String> body
    ) {
        try {
            Long idNacionalidad = Long.parseLong(body.get("idNacionalidad"));

            Nacionalidad nacionalidad = nacionalidadRepository.findById(idNacionalidad)
                    .orElseThrow(() -> new IllegalArgumentException("La nacionalidad seleccionada no existe"));

            Oferente oferente = new Oferente();

            oferente.setCedulaOferente(body.get("cedulaOferente"));
            oferente.setNombreOferente(body.get("nombreOferente"));
            oferente.setPrimerApellido(body.get("primerApellido"));
            oferente.setCorreoOferente(body.get("correoOferente"));
            oferente.setPasswordOferente(body.get("passwordOferente"));
            oferente.setTelefonoOferente(body.get("telefonoOferente"));
            oferente.setLugarResidencia(body.get("lugarResidencia"));
            oferente.setNacionalidad(nacionalidad);

            oferenteService.registrarOferente(oferente);

            return mensaje("Registro enviado correctamente. El oferente queda pendiente de aprobación.");
        } catch (Exception e) {
            return error(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    private Map<String, Object> empresaPendienteDTO(Empresa empresa) {
        Map<String, Object> dto = new LinkedHashMap<>();

        dto.put("idEmpresa", empresa.getIdEmpresa());
        dto.put("nombreEmpresa", empresa.getNombreEmpresa());
        dto.put("correoEmpresa", empresa.getCorreoEmpresa());
        dto.put("telefono", empresa.getTelefono());
        dto.put("localizacion", empresa.getLocalizacion());
        dto.put("descripcionEmpresa", empresa.getDescripcionEmpresa());
        dto.put("fechaRegistroEmpresa", empresa.getFechaRegistroEmpresa() != null
                ? empresa.getFechaRegistroEmpresa().toString()
                : "");

        return dto;
    }

    private Map<String, Object> oferentePendienteDTO(Oferente oferente) {
        Map<String, Object> dto = new LinkedHashMap<>();

        dto.put("cedulaOferente", oferente.getCedulaOferente());
        dto.put("nombreOferente", oferente.getNombreOferente());
        dto.put("primerApellido", oferente.getPrimerApellido());
        dto.put("correoOferente", oferente.getCorreoOferente());
        dto.put("telefonoOferente", oferente.getTelefonoOferente());
        dto.put("lugarResidencia", oferente.getLugarResidencia());

        if (oferente.getNacionalidad() != null) {
            dto.put("nacionalidad", oferente.getNacionalidad().getNombreNacionalidad());
        } else {
            dto.put("nacionalidad", "");
        }

        return dto;
    }

    private Long obtenerIdEmpresaAutenticada() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            throw new IllegalStateException("No hay empresa autenticada");
        }

        return Long.parseLong(authentication.getName());
    }

    @GetMapping("/admin/empresas-pendientes")
    public ResponseEntity<Map<String, Object>> listarEmpresasPendientes() {
        return ok(
                adminService.listarEmpresasPendientes()
                        .stream()
                        .map(this::empresaPendienteDTO)
                        .toList()
        );
    }

    @PostMapping("/admin/empresas/{idEmpresa}/aprobar")
    public ResponseEntity<Map<String, Object>> aprobarEmpresa(
            @PathVariable Long idEmpresa
    ) {
        try {
            adminService.aprobarEmpresa(idEmpresa);
            return mensaje("Empresa aprobada correctamente.");
        } catch (Exception e) {
            return error(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/admin/oferentes-pendientes")
    public ResponseEntity<Map<String, Object>> listarOferentesPendientes() {
        return ok(
                adminService.listarOferentesPendientes()
                        .stream()
                        .map(this::oferentePendienteDTO)
                        .toList()
        );
    }

    @PostMapping("/admin/oferentes/{cedula}/aprobar")
    public ResponseEntity<Map<String, Object>> aprobarOferente(
            @PathVariable String cedula
    ) {
        try {
            adminService.aprobarOferente(cedula);
            return mensaje("Oferente aprobado correctamente.");
        } catch (Exception e) {
            return error(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/admin/caracteristicas")
    public ResponseEntity<Map<String, Object>> listarCaracteristicas(
            @RequestParam(required = false) Long padreId
    ) {
        try {
            Caracteristica actual = null;

            if (padreId != null) {
                actual = adminService.buscarCaracteristica(padreId)
                        .orElseThrow(() -> new IllegalArgumentException("Característica no encontrada"));
            }

            Map<String, Object> data = new LinkedHashMap<>();

            data.put("padreId", padreId);

            data.put("actual", actual != null
                    ? caracteristicaDTO(actual)
                    : null
            );

            data.put("ruta", actual != null
                    ? construirRutaCaracteristica(actual)
                    : List.of()
            );

            data.put("raices", adminService.listarRaices()
                    .stream()
                    .map(this::caracteristicaDTO)
                    .toList()
            );

            data.put("subcategorias", adminService.listarSubcategorias(padreId)
                    .stream()
                    .map(this::caracteristicaDTO)
                    .toList()
            );

            return ok(data);

        } catch (Exception e) {
            return error(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/admin/caracteristicas")
    public ResponseEntity<Map<String, Object>> registrarCaracteristica(
            @RequestBody Map<String, String> body
    ) {
        try {
            String nombre = valor(body, "nombre", "nombreCaracteristica");
            String padreIdTexto = valor(body, "padreId");

            if (nombre == null || nombre.isBlank()) {
                return error(HttpStatus.BAD_REQUEST, "Debe indicar el nombre de la característica");
            }

            Long padreId = null;

            if (padreIdTexto != null && !padreIdTexto.isBlank()) {
                padreId = Long.parseLong(padreIdTexto);
            }

            Caracteristica caracteristica = adminService.registrarCaracteristica(nombre, padreId);

            return ok(caracteristicaDTO(caracteristica));

        } catch (Exception e) {
            return error(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/catalogo/caracteristicas")
    public ResponseEntity<Map<String, Object>> listarCatalogoCaracteristicas() {
        return ok(
                adminService.listarTodasCaracteristicas()
                        .stream()
                        .map(this::caracteristicaDTO)
                        .toList()
        );
    }

    @GetMapping("/empresa/puestos")
    public ResponseEntity<Map<String, Object>> listarPuestosEmpresa() {
        try {
            Long idEmpresa = obtenerIdEmpresaAutenticada();

            return ok(
                    empresaService.listarPuestos(idEmpresa)
                            .stream()
                            .map(this::puestoDTO)
                            .toList()
            );

        } catch (Exception e) {
            return error(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/empresa/puestos")
    public ResponseEntity<Map<String, Object>> publicarPuesto(
            @RequestBody Map<String, Object> body
    ) {
        try {
            Long idEmpresa = obtenerIdEmpresaAutenticada();

            String titulo = texto(body.get("titulo"));
            String descripcion = texto(body.get("descripcion"));
            Double salario = numeroDouble(body.get("salario"));
            String tipoPublicacion = texto(body.get("tipoPublicacion"));

            if (titulo == null || titulo.isBlank()) {
                return error(HttpStatus.BAD_REQUEST, "Debe indicar el título del puesto");
            }

            if (descripcion == null || descripcion.isBlank()) {
                return error(HttpStatus.BAD_REQUEST, "Debe indicar la descripción del puesto");
            }

            if (salario == null || salario <= 0) {
                return error(HttpStatus.BAD_REQUEST, "Debe indicar un salario válido");
            }

            if (tipoPublicacion == null || tipoPublicacion.isBlank()) {
                return error(HttpStatus.BAD_REQUEST, "Debe indicar el tipo de publicación");
            }

            tipoPublicacion = tipoPublicacion.toUpperCase();

            if (!tipoPublicacion.equals("PUBLICA") && !tipoPublicacion.equals("PRIVADA")) {
                return error(HttpStatus.BAD_REQUEST, "El tipo de publicación debe ser PUBLICA o PRIVADA");
            }

            Object requisitosObj = body.get("requisitos");

            if (!(requisitosObj instanceof List<?> requisitosRaw) || requisitosRaw.isEmpty()) {
                return error(HttpStatus.BAD_REQUEST, "Debe agregar al menos una característica requerida");
            }

            List<Long> caracteristicaIds = new ArrayList<>();
            List<Integer> niveles = new ArrayList<>();

            for (Object item : requisitosRaw) {
                if (!(item instanceof Map<?, ?> requisitoMap)) {
                    continue;
                }

                Long caracteristicaId = numeroLong(requisitoMap.get("caracteristicaId"));
                Integer nivel = numeroInteger(requisitoMap.get("nivelRequerido"));

                if (caracteristicaId == null || nivel == null) {
                    return error(HttpStatus.BAD_REQUEST, "Cada requisito debe tener característica y nivel");
                }

                if (nivel < 1 || nivel > 5) {
                    return error(HttpStatus.BAD_REQUEST, "El nivel debe estar entre 1 y 5");
                }

                caracteristicaIds.add(caracteristicaId);
                niveles.add(nivel);
            }

            Puesto puesto = new Puesto();
            puesto.setTitulo(titulo);
            puesto.setDescripcion(descripcion);
            puesto.setSalario(salario);
            puesto.setTipoPublicacion(tipoPublicacion);
            puesto.setEstado(true);
            puesto.setFechaRegistro(LocalDate.now());

            empresaService.publicarPuesto(puesto, idEmpresa, caracteristicaIds, niveles);

            return mensaje("Puesto publicado correctamente.");

        } catch (Exception e) {
            return error(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/empresa/puestos/{idPuesto}/desactivar")
    public ResponseEntity<Map<String, Object>> desactivarPuestoEmpresa(
            @PathVariable Long idPuesto
    ) {
        try {
            Long idEmpresa = obtenerIdEmpresaAutenticada();

            empresaService.desactivarPuestoEmpresa(idPuesto, idEmpresa);

            return mensaje("Puesto desactivado correctamente.");

        } catch (Exception e) {
            return error(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}