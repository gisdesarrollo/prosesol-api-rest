package com.prosesol.api.rest.controllers;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

import javax.validation.Valid;

import com.prosesol.api.rest.utils.GenerarClave;
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

import com.josketres.rfcfacil.Rfc;
import com.prosesol.api.rest.models.entity.Afiliado;
import com.prosesol.api.rest.models.entity.Servicio;
import com.prosesol.api.rest.services.IAfiliadoService;
import com.prosesol.api.rest.services.IServicioService;

@Controller
@RequestMapping("/beneficiarios")
public class BeneficiarioController {

	protected static final Log logger = LogFactory.getLog(BeneficiarioController.class);

	@Value("${app.clave}")
	private String clave;

	@Autowired
	private IAfiliadoService afiliadoService;

	@Autowired
	private IServicioService servicioService;

	@Autowired
	private GenerarClave generarClave;

	private static Long idAfiliado;

	@RequestMapping(value = "/crear/{id}")
	public String crear(@PathVariable("id") Long id, Map<String, Object> model, RedirectAttributes redirect) {

		idAfiliado = id;

		Afiliado afiliado = new Afiliado();
		model.put("afiliado", afiliado);
		
		afiliado = afiliadoService.findById(id);
		if (afiliado == null) {
			redirect.addFlashAttribute("error", "Afiliado no registrado");
			return "redirect:/afiliados/servicio";
		}	
		model.put("id", idAfiliado);

		return "beneficiarios/crear";
	}

	@RequestMapping(value = "/guardar", method = RequestMethod.POST, params = "action=save")
	public String guardar(Afiliado beneficiario, BindingResult result, Model model,
			RedirectAttributes redirect, SessionStatus status) {

	
		Date fechaAlta = new Date();
		Afiliado Afiliado = afiliadoService.findById(idAfiliado);
		Servicio servicio = servicioService.findById(Afiliado.getServicio().getId());
        Double saldoAcumulado=0.0;
		Rfc rfc = null;
		try {
			if (beneficiario.getFechaNacimiento() == null) {
				redirect.addFlashAttribute("error", "Fecha Nacimiento Invalido Debe Ser dd/mm/yyyy");

				return "redirect:/beneficiarios/crear/" + idAfiliado;

			}
			if (beneficiario.getRfc() == null || beneficiario.getRfc().equals("")) {
				LocalDate fechaNacimiento = beneficiario.getFechaNacimiento().toInstant().atZone(ZoneId.systemDefault())
						.toLocalDate();

				rfc = new Rfc.Builder().name(beneficiario.getNombre()).firstLastName(beneficiario.getApellidoPaterno())
						.secondLastName(beneficiario.getApellidoMaterno()).birthday(fechaNacimiento.getDayOfMonth(),
								fechaNacimiento.getMonthValue(), fechaNacimiento.getYear())
						.build();

				beneficiario.setRfc(rfc.toString());

				System.out.println(rfc.toString());

			}
			saldoAcumulado=Afiliado.getSaldoAcumulado();
			saldoAcumulado += servicio.getCostoBeneficiario() + servicio.getInscripcionBeneficiario();
        	Afiliado.setSaldoAcumulado(saldoAcumulado);
        	Afiliado.setSaldoCorte(saldoAcumulado);
	
			beneficiario.setEstatus(2);
			beneficiario.setServicio(Afiliado.getServicio());
			beneficiario.setIsBeneficiario(true);
			beneficiario.setClave(generarClave.getClaveAfiliado(clave));
			beneficiario.setFechaAlta(fechaAlta);
			afiliadoService.save(Afiliado);
			afiliadoService.save(beneficiario);
			guardarRelAfiliadoBeneficiario(beneficiario, idAfiliado);
			status.setComplete();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error al momento de ejecutar el proceso: " + e);
			redirect.addFlashAttribute("error", "Rfc Invalido");
			return "redirect:/beneficiarios/crear/" + idAfiliado;
		}

		redirect.addFlashAttribute("success", "Beneficiario Creado con Exito");
		// return "redirect:/beneficiarios/crear/" + idAfiliado;
		return "redirect:/afiliados/bienvenido/" + idAfiliado;
	}

	@RequestMapping(value = "/guardar", method = RequestMethod.POST, params = "action=saveCrear")
	public String guardarCrear(Afiliado beneficiario, BindingResult result,
			Model model, RedirectAttributes redirect, SessionStatus status) {

		

		Date fechaAlta = new Date();
		Afiliado afiliado = afiliadoService.findById(idAfiliado);
		Rfc rfc = null;
		Servicio servicio = servicioService.findById(afiliado.getServicio().getId());
        Double saldoAcumulado=0.0;
		try {
			if (beneficiario.getFechaNacimiento() == null) {
				redirect.addFlashAttribute("error", "Fecha Nacimiento Invalido Debe Ser dd/mm/yyyy");

				return "redirect:/beneficiarios/crear/" + idAfiliado;

			}
			if (beneficiario.getRfc() == null || beneficiario.getRfc().equals("")) {
				LocalDate fechaNacimiento = beneficiario.getFechaNacimiento().toInstant().atZone(ZoneId.systemDefault())
						.toLocalDate();

				rfc = new Rfc.Builder().name(beneficiario.getNombre()).firstLastName(beneficiario.getApellidoPaterno())
						.secondLastName(beneficiario.getApellidoMaterno()).birthday(fechaNacimiento.getDayOfMonth(),
								fechaNacimiento.getMonthValue(), fechaNacimiento.getYear())
						.build();

				beneficiario.setRfc(rfc.toString());

				System.out.println(rfc.toString());

			}
			saldoAcumulado=afiliado.getSaldoAcumulado();
			saldoAcumulado += servicio.getCostoBeneficiario() + servicio.getInscripcionBeneficiario();
        	afiliado.setSaldoAcumulado(saldoAcumulado);
        	afiliado.setSaldoCorte(saldoAcumulado);
			
			beneficiario.setEstatus(3);
			beneficiario.setServicio(afiliado.getServicio());
			beneficiario.setIsBeneficiario(true);
			beneficiario.setClave(generarClave.getClaveAfiliado(clave));
			beneficiario.setFechaAlta(fechaAlta);
			afiliadoService.save(afiliado);
			afiliadoService.save(beneficiario);
			guardarRelAfiliadoBeneficiario(beneficiario, idAfiliado);
			status.setComplete();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error al momento de ejecutar el proceso: " + e);
			redirect.addFlashAttribute("error", "Rfc Invalido");
			return "redirect:/beneficiarios/crear/" + idAfiliado;
		}

		redirect.addFlashAttribute("success", "Beneficiario Creado con Exito");
		return "redirect:/beneficiarios/crear/" + idAfiliado;
		// return "redirect:/afiliados/bienvenido/"+idAfiliado;
	}

	public void guardarRelAfiliadoBeneficiario(Afiliado beneficiario, Long id) {
		afiliadoService.insertBeneficiarioUsingJpa(beneficiario, id);
	}
}
