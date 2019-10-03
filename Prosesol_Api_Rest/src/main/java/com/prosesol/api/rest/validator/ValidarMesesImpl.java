package com.prosesol.api.rest.validator;

import java.time.Month;

import org.springframework.stereotype.Service;

import com.prosesol.api.rest.services.IValidarMeses;
import com.prosesol.api.rest.utils.Meses30;




@Service
public class ValidarMesesImpl implements IValidarMeses{

	@Override
	public boolean has30Days(Month month) {
				
		for(Meses30 mes30 : Meses30.values()) {
			if(mes30.name() == month.name()) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public boolean isFebruary(Month month) {
		
		if(month.getValue() == 2) {
			return true;
		}
		
		return false;
	}

}
