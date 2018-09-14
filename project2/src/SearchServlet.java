import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Objects;


// Declaring a WebServlet called SearchServlet, which maps to url "/api/search"
@WebServlet(name = "SearchServlet", urlPatterns = "/api/search")
public class SearchServlet extends HttpServlet {
	private static final long serialVersionUID = 2L;

	// Create a dataSource which registered in web.xml
	@Resource(name = "jdbc/moviedb")
	private DataSource dataSource;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("application/json"); // Response mime type
		
		// Retrieve parameter id from url request.
		String title = request.getParameter("movie");
		String year = request.getParameter("year");
		String director = request.getParameter("director");
		String star = request.getParameter("star");

        title = Objects.toString(title, "");
        director = Objects.toString(director, "");
        star = Objects.toString(star, "");
        year = Objects.toString(year, "");
		// Output stream to STDOUT
		PrintWriter out = response.getWriter();

		try {
			// check for invalid year input
			if (!isNumeric(year) && year != "") {
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("status", "fail");
				jsonObject.addProperty("message", "Invalid year input. ");
	            out.write(jsonObject.toString());
				}
			else {
				// Create a JsonObject based on the data we retrieve from rs
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("status", "success");
				jsonObject.addProperty("movie_title", title);
				jsonObject.addProperty("movie_year", year);
				jsonObject.addProperty("director", director);
				jsonObject.addProperty("star", star);
	            // write JSON string to output
	            out.write(jsonObject.toString());
	            System.out.println("sending it out");
	            // set response status to 200 (OK)
	            response.setStatus(200);
			}

		} catch (Exception e) {
			// write error message JSON object to output
			e.printStackTrace();
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("status", "fail");
			jsonObject.addProperty("message", e.getMessage());
			out.write(jsonObject.toString());

			// set reponse status to 500 (Internal Server Error)
			response.setStatus(500);
		}
		out.close();

	}
	
	public boolean isNumeric(String s) {  
	    return s != null && s.matches("[-+]?\\d*\\.?\\d+");  
	}  

}


