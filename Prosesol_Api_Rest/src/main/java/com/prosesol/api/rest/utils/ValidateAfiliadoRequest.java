package com.prosesol.api.rest.utils;

import com.josketres.rfcfacil.Rfc;
import com.prosesol.api.rest.controllers.exception.AfiliadoException;
import com.prosesol.api.rest.models.entity.Afiliado;
import com.prosesol.api.rest.models.entity.Servicio;
import com.prosesol.api.rest.models.entity.schemas.AfiliadoRequest;
import com.prosesol.api.rest.services.IAfiliadoService;
import com.prosesol.api.rest.services.IServicioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Service
public class ValidateAfiliadoRequest {

    @Autowired
    private IServicioService servicioService;

    @Autowired
    private IAfiliadoService afiliadoService;

    public Afiliado validateAfiliadoFromJson(AfiliadoRequest afiliadoRequest)throws AfiliadoException{

        Afiliado afiliado = new Afiliado();
        Rfc rfc;

        if(afiliadoRequest.getId() != null){
            throw new AfiliadoException(4000, "El Afiliado no debe de contener un id");
        }
        if(!afiliadoRequest.getClave().equals("")){
            throw new AfiliadoException(4000, "El Afiliado no debe de contener una clave");
        }
        if(afiliadoRequest.getNombre().equals(null) || afiliadoRequest.getNombre().equals("")){
            throw new AfiliadoException(4000, "El Afiliado o Beneficiario no cuenta con nombre");
        }else{
            afiliado.setNombre(afiliadoRequest.getNombre());
        }
        if(afiliadoRequest.getApellidoPaterno().equals(null) || afiliadoRequest.getApellidoPaterno().equals("")){
            throw new AfiliadoException(4000, "El Afiliado o Beneficiario no cuenta con apellido paterno");
        }else{
            afiliado.setApellidoPaterno(afiliadoRequest.getApellidoPaterno());
        }
        if(afiliadoRequest.getApellidoMaterno().equals(null) || afiliadoRequest.getApellidoMaterno().equals("")){
            throw new AfiliadoException(4000, "El Afiliado o Beneficiario no cuenta con apellido materno");
        }else{
            afiliado.setApellidoMaterno(afiliadoRequest.getApellidoMaterno());
        }
        if(afiliadoRequest.getFechaNacimiento().equals(null) || afiliadoRequest.getFechaNacimiento().equals("")){
            throw new AfiliadoException(4000, "El Afiliado o Beneficiario no cuenta con su fecha de nacimiento");
        }else{
            afiliado.setFechaNacimiento(afiliadoRequest.getFechaNacimiento());
        }
        afiliado.setLugarNacimiento(afiliadoRequest.getLugarNacimiento());
        afiliado.setEstadoCivil(afiliadoRequest.getEstadoCivil());
        afiliado.setOcupacion(afiliadoRequest.getOcupacion());
        afiliado.setSexo(afiliadoRequest.getSexo());
        afiliado.setCurp(afiliadoRequest.getCurp());
        afiliado.setNss(afiliadoRequest.getNss());
        if(afiliadoRequest.getRfc().equals(null) || afiliadoRequest.getRfc().equals("") ||
                afiliadoRequest.getRfc().length() < 13){
            LocalDate fechaNacimiento = afiliadoRequest.getFechaNacimiento().toInstant()
                                                       .atZone(ZoneId.systemDefault())
                                                       .toLocalDate();

            rfc = new Rfc.Builder()
                         .name(afiliadoRequest.getNombre())
                         .firstLastName(afiliadoRequest.getApellidoPaterno())
                         .secondLastName(afiliadoRequest.getApellidoMaterno())
                         .birthday(fechaNacimiento.getDayOfMonth(), fechaNacimiento.getMonthValue()
                            , fechaNacimiento.getYear())
                         .build();

            afiliado.setRfc(rfc.toString());
        }else{
            afiliado.setRfc(afiliadoRequest.getRfc());
        }

        boolean isAfiliado = validaAfiliado(afiliado);

        if(!isAfiliado){
            throw new AfiliadoException(4000, "El afiliado " + afiliado.getNombre() + " " +
                    afiliado.getApellidoPaterno() + " " + afiliado.getApellidoMaterno() +
                    " con RFC " + afiliado.getRfc() + " ya se encuentra registrado");
        }

        afiliado.setTelefonoFijo(afiliadoRequest.getTelefonoFijo());
        afiliado.setTelefonoMovil(afiliadoRequest.getTelefonoMovil());
        afiliado.setEmail(afiliadoRequest.getEmail());
        if(afiliadoRequest.getEntidadFederativa().length() > 3){
            throw new AfiliadoException(4000, "El código de la entidad es incorrecto (Valor = 3)");
        }else{
            afiliado.setEntidadFederativa(afiliadoRequest.getEntidadFederativa());
        }
        afiliado.setInfonavit(afiliadoRequest.getInfonavit());

        if(afiliadoRequest.getIsBeneficiario() != null) {
            if (afiliadoRequest.getIsBeneficiario().equals(true)) {
                throw new AfiliadoException(4000, "El afiliado titular no puede ser beneficiario");
            }else{
                if (afiliadoRequest.getServicio() != null) {
                    Servicio servicio = servicioService.findById(afiliadoRequest.getServicio().getId());
                    if(servicio==null) {
                    	 throw new AfiliadoException(4000, "El servicio no esta registrado");
                    }
                    afiliado.setServicio(servicio);
                } else {
                    throw new AfiliadoException(4000, "El servicio para el afiliado titular no debe quedar vacío, " +
                            "proporcione un servicio");
                }
            }
        }else{
            throw new AfiliadoException(4000, "El campo 'isBeneficiario' no debe quedar vacío");
        }

        afiliado.setIsBeneficiario(afiliadoRequest.getIsBeneficiario());
        afiliado.setFechaAfiliacion(new Date());
        afiliado.setFechaAlta(new Date());
        afiliado.setEstatus(1);


        return afiliado;
    }

