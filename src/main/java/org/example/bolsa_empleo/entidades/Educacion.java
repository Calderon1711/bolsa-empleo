package org.example.bolsa_empleo.entidades;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "educacion")
public class Educacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idEducacion; // PK autoincremental

    @ManyToOne
    @JoinColumn(name = "idCurriculum", referencedColumnName = "idCurriculum")
    private CV curriculum; // FK hacia CV

    private String institucion;   // nombre de la institución
    private String titulo;        // título obtenido
    private LocalDate fechaInicio; // fecha de inicio
    private LocalDate fechaFin;    // fecha de fin
    private String descripcion;   // descripción de estudios

    // Constructor vacío
    public Educacion() {}

    // Constructor con parámetros
    public Educacion(CV curriculum, String institucion, String titulo,
                     LocalDate fechaInicio, LocalDate fechaFin, String descripcion) {
        this.curriculum = curriculum;
        this.institucion = institucion;
        this.titulo = titulo;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.descripcion = descripcion;
    }

    // Getters y Setters
    public long getIdEducacion() { return idEducacion; }
    public void setIdEducacion(long idEducacion) { this.idEducacion = idEducacion; }

    public CV getCurriculum() { return curriculum; }
    public void setCurriculum(CV curriculum) { this.curriculum = curriculum; }

    public String getInstitucion() { return institucion; }
    public void setInstitucion(String institucion) { this.institucion = institucion; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}