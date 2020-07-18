function getZipCode(){

    var codigoPostal = $('#codigoPostal').val();
    var toAppend = "";

    $("#entidadFederativa").val('');
    $("#municipio").val('');
    $("#ciudad").val('');
    $("#colonia").empty();

    $.ajax({
        type : "GET",
        url : "/api/zip",
        dataType : "json",
        data : {codigoPostal : codigoPostal},
        success : function(data){
            console.log("SUCCESS: ", data);
            var estado = convierteEstado(data.estado);
            $("#entidadFederativa").val(estado);
            $("#municipio").val(data.municipio);
            $("#ciudad").val(data.ciudad);
            $.each(data.colonias, function(k,v){
                toAppend += "<option th:value='" + k + "'>" + v + "</option>";
            });
            $("#colonia").append(toAppend);
        }
    });
}

function convierteEstado(e){

    var result = "";

    switch(e){
        case 'Aguascalientes':
            result = "AGU";
        break;
        case 'Baja California':
            result = "BCN";
        break;
        case 'Baja California Sur':
            result = "BCS";
        break;
        case 'Campeche':
            result = "CAM";
        break;
        case 'Chiapas':
            result = "CHP";
        break;
        case 'Chihuahua':
            result = "CHH";
        break;
        case 'Coahuila de Zaragoza':
            result = "COA";
        break;
        case 'Colima':
            result = "COL";
        break;
        case 'Ciudad de Mexico':
            result = "CMX";
        break;
        case 'Durango':
            result = "DUR";
        break;
        case 'Guanajuato':
            result = "GUA";
        break;
        case 'Guerrero':
            result = "GRO";
        break;
        case 'Hidalgo':
            result = "HID";
        break;
        case 'Jalisco':
            result = "JAL";
        break;
        case 'Mexico':
            result = "MEX";
        break;
        case 'Michoacan de Ocampo':
            result = "MIC";
        break;
        case 'Morelos':
            result = "MOR";
        break;
        case 'Nayarit':
            result = "NAY";
        break;
        case 'Nuevo Leon':
            result = "NLE";
        break;
        case 'Oaxaca':
            result = "OAX";
        break;
        case 'Puebla':
            result = "PUE";
        break;
        case 'Queretaro':
            result = "QUE";
        break;
        case 'Quintana Roo':
            result = "ROO";
        break;
        case 'San Luis Potosi':
            result = "SLP";
        break;
        case 'Sinaloa':
            result = "SIN";
        break;
        case 'Sonora':
            result = "SON";
        break;
        case 'Tabasco':
            result = "TAB";
        break;
        case 'Tamaulipas':
            result = "TAM";
        break;
        case 'Tlaxcala':
            result = "TLA";
        break;
        case 'Veracruz de Ignacio de la Llave':
            result = "VER";
        break;
        case 'Yucatan':
            result = "YUC";
        break;
        case 'Zacatecas':
            result = "ZAC";
        break;
    }

    return result;

}