// Get id from URL

function handleGStr(string, type, getString) {
	if (string) {
		if (getString !== "addStar.html") {
			getString +="&";
		} else {
			getString += "?"; 
		}
		getString += type + string;
	}
	return getString;
}


function handleSearchResult(resultDataString) {
	resultDataJson = resultDataString;
    console.log("handle login response");
    console.log(resultDataJson);
    console.log(resultDataJson["status"]);

    // If login success, redirect to index.html page
    if (resultDataJson["status"] === "success") {
    	console.log("success");
    	alert("Success");
    	window.location.replace("addStar.html");


    }
    // If login fail, display error message on <div> with id "search_error_message"
    else {
      //  resultDataJson = JSON.parse(resultDataString);
        console.log("show error message");
        console.log(resultDataJson["message"]);
        jQuery("#add_error_message").text(resultDataJson["message"]);
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
        "api/addStar",
        // Serialize the login form to the data sent by POST request
        jQuery("#add_star").serialize(),
        (resultDataString) => handleSearchResult(resultDataString));
}

// Bind the submit action of the form to a handler function
jQuery("#add_star").submit((event) => submitForm(event));
