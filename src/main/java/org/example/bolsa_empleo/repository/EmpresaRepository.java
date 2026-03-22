package org.example.bolsa_empleo.repository;

import org.example.bolsa_empleo.entidades.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

    // Buscar empresa por correo (usado en login)
    Optional<Empresa> findByCorreoEmpresa(String correoEmpresa);
    List<Empresa> findByAprobadaFalseOrderByFechaRegistroEmpresaDesc();

}