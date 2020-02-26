package com.prosesol.api.rest.models.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Luis Enrique Morales Soriano
 */
@Entity
@Table(name = "clientes_openpay")
public class Cliente implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_suscripcion")
    private Suscripcion suscripcion;

    @Column(name = "id_cliente_openpay")
    private String clienteOpenpay;

    @Column(name = "estatus")
    private Boolean estatus;

    public Cliente(){}

    public Cliente(Suscripcion suscripcion, String clienteOpenpay, Boolean estatus) {
        this.suscripcion = suscripcion;
        this.clienteOpenpay = clienteOpenpay;
        this.estatus = estatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Suscripcion getSuscripcion() {
        return suscripcion;
    }

    public void setSuscripcion(Suscripcion suscripcion) {
        this.suscripcion = suscripcion;
    }

    public String getClienteOpenpay() {
        return clienteOpenpay;
    }

    public void setClienteOpenpay(String clienteOpenpay) {
        this.clienteOpenpay = clienteOpenpay;
    }

    public Boolean getEstatus() {
        return estatus;
    }

    public void setEstatus(Boolean estatus) {
        this.estatus = estatus;
    }
}
