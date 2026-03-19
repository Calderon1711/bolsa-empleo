package org.example.bolsa_empleo.entidades;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "experiencia_laboral")
public class Experiencia_Laboral {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idExperiencia; // PK autoincremental

    @ManyToOne
    @JoinColumn(name = "idCurriculum", referencedColumnName = "idCurriculum")
    private CV curriculum; // FK hacia CV

    private String empresa;       // nombre de la empresa
    private String puesto;        // puesto desempeñado
    private LocalDate fechaInicio; // fecha de inicio
    private LocalDate fechaFin;    // fecha de fin
    private String descripcion;   // descripción del trabajo

    // Constructor vacío
    public Experiencia_Laboral() {}

    // Constructor con parámetros
    public Experiencia_Laboral(CV curriculum, String empresa, String puesto,
                              LocalDate fechaInicio, LocalDate fechaFin, String descripcion) {
        this.curriculum = curriculum;
        this.empresa = empresa;
        this.puesto = puesto;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.descripcion = descripcion;
    }

    // Getters y Setters
    public long getIdExperiencia() { return idExperiencia; }
    public void setIdExperiencia(long idExperiencia) { this.idExperiencia = idExperiencia; }

    public CV getCurriculum() { return curriculum; }
    public void setCurriculum(CV curriculum) { this.curriculum = curriculum; }

    public String getEmpresa() { return empresa; }
    public void setEmpresa(String empresa) { this.empresa = empresa; }

    public String getPuesto() { return puesto; }
    public void setPuesto(String puesto) { this.puesto = puesto; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}