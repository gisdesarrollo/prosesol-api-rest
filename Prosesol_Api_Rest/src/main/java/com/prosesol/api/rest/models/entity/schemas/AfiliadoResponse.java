package com.prosesol.api.rest.models.entity.schemas;

import java.util.ArrayList;
import java.util.List;

public class AfiliadoResponse {

    private String clave;

    private String nombre;

    private String apellidoPaterno;

    private String apellidoMaterno;

    private String rfc;

    private List<AfiliadoResponse> beneficiarios;

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = apellidoPaterno;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = apellidoMaterno;
    }

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public List<AfiliadoResponse> getBeneficiarios() {
        return beneficiarios;
    }

    public void setBeneficiarios(List<AfiliadoResponse> beneficiarios) {
        this.beneficiarios = beneficiarios;
    }
}
