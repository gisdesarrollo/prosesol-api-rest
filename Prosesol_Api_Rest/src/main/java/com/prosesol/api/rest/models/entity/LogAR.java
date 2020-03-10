package com.prosesol.api.rest.models.entity;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Luis Enrique Morales Soriano
 */
@Entity
@Table(name = "logs")
public class LogAR implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_log")
    private Long id;

    @Column(name = "nombre_archivo")
    private String nombre;

    @Column(name = "fecha_creacion")
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "dd/MM/yy")
    private Date fecha;

    @Column(name = "numero_registros")
    private Integer numeroRegistros;

    @Lob
    @Column(name = "archivo")
    private byte[] archivo;

    @Column(name = "is_vigor")
    private boolean isVigor;

    @Column(name = "is_conciliacion")
    private boolean isConciliacion;

    public LogAR() {
    }

    public LogAR(String nombre, Date fecha, Integer numeroRegistros, byte[] archivo, boolean isVigor,
                 boolean isConciliacion) {
        this.nombre = nombre;
        this.fecha = fecha;
        this.numeroRegistros = numeroRegistros;
        this.archivo = archivo;
        this.isVigor = isVigor;
        this.isConciliacion = isConciliacion;
    }



/* public LogCM(String nombre, Date fecha, Integer numeroRegistros, byte[] archivo, boolean isVigor) {
        this.nombre = nombre;
        this.fecha = fecha;
        this.numeroRegistros = numeroRegistros;
        this.archivo = archivo;
        this.isVigor = isVigor;
    }*/

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Integer getNumeroRegistros() {
        return numeroRegistros;
    }

    public void setNumeroRegistros(Integer numeroRegistros) {
        this.numeroRegistros = numeroRegistros;
    }

    public byte[] getArchivo() {
        return archivo;
    }

    public void setArchivo(byte[] archivo) {
        this.archivo = archivo;
    }

    public boolean isVigor() {
        return isVigor;
    }

    public void setVigor(boolean vigor) {
        isVigor = vigor;
    }


    public boolean isConciliacion() {
        return isConciliacion;
    }

    public void setConciliacion(boolean isConciliacion) {
        this.isConciliacion = isConciliacion;
    }

    @Override
    public String toString() {
        return "LogCM{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", fecha=" + fecha +
                ", numeroRegistros=" + numeroRegistros +
                '}';
    }

}
