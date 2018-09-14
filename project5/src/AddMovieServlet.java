import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
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


// Declaring a WebServlet called AddMovieServlet, which maps to url "/api/addMovie"
@WebServlet(name = "AddMovieServlet", urlPatterns = "/api/addMovie")
public class AddMovieServlet extends HttpServlet {
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
		String id = request.getParameter("id");
		String title = request.getParameter("movie");
		String year = request.getParameter("year");
		String director = request.getParameter("director");
		String star = request.getParameter("star");
		String genre = request.getParameter("genre");
		//String rating = request.getParameter("rating");

        id = Objects.toString(id, "");
        title = Objects.toString(title, "");
        director = Objects.toString(director, "");
        star = Objects.toString(star, "");
        year = Objects.toString(year, "");
        genre = Objects.toString(genre, "");
        //rating = Objects.toString(rating, "");

		// Output stream to STDOUT
		PrintWriter out = response.getWriter();

		try {
            Context initCtx = new InitialContext();

            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            if (envCtx == null)
                out.println("envCtx is NULL");

            // Look up our data source
            DataSource ds = (DataSource) envCtx.lookup("jdbc/WriteDB");
            
            if (ds == null)
                out.println("ds is null.");

            Connection dbCon = ds.getConnection();
            if (dbCon == null)
                out.println("dbcon is null.");
                        
            String query = "call add_movie(?, ?, ?, ?, ?, ?);";        
            
            String checkQuery = "SELECT * FROM movies\n" + 
            		"WHERE title =? AND year =? AND director =?;";
            
            java.sql.PreparedStatement checkStatement = dbCon.prepareStatement(checkQuery);
			
            java.sql.PreparedStatement preparedStatement = dbCon.prepareStatement(query);
            
            checkStatement.setString(1, title);
            checkStatement.setString(2, year);
            checkStatement.setString(3, director);

			ResultSet resultSet1 = checkStatement.executeQuery();
			if (title == "" || year == "" || director == "") {
    			JsonObject jsonObject = new JsonObject();
    			jsonObject.addProperty("status", "fail");
    			jsonObject.addProperty("message", "Movie title, year, and director cannot be empty." );
    		    response.getWriter().write(jsonObject.toString());
    		    return;
			}
			if (resultSet1.first()) {
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("status", "fail");
				jsonObject.addProperty("message", "Duplicate movie");
				out.write(jsonObject.toString());
			}
			else {
	            if (id == "") {
	                String maxIdQuery =  "SELECT max(id) from movies;";
	                java.sql.PreparedStatement maxIdStatement = dbCon.prepareStatement(maxIdQuery);
	    			ResultSet resultSet2 = maxIdStatement.executeQuery();
	    			while (resultSet2.next()) {
	    				String maxID = resultSet2.getString("max(id)");
	    				String s_maxID = maxID.substring(2);
	    				Integer i_maxID = Integer.parseInt(s_maxID);
	    				i_maxID ++;
	    				s_maxID = Integer.toString(i_maxID);
	    				s_maxID = "tt0" + s_maxID;
	    				
		            preparedStatement.setString(1, s_maxID);
		            preparedStatement.setString(2, title);
		            preparedStatement.setString(3, year);
		            preparedStatement.setString(4, director);
		            preparedStatement.setString(5, genre);
		            preparedStatement.setString(6, star);
	    			}
	            }
	            else {
	                preparedStatement.setString(1, id);
	                preparedStatement.setString(2, title);
	                preparedStatement.setString(3, year);
	                preparedStatement.setString(4, director);
	                preparedStatement.setString(5, genre);
	                preparedStatement.setString(6, star);
	            }
            preparedStatement.executeUpdate();
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("status", "success");
			jsonObject.addProperty("message", "Successful insertion. ");
		    response.getWriter().write(jsonObject.toString());
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


