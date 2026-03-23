package org.example.bolsa_empleo.repository;

import org.example.bolsa_empleo.entidades.Puesto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PuestoRepository extends JpaRepository<Puesto, Long> {

    // Los 5 puestos públicos activos más recientes (para la página de inicio)
    List<Puesto> findByTipoPublicacionAndEstadoOrderByFechaRegistroDesc(
            String tipoPublicacion, Boolean estado, Pageable pageable);

    // Puestos públicos activos que tengan AL MENOS UNA de las características indicadas
    @Query("SELECT DISTINCT p FROM Puesto p WHERE p.tipoPublicacion = 'PUBLICA' AND p.estado = true " +
           "AND EXISTS (SELECT pc FROM PuestoCaracteristica pc WHERE pc.puesto = p AND pc.caracteristica.id IN :caracteristicaIds)")
    List<Puesto> findPublicosByCaracteristicas(@Param("caracteristicaIds") List<Long> caracteristicaIds);

    // Todos los puestos activos de una empresa
    List<Puesto> findByEmpresaIdEmpresa(Long idEmpresa);

    // Buscar por correo de la empresa dueña (usado en login para verificar pertenencia)
    Optional<Puesto> findByIdAndEmpresaIdEmpresa(Long id, Long idEmpresa);

    List<Puesto> findByTipoPublicacionAndEstadoOrderByFechaRegistroDesc(String publica, boolean estado);
}