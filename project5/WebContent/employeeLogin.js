/**
 * Handle the data returned by EmployeeLoginServlet
 * @param resultDataString jsonObject
 */
function handleLoginResult(resultDataString) {
	console.log(resultDataString);
    //resultDataJson = JSON.parse(resultDataString);

    console.log("handle login response");
    console.log(resultDataString);
    console.log(resultDataString["status"]);

    // If login success, redirect to edashboard.html page
    if (resultDataString["status"] === "success") {
        window.location.replace("edashboard.html");
    }
    // If login fail, display error message on <div> with id "login_error_message"
    else {
        console.log("show error message");
        console.log(resultDataString["message"]);
        alert(resultDataString["message"]);
    }
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitLoginForm(formSubmitEvent) {
    console.log("submit login form");

    // Important: disable the default action of submitting the form
    //   which will cause the page to refresh
    //   see jQuery reference for details: https://api.jquery.com/submit/
    formSubmitEvent.preventDefault();

    jQuery.post(
        "api/employeeLogin",
        // Serialize the login form to the data sent by POST request
        jQuery("#login_form").serialize(),
        (resultDataString) => handleLoginResult(resultDataString))
}

// Bind the submit action of the form to a handler function
jQuery("#login_form").submit((event) => submitLoginForm(event));