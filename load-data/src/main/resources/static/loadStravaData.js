function getDataFromStrava() {
    // call endpoint /getData
    // we use POST here since we are sending a message body and "put" new resources to the server
    $.ajax({
        type: "POST",
        url: "http://localhost:8080/getData", // TODO: host must be determined automatically
        data: JSON.stringify({
            jwt: getUrlParameter("jwt")
        }),
        contentType: "application/json; charset=utf-8"
    }).done(function(data) {alert(data)});

}

function getCurrentData() {
    // call endpoint /current
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