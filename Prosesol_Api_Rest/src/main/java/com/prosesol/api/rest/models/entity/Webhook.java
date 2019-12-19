package com.prosesol.api.rest.models.entity;

import javax.persistence.*;

/**
 * @author Luis Enrique Morales Soriano
 */
@Table(name = "webhook")
@Entity
public class Webhook {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "estatus")
    private String estatus;

    public Webhook(){}

    public Webhook(String estatus) {
        this.estatus = estatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }
}
