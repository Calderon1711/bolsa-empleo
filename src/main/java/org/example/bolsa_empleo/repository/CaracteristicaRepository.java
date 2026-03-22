package org.example.bolsa_empleo.repository;

import org.example.bolsa_empleo.entidades.Caracteristica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CaracteristicaRepository extends JpaRepository<Caracteristica, Long> {

    // Características raíz (sin padre) — punto de entrada del árbol
    List<Caracteristica> findByPadreIsNull();
}