package org.example.bolsa_empleo.repository;

import org.example.bolsa_empleo.entidades.OferenteCaracteristica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OferenteCaracteristicaRepository extends JpaRepository<OferenteCaracteristica, Long> {

    // Todas las habilidades de un oferente específico
    List<OferenteCaracteristica> findByOferenteCedulaOferente(String cedulaOferente);
}