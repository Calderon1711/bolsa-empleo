package org.example.bolsa_empleo.entidades;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "postulacion")
public class Postulacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_postulacion", nullable = false)
    private LocalDate fechaPostulacion;

    @Column(nullable = false, length = 50)
    private String estado;
    // Ejemplos: "ENVIADA", "EN_REVISION", "ACEPTADA", "RECHAZADA"



    @ManyToOne
    @JoinColumn(name = "oferente_id", nullable = false)
    private Oferente oferente;

    @ManyToOne
    @JoinColumn(name = "puesto_id", nullable = false)
    private Puesto puesto;


    public Postulacion() {}

    public Postulacion(LocalDate fechaPostulacion, String estado, Oferente oferente, Puesto puesto) {
        this.fechaPostulacion = fechaPostulacion;
        this.estado = estado;
        this.oferente = oferente;
        this.puesto = puesto;
    }


    public Long getId() {
        return id;
    }

    public LocalDate getFechaPostulacion() {
        return fechaPostulacion;
    }

    public void setFechaPostulacion(LocalDate fechaPostulacion) {
        this.fechaPostulacion = fechaPostulacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Oferente getOferente() {
        return oferente;
    }

    public void setOferente(Oferente oferente) {
        this.oferente = oferente;
    }

    public Puesto getPuesto() {
        return puesto;
    }

    public void setPuesto(Puesto puesto) {
        this.puesto = puesto;
    }
}
