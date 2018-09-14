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

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleMovieResult(mData,movieId) {
	let movieTableBodyElement = jQuery("#"+movieId);
	let rowHTML="";
	rowHTML += '<a href="single-movie.html?id=' + mData[0]['movie_id'] + '">'+ mData[0]['movie_title'] + '</a>';
	movieTableBodyElement.append(rowHTML);
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
        jQuery.ajax({
            dataType: "json", // Setting return data type
            method: "GET", // Setting request method
            url: "api/single-movie?id=" + movieId, // Setting request url, which is mapped by CartServlet
            success: (mData) => handleMovieResult(mData, movieId) // Setting callback function to handle data returned successfully by the CartServlet
        });
        cartInfoElement.append(rowHTML);
    }
    
	$(document).ready( function () {
	    $('#cart_table').DataTable();
	} );
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
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let id = getParameterByName('id');
let value = getParameterByName('value');
let title = getParameterByName('title');
let getString = "";
getString = handleGStr(id, "id=", getString);
getString = handleGStr(value, "value=", getString);
// Makes the HTTP GET request and registers on success callback function handleResult
if (id != null) {
	jQuery.ajax({
	    dataType: "json",  // Setting return data type
	    method: "POST",// Setting request method
		url: "api/cart?id=" + id + '&value=' + value, // Setting request url, which is mapped by StarsServlet in Stars.java
	    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the CartServlet
	});
}
else {
	jQuery.ajax({
		dataType: "json",  // Setting return data type
		method: "GET",// Setting request method
		url: "api/cart", // Setting default cart url
		success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the CartServlet
	});
}
