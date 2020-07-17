package com.prosesol.api.rest.response.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Luis Enrique Morales Soriano
 */
public class ZipCodeRS {

    private int id;
    private int idEstado;
    private String estado;
    private int idMunicipio;
    private String municipio;
    private String ciudad;
    private String zona;
    private int codigoPostal;
    private String asentamentio;
    private String tipo;
    private Date createdAt;
    private Date updatedAt;
    private List<String> colonias;

    public ZipCodeRS(){
        colonias = new ArrayList<>();
    }

    @JsonCreator
    public ZipCodeRS(
            @JsonProperty("id") int id,
            @JsonProperty("idEstado") int idEstado,
            @JsonProperty("estado") String estado,
            @JsonProperty("idMunicipio") int idMunicipio,
            @JsonProperty("municipio") String municipio,
            @JsonProperty("ciudad") String ciudad,
            @JsonProperty("zona") String zona,
            @JsonProperty("cp") int codigoPostal,
            @JsonProperty("asentamiento") String asentamiento,
            @JsonProperty("tipo") String tipo,
            @JsonProperty("created_at") Date createdAt,
            @JsonProperty("updated_at") Date updatedAt,
            @JsonProperty("colonias") List<String> colonias
    ){
        this.id = id;
        this.idEstado = idEstado;
        this.estado = estado;
        this.idMunicipio = idMunicipio;
        this.municipio = municipio;
        this.ciudad = ciudad;
        this.zona = zona;
        this.codigoPostal = codigoPostal;
        this.asentamentio = asentamiento;
        this.tipo = tipo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.colonias = colonias;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdEstado() {
        return idEstado;
    }

    public void setIdEstado(int idEstado) {
        this.idEstado = idEstado;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getIdMunicipio() {
        return idMunicipio;
    }

    public void setIdMunicipio(int idMunicipio) {
        this.idMunicipio = idMunicipio;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getZona() {
        return zona;
    }

    public void setZona(String zona) {
        this.zona = zona;
    }

    public int getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(int codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public String getAsentamentio() {
        return asentamentio;
    }

    public void setAsentamentio(String asentamentio) {
        this.asentamentio = asentamentio;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<String> getColonias() {
        return colonias;
    }

    public void setColonias(List<String> colonias) {
        this.colonias = colonias;
    }

    @Override
    public String toString() {
        return "ZipCodeRS{" +
                "id=" + id +
                ", idEstado=" + idEstado +
                ", estado='" + estado + '\'' +
                ", idMunicipio=" + idMunicipio +
                ", municipio='" + municipio + '\'' +
                ", ciudad='" + ciudad + '\'' +
                ", zona='" + zona + '\'' +
                ", codigoPostal=" + codigoPostal +
                ", asentamentio='" + asentamentio + '\'' +
                ", tipo='" + tipo + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", colonias=" + colonias +
                '}';
    }
}
