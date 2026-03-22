package org.example.bolsa_empleo.service;

import org.example.bolsa_empleo.entidades.*;
import org.example.bolsa_empleo.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OferenteService {

    private final OferenteRepository oferenteRepository;
    private final OferenteCaracteristicaRepository oferenteCaracteristicaRepository;
    private final CaracteristicaRepository caracteristicaRepository;
    private final PuestoRepository puestoRepository;
    private final PostulacionRepository postulacionRepository;
    private final CvRepository cvRepository;

    @Value("${app.cv.upload-dir:src/main/resources/static/uploads/cv}")
    private String uploadDir;

    public OferenteService(OferenteRepository oferenteRepository,
                           OferenteCaracteristicaRepository oferenteCaracteristicaRepository,
                           CaracteristicaRepository caracteristicaRepository,
                           PuestoRepository puestoRepository,
                           PostulacionRepository postulacionRepository,
                           CvRepository cvRepository) {
        this.oferenteRepository = oferenteRepository;
        this.oferenteCaracteristicaRepository = oferenteCaracteristicaRepository;
        this.caracteristicaRepository = caracteristicaRepository;
        this.puestoRepository = puestoRepository;
        this.postulacionRepository = postulacionRepository;
        this.cvRepository = cvRepository;
    }

    public Optional<Oferente> buscarOferente(String cedula) {
        return oferenteRepository.findById(cedula);
    }

    public List<OferenteCaracteristica> listarHabilidades(String cedula) {
        return oferenteCaracteristicaRepository.findByOferenteCedulaOferente(cedula);
    }

    public List<Caracteristica> obtenerCaracteristicasRaiz() {
        return caracteristicaRepository.findByPadreIsNull();
    }

    public List<Caracteristica> obtenerSubcategorias(Long padreId) {
        if (padreId == null) return caracteristicaRepository.findByPadreIsNull();
        return caracteristicaRepository.findByPadreIdOrderByNombreAsc(padreId);
    }

    public Optional<Caracteristica> buscarCaracteristica(Long id) {
        return caracteristicaRepository.findById(id);
    }

    public void guardarHabilidad(String cedulaOferente, Long caracteristicaId, Integer nivel) {
        Oferente oferente = oferenteRepository.findById(cedulaOferente)
                .orElseThrow(() -> new IllegalArgumentException("Oferente no encontrado"));
        Caracteristica caracteristica = caracteristicaRepository.findById(caracteristicaId)
                .orElseThrow(() -> new IllegalArgumentException("Característica no encontrada"));

        OferenteCaracteristica oc = new OferenteCaracteristica(oferente, caracteristica, nivel);
        oferenteCaracteristicaRepository.save(oc);
    }

    public List<Puesto> listarPuestosPublicos() {
        return puestoRepository.findPublicosByCaracteristicas(
                caracteristicaRepository.findAll().stream().map(Caracteristica::getId).toList());
    }

    public void postular(String cedulaOferente, Long puestoId) {
        if (postulacionRepository.existsByOferenteCedulaOferenteAndPuestoId(cedulaOferente, puestoId)) {
            return;
        }
        Oferente oferente = oferenteRepository.findById(cedulaOferente)
                .orElseThrow(() -> new IllegalArgumentException("Oferente no encontrado"));
        Puesto puesto = puestoRepository.findById(puestoId)
                .orElseThrow(() -> new IllegalArgumentException("Puesto no encontrado"));

        Postulacion postulacion = new Postulacion(LocalDate.now(), "ENVIADA", oferente, puesto);
        postulacionRepository.save(postulacion);
    }

    public List<Postulacion> listarPostulaciones(String cedulaOferente) {
        return postulacionRepository.findByOferenteCedulaOferenteOrderByFechaPostulacionDesc(cedulaOferente);
    }

    public Optional<CV> obtenerCv(String cedulaOferente) {
        return cvRepository.findByOferenteCedulaOferente(cedulaOferente);
    }

    public CV guardarCv(String cedulaOferente, String descripcion, MultipartFile archivo) throws IOException {
        Oferente oferente = oferenteRepository.findById(cedulaOferente)
                .orElseThrow(() -> new IllegalArgumentException("Oferente no encontrado"));

        if (archivo == null || archivo.isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar un archivo PDF");
        }

        String nombre = StringUtils.cleanPath(archivo.getOriginalFilename());
        if (!nombre.toLowerCase().endsWith(".pdf")) {
            throw new IllegalArgumentException("Solo se permite subir archivos PDF");
        }

        Path carpeta = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(carpeta);
        String nombreGuardado = UUID.randomUUID() + "_" + nombre;
        Path destino = carpeta.resolve(nombreGuardado);
        Files.copy(archivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

        CV cv = cvRepository.findByOferenteCedulaOferente(cedulaOferente).orElse(new CV());
        cv.setOferente(oferente);
        cv.setDescripcionCurriculum(descripcion);
        cv.setRutaDocumento(destino.toString());
        cv.setFechaCreacionCurriculum(LocalDateTime.now());
        return cvRepository.save(cv);
    }

    public Optional<Path> obtenerRutaCv(Long idCv) {
        return cvRepository.findById(idCv)
                .map(CV::getRutaDocumento)
                .filter(ruta -> ruta != null && !ruta.isBlank())
                .map(Paths::get);
    }
}
