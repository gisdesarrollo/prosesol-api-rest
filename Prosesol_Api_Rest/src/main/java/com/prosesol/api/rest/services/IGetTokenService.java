package com.prosesol.api.rest.services;

/**
 * @author Luis Enrique Morales Soriano
 */
public interface IGetTokenService {

    public String getTokenWithEmailAndPassword(String email, String password);

}
