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


// Declaring a WebServlet called AddInfoServlet, which maps to url "/api/addInfo"
@WebServlet(name = "AddInfoServlet", urlPatterns = "/api/addInfo")
public class AddInfoServlet extends HttpServlet {
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
		String movieId = request.getParameter("movie_id");
		String starId = request.getParameter("star_id");
		String genre = request.getParameter("genre");

        starId = Objects.toString(starId, "");
        movieId = Objects.toString(movieId, "");
        genre = Objects.toString(genre, "");

		// Output stream to STDOUT
		PrintWriter out = response.getWriter();

		try {
            Connection dbCon = dataSource.getConnection();
            String query = "call add_info(?, ?, ?);";        

            java.sql.PreparedStatement preparedStatement = dbCon.prepareStatement(query);
            
            preparedStatement.setString(1, movieId);
            preparedStatement.setString(2, genre);
            preparedStatement.setString(3, starId);
            
            preparedStatement.executeUpdate();
            
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("status", "success");
			jsonObject.addProperty("message", "Successful insertion. ");
		    response.getWriter().write(jsonObject.toString());
		} catch (Exception e) {
			// write error message JSON object to output
			e.printStackTrace();
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("status", "fail");
			jsonObject.addProperty("message", e.getMessage());
			out.write(jsonObject.toString());

			// set reponse status to 500 (Internal Server Error)
			//response.setStatus(500);
		}
		out.close();

	}
	
	public boolean isNumeric(String s) {  
	    return s != null && s.matches("[-+]?\\d*\\.?\\d+");  
	}  

}


