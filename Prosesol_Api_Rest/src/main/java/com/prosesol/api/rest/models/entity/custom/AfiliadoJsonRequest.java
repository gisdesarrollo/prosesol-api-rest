package com.prosesol.api.rest.models.entity.custom;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.prosesol.api.rest.models.entity.schemas.AfiliadoRequest;

import java.util.List;

public class AfiliadoJsonRequest {
	
	@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<AfiliadoRequest> afiliado;

    public List<AfiliadoRequest> getAfiliado() {
        return afiliado;
    }

    public void setAfiliado(List<AfiliadoRequest> afiliado) {
        this.afiliado = afiliado;
    }
}
