window.onload = function() {
	var host = location.host;
	var hostProsesol = "api.prosesol.org";
	var hostAssismex = "api.assismex.com";
	var hostLocal = "localhost:8080";
	
	if (host == hostProsesol) {
		document.getElementById('hostP').style.display = 'block';
	} 
	if (host == hostAssismex) {
		document.getElementById('hostA').style.display = 'block';
	} 
}
