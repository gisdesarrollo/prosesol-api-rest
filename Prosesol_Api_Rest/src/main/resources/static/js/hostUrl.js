window.onload = function() {
	var host = location.host;
	var hostProsesol = "www.api.prosesol.org";
	var hostAssismex = "www.api.assismex.org";
	//var hostLocal = "localhost:8080";
	
	if (host == hostProsesol) {
		document.getElementById('hostP').style.display = 'block';
	} 
	if (host == hostAssismex) {
		document.getElementById('hostA').style.display = 'block';
	} 
}
