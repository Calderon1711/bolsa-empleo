package org.example.bolsa_empleo.entidades;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "oferente_caracteristica")
public class OferenteCaracteristica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Muchos registros pueden pertenecer a un oferente
    @ManyToOne
    @JoinColumn(name = "oferente_id", nullable = false)
    private Oferente oferente;

    // Muchos registros pueden pertenecer a una caracteristica
    @ManyToOne
    @JoinColumn(name = "caracteristica_id", nullable = false)
    private Caracteristica caracteristica;

    // Nivel de dominio (1–10 por ejemplo)
    @Column(nullable = false)
    private Integer nivel;

    public OferenteCaracteristica() {}

    public OferenteCaracteristica(Oferente oferente, Caracteristica caracteristica, Integer nivel) {
        this.oferente = oferente;
        this.caracteristica = caracteristica;
        this.nivel = nivel;
    }

    // getters y setters
}