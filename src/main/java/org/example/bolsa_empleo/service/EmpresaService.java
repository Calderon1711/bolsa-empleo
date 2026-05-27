package org.example.bolsa_empleo.service;

import org.example.bolsa_empleo.entidades.*;
import org.example.bolsa_empleo.repository.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;

@Service
public class EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final PuestoRepository puestoRepository;
    private final PuestoCaracteristicaRepository puestoCaracteristicaRepository;
    private final CaracteristicaRepository caracteristicaRepository;
    private final OferenteRepository oferenteRepository;
    private final OferenteCaracteristicaRepository oferenteCaracteristicaRepository;
    private final PostulacionRepository postulacionRepository;
    private final CvRepository cvRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public EmpresaService(EmpresaRepository empresaRepository,
                          PuestoRepository puestoRepository,
                          PuestoCaracteristicaRepository puestoCaracteristicaRepository,
                          CaracteristicaRepository caracteristicaRepository,
                          OferenteRepository oferenteRepository,
                          OferenteCaracteristicaRepository oferenteCaracteristicaRepository,
                          CvRepository cvRepository,
                          PostulacionRepository postulacionRepository) {
        this.empresaRepository = empresaRepository;
        this.puestoRepository = puestoRepository;
        this.puestoCaracteristicaRepository = puestoCaracteristicaRepository;
        this.caracteristicaRepository = caracteristicaRepository;
        this.oferenteRepository = oferenteRepository;
        this.oferenteCaracteristicaRepository = oferenteCaracteristicaRepository;
        this.cvRepository = cvRepository;
        this.postulacionRepository = postulacionRepository;
    }

    // ── Registro ────────────────────────────────────────────────────────────────

    public void registrarEmpresa(Empresa empresa) {
        empresa.setPasswordEmpresa(passwordEncoder.encode(empresa.getPasswordEmpresa()));
        empresa.setFechaRegistroEmpresa(LocalDate.now());
        empresa.setAprobada(false);
        empresaRepository.save(empresa);
    }

    // ── Empresa ─────────────────────────────────────────────────────────────────

    public Empresa obtenerEmpresa(Long id) {
        return empresaRepository.findById(id).orElse(null);
    }

    // ── Puestos ─────────────────────────────────────────────────────────────────

    public List<Puesto> listarPuestos(Long idEmpresa) {
        return puestoRepository.findByEmpresaIdEmpresa(idEmpresa);
    }

    public void desactivarPuestoEmpresa(Long idPuesto, Long idEmpresa) {
        Puesto puesto = puestoRepository.findByIdAndEmpresaIdEmpresa(idPuesto, idEmpresa)
                .orElseThrow(() -> new IllegalArgumentException("El puesto no existe o no pertenece a esta empresa"));

        puesto.setEstado(false);
        puestoRepository.save(puesto);
    }

    public List<PuestoCaracteristica> obtenerRequisitosPuesto(Long idPuesto) {
        return puestoCaracteristicaRepository.findByPuestoId(idPuesto);
    }

    public void activarPuestoEmpresa(Long idPuesto, Long idEmpresa) {
        Puesto puesto = puestoRepository.findByIdAndEmpresaIdEmpresa(idPuesto, idEmpresa)
                .orElseThrow(() -> new IllegalArgumentException("El puesto no existe o no pertenece a esta empresa"));

        puesto.setEstado(true);
        puestoRepository.save(puesto);
    }

    public void desactivarPuesto(Long idPuesto) {
        Puesto puesto = puestoRepository.findById(idPuesto).orElseThrow();
        puesto.setEstado(false);
        puestoRepository.save(puesto);
    }

    public void activarPuesto(Long idPuesto) {
        Puesto puesto = puestoRepository.findById(idPuesto).orElseThrow();
        puesto.setEstado(true);
        puestoRepository.save(puesto);
    }

    // ── Postulaciones ────────────────────────────────────────────────────────────

    public List<Postulacion> obtenerPostulantes(Long puestoId) {
        return postulacionRepository.findByPuestoId(puestoId).stream()
                .filter(p -> "OFERENTE".equals(p.getOrigen()))
                .toList();
    }

    public void aceptarPostulacion(Long idPostulacion) {
        Postulacion p = postulacionRepository.findById(idPostulacion).orElseThrow();
        p.setEstado("ACEPTADA");
        postulacionRepository.save(p);
    }

    public void rechazarPostulacion(Long idPostulacion) {
        Postulacion p = postulacionRepository.findById(idPostulacion).orElseThrow();
        p.setEstado("RECHAZADA");
        postulacionRepository.save(p);
    }

    public void crearPostulacionConEstado(String cedulaOferente, Long puestoId, String estado) {
        Oferente oferente = oferenteRepository.findById(cedulaOferente).orElseThrow();
        Puesto puesto = puestoRepository.findById(puestoId).orElseThrow();
        Postulacion p = new Postulacion(LocalDate.now(), estado, "EMPRESA", oferente, puesto);
        postulacionRepository.save(p);
    }

    public void publicarPuesto(Puesto puesto, Long idEmpresa,
                               List<Long> caracteristicaIds, List<Integer> niveles) {
        Empresa empresa = empresaRepository.findById(idEmpresa).orElseThrow();
        puesto.setEmpresa(empresa);
        puesto.setFechaRegistro(LocalDate.now());
        puesto.setEstado(true);
        puestoRepository.save(puesto);

        if (caracteristicaIds != null && niveles != null) {
            for (int i = 0; i < caracteristicaIds.size(); i++) {
                Caracteristica car = caracteristicaRepository.findById(caracteristicaIds.get(i)).orElseThrow();
                puestoCaracteristicaRepository.save(new PuestoCaracteristica(puesto, car, niveles.get(i)));
            }
        }
    }

    // ── Tipo de cambio BCCR ─────────────────────────────────────────────────────

    public Double obtenerTipoCambio() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String json = restTemplate.getForObject(
                    "https://api.hacienda.go.cr/indicadores/tc/dolar", String.class);
            // Extraer "venta":{"valor":NNN.NN,...} sin Jackson
            java.util.regex.Matcher m = java.util.regex.Pattern
                    .compile("\"venta\"\\s*:\\s*\\{[^}]*\"valor\"\\s*:\\s*([0-9.]+)")
                    .matcher(json);
            if (m.find()) return Double.parseDouble(m.group(1));
        } catch (Exception e) {
            // Si falla la API se usa un valor por defecto
        }
        return 500.0;
    }

    // ── Búsqueda de candidatos (distancia coseno) ────────────────────────────────


    public static class ResultadoCandidato {
        private final Oferente oferente;
        private final double porcentajeCoincidencia;
        private final int requisitosCumplidos;
        private final int totalRequisitos;
        private final Long idPostulacion;
        private final String estadoPostulacion;

        public ResultadoCandidato(Oferente oferente, double porcentajeCoincidencia,
                                  int requisitosCumplidos, int totalRequisitos,
                                  Long idPostulacion, String estadoPostulacion) {
            this.oferente = oferente;
            this.porcentajeCoincidencia = porcentajeCoincidencia;
            this.requisitosCumplidos = requisitosCumplidos;
            this.totalRequisitos = totalRequisitos;
            this.idPostulacion = idPostulacion;
            this.estadoPostulacion = estadoPostulacion;
        }

        public Oferente getOferente() { return oferente; }
        public double getPorcentajeCoincidencia() { return porcentajeCoincidencia; }
        public int getRequisitosCumplidos() { return requisitosCumplidos; }
        public int getTotalRequisitos() { return totalRequisitos; }
        public Long getIdPostulacion() { return idPostulacion; }
        public String getEstadoPostulacion() { return estadoPostulacion; }
    }

    public List<ResultadoCandidato> buscarCandidatos(Long puestoId) {
        // Vector A: caracteristicaId → nivelRequerido del puesto
        List<PuestoCaracteristica> requisitos = puestoCaracteristicaRepository.findByPuestoId(puestoId);
        Map<Long, Integer> vectorA = new LinkedHashMap<>();
        for (PuestoCaracteristica pc : requisitos) {
            vectorA.put(pc.getCaracteristica().getId(), pc.getNivelRequerido());
        }

        if (vectorA.isEmpty()) return Collections.emptyList();

        // ||A||
        double normaA = 0.0;
        for (int v : vectorA.values()) normaA += (double) v * v;
        normaA = Math.sqrt(normaA);

        int totalRequisitos = vectorA.size();
        List<ResultadoCandidato> resultados = new ArrayList<>();

        // Solo oferentes aprobados
        List<Oferente> oferentes = oferenteRepository.findAll().stream()
                .filter(o -> Boolean.TRUE.equals(o.getAprobado()))
                .toList();

        for (Oferente oferente : oferentes) {
            // Vector B: caracteristicaId → nivel del oferente
            List<OferenteCaracteristica> habilidades =
                    oferenteCaracteristicaRepository.findByOferenteCedulaOferente(
                            oferente.getCedulaOferente());

            Map<Long, Integer> vectorB = new HashMap<>();
            for (OferenteCaracteristica oc : habilidades) {
                vectorB.put(oc.getCaracteristica().getId(), oc.getNivel());
            }

            // Contar características en común (requisitos cumplidos)
            int comunes = 0;
            for (Long idCar : vectorA.keySet()) {
                if (vectorB.containsKey(idCar)) comunes++;
            }

            if (comunes == 0) continue; // Sin solapamiento, se omite

            // Producto punto A·B (solo sobre las dimensiones del puesto)
            double dotProduct = 0.0;
            for (Map.Entry<Long, Integer> entry : vectorA.entrySet()) {
                Integer nivelB = vectorB.get(entry.getKey());
                if (nivelB != null) dotProduct += (double) entry.getValue() * nivelB;
            }

            // ||B||
            double normaB = 0.0;
            for (int v : vectorB.values()) normaB += (double) v * v;
            normaB = Math.sqrt(normaB);

            double similitud = (normaA == 0 || normaB == 0) ? 0.0
                    : (dotProduct / (normaA * normaB)) * 100.0;

            Long idPost = null;
            String estadoPost = null;
            Postulacion post = postulacionRepository.findByPuestoId(puestoId).stream()
                    .filter(p -> p.getOferente().getCedulaOferente()
                            .equals(oferente.getCedulaOferente()))
                    .findFirst().orElse(null);
            if (post != null) {
                idPost = post.getId();
                estadoPost = post.getEstado();
            }
            resultados.add(new ResultadoCandidato(oferente, similitud, comunes, totalRequisitos, idPost, estadoPost));
        }

        // Ordenar de mayor a menor % de coincidencia
        resultados.sort((a, b) -> Double.compare(b.getPorcentajeCoincidencia(), a.getPorcentajeCoincidencia()));
        return resultados;
    }

    public List<Map<String, Object>> buscarCandidatosCompatibles(Long idPuesto, Long idEmpresa) {
        Puesto puesto = puestoRepository.findByIdAndEmpresaIdEmpresa(idPuesto, idEmpresa)
                .orElseThrow(() -> new IllegalArgumentException("El puesto no existe o no pertenece a esta empresa"));

        List<PuestoCaracteristica> requisitos = puestoCaracteristicaRepository.findByPuestoId(idPuesto);

        if (requisitos.isEmpty()) {
            throw new IllegalArgumentException("El puesto no tiene requisitos registrados");
        }

        List<Oferente> oferentes = oferenteRepository.findByAprobadoTrue();

        List<Map<String, Object>> resultados = new ArrayList<>();

        for (Oferente oferente : oferentes) {
            List<OferenteCaracteristica> habilidades = oferenteCaracteristicaRepository
                    .findByOferenteCedulaOferente(oferente.getCedulaOferente());

            int totalRequisitos = requisitos.size();
            int coincidenciasCompletas = 0;
            double sumaPorcentajes = 0;

            List<Map<String, Object>> detalleCoincidencias = new ArrayList<>();

            for (PuestoCaracteristica requisito : requisitos) {
                OferenteCaracteristica habilidadEncontrada = habilidades.stream()
                        .filter(habilidad -> habilidad.getCaracteristica().getId()
                                .equals(requisito.getCaracteristica().getId()))
                        .findFirst()
                        .orElse(null);

                Integer nivelOferente = habilidadEncontrada != null
                        ? habilidadEncontrada.getNivel()
                        : null;

                Integer nivelRequerido = requisito.getNivelRequerido();

                boolean cumple = nivelOferente != null && nivelOferente >= nivelRequerido;

                if (cumple) {
                    coincidenciasCompletas++;
                }

                double porcentajeDetalle = 0;

                if (nivelOferente != null && nivelRequerido != null && nivelRequerido > 0) {
                    porcentajeDetalle = (nivelOferente * 100.0) / nivelRequerido;

                    if (porcentajeDetalle > 100) {
                        porcentajeDetalle = 100;
                    }
                }

                sumaPorcentajes += porcentajeDetalle;

                Map<String, Object> detalle = new LinkedHashMap<>();
                detalle.put("caracteristicaId", requisito.getCaracteristica().getId());
                detalle.put("caracteristicaNombre", requisito.getCaracteristica().getNombre());
                detalle.put("nivelRequerido", nivelRequerido);
                detalle.put("nivelOferente", nivelOferente);
                detalle.put("cumple", cumple);
                detalle.put("porcentajeDetalle", Math.round(porcentajeDetalle));

                detalleCoincidencias.add(detalle);
            }

            long porcentajeGeneral = Math.round(sumaPorcentajes / totalRequisitos);

            Map<String, Object> resultado = new LinkedHashMap<>();
            resultado.put("cedulaOferente", oferente.getCedulaOferente());
            resultado.put("nombreOferente", oferente.getNombreOferente());
            resultado.put("primerApellido", oferente.getPrimerApellido());
            resultado.put("correoOferente", oferente.getCorreoOferente());
            resultado.put("telefonoOferente", oferente.getTelefonoOferente());
            resultado.put("lugarResidencia", oferente.getLugarResidencia());

            if (oferente.getNacionalidad() != null) {
                resultado.put("nacionalidad", oferente.getNacionalidad().getNombreNacionalidad());
            } else {
                resultado.put("nacionalidad", "");
            }

            resultado.put("coincidencias", coincidenciasCompletas);
            resultado.put("totalRequisitos", totalRequisitos);
            resultado.put("porcentajeCoincidencia", porcentajeGeneral);
            resultado.put("detalleCoincidencias", detalleCoincidencias);
            resultado.put("tieneCv", cvRepository.findByOferenteCedulaOferente(
                    oferente.getCedulaOferente()
            ).isPresent());

            resultados.add(resultado);
        }

        resultados.sort((a, b) -> Long.compare(
                ((Number) b.get("porcentajeCoincidencia")).longValue(),
                ((Number) a.get("porcentajeCoincidencia")).longValue()
        ));

        return resultados;
    }

    public List<Puesto> listarUltimosPuestosPublicos() {
        return puestoRepository.findTop5ByTipoPublicacionAndEstadoOrderByFechaRegistroDesc(
                "PUBLICA",
                true
        );
    }
}
