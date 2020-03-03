$('#suscripcion').click(function() {
	var $afiliado = $("#payment-form");
	if (this.checked) {
		$.ajax({
			type : "GET",
			url : "/pagos/validaSuscripcion",
			data : $afiliado.serialize(),
			success : function(response) {
				if (response == true) {
					alert("Usted ya se encuentra domiciliado");
					$('#suscripcion').prop('checked', false);
				}
				
			}
		});
	}

});