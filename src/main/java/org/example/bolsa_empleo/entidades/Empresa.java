package org.example.bolsa_empleo.entidades;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "empresa")
public class
Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEmpresa;
    private String nombreEmpresa;
    private String correoEmpresa;
    private String passwordEmpresa;
    private String descripcionEmpresa;
    private String localizacion;
    private String telefono;
    private LocalDate fechaRegistroEmpresa;
    private Boolean aprobada;

    public Long getIdEmpresa() { return idEmpresa; }
    public void setIdEmpresa(Long idEmpresa) { this.idEmpresa = idEmpresa; }

    public String getNombreEmpresa() { return nombreEmpresa; }
    public void setNombreEmpresa(String nombreEmpresa) { this.nombreEmpresa = nombreEmpresa; }

    public String getCorreoEmpresa() { return correoEmpresa; }
    public void setCorreoEmpresa(String correoEmpresa) { this.correoEmpresa = correoEmpresa; }

    public String getPasswordEmpresa() { return passwordEmpresa; }
    public void setPasswordEmpresa(String passwordEmpresa) { this.passwordEmpresa = passwordEmpresa; }

    public String getDescripcionEmpresa() { return descripcionEmpresa; }
    public void setDescripcionEmpresa(String descripcionEmpresa) { this.descripcionEmpresa = descripcionEmpresa; }

    public String getLocalizacion() { return localizacion; }
    public void setLocalizacion(String localizacion) { this.localizacion = localizacion; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public LocalDate getFechaRegistroEmpresa() { return fechaRegistroEmpresa; }
    public void setFechaRegistroEmpresa(LocalDate fechaRegistroEmpresa) { this.fechaRegistroEmpresa = fechaRegistroEmpresa; }

    public Boolean getAprobada() { return aprobada; }
    public void setAprobada(Boolean aprobada) { this.aprobada = aprobada; }
}
