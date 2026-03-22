package org.example.bolsa_empleo.service;

import org.example.bolsa_empleo.entidades.Caracteristica;
import org.example.bolsa_empleo.entidades.Empresa;
import org.example.bolsa_empleo.entidades.Oferente;
import org.example.bolsa_empleo.repository.CaracteristicaRepository;
import org.example.bolsa_empleo.repository.EmpresaRepository;
import org.example.bolsa_empleo.repository.OferenteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    private final EmpresaRepository empresaRepository;
    private final OferenteRepository oferenteRepository;
    private final CaracteristicaRepository caracteristicaRepository;

    public AdminService(EmpresaRepository empresaRepository,
                        OferenteRepository oferenteRepository,
                        CaracteristicaRepository caracteristicaRepository) {
        this.empresaRepository = empresaRepository;
        this.oferenteRepository = oferenteRepository;
        this.caracteristicaRepository = caracteristicaRepository;
    }

    public List<Empresa> listarEmpresasPendientes() {
        return empresaRepository.findByAprobadaFalseOrderByFechaRegistroEmpresaDesc();
    }

    public List<Oferente> listarOferentesPendientes() {
        return oferenteRepository.findByAprobadoFalseOrderByNombreOferenteAsc();
    }

    public void aprobarEmpresa(Long idEmpresa) {
        Empresa empresa = empresaRepository.findById(idEmpresa)
                .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada"));
        empresa.setAprobada(true);
        empresaRepository.save(empresa);
    }

    public void aprobarOferente(String cedula) {
        Oferente oferente = oferenteRepository.findById(cedula)
                .orElseThrow(() -> new IllegalArgumentException("Oferente no encontrado"));
        oferente.setAprobado(true);
        oferenteRepository.save(oferente);
    }

    public List<Caracteristica> listarRaices() {
        return caracteristicaRepository.findByPadreIsNull();
    }

    public List<Caracteristica> listarSubcategorias(Long padreId) {
        if (padreId == null) return caracteristicaRepository.findByPadreIsNull();
        return caracteristicaRepository.findByPadreIdOrderByNombreAsc(padreId);
    }

    public Optional<Caracteristica> buscarCaracteristica(Long id) {
        return caracteristicaRepository.findById(id);
    }

    public Caracteristica registrarCaracteristica(String nombre, Long padreId) {
        Caracteristica c = new Caracteristica();
        c.setNombre(nombre);
        if (padreId != null) {
            caracteristicaRepository.findById(padreId).ifPresent(c::setPadre);
        }
        return caracteristicaRepository.save(c);
    }
}
