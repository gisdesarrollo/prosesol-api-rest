$(document).ready(function() {
	$('#fechaNacimiento').datepicker({
		format : 'dd/mm/yyyy',
		language : "es-ES",
		autoHide : true

	});

	$('#nombre').on('input', function(e) {

		if (!/^[ a-záéíóúüñ]*$/i.test(this.value)) {
			this.value = this.value.replace(/[^ a-záéíóúüñ]+/ig, "");
		}
	});
	$('#apellidoPaterno').on('input', function(e) {
		if (!/^[ a-záéíóúüñ]*$/i.test(this.value)) {
			this.value = this.value.replace(/[^ a-záéíóúüñ]+/ig, "");
		}
	});
	$('#apellidoMaterno').on('input', function(e) {
		if (!/^[ a-záéíóúüñ]*$/i.test(this.value)) {
			this.value = this.value.replace(/[^ a-záéíóúüñ]+/ig, "");
		}
	});

});

function validaDatosAfiliado() {

	var focusSet = false;
	var validation = true;
	var frm = document.formu;
	var nombre = $("#nombre").val();
	var apellidoPaterno = $("#apellidoPaterno").val();
	var apellidoMaterno = $("#apellidoMaterno").val();
	var fechaNacimiento = $("#fechaNacimiento").val();
	var fechaNacimiento = $("#rfc").val();

	if (!$("#nombre").val()) {
		if ($("#nombre").parent().next(".validation").length == 0) {
			$("#nombreVacio").delay(100).fadeIn("slow");
		}
		$("#nombre").focus();
		focusSet = true;
		validation = false;
	} else {
		$("#nombreVacio").fadeOut("slow");

	}
	if (!$("#apellidoPaterno").val()) {
		if ($("#apellidoPaterno").parent().next(".validation").length == 0) {
			$("#apellPaterVacio").delay(100).fadeIn("slow");
		}
		$("#apellidoPaterno").focus();
		focusSet = true;
		validation = false;
	} else {
		$("#apellPaterVacio").fadeOut("slow");

	}
	if (!$("#apellidoMaterno").val()) {
		if ($("#apellidoMaterno").parent().next(".validation").length == 0) {
			$("#apellMaterVacio").delay(100).fadeIn("slow");
		}
		$("#apellidoMaterno").focus();
		focusSet = true;
		validation = false;
	} else {
		$("#apellMaterVacio").fadeOut("slow");

	}
	if (!$("#fechaNacimiento").val()) {
		if ($("#fechaNacimiento").parent().next(".validation").length == 0) {
			$("#fechaNacVacio").delay(100).fadeIn("slow");
		}
		$("#fechaNacimiento").focus();
		focusSet = true;
		validation = false;
	} else {
		$("#fechaNacVacio").fadeOut("slow");

	}
	if (!$("#rfc").val()) {
		if ($("#rfc").parent().next(".validation").length == 0) {
			$("#rfcVacio").delay(100).fadeIn("slow");
		}
		$("#rfc").focus();
		focusSet = true;
		validation = false;
	} else {
		$("#rfc").fadeOut("slow");

	}
	if (validation == true) {
		 $("#disabledButton").prop( "disabled", true);
		frm.action = "/afiliados/crear";
		frm.submit();
	}
}
