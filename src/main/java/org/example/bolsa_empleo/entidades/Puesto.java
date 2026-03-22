package org.example.bolsa_empleo.entidades;
import jakarta.persistence.*;
import java.time.LocalDate;

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

    // "PUBLICA" o "PRIVADA"
    @Column(nullable = false, length = 10)
    private String tipoPublicacion;

    private LocalDate fechaRegistro;

    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    public Puesto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Double getSalario() { return salario; }
    public void setSalario(Double salario) { this.salario = salario; }

    public Boolean getEstado() { return estado; }
    public void setEstado(Boolean estado) { this.estado = estado; }

    public String getTipoPublicacion() { return tipoPublicacion; }
    public void setTipoPublicacion(String tipoPublicacion) { this.tipoPublicacion = tipoPublicacion; }

    public LocalDate getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDate fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }
}