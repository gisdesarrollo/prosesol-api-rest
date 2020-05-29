package com.prosesol.api.rest.utils.scheduler;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.prosesol.api.rest.models.entity.custom.PreguntaRespuestaCandidatoCustom;
import com.prosesol.api.rest.services.ICandidatoService;
import com.prosesol.api.rest.services.IRelPreguntaRespuestaCandidatoService;

@Configuration
@EnableScheduling
public class DeleteCandidatoScheduler {

	protected static final Log LOG = LogFactory.getLog(DeleteCandidatoScheduler.class);

	@Autowired
	private IRelPreguntaRespuestaCandidatoService relPreguntaRespuestaCandidatoService;

	@Autowired
	private ICandidatoService candidatoService;

	@Scheduled(cron = "0 0 * * * *", zone = "America/Mexico_City")
	public void DeleteCandidatoByTime() {

		String formatoFecha = "yyyy-dd-MM HH:mm";
		SimpleDateFormat SDF = new SimpleDateFormat(formatoFecha);
		DateTimeFormatter DTF = DateTimeFormatter.ofPattern(formatoFecha);
		String fechaHoy = SDF.format(new Date());
		String fechaCandidato;

		List<PreguntaRespuestaCandidatoCustom> listaFechas = relPreguntaRespuestaCandidatoService
				.getDatetimeByCandidato();
		for (PreguntaRespuestaCandidatoCustom lista : listaFechas) {
			
			fechaCandidato = SDF.format(lista.getFecha());
			LocalDateTime fechaC = LocalDateTime.parse(fechaCandidato, DTF);
			LocalDateTime fechaH = LocalDateTime.parse(fechaHoy, DTF);

			//Asigna tiempo de 24 horas para expirar
			fechaC = fechaC.plusHours(24);
			// valida si el tiempo a expirado
			if (fechaC.isBefore(fechaH)) {
				candidatoService.deleteById(lista.getIdAfiliado());
				LOG.info("El candidato se eliminó correctamente por tiempo de expiración");
			}
			if (fechaC.isEqual(fechaH)) {
				candidatoService.deleteById(lista.getIdAfiliado());
				LOG.info("El candidato se eliminó correctamente por tiempo de expiración");
			}

		}
	}
}
