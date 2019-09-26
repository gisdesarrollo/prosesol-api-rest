function generar() {

    var focusSet = false;
    var validation = true;

    var nombre = $("#nombre").val();
    var apellidoPaterno = $("#apellidoPaterno").val();
    var apellidoMaterno = $("#apellidoMaterno").val();
    var fechaNacimiento = $("#fechaNacimiento").val();

    if(!$("#nombre").val()){
        if($("#nombre").parent().next(".validation").length == 0){
            $("#nombre").parent().after("<div class='validation' style='color:red;margin-bottom: 20px;'>Campo obligatorio</div>")
        }
        $("#nombre").focus();
        focusSet = true;
        validation = false;
    }else{
        $("#nombre").parent().next(".validation").remove();
    }
    if(!$("#apellidoPaterno").val()){
        if($("#apellidoPaterno").parent().next(".validation").length == 0){
            $("#apellidoPaterno").parent().after("<div class='validation' style='color:red;margin-bottom: 20px;'>Campo obligatorio</div>")
        }
        $("#apellidoPaterno").focus();
        focusSet = true;
        validation = false;
    }else{
        $("#apellidoPaterno").parent().next(".validation").remove();
    }
    if(!$("#apellidoMaterno").val()){
        if($("#apellidoMaterno").parent().next(".validation").length == 0){
            $("#apellidoMaterno").parent().after("<div class='validation' style='color:red;margin-bottom: 20px;'>Campo obligatorio</div>")
        }
        $("#apellidoMaterno").focus();
        focusSet = true;
        validation = false;
    }else{
        $("#apellidoMaterno").parent().next(".validation").remove();
    }
     if(!$("#fechaNacimiento").val()){
         if($("#fechaNacimiento").parent().next(".validation").length == 0){
             $("#fechaNacimiento").parent().after("<div class='validation' style='color:red;margin-bottom: 20px;'>Campo obligatorio</div>")
         }
         $("#fechaNacimiento").focus();
         focusSet = true;
         validation = false;
     }else{
        $("#fechaNacimiento").parent().next(".validation").remove();
     }

     if(validation == true){
        $.ajax({
                type : "GET",
                url : "/afiliados/generarRfc",
                data : {nombre : nombre,
                        apellidoPaterno : apellidoPaterno,
                        apellidoMaterno : apellidoMaterno,
                        fechaNacimiento : fechaNacimiento},
                success : function(data){
                    $("#rfc").val(data);
                }
            });
     }
}
