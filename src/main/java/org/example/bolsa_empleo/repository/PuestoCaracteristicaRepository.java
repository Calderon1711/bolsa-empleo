package org.example.bolsa_empleo.repository;

import org.example.bolsa_empleo.entidades.PuestoCaracteristica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PuestoCaracteristicaRepository extends JpaRepository<PuestoCaracteristica, Long> {

    // Todas las características requeridas de un puesto específico
    List<PuestoCaracteristica> findByPuestoId(Long puestoId);
}