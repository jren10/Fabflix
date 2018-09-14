/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs three steps:
 *      1. Get parameter from request URL so it know which id to look for
 *      2. Use jQuery to talk to backend API to get the json data.
 *      3. Populate the data to correct html elements.
 */


/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */
function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

function handleGStr(string, type, getString) {
	if (string) {
		if (getString.length) {
			getString +="&";
		} else {
			getString += "?"; 
		}
		getString += type + string;
	}
	console.log("function=" + getString);
	return getString;
}

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleMovieResult(mData) {
	let movieTableBodyElement = jQuery("#"+movieId);
	let rowHTML="";
	rowHTML += "<th>" + mData[0]['movie_id'] + "</th>";
	rowHTML += "<th>" + mData[0]['movie_quantity'] + "</th>";
	rowHTML += '<a href="single-movie.html?id=' + mData[0]['movie_id'] + '">'+ mData[0]['movie_title'] + '</a>';
	movieTableBodyElement.append(rowHTML);
}

function handleCheckoutResult(resultDataString) {
	//console.log("handle search response");
	//console.log(resultDataString)
	resultDataJson = resultDataString;
    console.log(resultDataJson);

    // If success, redirect to movieList.html page
    if (resultDataJson["status"] === "success") {
    	//console.log("success")
    	let getString = "confirmation.html";
    	getString = handleGStr(resultDataJson["first_name"], "first_name=", getString);
    	getString = handleGStr(resultDataJson["last_name"], "last_name=", getString);
    	getString = handleGStr(resultDataJson["credit_card"], "credit_card=", getString);
    	getString = handleGStr(resultDataJson["expiration"], "expiration=", getString);
    	// console.log(getString);
    	window.location.replace(getString);
    }
    // If login fail, display error message on <div> with id "login_error_message"
    else {
        console.log("show error message");
        console.log(resultDataJson["message"]);
        jQuery("#checkout_error_message").text(resultDataJson["message"]);
    }
}

function handleResult(resultData) {
    // find the empty h3 body by id "cart_info"
    let cartInfoElement = jQuery("#cart_info");
    
    for (let i = 0; i < resultData.length; i++) {
    	let movieId = resultData[i]["movie_id"];
    	let quantity = parseInt(resultData[i]["movie_quantity"]);
    	let url = "cart.html?id=" + resultData[i]["movie_id"] + "&value=";
    	let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + resultData[i]["movie_id"] + "</th>";
        rowHTML += "<th id=" + movieId +"></th>";
        rowHTML += "<th>";
        rowHTML += "<a href=" + url + (-1) + ">-</a>";
        rowHTML += resultData[i]["movie_quantity"];
        rowHTML += "<a href=" + url + (1) + ">+</a>";
        rowHTML += "</th>";
        rowHTML += "</tr>";

        cartInfoElement.append(rowHTML);
    }
    
	$(document).ready( function () {
	    $('#cart_table').DataTable();
	} );
}
/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

function submitCheckoutForm(formSubmitEvent) {
    console.log("submit checkout form");

    // Important: disable the default action of submitting the form
    //   which will cause the page to refresh
    //   see jQuery reference for details: https://api.jquery.com/submit/
    formSubmitEvent.preventDefault();

    jQuery.post(
        "api/checkout",
        // Serialize the checkout form to the data sent by POST request
        jQuery("#checkout_form").serialize(),
        (resultDataString) => handleCheckoutResult(resultDataString));

}

// Bind the submit action of the form to a handler function
jQuery("#checkout_form").submit((event) => submitCheckoutForm(event));
