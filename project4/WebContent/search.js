// Get id from URL

function handleGStr(string, type, getString) {
	if (string) {
		if (getString !== "movieList.html") {
			getString +="&";
		} else {
			getString += "?"; 
		}
		getString += type + string;
	}
	return getString;
}


function handleSearchResult(resultDataString) {
	console.log("handle search response");
	console.log(resultDataString);
	resultDataJson = resultDataString;
    console.log(resultDataJson);

    // If success, redirect to movieList.html page
    if (resultDataJson["status"] === "success") {
    	console.log("success")
    	let getString = "movieList.html";
    	// add search parameters to link
    	getString = handleGStr(resultDataJson["movie_title"], "title=", getString);
    	getString = handleGStr(resultDataJson["movie_year"], "year=", getString);
    	getString = handleGStr(resultDataJson["director"], "director=", getString);
    	getString = handleGStr(resultDataJson["star"], "star=", getString);
    	// console.log(getString);
    	window.location.replace(getString);
    }
    // If login fail, display error message on <div> with id "search_error_message"
    else {
      //  resultDataJson = JSON.parse(resultDataString);
        console.log("show error message");
        console.log(resultDataJson["message"]);
        jQuery("#search_error_message").text(resultDataJson["message"]);
    }
}

/**
 * Submit the search content with GET method
 * @param formSubmitEvent
 */
function submitForm(formSubmitEvent) {
    console.log("submit search form");

    // Important: disable the default action of submitting the form
    //   which will cause the page to refresh
    //   see jQuery reference for details: https://api.jquery.com/submit/
    formSubmitEvent.preventDefault();
    
    jQuery.get(
        "api/search",
        // Serialize the login form to the data sent by POST request
        jQuery("#search_form").serialize(),
        (resultDataString) => handleSearchResult(resultDataString));
}

// Bind the submit action of the form to a handler function
jQuery("#search_form").submit((event) => submitForm(event));
