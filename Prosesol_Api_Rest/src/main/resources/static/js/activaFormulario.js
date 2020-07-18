$("#si").click(function(event){
	document.getElementById("for").style.display = "block";
	 
	});
$("#no").click(function(event){
	document.getElementById("for").style.display = "none";
	$('#nombPariente').val('');
	$('#apellPariente').val('');
	$('#cargoPariente').val('');
	$('#parentPariente').val('');
	$('#periodPariente').val('');
	});