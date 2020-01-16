package com.prosesol.api.rest.controllers.utils;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.prosesol.api.rest.models.dao.IPagoDao;
import com.prosesol.api.rest.models.entity.Pago;
import com.prosesol.api.rest.services.IPagoService;

@RestController
@RequestMapping("/pagos")
public class ValidaSuscripcionRestController {

	protected static final Log LOG = LogFactory.getLog(ValidaSuscripcionRestController.class);
	
	@Autowired
	private IPagoService pagoService;

	@RequestMapping(value = "/validaSuscripcion", method = RequestMethod.GET)
	@ResponseBody
	public boolean validaSuscripcionPagos(@RequestParam(value = "rfc") String rfc, HttpServletRequest request,
			HttpServletResponse response) {
		
		boolean suscripcion=false;
		try {
			
			
			List<Pago> pagos = pagoService.getPagoByRfc(rfc);
			if(pagos==null) {
				suscripcion=false;
			}else {
			for(Pago pago: pagos) {
				if (pago.getIdSuscripcion()!=null) {
					suscripcion=true;
					LOG.info("Afiliado con suscripcion");
					break;
				}else {
					suscripcion=false;
					
				}
			}
			
			
		}
		} catch (Exception e) {
			LOG.error("Error al momento de realizar la consulta", e);
		}
		return suscripcion;
	}

}
