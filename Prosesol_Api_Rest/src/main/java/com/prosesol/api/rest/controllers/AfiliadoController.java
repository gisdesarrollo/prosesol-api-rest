package com.prosesol.api.rest.controllers;

import com.prosesol.api.rest.models.entity.Afiliado;
import com.prosesol.api.rest.models.entity.Servicio;
import com.prosesol.api.rest.services.IAfiliadoService;
import com.prosesol.api.rest.services.IServicioService;
import com.prosesol.api.rest.utils.GenerarClave;
import com.prosesol.api.rest.utils.Paises;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.validator.constraints.Length;
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

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

	@Autowired
	private GenerarClave generarClave;

	@RequestMapping(value = "/servicio")
	public String seleccionarServicio(Map<String, Object> model) {
		Afiliado afiliado = new Afiliado();

		model.put("afiliado", afiliado);

		return "afiliados/servicio";
	}

	@RequestMapping(value = "/crear")
	public String crear(@RequestParam(value = "servicio") Integer servicio, Map<String, Object> model,
			RedirectAttributes redirect) {

		Afiliado afiliado = new Afiliado();

		if (servicio == null) {
			redirect.addFlashAttribute("error", "Debes seleccionar un servicio");
			return "redirect:/afiliados/servicio";
		}
		model.put("servicios", servicio);
		model.put("afiliado", afiliado);

		return "afiliados/crear";
	}

	@RequestMapping(value = "/crear", method = RequestMethod.POST, params = "action=save")
	public String guardar(@Valid Afiliado afiliado, BindingResult result,
			Model model, RedirectAttributes redirect, SessionStatus status) {

		String mensajeFlash = null;
		Date date = new Date();

		try {
			if (result.hasErrors()) {

				Long serv = afiliado.getServicio().getId();
				if (serv != null) {
					System.out.println(serv);
					model.addAttribute("servicios", serv);
				}
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
				afiliado.setClave(generarClave.getClaveAfiliado(clave));
				mensajeFlash = "Registro creado con éxito";
			}
			afiliado.setEstatus(2);
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
		redirect.addFlashAttribute("success", "Afiliado creado con éxito");
		return "redirect:/beneficiarios/crear/" + afiliado.getId();
	}

	@RequestMapping(value = "/bienvenido/{id}")
	public String mostrar(@PathVariable("id") Long id, Model model) {

		Double d1, d2, d3, d4, costoAfiliado, costoBeneficiario, mensualidad;
		try {
			Afiliado afiliado = afiliadoService.findById(id);
			Long idServicio = afiliado.getServicio().getId();
			Servicio serv = servicioService.findById(idServicio);

			d1 = serv.getCostoTitular();
			d2 = serv.getInscripcionTitular();
			mensualidad = d1;
			costoAfiliado = d1 + d2;

			List<Afiliado> beneficiarios = afiliadoService.getBeneficiarioByIdByIsBeneficiario(id);
			if (beneficiarios != null) {
				d1 = serv.getCostoBeneficiario();
				d2 = serv.getInscripcionBeneficiario();
				costoBeneficiario = d1 + d2;

				model.addAttribute("costoBeneficiario", costoBeneficiario);
				model.addAttribute("beneficiarios", beneficiarios);
				for (Integer x = 0; x < beneficiarios.size(); x++) {
					d3 = serv.getCostoBeneficiario();
					// d4=serv.getInscripcionBeneficiario();
					mensualidad += d3;
				}

			}

			model.addAttribute("costoAfiliado", costoAfiliado);
			model.addAttribute("mensualidad", mensualidad);
			model.addAttribute("id", id);
			model.addAttribute("afiliado", afiliado);

		} catch (Exception e) {
			System.out.println("error al momento de buscar en afiliado" + e);
		}
		return "afiliados/bienvenido";
	}

	@RequestMapping(value = "/guardar/{id}")
	public String guardaSaldoAfiliado(@PathVariable("id") Long id, Model model, SessionStatus status) {

		Afiliado afiliado = new Afiliado();

		Double d1, d2, d3, d4, saldoAcumulado;
		try {
			afiliado = afiliadoService.findById(id);
			Long idServicio = afiliado.getServicio().getId();
			Servicio serv = servicioService.findById(idServicio);

			d1 = serv.getCostoTitular();
			d2 = serv.getInscripcionTitular();
		
			saldoAcumulado = d1 + d2;

			List<Afiliado> beneficiarios = afiliadoService.getBeneficiarioByIdByIsBeneficiario(id);
			if (beneficiarios != null) {
				

				for (Integer x = 0; x < beneficiarios.size(); x++) {
					d3= serv.getCostoBeneficiario();
				    d4=serv.getInscripcionBeneficiario();
					saldoAcumulado += d3+d4;
				}

			}
			
			afiliado.setSaldoAcumulado(saldoAcumulado);
			afiliado.setSaldoCorte(saldoAcumulado);
			afiliadoService.save(afiliado);
			status.setComplete();

		} catch (Exception e) {
			System.out.println("error al momento de insertar Saldo acumulado" + e);
		}

		return "redirect:/prosesol/buscar";
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
}
