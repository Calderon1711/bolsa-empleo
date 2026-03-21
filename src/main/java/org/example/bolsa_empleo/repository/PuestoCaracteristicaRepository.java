package org.example.bolsa_empleo.repository;

import org.example.bolsa_empleo.entidades.PuestoCaracteristica;
import org.springframework.data.jpa.repository.JpaRepository;//importa la interfaz de spring data jpa(inclutye metiodos como guardar. buscarx id,etc)
import org.springframework.stereotype.Repository;//esto le dice a spring que esta interfaz es un componente de acceso de datos

@Repository
public interface PuestoCaracteristicaRepository extends JpaRepository<PuestoCaracteristica, Long> { //define la interfaz que va manejar la tabla de empresa
}