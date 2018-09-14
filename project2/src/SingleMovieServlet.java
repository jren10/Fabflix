import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "SingleMovieServlet", urlPatterns = "/api/single-movie")
public class SingleMovieServlet extends HttpServlet {
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

		// Output stream to STDOUT
		PrintWriter out = response.getWriter();

		try {
			// Get a connection from dataSource
			Connection dbcon = dataSource.getConnection();

            String query = ("SELECT m.id, m.title, m.year, m.director, GROUP_CONCAT(DISTINCT g.name separator ',') AS genres, GROUP_CONCAT(DISTINCT s.name, ',', s.id separator ',') AS starNameID, r.rating\r\n" + 
            		"    				FROM movies m, stars_in_movies sim, stars s, genres g, genres_in_movies gim, ratings r\r\n" + 
            		"    				WHERE m.id = sim.movieid AND s.id = sim.starId AND g.id = gim.genreId AND m.id = gim.movieId AND m.id = r.movieId \r\n" + 
            		"					AND m.id = '" + id + "' \r\n"	+
            		"    				GROUP BY m.title; \r\n");

			// Declare our statement
			PreparedStatement statement = dbcon.prepareStatement(query);
			
			// Perform the query
			ResultSet rs = statement.executeQuery();

			JsonArray jsonArray = new JsonArray();

			// Iterate through each row of rs
			while (rs.next()) {
                String m_id = rs.getString("id");
                String m_title = rs.getString("title"	);
                String m_year = rs.getString("year");
                String m_director = rs.getString("director");
                String genres = rs.getString("genres");
                String stars = rs.getString("starNameID");
                double rating = rs.getDouble("rating");
                
                DecimalFormat decimalFormat = new DecimalFormat("#.0");
                String s_rating = decimalFormat.format(rating);

                String[] individualStar = stars.split(",");
                List<String> starList = new ArrayList<String>(Arrays.asList(individualStar));
                JsonArray starNameArray = new JsonArray();
                for (int i = 0; i < starList.size(); i = i+2) {
                    starNameArray.add(starList.get(i));
                }
                JsonArray starIDArray = new JsonArray();
                for (int i = 1; i < starList.size(); i = i+2) {
                    starIDArray.add(starList.get(i));
                }
                
                String[] individualGenre = genres.split(",");
                List<String> genreList = new ArrayList<String>(Arrays.asList(individualGenre));
                JsonArray genreArray = new JsonArray();
                for (int i = 0; i < genreList.size(); i++) {
                    genreArray.add(genreList.get(i));
                }
                
				JsonObject jsonObject = new JsonObject();	
				jsonObject.addProperty("movie_id", m_id);
				jsonObject.addProperty("movie_title", m_title);
				jsonObject.addProperty("movie_year", m_year);
				jsonObject.addProperty("movie_director", m_director);
				jsonObject.addProperty("movie_rating", s_rating);
				jsonObject.add("genre", genreArray);
				jsonObject.add("star_name_array", starNameArray);
				jsonObject.add("star_id_array", starIDArray);
				jsonArray.add(jsonObject);  
			}
			
            // write JSON string to output
            out.write(jsonArray.toString());
            // set response status to 200 (OK)
            response.setStatus(200);

			rs.close();
			statement.close();
			dbcon.close();
		} catch (Exception e) {
			// write error message JSON object to output
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());

			// set reponse status to 500 (Internal Server Error)
			response.setStatus(500);
		}
		out.close();

	}

}
