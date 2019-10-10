package com.prosesol.api.rest.models.entity.custom;

import com.prosesol.api.rest.models.entity.schemas.AfiliadoResponse;

import java.util.ArrayList;
import java.util.List;

public class AfiliadoJsonResponse {

    private List<AfiliadoResponse> afiliado;

    public AfiliadoJsonResponse() {
        afiliado = new ArrayList<AfiliadoResponse>();
    }

    public List<AfiliadoResponse> getAfiliado() {
        return afiliado;
    }

    public void setAfiliado(List<AfiliadoResponse> afiliado) {
        this.afiliado = afiliado;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        for (AfiliadoResponse ar : afiliado) {
            sb.append("Clave Afiliado: ").append(ar.getClave())
                    .append("Nombre Completo: ").append(ar.getNombre())
                    .append(ar.getApellidoPaterno()).append(ar.getApellidoMaterno())
                    .append("RFC: ").append(ar.getRfc());
        }
        return sb.toString();
    }
}
