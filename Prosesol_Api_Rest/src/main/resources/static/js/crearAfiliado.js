$(document).ready(function() {
	$('#fechaNacimiento').datepicker({
		format : 'dd/mm/yyyy',
		language : "es-ES",
		autoHide : true

	});
	
	$('#nombre').on('input', function(e) {
		if (!/^[ A-ZÁÉÍÓÚÜÑ]*$/.test(this.value)) {
				this.value = this.value.toUpperCase();
				this.value = this.value.replace(/[^ A-ZÁÉÍÓÚÜÑ]+$/, "");
			}
		});
		$('#apellidoPaterno').on('input', function(e) {
			if (!/^[ A-ZÁÉÍÓÚÜÑ]*$/.test(this.value)) {
				this.value = this.value.toUpperCase();
				this.value = this.value.replace(/[^ A-ZÁÉÍÓÚÜÑ]+$/, "");
			}
		});
		$('#apellidoMaterno').on('input', function(e) {
			if (!/^[ A-ZÁÉÍÓÚÜÑ]*$/.test(this.value)) {
				this.value = this.value.toUpperCase();
				this.value = this.value.replace(/[^ A-ZÁÉÍÓÚÜÑ]+$/, "");
			}
		});
		$('#lugarNacimiento').on('input', function(e) {
			if (!/^[ A-ZÁÉÍÓÚÜÑ]*$/.test(this.value)) {
				this.value = this.value.toUpperCase();
				this.value = this.value.replace(/[^ A-ZÁÉÍÓÚÜÑ]+$/, "");
			}
		});
		$('#ocupacion').on('input', function(e) {
			if (!/^[ A-ZÁÉÍÓÚÜÑ]*$/.test(this.value)) {
				this.value = this.value.toUpperCase();
				this.value = this.value.replace(/[^ A-ZÁÉÍÓÚÜÑ]+$/, "");
			}
		});
		$('#lugarNacimiento').on('input', function(e) {
			if (!/^[ A-ZÁÉÍÓÚÜÑ]*$/.test(this.value)) {
				this.value = this.value.toUpperCase();
				this.value = this.value.replace(/[^ A-ZÁÉÍÓÚÜÑ]+$/, "");
			}
		});
		$('#curp').on('input', function(e) {
			if (!/^[ A-Z0-9]*$/.test(this.value)) {
				this.value = this.value.toUpperCase();
				this.value = this.value.replace(/[^ A-Z0-9]+$/, "");
			}
		});
		$('#nss').on('input', function(e) {
			if (!/^[ 0-9]*$/.test(this.value)) {
				this.value = this.value.replace(/[^ 0-9]+$/, "");
			}
		});
		$('#rfc').on('input', function(e) {
			if (!/^[ A-Z0-9]*$/.test(this.value)) {
				this.value = this.value.toUpperCase();
				this.value = this.value.replace(/[^ A-Z0-9]+$/, "");
			}
		});
		$('#telefonoFijo').on('input', function(e) {
			if (!/^[ 0-9]*$/.test(this.value)) {
				this.value = this.value.replace(/[^ 0-9]+$/, "");
			}
		});
		$('#telefonoMovil').on('input', function(e) {
			if (!/^[ 0-9]*$/.test(this.value)) {
				this.value = this.value.replace(/[^ 0-9]+$/, "");
			}
		});
		$('#direccion').on('input', function(e) {
			if (!/^[ A-Z0-9ÁÉÍÓÚÜÑ]*$/.test(this.value)) {
				this.value = this.value.toUpperCase();
				this.value = this.value.replace(/[^ A-Z0-9ÁÉÍÓÚÜÑ]+$/, "");
			}
		});
		$('#municipio').on('input', function(e) {
			if (!/^[ A-ZÁÉÍÓÚÜÑ]*$/.test(this.value)) {
				this.value = this.value.toUpperCase();
				this.value = this.value.replace(/[^ A-ZÁÉÍÓÚÜÑ]+$/, "");
			}
		});
		$('#codigoPostal').on('input', function(e) {
			if (!/^[ 0-9]*$/.test(this.value)) {
				this.value = this.value.replace(/[^ 0-9]+$/, "");
			}
		});


	
});

