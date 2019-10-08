package com.prosesol.api.rest.controllers;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
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

import com.josketres.rfcfacil.Rfc;
import com.prosesol.api.rest.models.entity.Afiliado;
import com.prosesol.api.rest.services.IAfiliadoService;
import com.prosesol.api.rest.services.IServicioService;

@Controller
//@SessionAttributes("afiliado")
@RequestMapping("/beneficiarios")
public class BeneficiarioController {

	protected static final Log logger = LogFactory.getLog(BeneficiarioController.class);

	@Autowired
	private IAfiliadoService afiliadoService;

	@Autowired
	private IServicioService servicioService;

	private static Long idAfiliado;

	@RequestMapping(value = "/crear/{id}")
	public String crear(@PathVariable("id") Long id, Map<String, Object> model, RedirectAttributes redirect) {

		idAfiliado = id;
		System.out.println("la id afiliado es: " + idAfiliado);

		Afiliado afiliado = new Afiliado();
		model.put("afiliado", afiliado);
		
		afiliado = afiliadoService.findById(id);
		if (afiliado == null) {
			redirect.addFlashAttribute("error", "Afiliado no registrado");
			return "redirect:/afiliados/servicio";
		}
		// System.out.println(afiliado.getServicio());
		
		model.put("id", idAfiliado);

		return "beneficiarios/crear";
	}

	@RequestMapping(value = "/guardar", method = RequestMethod.POST, params = "action=save")
	public String guardar(@ModelAttribute("clave") String clave, Afiliado afiliado, BindingResult result, Model model,
			RedirectAttributes redirect, SessionStatus status) {

		System.out.println("id: " + idAfiliado);

		Date fechaAlta = new Date();
		Afiliado buscoAfiliadoServicio = afiliadoService.findById(idAfiliado);
		Rfc rfc = null;
		try {
			if (afiliado.getFechaNacimiento() == null) {
				redirect.addFlashAttribute("error", "Fecha Nacimiento Invalido Debe Ser dd/mm/yyyy");

				return "redirect:/beneficiarios/crear/" + idAfiliado;

			}
			if (afiliado.getRfc() == null || afiliado.getRfc().equals("")) {
				LocalDate fechaNacimiento = afiliado.getFechaNacimiento().toInstant().atZone(ZoneId.systemDefault())
						.toLocalDate();

				rfc = new Rfc.Builder().name(afiliado.getNombre()).firstLastName(afiliado.getApellidoPaterno())
						.secondLastName(afiliado.getApellidoMaterno()).birthday(fechaNacimiento.getDayOfMonth(),
								fechaNacimiento.getMonthValue(), fechaNacimiento.getYear())
						.build();

				afiliado.setRfc(rfc.toString());

				System.out.println(rfc.toString());

			}

			buscoAfiliadoServicio.getServicio();
			afiliado.setEstatus(2);
			afiliado.setServicio(buscoAfiliadoServicio.getServicio());
			afiliado.setIsBeneficiario(true);
			afiliado.setClave(clave);
			afiliado.setFechaAlta(fechaAlta);

			afiliadoService.save(afiliado);
			guardarRelAfiliadoBeneficiario(afiliado, idAfiliado);
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
	public String guardarCrear(@ModelAttribute("clave") String clave, Afiliado afiliado, BindingResult result,
			Model model, RedirectAttributes redirect, SessionStatus status) {

		System.out.println("id: " + idAfiliado);

		Date fechaAlta = new Date();
		Afiliado resul = afiliadoService.findById(idAfiliado);
		Rfc rfc = null;
		try {
			if (afiliado.getFechaNacimiento() == null) {
				redirect.addFlashAttribute("error", "Fecha Nacimiento Invalido Debe Ser dd/mm/yyyy");

				return "redirect:/beneficiarios/crear/" + idAfiliado;

			}
			if (afiliado.getRfc() == null || afiliado.getRfc().equals("")) {
				LocalDate fechaNacimiento = afiliado.getFechaNacimiento().toInstant().atZone(ZoneId.systemDefault())
						.toLocalDate();

				rfc = new Rfc.Builder().name(afiliado.getNombre()).firstLastName(afiliado.getApellidoPaterno())
						.secondLastName(afiliado.getApellidoMaterno()).birthday(fechaNacimiento.getDayOfMonth(),
								fechaNacimiento.getMonthValue(), fechaNacimiento.getYear())
						.build();

				afiliado.setRfc(rfc.toString());

				System.out.println(rfc.toString());

			}

			resul.getServicio();
			afiliado.setEstatus(3);
			afiliado.setServicio(resul.getServicio());
			afiliado.setIsBeneficiario(true);
			afiliado.setClave(clave);
			afiliado.setFechaAlta(fechaAlta);

			afiliadoService.save(afiliado);
			guardarRelAfiliadoBeneficiario(afiliado, idAfiliado);
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

	@ModelAttribute("clave")
	public String getClave() {
		return afiliadoService.getAllClave();
	}
	/*
	 * @ModelAttribute("clave") public String getClaveAfiliado() {
	 * 
	 * String clave = "0123456789"; String claveAfiliado = "PR-";
	 * 
	 * for (int i = 0; i < 10; i++) { claveAfiliado += (clave.charAt((int)
	 * (Math.random() * clave.length()))); }
	 * 
	 * return claveAfiliado; }
	 */
}
