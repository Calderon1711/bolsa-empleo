package org.example.bolsa_empleo.entidades;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "caracteristica")
public class Caracteristica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    // Padre (auto-relación)
    @ManyToOne
    @JoinColumn(name = "padre_id")
    private Caracteristica padre;

    // Hijos (auto-relación inversa)
    @OneToMany(mappedBy = "padre")
    private List<Caracteristica> hijos;

    // Constructores
    public Caracteristica() {}

    public Caracteristica(String nombre) {
        this.nombre = nombre;
    }

    // Getters y Setters
    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }

    public void setNombre(String nombre) { this.nombre = nombre; }

    public Caracteristica getPadre() { return padre; }

    public void setPadre(Caracteristica padre) { this.padre = padre; }

    public List<Caracteristica> getHijos() { return hijos; }

    public void setHijos(List<Caracteristica> hijos) { this.hijos = hijos; }
}