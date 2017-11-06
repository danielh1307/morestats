// call REST API to load strava data - JWT is sent as JSON in the body of the request
function getDataFromStravaWithJson() {
    // we want to avoid double click, so button is disabled during loading
    $("#getDataFromStravaJ").prop("disabled", true);

    // call endpoint /getData
    // we use POST here since we are sending a message body and "put" new resources to the server
    var jqxhr = $.ajax({
        type: "POST",
        url: "http://localhost:8080/morestats/getStravaDataJ", // TODO: host must be determined automatically
        data: JSON.stringify({
            jwt: getUrlParameter("jwt")
        }),
        contentType: "application/json; charset=utf-8"
    });

    // if the call is successful, we show the result
    jqxhr.done(function(data) { alert(data); });
    // if there is an error, we show the error
    jqxhr.fail(function(data) { alert("There was an error: " + data); });
    // in any case, we have to enable the button again
    jqxhr.always(function() { $("#getDataFromStravaJ").prop("disabled", false); });

}

// call REST API to load strava data - JWT is sent as bearer token in HTTP header
function getDataFromStravaWithHeader() {
    // we want to avoid double click, so button is disabled during loading
    $("#getDataFromStravaH").prop("disabled", true);

    var jqxhr = $.ajax({
        type: "GET",
        url: "http://localhost:8080/morestats/getStravaDataH", // TODO: host must be determined automatically
        beforeSend: function(xhr) {
            xhr.setRequestHeader("Authorization", "Bearer " + getUrlParameter("jwt"));
        }
    });

    // if the call is successful, we show the result
    jqxhr.done(function(data) { alert(data); });
    // if there is an error, we show the error
    jqxhr.fail(function(data) { alert("There was an error: " + data); });
    // in any case, we have to enable the button again
    jqxhr.always(function() { $("#getDataFromStravaH").prop("disabled", false); });
}

/**
 * Returns an URL parameter.
 *
 * @param sParam
 *                the name of the parameter.
 * @returns the value of that parameter.
 */
function getUrlParameter(sParam) {
    var sPageURL = decodeURIComponent(window.location.search.substring(1));
    var sURLVariables = sPageURL.split('&');
    var sParameterName;
    var i;

    for (i = 0; i < sURLVariables.length; i++) {
	sParameterName = sURLVariables[i].split('=');

	if (sParameterName[0] === sParam) {
	    return sParameterName[1] === undefined ? true : sParameterName[1];
	}
    }

    return '';
};