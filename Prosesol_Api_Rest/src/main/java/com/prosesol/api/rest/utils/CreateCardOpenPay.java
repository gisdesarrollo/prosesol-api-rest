package com.prosesol.api.rest.utils;

import org.springframework.stereotype.Service;

import com.prosesol.api.rest.models.entity.Afiliado;
import com.prosesol.api.rest.models.entity.Pago;

import mx.openpay.client.Address;
import mx.openpay.client.Card;
import mx.openpay.client.core.OpenpayAPI;
import mx.openpay.client.exceptions.OpenpayServiceException;
import mx.openpay.client.exceptions.ServiceUnavailableException;

@Service
public class CreateCardOpenPay {
	
	private OpenpayAPI api;
	
	public String createCardoOpenPay(Pago pago, Afiliado afiliado, String openpayURL, String openpayPk, String openpayId) throws OpenpayServiceException, ServiceUnavailableException {
		
		System.out.println(pago.getRfc());
		
		api = new OpenpayAPI(openpayURL, openpayPk, openpayId);
		
		String nombrAfiliado = afiliado.getNombre() + ' ' + afiliado.getApellidoPaterno() + ' ' + afiliado.getApellidoMaterno();		
		
		Card card = new Card();
		
		card.holderName(nombrAfiliado);
		card.cardNumber(pago.getCreditCardNumber());
		card.cvv2(String.valueOf(pago.getCvv2()));
		card.expirationMonth(pago.getExpirationMonth());
		card.expirationYear(pago.getExpirationYear());
		
		Address direccion = new Address();
		direccion.city(pago.getCiudad());
		direccion.state(pago.getEstado());
		direccion.postalCode(String.valueOf(pago.getCodigoPostal()));
		direccion.line1(pago.getDireccion1());
		direccion.line2(pago.getDireccion2());
		if(pago.getDireccion3() != null) {
			direccion.line3(pago.getDireccion3());
		}
		card.address(direccion);
		
		card = api.cards().create(card);
		
		return "1";
	}
	
}