function validaDatosAfiliado(e) {

   	var focusSet = false;
	var validation = true;
	var frm = document.formu;
	var nombre = $("#nombre").val();
	var apellidoPaterno = $("#apellidoPaterno").val();
	var apellidoMaterno = $("#apellidoMaterno").val();
	var fechaNacimiento = $("#fechaNacimiento").val();

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
		$("#rfcVacio").fadeOut("slow");

	}

	// Se validan campos especiales para el envío de la información para la membresía Covid-19
	if(e == 1){
	    var accordionOption = $('#selector').val();

	    if (!$("#ocupacion").val()) {
            if ($("#ocupacion").parent().next(".validation").length == 0) {
                $("#ocupacionVacio").delay(100).fadeIn("slow");
            }
            $("#ocupacion").focus();
            focusSet = true;
            validation = false;
        } else {
            $("#ocupacionVacio").fadeOut("slow");

        }
        if (!$("#telefonoMovil").val()) {
            if ($("#telefonoMovil").parent().next(".validation").length == 0) {
                $("#movilVacio").delay(100).fadeIn("slow");
            }
            $("#telefonoMovil").focus();
            focusSet = true;
            validation = false;
        } else {
            $("#movilVacio").fadeOut("slow");

        }
        if (!$("#email").val()) {
            if ($("#email").parent().next(".validation").length == 0) {
                $("#emailVacio").delay(100).fadeIn("slow");
            }
            $("#email").focus();
            focusSet = true;
            validation = false;
        } else {
            $("#emailVacio").fadeOut("slow");

        }
        if (!$("#codigoPostal").val()) {
            if ($("#codigoPostal").parent().next(".validation").length == 0) {
                $("#zipVacio").delay(100).fadeIn("slow");
            }
            $("#codigoPostal").focus();
            focusSet = true;
            validation = false;
        } else {
            $("#zipVacio").fadeOut("slow");

        }
        if (!$("#calle").val()) {
            if ($("#calle").parent().next(".validation").length == 0) {
                $("#calleVacio").delay(100).fadeIn("slow");
            }
            $("#calle").focus();
            focusSet = true;
            validation = false;
        } else {
            $("#calleVacio").fadeOut("slow");

        }
        if (!$("#noExterior").val()) {
            if ($("#noExterior").parent().next(".validation").length == 0) {
                $("#noExteriorVacio").delay(100).fadeIn("slow");
            }
            $("#noExterior").focus();
            focusSet = true;
            validation = false;
        } else {
            $("#noExteriorVacio").fadeOut("slow");

        }

        if(accordionOption == 1){
            if (!$("#nombPariente").val()) {
                if ($("#nombPariente").parent().next(".validation").length == 0) {
                    $("#nombParienteVacio").delay(100).fadeIn("slow");
                }
                $("#nombPariente").focus();
                focusSet = true;
                validation = false;
            } else {
                $("#nombParienteVacio").fadeOut("slow");

            }
            if (!$("#apellPariente").val()) {
                if ($("#apellPariente").parent().next(".validation").length == 0) {
                    $("#apellParienteVacio").delay(100).fadeIn("slow");
                }
                $("#apellPariente").focus();
                focusSet = true;
                validation = false;
            } else {
                $("#apellParienteVacio").fadeOut("slow");

            }
            if (!$("#cargoPariente").val()) {
                if ($("#cargoPariente").parent().next(".validation").length == 0) {
                    $("#cargoParienteVacio").delay(100).fadeIn("slow");
                }
                $("#cargoPariente").focus();
                focusSet = true;
                validation = false;
            } else {
                $("#cargoPcargoParienteVacioariente").fadeOut("slow");

            }
            if (!$("#parentPariente").val()) {
                if ($("#parentPariente").parent().next(".validation").length == 0) {
                    $("#parentParienteVacio").delay(100).fadeIn("slow");
                }
                $("#parentPariente").focus();
                focusSet = true;
                validation = false;
            } else {
                $("#parentParienteVacio").fadeOut("slow");

            }
        }else if(accordionOption == ""){
            $("#selectorVacio").delay(100).fadeIn("slow");
            validation = false;
        }else if(accordionOption != 1){
            $("#selectorVacio").fadeOut("slow");
        }
	}

	if (validation == true) {
		frm.action = "/afiliados/crear";
		frm.submit();
	}
}
