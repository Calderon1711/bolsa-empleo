package org.example.bolsa_empleo.repository;

import org.example.bolsa_empleo.entidades.Oferente;
import org.example.bolsa_empleo.entidades.Puesto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OferenteRepository extends JpaRepository<Oferente, String> {

    // Buscar oferente por correo (usado en login)
    Optional<Oferente> findByCorreoOferente(String correoOferente);
    List<Oferente> findByAprobadoFalseOrderByNombreOferenteAsc();
}