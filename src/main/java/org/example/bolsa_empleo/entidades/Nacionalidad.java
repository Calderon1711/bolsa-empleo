package org.example.bolsa_empleo.entidades;
import jakarta.persistence.*;

@Entity
@Table(name = "nacionalidad")
public class Nacionalidad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idNacionalidad;
    private String nombreNacionalidad;


    public Nacionalidad(){}

    public Nacionalidad(String nombre){
        this.nombreNacionalidad = nombre;
    }

    public Long getIdNacionalidad() { return idNacionalidad; }
    public String getNombreNacionalidad() { return nombreNacionalidad; }
    public void setIdNacionalidad(Long idNacionalidad) { this.idNacionalidad = idNacionalidad; }
    public void setNombreNacionalidad(String nombreNacionalidad) { this.nombreNacionalidad = nombreNacionalidad; }

}
