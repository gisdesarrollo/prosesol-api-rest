package com.prosesol.api.rest.models.entity;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Luis Enrique Morales Soriano
 */
@Entity
@Table(name = "periodicidades")
public class Periodicidad implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_periodicidad", unique = true, nullable = false)
    private Long id;

    @Column(name = "nombre")
    @NotEmpty(message = "Proporcione el nombre del Periodo")
    private String nombre;

    @Column(name = "periodo")
    private String periodo;

    @OneToMany(mappedBy = "periodicidad", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Afiliado> afiliado;

    public Periodicidad() {
        afiliado = new ArrayList<Afiliado>();
    }

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

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public List<Afiliado> getAfiliado() {
        return afiliado;
    }

    public void setAfiliado(List<Afiliado> afiliado) {
        this.afiliado = afiliado;
    }

}
