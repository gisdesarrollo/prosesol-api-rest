$(document).ready(function() {

    OpenPay.setId('mmlithfv3otobghccu6a');
    OpenPay.setApiKey('pk_3ff10b69ab4e491fb4b46ef8654a1f98');
    OpenPay.setSandboxMode(true);
    //Se genera el id de dispositivo
//    var deviceSessionId = OpenPay.deviceData.setup("payment-form", "deviceIdHiddenFieldName");

    $('#pay-button').on('click', function(event) {
        event.preventDefault();
        if(!$('#name').val()){
            if($("#name").parent().next(".validation").length == 0){
                   // $("#nombre").parent().after("<div class='validation' style='color:red;margin-bottom: 20px;'>Campo obligatorio</div>")
                	 $("#validaNombreTarjeta").delay(100).fadeIn("slow");
                }
                $("#name").focus();
                focusSet = true;
                validation = false;
        }else{
                //$("#nombre").parent().next(".validation").remove();
            	 $("#validaNombreTarjeta").fadeOut("slow");
            	 validation = true;

        }
        if(!$('#cardnumber').val()){
            if($("#cardnumber").parent().next(".validation").length == 0){
                   // $("#nombre").parent().after("<div class='validation' style='color:red;margin-bottom: 20px;'>Campo obligatorio</div>")
                     $("#validaNumeroTarjeta").delay(100).fadeIn("slow");
                }
                $("#cardnumber").focus();
                focusSet = true;
                validation = false;
        }else{
                //$("#nombre").parent().next(".validation").remove();
                 $("#validaNumeroTarjeta").fadeOut("slow");
                 validation = true;

        }
        if(!$('#expirationmonth').val()){
            if($("#expirationmonth").parent().next(".validation").length == 0){
                   // $("#nombre").parent().after("<div class='validation' style='color:red;margin-bottom: 20px;'>Campo obligatorio</div>")
                     $("#validaMesTarjeta").delay(100).fadeIn("slow");
                }
                $("#expirationmonth").focus();
                focusSet = true;
                validation = false;
        }else{
                //$("#nombre").parent().next(".validation").remove();
                 $("#validaMesTarjeta").fadeOut("slow");
                 validation = true;

        }
        if(!$('#expirationyear').val()){
            if($("#expirationyear").parent().next(".validation").length == 0){
                   // $("#nombre").parent().after("<div class='validation' style='color:red;margin-bottom: 20px;'>Campo obligatorio</div>")
                     $("#validaAnioTarjeta").delay(100).fadeIn("slow");
                }
                $("#expirationyear").focus();
                focusSet = true;
                validation = false;
        }else{
                //$("#nombre").parent().next(".validation").remove();
                 $("#validaAnioTarjeta").fadeOut("slow");
                 validation = true;

        }
        if(!$('#securitycode').val()){
            if($("#securitycode").parent().next(".validation").length == 0){
                   // $("#nombre").parent().after("<div class='validation' style='color:red;margin-bottom: 20px;'>Campo obligatorio</div>")
                     $("#validaCodigo").delay(100).fadeIn("slow");
                }
                $("#securitycode").focus();
                focusSet = true;
                validation = false;
        }else{
                //$("#nombre").parent().next(".validation").remove();
                 $("#validaCodigo").fadeOut("slow");
                 validation = true;

        }
        if(!$('#montoPagar').val()){
            if($('#montoPagar').parent().next(".validation").length == 0){
                $("#validaMontoPagar").delay(100).fadeIn("slow");
            }
            $("#montoPagar").focus();
            focusSet = true;
            validation = false;
        }else{
             $("#validaMontoPagar").fadeOut("slow");
             validation = true;
        }

        if(validation){
            $("#pay-button").prop( "disabled", true);
            OpenPay.token.extractFormAndCreate('payment-form', sucess_callbak, error_callbak);
        }

    });

    var sucess_callbak = function(response) {
      var token_id = response.data.id;
      $('#token_id').val(token_id);
      $('#payment-form').submit();
    };

    var error_callbak = function(response) {
        var desc = response.data.description != undefined ? response.data.description : response.message;
//        alert("ERROR [" + response.status + "] " + desc);
        $("#pay-button").prop("disabled", false);
    };

});