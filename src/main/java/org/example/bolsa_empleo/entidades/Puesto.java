package org.example.bolsa_empleo.entidades;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "puesto")
public class Puesto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(length = 1000)
    private String descripcion;

    private Double salario;

    private Boolean estado; // activo o cerrado

    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    public Puesto() {}

    public Puesto(String titulo, String descripcion, Double salario, Boolean estado, Empresa empresa) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.salario = salario;
        this.estado = estado;
        this.empresa = empresa;
    }

    // getters y setters
}