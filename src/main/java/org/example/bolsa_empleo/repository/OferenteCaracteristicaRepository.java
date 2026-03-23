package org.example.bolsa_empleo.repository;

import org.example.bolsa_empleo.entidades.OferenteCaracteristica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OferenteCaracteristicaRepository extends JpaRepository<OferenteCaracteristica, Long> {

    List<OferenteCaracteristica> findByOferenteCedulaOferente(String cedulaOferente);

    Optional<OferenteCaracteristica> findByOferenteCedulaOferenteAndCaracteristicaId(String cedulaOferente, Long caracteristicaId);
}