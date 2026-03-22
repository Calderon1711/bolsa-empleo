package org.example.bolsa_empleo.entidades;

import jakarta.persistence.*;

@Entity
@Table(name = "puesto_caracteristica")
public class PuestoCaracteristica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "puesto_id", nullable = false)
    private Puesto puesto;

    @ManyToOne
    @JoinColumn(name = "caracteristica_id", nullable = false)
    private Caracteristica caracteristica;

    @Column(nullable = false)
    private Integer nivelRequerido;

    public PuestoCaracteristica() {}

    public PuestoCaracteristica(Puesto puesto, Caracteristica caracteristica, Integer nivelRequerido) {
        this.puesto = puesto;
        this.caracteristica = caracteristica;
        this.nivelRequerido = nivelRequerido;
    }

    public Long getId() {
        return id;
    }

    public Puesto getPuesto() {
        return puesto;
    }

    public Caracteristica getCaracteristica() {
        return caracteristica;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public void setPuesto(Puesto puesto) {
        this.puesto = puesto;
    }

    public void setCaracteristica(Caracteristica caracteristica) {
        this.caracteristica = caracteristica;
    }

    public Integer getNivelRequerido() { return nivelRequerido; }
    public void setNivelRequerido(Integer nivelRequerido) { this.nivelRequerido = nivelRequerido; }
}
