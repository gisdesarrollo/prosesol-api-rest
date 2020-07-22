package com.prosesol.api.rest.services;

import com.prosesol.api.rest.models.entity.Candidato;


public interface ICandidatoService {

    public Candidato findById(Long id);

    public void save(Candidato candidato);

    public Candidato findByRfc(String rfc);

    public void deleteById(Long id);

    public void deleteRelCandidatoPagosById(Long idCandidato);
}
