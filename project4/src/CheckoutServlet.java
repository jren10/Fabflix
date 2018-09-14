import javax.annotation.Resource;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A servlet that takes input from a html <form> and talks to MySQL moviedb,
 * generates output as a html <table>
 */

// Declaring a WebServlet called CartServlet, which maps to url "/form"
@WebServlet(name = "CheckoutServlet", urlPatterns = "/api/checkout")
public class CheckoutServlet extends HttpServlet {
	private static final long serialVersionUID = 2L;

	@Resource(name = "jdbc/moviedb")
	private DataSource dataSource;
    
    // Use http POST
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
    	response.setContentType("application/json");    // Response mime type
    	
        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        JsonArray jsonArray = new JsonArray();
        HttpSession session = request.getSession();
        User userInfo = (User)session.getAttribute("user");
        
        try {
	        Connection dbCon = dataSource.getConnection();
	    				
	    	String firstName = request.getParameter("first_name");
	    	String lastName = request.getParameter("last_name");
	    	String creditCard = request.getParameter("credit_card");
	    	String expiration = request.getParameter("expiration");

	    	/*String query = "SELECT * \n" + 
	    			"FROM creditcards\n" + 
	    			"WHERE id = '" + creditCard + "' AND firstName = '" + firstName + "' AND lastName = '" + lastName + "' AND expiration = '" + expiration + "';";*/
	    	
	    	String query = "SELECT * \n" + 
	    			"FROM creditcards \n" + 
	    			"WHERE id = ? AND firstName = ? AND lastName = ? AND expiration = ?;";
	    	
	        java.sql.PreparedStatement preparedStatement = dbCon.prepareStatement(query);
	        preparedStatement.setString(1, creditCard);
	        preparedStatement.setString(2, firstName);
	        preparedStatement.setString(3, lastName);
	        preparedStatement.setString(4, expiration);

			ResultSet resultSet = preparedStatement.executeQuery();

			
			if (!resultSet.first()) {
			    JsonObject responseJsonObject = new JsonObject();
			    responseJsonObject.addProperty("status", "fail");
			    responseJsonObject.addProperty("message", "Invalid payment information. ");
			    out.write(responseJsonObject.toString());
			}
			else {
			    JsonObject responseJsonObject = new JsonObject();
			    responseJsonObject.addProperty("status", "success");
			    responseJsonObject.addProperty("message", "success");
	            out.write(responseJsonObject.toString());	
			}
			
        } catch (Exception ex) {
            // Output Error Massage to html
        	JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("status", "exception");
            jsonArray.add(responseJsonObject);
            out.write(jsonArray.toString());
            return;
        }
        
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
    	doPost(request, response);
    }
}
