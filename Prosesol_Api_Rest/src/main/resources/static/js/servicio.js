function getValServicio(){

    var sel = document.getElementById("servicio").value;
    var urlServicio = "/afiliados/servicio/";

    if(sel == ""){
        alert("Seleccione un servicio");
    }else{
        $.ajax({
            type : 'POST',
            url : urlServicio + sel,
            complete : function(){
                window.location.replace(urlServicio + sel);
            }
        });
    }
}
