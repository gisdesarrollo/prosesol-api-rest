function showFormulario()
{
	var resultado = document.getElementById('selector').value;
	if(resultado==5){
		document.getElementById('activeform').style.display = 'block';
	}else{
		document.getElementById('activeform').style.display = 'none';
		$('#nombPariente').val('');
		$('#apellPariente').val('');
		$('#cargoPariente').val('');
		$('#parentPariente').val('');
		$('#periodPariente').val('');
	}
}