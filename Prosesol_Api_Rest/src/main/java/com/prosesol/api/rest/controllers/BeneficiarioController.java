package com.prosesol.api.rest.controllers;

import java.util.Map;

import javax.validation.Valid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.prosesol.api.rest.models.entity.Afiliado;
import com.prosesol.api.rest.services.IAfiliadoService;
import com.prosesol.api.rest.services.IServicioService;

@Controller
//@SessionAttributes("afiliado")
@RequestMapping("/beneficiarios")
public class BeneficiarioController {
	
	protected static final Log logger = LogFactory.getLog(BeneficiarioController.class);

	@Value("${app.clave}")
	private String clave;
	
	@Autowired
	private IAfiliadoService afiliadoService;

	@Autowired
	private IServicioService servicioService;
	
	private static Long idAfiliado;
	
	@RequestMapping(value="/crear/{id}")
	public String crear(@PathVariable("id") Long id,Map<String, Object> model) {
		
		idAfiliado=id;
		System.out.println("la id afiliado es: "+idAfiliado);
		
		Afiliado afiliado=new Afiliado();
		model.put("afiliado", afiliado);
			
		return "beneficiarios/crear";
	}
	
	@RequestMapping(value="/guardar",method = RequestMethod.POST)
	public String guardar(@ModelAttribute("clave") String clave,@Valid Afiliado afiliado, BindingResult result, Model model, 
			  RedirectAttributes redirect, SessionStatus status) {
		
		System.out.println("id: "+idAfiliado);
		System.out.println("clave: "+clave);
		try {
		if(result.hasErrors()) {
			System.out.println("Error en el proceso");
			
			return "redirect:/afiliados/bienvenido/"+idAfiliado;
			//return "beneficiario/crear";
			}
		}catch (Exception e) {
			e.printStackTrace();
			logger.error("Error al momento de ejecutar el proceso: " + e);
		}
		return "redirect:/afiliados/bienvenido/"+idAfiliado;
	}
	
	@ModelAttribute("clave")
	public String getClaveAfiliado() {
		
		String clave = "0123456789";
		String claveAfiliado = "";
		
		for(int i = 0; i < 10; i++) {
			claveAfiliado += (clave.charAt((int)(Math.random() * clave.length())));
		}
		
		return claveAfiliado;
	}
}
