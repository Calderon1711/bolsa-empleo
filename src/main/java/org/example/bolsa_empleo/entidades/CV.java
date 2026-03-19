package org.example.bolsa_empleo.entidades;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "cv")
public class CV {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idCurriculum; // PK autoincremental del CV

    @OneToOne
    @JoinColumn(name = "cedulaOferente", referencedColumnName = "cedulaOferente")
    private Oferente oferente; // FK hacia Oferente (cédula)

    private String descripcionCurriculum; // descripción del CV
    private String rutaDocumento;        // ruta del archivo
    private LocalDateTime fechaCreacionCurriculum; // fecha de creación

    // Constructor vacío
    public CV() {}

    // Constructor con parámetros
    public CV(Oferente oferente, String descripcionCurriculum, String rutaDocumento, LocalDateTime fechaCreacionCurriculum) {
        this.oferente = oferente;
        this.descripcionCurriculum = descripcionCurriculum;
        this.rutaDocumento = rutaDocumento;
        this.fechaCreacionCurriculum = fechaCreacionCurriculum;
    }

    // Getters y Setters
    public long getIdCurriculum() { return idCurriculum; }
    public void setIdCurriculum(long idCurriculum) { this.idCurriculum = idCurriculum; }

    public Oferente getOferente() { return oferente; }
    public void setOferente(Oferente oferente) { this.oferente = oferente; }

    public String getDescripcionCurriculum() { return descripcionCurriculum; }
    public void setDescripcionCurriculum(String descripcionCurriculum) { this.descripcionCurriculum = descripcionCurriculum; }

    public String getRutaDocumento() { return rutaDocumento; }
    public void setRutaDocumento(String rutaDocumento) { this.rutaDocumento = rutaDocumento; }

    public LocalDateTime getFechaCreacionCurriculum() { return fechaCreacionCurriculum; }
    public void setFechaCreacionCurriculum(LocalDateTime fechaCreacionCurriculum) { this.fechaCreacionCurriculum = fechaCreacionCurriculum; }
}