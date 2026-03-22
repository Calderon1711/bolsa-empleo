package org.example.bolsa_empleo.service;

import org.example.bolsa_empleo.entidades.Caracteristica;
import org.example.bolsa_empleo.entidades.Puesto;
import org.example.bolsa_empleo.repository.CaracteristicaRepository;
import org.example.bolsa_empleo.repository.PuestoRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class BusquedaService {

    private final PuestoRepository puestoRepository;
    private final CaracteristicaRepository caracteristicaRepository;

    public BusquedaService(PuestoRepository puestoRepository,
                           CaracteristicaRepository caracteristicaRepository) {
        this.puestoRepository = puestoRepository;
        this.caracteristicaRepository = caracteristicaRepository;
    }

    public List<Caracteristica> obtenerCaracteristicasRaiz() {
        return caracteristicaRepository.findByPadreIsNull();
    }

    public List<Puesto> buscarPorCaracteristicas(List<Long> caracteristicaIds) {
        if (caracteristicaIds == null || caracteristicaIds.isEmpty()) {
            return Collections.emptyList();
        }
        return puestoRepository.findPublicosByCaracteristicas(caracteristicaIds);
    }
}
