package org.example.bolsa_empleo.entidades;

import jakarta.persistence.*;

@Entity
@Table(name = "oferente")
public class Oferente {

    @Id
    @Column(name = "cedulaOferente", nullable = false, length = 20)
    private String cedulaOferente;

    private String nombreOferente;
    private String primerApellido;
    private String correoOferente;
    private String passwordOferente;
    private String telefonoOferente;
    private String lugarResidencia;
    private Boolean aprobado;

    @ManyToOne
    @JoinColumn(name = "idNacionalidad")
    private Nacionalidad nacionalidad;

    public Oferente() {}

    public String getCedulaOferente() { return cedulaOferente; }
    public void setCedulaOferente(String cedulaOferente) { this.cedulaOferente = cedulaOferente; }

    public String getNombreOferente() { return nombreOferente; }
    public void setNombreOferente(String nombreOferente) { this.nombreOferente = nombreOferente; }

    public String getPrimerApellido() { return primerApellido; }
    public void setPrimerApellido(String primerApellido) { this.primerApellido = primerApellido; }

    public String getCorreoOferente() { return correoOferente; }
    public void setCorreoOferente(String correoOferente) { this.correoOferente = correoOferente; }

    public String getPasswordOferente() { return passwordOferente; }
    public void setPasswordOferente(String passwordOferente) { this.passwordOferente = passwordOferente; }

    public String getTelefonoOferente() { return telefonoOferente; }
    public void setTelefonoOferente(String telefonoOferente) { this.telefonoOferente = telefonoOferente; }

    public String getLugarResidencia() { return lugarResidencia; }
    public void setLugarResidencia(String lugarResidencia) { this.lugarResidencia = lugarResidencia; }

    public Boolean getAprobado() { return aprobado; }
    public void setAprobado(Boolean aprobado) { this.aprobado = aprobado; }

    public Nacionalidad getNacionalidad() { return nacionalidad; }
    public void setNacionalidad(Nacionalidad nacionalidad) { this.nacionalidad = nacionalidad; }
}