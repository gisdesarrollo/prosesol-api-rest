$(document).ready(function() {

    OpenPay.setId('mmlithfv3otobghccu6a');
    OpenPay.setApiKey('pk_3ff10b69ab4e491fb4b46ef8654a1f98');
    OpenPay.setSandboxMode(true);
    //Se genera el id de dispositivo
    var deviceSessionId = OpenPay.deviceData.setup("payment-form", "deviceIdHiddenFieldName");
    
    $('#pay-button').on('click', function(event) {
        event.preventDefault();
        $("#pay-button").prop( "disabled", true);
        OpenPay.token.extractFormAndCreate('payment-form', sucess_callbak, error_callbak);                
    });

    var sucess_callbak = function(response) {
      var token_id = response.data.id;
      $('#token_id').val(token_id);
      $('#payment-form').submit();
    };

    var error_callbak = function(response) {
        var desc = response.data.description != undefined ? response.data.description : response.message;
        alert("ERROR [" + response.status + "] " + desc);
        $("#pay-button").prop("disabled", false);
    };

});