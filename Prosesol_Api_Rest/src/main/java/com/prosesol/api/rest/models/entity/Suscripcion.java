package com.prosesol.api.rest.models.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Luis Enrique Morales Soriano
 */
@Entity
@Table(name = "suscripciones_openpay")
public class Suscripcion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_suscripcion")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_plan")
    private Plan plan;

    @Column(name = "id_suscripcion_openpay")
    private String suscripcionOpenpay;

    @Column(name = "estatus")
    private Boolean estatus;

    @OneToMany(mappedBy = "suscripcion", fetch = FetchType.LAZY, cascade = CascadeType.ALL,
                orphanRemoval = true)
    private List<Cliente> clientes;

    public Suscripcion(){}

    public Suscripcion(Plan plan, String suscripcionOpenpay, Boolean estatus) {
        this.plan = plan;
        this.suscripcionOpenpay = suscripcionOpenpay;
        this.estatus = estatus;
        this.clientes = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Plan getPlan() {
        return plan;
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
    }

    public String getSuscripcionOpenpay() {
        return suscripcionOpenpay;
    }

    public void setSuscripcionOpenpay(String suscripcionOpenpay) {
        this.suscripcionOpenpay = suscripcionOpenpay;
    }

    public Boolean getEstatus() {
        return estatus;
    }

    public void setEstatus(Boolean estatus) {
        this.estatus = estatus;
    }

    public List<Cliente> getClientes() {
        return clientes;
    }

    public void setClientes(List<Cliente> clientes) {
        this.clientes = clientes;
    }
}
