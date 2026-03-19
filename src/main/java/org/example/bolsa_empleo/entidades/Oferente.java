package org.example.bolsa_empleo.entidades;

import jakarta.persistence.*;

@Entity
@Table(name = "oferente")
public class Oferente {

    @Id
    @Column(name = "cedulaOferente", nullable = false, length = 20)
    private String cedulaOferente; // PK: cédula conocida por el usuario

    private String nombreOferente;
    private String correoOferente;
    private String passwordOferente;
    private String telefonoOferente;

    // Relación ManyToOne hacia Nacionalidad
    @ManyToOne
    @JoinColumn(name = "idNacionalidad")
    private Nacionalidad nacionalidad;

    // Constructor vacío
    public Oferente() {}

    // Constructor con parámetros
    public Oferente(String cedulaOferente, String nombreOferente, String correoOferente,
                    String passwordOferente, String telefonoOferente, Nacionalidad nacionalidad) {
        this.cedulaOferente = cedulaOferente;
        this.nombreOferente = nombreOferente;
        this.correoOferente = correoOferente;
        this.passwordOferente = passwordOferente;
        this.telefonoOferente = telefonoOferente;
        this.nacionalidad = nacionalidad;
    }

    // Getters y Setters
    public String getCedulaOferente() { return cedulaOferente; }
    public void setCedulaOferente(String cedulaOferente) { this.cedulaOferente = cedulaOferente; }

    public String getNombreOferente() { return nombreOferente; }
    public void setNombreOferente(String nombreOferente) { this.nombreOferente = nombreOferente; }

    public String getCorreoOferente() { return correoOferente; }
    public void setCorreoOferente(String correoOferente) { this.correoOferente = correoOferente; }

    public String getPasswordOferente() { return passwordOferente; }
    public void setPasswordOferente(String passwordOferente) { this.passwordOferente = passwordOferente; }

    public String getTelefonoOferente() { return telefonoOferente; }
    public void setTelefonoOferente(String telefonoOferente) { this.telefonoOferente = telefonoOferente; }

    public Nacionalidad getNacionalidad() { return nacionalidad; }
    public void setNacionalidad(Nacionalidad nacionalidad) { this.nacionalidad = nacionalidad; }
}