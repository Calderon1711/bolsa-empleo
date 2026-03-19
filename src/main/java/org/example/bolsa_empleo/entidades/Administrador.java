package org.example.bolsa_empleo.entidades;

import jakarta.persistence.*;

@Entity
@Table(name = "administrador")
public class Administrador {

    @Id
    @Column(name = "cedulaAdministrador", nullable = false, length = 20)
    private String cedulaAdministrador; // PK: cédula conocida por el admin

    private String nombreAdministrador;
    private String correoAdministrador;
    private String passwordAdministrador;

    // Constructor vacío
    public Administrador() {}

    // Constructor con parámetros
    public Administrador(String cedulaAdministrador, String nombreAdministrador,
                         String correoAdministrador, String passwordAdministrador) {
        this.cedulaAdministrador = cedulaAdministrador;
        this.nombreAdministrador = nombreAdministrador;
        this.correoAdministrador = correoAdministrador;
        this.passwordAdministrador = passwordAdministrador;
    }

    // Getters y Setters
    public String getCedulaAdministrador() { return cedulaAdministrador; }
    public void setCedulaAdministrador(String cedulaAdministrador) { this.cedulaAdministrador = cedulaAdministrador; }

    public String getNombreAdministrador() { return nombreAdministrador; }
    public void setNombreAdministrador(String nombreAdministrador) { this.nombreAdministrador = nombreAdministrador; }

    public String getCorreoAdministrador() { return correoAdministrador; }
    public void setCorreoAdministrador(String correoAdministrador) { this.correoAdministrador = correoAdministrador; }

    public String getPasswordAdministrador() { return passwordAdministrador; }
    public void setPasswordAdministrador(String passwordAdministrador) { this.passwordAdministrador = passwordAdministrador; }
}