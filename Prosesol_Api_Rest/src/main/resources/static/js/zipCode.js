function getZipCode(){

    var codigoPostal = $('#codigoPostal').val();
    var toAppend = "";

    $("#entidadFederativa").val('');
    $("#municipio").val('');
    $("#ciudad").val('');
    $("#colonias").empty().append('');

    $.ajax({
        type : "GET",
        url : "/api/zip",
        dataType : "json",
        data : {codigoPostal : codigoPostal},
        success : function(data){
            console.log("SUCCESS: ", data);
            $("#entidadFederativa").val(data.estado);
            $("#municipio").val(data.municipio);
            $("#ciudad").val(data.ciudad);
            $.each(data.colonias, function(k,v){
                toAppend += "<option th:value='" + k + "'>" + v + "</option>";
            });
            $("#colonia").append(toAppend);
        }
    });
}