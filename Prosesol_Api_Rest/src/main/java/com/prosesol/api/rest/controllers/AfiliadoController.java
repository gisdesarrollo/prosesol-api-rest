package com.prosesol.api.rest.controllers;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.josketres.rfcfacil.Rfc;
import com.prosesol.api.rest.models.entity.Afiliado;
import com.prosesol.api.rest.models.entity.Servicio;
import com.prosesol.api.rest.services.IAfiliadoService;
import com.prosesol.api.rest.services.IServicioService;
import com.prosesol.api.rest.utils.Paises;

@Controller
@RequestMapping("/afiliados")
public class AfiliadoController {

	protected static final Log logger = LogFactory.getLog(AfiliadoController.class);

	@Value("${app.clave}")
	private String clave;

	@Autowired
	private IAfiliadoService afiliadoService;

	@Autowired
	private IServicioService servicioService;

	@RequestMapping(value = "/generarRfc", method = RequestMethod.POST)
	public String generarRfc(Afiliado afiliado, BindingResult result, RedirectAttributes redirect) {

		Rfc rfc = null;
		String mensajeFlash = null;
		System.out.println(afiliado.getFechaNacimiento());

		try {
			if (afiliado.getFechaNacimiento() == null) {

				mensajeFlash = "Error al generar fecha debe ser dd/mm/yyyy";
				logger.info(mensajeFlash);
				redirect.addFlashAttribute("error", mensajeFlash);
				return "redirect:/afiliados/crear";
			} else {

				// LocalDate fecha = LocalDate.parse(afiliado.getFechaNacimiento());

				LocalDate fecha = afiliado.getFechaNacimiento().toInstant().atZone(ZoneId.systemDefault())
						.toLocalDate();

				rfc = new Rfc.Builder().name(afiliado.getNombre()).firstLastName(afiliado.getApellidoPaterno())
						.secondLastName(afiliado.getApellidoMaterno())
						.birthday(fecha.getDayOfMonth(), fecha.getMonthValue(), fecha.getYear()).build();

				System.out.println("rfc creado:" + rfc.toString());
				redirect.addFlashAttribute("rfc", rfc);

				// afiliado.setNombre(datos[0]);
				System.out.println(afiliado.getNombre());
				// System.out.println(date1);
				redirect.addFlashAttribute("nombre", afiliado.getNombre());
				redirect.addFlashAttribute("apellidoPaterno", afiliado.getApellidoPaterno());
				redirect.addFlashAttribute("apellidoMaterno", afiliado.getApellidoMaterno());
				redirect.addFlashAttribute("fechaNacimiento", fecha);
				redirect.addFlashAttribute("lugarNacimiento", afiliado.getLugarNacimiento());
				redirect.addFlashAttribute("estadoCivil", afiliado.getEstadoCivil());
				redirect.addFlashAttribute("ocupacion", afiliado.getOcupacion());
				redirect.addFlashAttribute("sexo", afiliado.getSexo());
				redirect.addFlashAttribute("pais", afiliado.getPais());
				redirect.addFlashAttribute("curp", afiliado.getCurp());
				redirect.addFlashAttribute("nss", afiliado.getNss());

			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error al momento de ejecutar el proceso: " + e);

		}

		return "redirect:/afiliados/crear";
	}

	@RequestMapping(value = "/crear")
	public String crear(Map<String, Object> model) {
		Afiliado afiliado = new Afiliado();

		model.put("afiliado", afiliado);

		return "afiliados/crear";
	}

	@RequestMapping(value = "/guardar", method = RequestMethod.POST)
	public String guardar(@ModelAttribute(name = "clave") String clave, @Valid Afiliado afiliado, BindingResult result,
			Model model, RedirectAttributes redirect, SessionStatus status) {

		System.out.println(clave);
		String mensajeFlash = null;
		Date date = new Date();

		try {
			if (result.hasErrors()) {
				model.addAttribute("rfc", afiliado.getRfc());
				model.addAttribute("nombre", afiliado.getNombre());
				model.addAttribute("apellidoPaterno", afiliado.getApellidoPaterno());
				model.addAttribute("apellidoMaterno", afiliado.getApellidoMaterno());
				redirect.addFlashAttribute("fechaNacimiento", afiliado.getFechaNacimiento());
				redirect.addFlashAttribute("lugarNacimiento", afiliado.getLugarNacimiento());
				redirect.addFlashAttribute("estadoCivil", afiliado.getEstadoCivil());
				redirect.addFlashAttribute("ocupacion", afiliado.getOcupacion());
				redirect.addFlashAttribute("sexo", afiliado.getSexo());
				redirect.addFlashAttribute("pais", afiliado.getPais());
				redirect.addFlashAttribute("curp", afiliado.getCurp());
				redirect.addFlashAttribute("nss", afiliado.getNss());
				return "/afiliados/crear";

			}
			if (afiliado.getId() != null) {

				if (afiliado.getIsBeneficiario().equals(true)) {
					afiliado.setIsBeneficiario(true);
				} else {
					afiliado.setIsBeneficiario(false);
				}

				mensajeFlash = "Registro editado con éxito";

			} else {
				/*
				 * if (afiliado.getRfc() == null || afiliado.getRfc().equals("")) { LocalDate
				 * fechaNacimiento =
				 * afiliado.getFechaNacimiento().toInstant().atZone(ZoneId.systemDefault())
				 * .toLocalDate();
				 * 
				 * rfc = new Rfc.Builder().name(afiliado.getNombre()).firstLastName(afiliado.
				 * getApellidoPaterno())
				 * .secondLastName(afiliado.getApellidoMaterno()).birthday(fechaNacimiento.
				 * getDayOfMonth(), fechaNacimiento.getMonthValue(), fechaNacimiento.getYear())
				 * .build();
				 * 
				 * afiliado.setRfc(rfc.toString());
				 * 
				 * System.out.println(rfc.toString()); }
				 */
				afiliado.setIsBeneficiario(false);
				afiliado.setFechaAlta(date);
				afiliado.setClave(clave);
				mensajeFlash = "Registro creado con éxito";
			}
			afiliado.setEstatus(1);
			logger.info(mensajeFlash);
			afiliadoService.save(afiliado);
			status.setComplete();

		} catch (DataIntegrityViolationException e) {
			e.printStackTrace();
			logger.error("Error al momento de ejecutar el proceso: " + e);
			redirect.addFlashAttribute("error", "El RFC ya existe en la base de datos ");

			return "redirect:/afiliados/crear";
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error al momento de ejecutar el proceso: " + e);

			redirect.addFlashAttribute("error", "Ocurrió un error al momento de insertar el Afiliado");

			return "redirect:/afiliados/crear";
		}

		mensajeFlash = "id del afiliado creado es: " + afiliado.getId();
		logger.info(mensajeFlash);
		redirect.addFlashAttribute("success", "afiliado creado con exito");
		return "redirect:/beneficiarios/crear/" + afiliado.getId();
	}

	@RequestMapping(value = "/bienvenido/{id}")
	public String mostrar(@PathVariable("id") Long id, Model model) {
		try {
			Afiliado afiliado = afiliadoService.findById(id);

			model.addAttribute("afiliado", afiliado);
		} catch (Exception e) {
			System.out.println("error al momento de buscar en afiliado" + e);
		}
		return "afiliados/bienvenido";
	}

	/**
	 * Método para mostrar los estados Dentro del list box de crear afiliados
	 * 
	 * @param(name = "estados")
	 */

	@ModelAttribute("estados")
	public List<String> getAllEstados() {
		return afiliadoService.getAllEstados();
	}

	/**
	 * Método para mostrar los países Dentro del list box de crear afiliados
	 * 
	 * @param(name = "paises")
	 */

	@ModelAttribute("paises")
	public List<Paises> getAllPaises() {
		return afiliadoService.getAllPaises();
	}

	/**
	 * Método para mostrar los servicios Dentro del list box de crear afiliados
	 * 
	 * @param(name = "servicios")
	 */

	@ModelAttribute("servicios")
	public List<Servicio> getAllServicios() {
		return servicioService.findAll();
	}

	/**
	 * Método para asignar una clave para el Afiliado
	 * 
	 * @param(name = "clave")
	 */

	@ModelAttribute("clave")
	public String getClaveAfiliado() {

		String claveAfiliado = "PR-";

		for (int i = 0; i < 10; i++) {
			claveAfiliado += (clave.charAt((int) (Math.random() * clave.length())));
		}

		return claveAfiliado;
	}

}
