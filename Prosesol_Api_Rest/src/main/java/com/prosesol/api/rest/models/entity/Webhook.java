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

    @Column(name = "body")
    private String body;

    public Webhook(){}

    public Webhook(String body) {
        this.body = body;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
