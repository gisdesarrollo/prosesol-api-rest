package com.prosesol.api.rest.services;

import java.time.Month;

public interface IValidarMeses {

	public boolean has30Days(Month month);
	
	public boolean isFebruary(Month month);
	
}
