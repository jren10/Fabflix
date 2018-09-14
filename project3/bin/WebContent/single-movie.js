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

function handleResult(resultData) {

    console.log("handleResult: populating movie info from resultData");

    // populate the star info h3
    // find the empty h3 body by id "star_info"
    let starInfoElement = jQuery("#movie_info"); 

    // append two html <p> created to the h3 body, which will refresh the page
    starInfoElement.append("<p>ID: " + resultData[0]["movie_id"] + "</p>" +
    	"<p>Title: " + resultData[0]["movie_title"] + "</p>" +
    	"<p>Director: " + resultData[0]["movie_director"] + "</p>" +
    	"<p>Year: " + resultData[0]["movie_year"] + "</p>" +
    	"<p>Genres: ");
    	var genre_array_length = resultData[0]["genre"].length;
    	for (var i = 0; i < genre_array_length; i++) {
    		starInfoElement.append("<p><a href = 'movieList.html?genre=" + resultData[0]["genre"][i] + "'>" + resultData[0]["genre"][i] + "</a>");
    	}
    	
        starInfoElement.append("</p>" + "<p>Stars: ");
    
    	var star_array_length = resultData[0]["star_id_array"].length;
        for (var i = 0; i < star_array_length; i++) {
    		starInfoElement.append("<p><a href ='star.html?id=" + resultData[0]["star_id_array"][i] + "'>" + resultData[0]["star_name_array"][i] + "</a>");
            }
    	starInfoElement.append(
    	"</p>" +
    	"<p>Rating: " + resultData[0]["movie_rating"] + "</p>" +
    	"<p><a href = 'cart.html?id=" + resultData[0]["movie_id"] + "&value=1'>" + "Add Movie to Shopping Cart</a></p>");

    console.log("handleResult: populating movie info from resultData");

}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let movieId = getParameterByName('id');

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-movie?id=" + movieId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});