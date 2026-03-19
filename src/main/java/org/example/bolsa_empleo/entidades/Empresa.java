package org.example.bolsa_empleo.entidades;
import jakarta.persistence.*;//importa todos los comandos para crear las tablas automaticamente

import java.time.LocalDate;

@Entity//indica que es una entidad JPA para mapear una tabla
@Table(name = "empresa")// aca le pongo nombre a la tabla
public class Empresa {

   @Id//indica que el siguiente atributo tendra una llave primaria
   @GeneratedValue(strategy = GenerationType.IDENTITY)//significa que la BD se encarga de autoincrementar este valor
   private Long idEmpresa; //long es mas seguro que int pero se podria usar int
   private String nombreEmpresa;
   private String correoEmpresa;
   private String passwordEmpresa;
   private String descripcionEmpresa;
   private LocalDate fechaRegistroEmpresa;

    public Long getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(Long id_empresa) {
        this.idEmpresa = id_empresa;
    }

    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    public void setNombreEmpresa(String nombre_empresa) {
        this.nombreEmpresa = nombre_empresa;
    }

    public String getCorreoEmpresa() {
        return correoEmpresa;
    }

    public void setCorreoEmpresa(String correo_empresa) {
        this.correoEmpresa = correo_empresa;
    }

    public String getPasswordEmpresa() {
        return passwordEmpresa;
    }

    public void setPasswordEmpresa(String password_empresa) {
        this.passwordEmpresa = password_empresa;
    }

    public String getDescripcionEmpresa() {
        return descripcionEmpresa;
    }

    public void setDescripcionEmpresa(String descripcion_empresa) {
        this.descripcionEmpresa = descripcion_empresa;
    }

    public LocalDate getFechaRegistroEmpresa() {
        return fechaRegistroEmpresa;
    }

    public void setFechaRegistroEmpresa(LocalDate fecha_registro_empresa) {
        this.fechaRegistroEmpresa = fecha_registro_empresa;
    }
}
