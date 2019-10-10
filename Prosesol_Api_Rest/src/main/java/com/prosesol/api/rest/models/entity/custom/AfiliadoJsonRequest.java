package com.prosesol.api.rest.models.entity.custom;

import com.prosesol.api.rest.models.entity.schemas.AfiliadoRequest;

import java.util.List;

public class AfiliadoJsonRequest {

    private List<AfiliadoRequest> afiliado;

    public List<AfiliadoRequest> getAfiliado() {
        return afiliado;
    }

    public void setAfiliado(List<AfiliadoRequest> afiliado) {
        this.afiliado = afiliado;
    }
}
