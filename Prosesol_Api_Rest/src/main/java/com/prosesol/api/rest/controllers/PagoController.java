package com.prosesol.api.rest.controllers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.prosesol.api.rest.controllers.exception.OpenpayException;
import com.prosesol.api.rest.models.entity.Afiliado;
import com.prosesol.api.rest.services.IAfiliadoService;

import mx.openpay.client.Charge;
import mx.openpay.client.Customer;
import mx.openpay.client.core.OpenpayAPI;
import mx.openpay.client.core.requests.transactions.CreateCardChargeParams;
import mx.openpay.client.exceptions.OpenpayServiceException;
import mx.openpay.client.exceptions.ServiceUnavailableException;

@Controller
public class PagoController {

	protected final Logger LOG = LoggerFactory.getLogger(PagoController.class);
	
	@Value("${openpay.id}")
	private String openpayId;
	
	@Value("${openpay.pk}")
	private String openpayPk;
	
	@Value("${openpay.url}")
	private String openpayURL;
	
	@Autowired
	private IAfiliadoService afiliadoService;
	
	private Afiliado afiliado;
				
	private OpenpayAPI apiOpenpay;
	
	private Customer customer;
	
	private String description;
	
	private int code;
	
	
	public PagoController() {	
		customer = new Customer();
	}
	
	
	@GetMapping("/main")
	public String index() {
		return "main";
	}
	
	@RequestMapping(value = "/pagos", method = RequestMethod.POST)
	public String obtenerAfiliadoByRfc(@ModelAttribute(name = "rfc") String rfc, Model model, 
			RedirectAttributes redirect){
				
		afiliado = afiliadoService.findByRfc(rfc);
				
		if(rfc.length() < 13) {
			
			LOG.info("ERR: ", "El RFC no cumple con los campos necesarios");
			redirect.addFlashAttribute("error", "El RFC No cumple con los campos necesarios");
			return "redirect:/main";
			
		}
				
		if(afiliado == null) {
			
			LOG.info("ERR: ", "No existe el afiliado, proporcione otro RFC");
			redirect.addFlashAttribute("error", "No existe el afiliado, proporcione otro RFC");
			return "redirect:/main";
			
		}else {
			
			System.out.println(afiliado.toString());
			
			if(afiliado.isBeneficiario().equals("0")) {
				model.addAttribute("afiliado", afiliado);				
			}else {
				LOG.info("ERR: ", "El afiliado no es titular del servicio");
				redirect.addFlashAttribute("error", "El afiliado no es titular del servicio");
				return "redirect:/main";
			}
		}
		
		return "pagos";
		
	}
	
	@RequestMapping(value = "/realizarPago", method = RequestMethod.POST)
	public String realizarCargoTarjeta(@ModelAttribute("token_id") String tokenId,
									   @ModelAttribute("deviceIdHiddenFieldName") String deviceSessionId,
								       RedirectAttributes redirect, SessionStatus status) throws OpenpayException{
		
		apiOpenpay = new OpenpayAPI(openpayURL, openpayPk, openpayId);
		
		String name = afiliado.getNombre();
		String lastName = afiliado.getApellidoPaterno() + ' ' + afiliado.getApellidoMaterno();
		BigDecimal amount = BigDecimal.valueOf(afiliado.getSaldoCorte());
		
		amount = amount.setScale(2, BigDecimal.ROUND_HALF_EVEN);		
		
		try {
		
			customer.setName(name);
			customer.setLastName(lastName);
			customer.setEmail("luisenrique.moralessoriano@gmail.com");
			
			CreateCardChargeParams creditCardcharge = new CreateCardChargeParams()
														  .cardId(tokenId)
														  .amount(amount)
														  .description("Cargo a nombre de: " + afiliado.getNombre())
														  .deviceSessionId(deviceSessionId)
														  .customer(customer);
			
			@SuppressWarnings("deprecation")
			Charge charge = apiOpenpay.charges().create(creditCardcharge);
			
			System.out.println(charge.getAuthorization());
			
		} catch (OpenpayServiceException | ServiceUnavailableException e) {
			
			String response = e.toString().substring(e.toString().indexOf("("), e.toString().lastIndexOf(")"));	
			String responseValues[] = response.split(",");
			
			ArrayList<String> list = new ArrayList<String>(Arrays.asList(responseValues));
			
			for(String value : list) {
				System.out.println(value);
				if(value.contains("description")) {
					description = value.substring(value.lastIndexOf("=") + 1);
				}
				if(value.contains("httpCode")) {
					code = Integer.parseInt(value.substring(value.lastIndexOf("=") + 1));
				}
			}
			
			LOG.error(description, new OpenpayException(code, description));
			
			redirect.addFlashAttribute("error", description);
			return "redirect:/main";
			
		}		
			
		return "redirect:/main";
	}
	
}
