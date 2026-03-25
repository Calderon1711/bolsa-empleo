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
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public EmpresaService(EmpresaRepository empresaRepository,
                          PuestoRepository puestoRepository,
                          PuestoCaracteristicaRepository puestoCaracteristicaRepository,
                          CaracteristicaRepository caracteristicaRepository,
                          OferenteRepository oferenteRepository,
                          OferenteCaracteristicaRepository oferenteCaracteristicaRepository) {
        this.empresaRepository = empresaRepository;
        this.puestoRepository = puestoRepository;
        this.puestoCaracteristicaRepository = puestoCaracteristicaRepository;
        this.caracteristicaRepository = caracteristicaRepository;
        this.oferenteRepository = oferenteRepository;
        this.oferenteCaracteristicaRepository = oferenteCaracteristicaRepository;
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

        public ResultadoCandidato(Oferente oferente, double porcentajeCoincidencia,
                                  int requisitosCumplidos, int totalRequisitos) {
            this.oferente = oferente;
            this.porcentajeCoincidencia = porcentajeCoincidencia;
            this.requisitosCumplidos = requisitosCumplidos;
            this.totalRequisitos = totalRequisitos;
        }

        public Oferente getOferente() { return oferente; }
        public double getPorcentajeCoincidencia() { return porcentajeCoincidencia; }
        public int getRequisitosCumplidos() { return requisitosCumplidos; }
        public int getTotalRequisitos() { return totalRequisitos; }
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

            resultados.add(new ResultadoCandidato(oferente, similitud, comunes, totalRequisitos));
        }

        // Ordenar de mayor a menor % de coincidencia
        resultados.sort((a, b) -> Double.compare(b.getPorcentajeCoincidencia(), a.getPorcentajeCoincidencia()));
        return resultados;
    }
}