    public void validateBeneficiarioFromJson(Afiliado afiliado)throws AfiliadoException{

        Rfc rfc;

        if(afiliado.getId() != null){
            throw new AfiliadoException(4000, "El Beneficiario no debe de contener un id");
        }
        if(!afiliado.getClave().equals("")){
            throw new AfiliadoException(4000, "El Beneficiario no debe de contener una clave");
        }
        if(afiliado.getNombre().equals(null) || afiliado.getNombre().equals("")){
            throw new AfiliadoException(4000, "El Beneficiario no cuenta con nombre");
        }
        if(afiliado.getApellidoPaterno().equals(null) || afiliado.getApellidoPaterno().equals("")){
            throw new AfiliadoException(4000, "El Afiliado o Beneficiario no cuenta con apellido paterno");
        }
        if(afiliado.getApellidoMaterno().equals(null) || afiliado.getApellidoMaterno().equals("")){
            throw new AfiliadoException(4000, "El Afiliado o Beneficiario no cuenta con apellido materno");
        }
        if(afiliado.getFechaNacimiento().equals(null) || afiliado.getFechaNacimiento().equals("")){
            throw new AfiliadoException(4000, "El Afiliado o Beneficiario no cuenta con su fecha de nacimiento");
        }
        if(afiliado.getRfc().equals(null) || afiliado.getRfc().equals("") ||
            afiliado.getRfc().length() < 13){
            LocalDate fechaNacimiento = afiliado.getFechaNacimiento().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            rfc = new Rfc.Builder()
                    .name(afiliado.getNombre())
                    .firstLastName(afiliado.getApellidoPaterno())
                    .secondLastName(afiliado.getApellidoMaterno())
                    .birthday(fechaNacimiento.getDayOfMonth(), fechaNacimiento.getDayOfMonth()
                            , fechaNacimiento.getYear())
                    .build();

            afiliado.setRfc(rfc.toString());
        }else{
            afiliado.setRfc(afiliado.getRfc());
        }

        boolean isAfiliado = validaAfiliado(afiliado);

        if(!isAfiliado){
            throw new AfiliadoException(4000, "El beneficiario " + afiliado.getNombre() + " " +
                    afiliado.getApellidoPaterno() + " " + afiliado.getApellidoMaterno() +
                    " con RFC " + afiliado.getRfc() + " ya se encuentra registrado");
        }

        if(afiliado.getEntidadFederativa().length() > 3){
            throw new AfiliadoException(4000, "El código de la entidad es incorrecto (Valor = 3)");
        }else{
            afiliado.setEntidadFederativa(afiliado.getEntidadFederativa());
        }

        if(afiliado.getIsBeneficiario() != null) {

            if (afiliado.getIsBeneficiario().equals(false)) {
                throw new AfiliadoException(4000, "El beneficiario no puede ser titular del servicio");
            }
        }else {
            throw new AfiliadoException(4000, "El campo 'isBeneficiario' no debe quedar vacío");
        }
    }

    /**
     * Valida si el afiliado ya se encuentra en la BBDD
     * @param afiliado
     * @return
     */
    private boolean validaAfiliado(Afiliado afiliado) {

        afiliado = afiliadoService.findByRfc(afiliado.getRfc());

        if(afiliado != null){
            return false;
        }

        return true;
    }
}
