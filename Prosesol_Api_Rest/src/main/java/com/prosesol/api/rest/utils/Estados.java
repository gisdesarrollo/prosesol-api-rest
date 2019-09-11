package com.prosesol.api.rest.utils;

import java.util.ArrayList;
import java.util.List;

public class Estados {

	private final static List<String> ESTADOS;
	
	static {
		ESTADOS = new ArrayList<String>();
		ESTADOS.add("AGU");
		ESTADOS.add("BCN");
		ESTADOS.add("BCS");
		ESTADOS.add("CAM");
		ESTADOS.add("CHH");
		ESTADOS.add("CHP");
		ESTADOS.add("COA");
		ESTADOS.add("COL");
		ESTADOS.add("CDMX");
		ESTADOS.add("DUR");
		ESTADOS.add("GRO");
		ESTADOS.add("GUA");
		ESTADOS.add("HID");
		ESTADOS.add("JAL");
		ESTADOS.add("MEX");
		ESTADOS.add("MIC");
		ESTADOS.add("MOR");
		ESTADOS.add("NAY");
		ESTADOS.add("NLE");
		ESTADOS.add("OAX");
		ESTADOS.add("PUE");
		ESTADOS.add("QUE");
		ESTADOS.add("ROO");
		ESTADOS.add("SIN");
		ESTADOS.add("SLP");
		ESTADOS.add("SON");
		ESTADOS.add("TAB");
		ESTADOS.add("TAM");
		ESTADOS.add("TLA");
		ESTADOS.add("VER");
		ESTADOS.add("YUC");
		ESTADOS.add("ZAC");
	}

	public List<String> getEstados() {
		return ESTADOS;
	}
	
	
}

