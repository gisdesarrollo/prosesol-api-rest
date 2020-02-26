package com.prosesol.api.rest.models.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Luis Enrique Morales Soriano
 */
@Entity
@Table(name = "planes_openpay")
public class Plan implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_plan")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_servicio")
    private Servicio servicio;

    @Column(name = "nombre_servicio")
    private String nombreServicio;

    @Column(name = "id_plan_openpay")
    private String planOpenpay;

    @OneToMany(mappedBy = "plan", fetch = FetchType.LAZY, cascade = CascadeType.ALL,
                orphanRemoval = true)
    private List<Suscripcion> suscripciones;

    public Plan(){}

    public Plan(Servicio servicio, String nombreServicio, String planOpenpay) {
        this.servicio = servicio;
        this.nombreServicio = nombreServicio;
        this.planOpenpay = planOpenpay;
        this.suscripciones = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Servicio getServicio() {
        return servicio;
    }

    public void setServicio(Servicio servicio) {
        this.servicio = servicio;
    }

    public String getNombreServicio() {
        return nombreServicio;
    }

    public void setNombreServicio(String nombreServicio) {
        this.nombreServicio = nombreServicio;
    }

    public String getPlanOpenpay() {
        return planOpenpay;
    }

    public void setPlanOpenpay(String planOpenpay) {
        this.planOpenpay = planOpenpay;
    }

    public List<Suscripcion> getSuscripciones() {
        return suscripciones;
    }

    public void setSuscripciones(List<Suscripcion> suscripciones) {
        this.suscripciones = suscripciones;
    }
}
