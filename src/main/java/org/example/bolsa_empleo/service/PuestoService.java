package org.example.bolsa_empleo.service;

import org.example.bolsa_empleo.entidades.Puesto;
import org.example.bolsa_empleo.entidades.PuestoCaracteristica;
import org.example.bolsa_empleo.repository.PuestoCaracteristicaRepository;
import org.example.bolsa_empleo.repository.PuestoRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PuestoService {

    private final PuestoRepository puestoRepository;
    private final PuestoCaracteristicaRepository puestoCaracteristicaRepository;

    public PuestoService(PuestoRepository puestoRepository,
                         PuestoCaracteristicaRepository puestoCaracteristicaRepository) {
        this.puestoRepository = puestoRepository;
        this.puestoCaracteristicaRepository = puestoCaracteristicaRepository;
    }

    public List<Puesto> obtenerUltimosCincoPuestosPublicos() {
        return puestoRepository.findByTipoPublicacionAndEstadoOrderByFechaRegistroDesc(
                "PUBLICA", true, PageRequest.of(0, 5));
    }

    public List<PuestoCaracteristica> obtenerCaracteristicasDePuesto(Long puestoId) {
        return puestoCaracteristicaRepository.findByPuestoId(puestoId);
    }
}
