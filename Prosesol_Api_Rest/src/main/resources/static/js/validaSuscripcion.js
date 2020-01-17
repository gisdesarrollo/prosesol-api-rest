$('#suscripcion').click(function() {
	var rfc = $("#rfc").val();
	if (this.checked) {
		$.ajax({
			type : "GET",
			url : "/pagos/validaSuscripcion",
			data : {
				rfc : rfc
			},
			success : function(data) {
				if (data == true) {
					alert("Usted ya se encuentra domiciliado");
					$('#suscripcion').prop('checked', false);
				}
				
			}
		});
	}

});