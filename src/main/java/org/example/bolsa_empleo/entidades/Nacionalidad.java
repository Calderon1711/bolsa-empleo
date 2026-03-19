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

    public Long getId_Nacionalidad() {return idNacionalidad;}
    public String getNombre_Nacionalidad() {return nombreNacionalidad;}
    public void setId_Nacionalidad(long id) {this.idNacionalidad = id;}
    public void setNombre_Nacionalidad(String nombre) {this.nombreNacionalidad = nombre;}

}
