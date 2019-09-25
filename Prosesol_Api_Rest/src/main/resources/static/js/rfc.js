function generar() {
	var frm = document.formu;
	var nombre = document.formu.nombre.value;
	var apellidoPaterno = document.formu.apellidoPaterno.value;
	var apellidoMaterno = document.formu.apellidoMaterno.value;
	var fechaNacimiento = document.formu.fechaNacimiento.value;

	if (nombre == "" && apellidoPaterno == "" && apellidoMaterno == ""
			&& fechaNacimiento == "") {
		alert('Debes Escribir Contenido En Los Campos: \n 1.Nombre\n 2.Apellidos \n 3 Fecha Nacimiento');
		frm.nombre.focus()

	} else if (nombre == "") {
		alert('El campo nombre no debe ir vacío');
		frm.nombre.focus()

	} else if (apellidoPaterno == "") {
		alert('El campo apellido paterno no debe ir vacío');
		frm.apellidoPaterno.focus()

	} else if (apellidoMaterno == "") {
		alert('El campo apellido Materno no debe ir vacío');
		frm.apellidoMaterno.focus()

	} else if (fechaNacimiento == "") {
		alert('La fecha de nacimiento no debe ir vacío');
		frm.fechaNacimiento.focus()

	} else {
		frm.action = "/afiliados/generarRfc";
		frm.submit();

		
	}

}

function guardar() {
	document.forms.formu.action = '/afiliados/guardar';
	document.forms.formu.submit();
}
