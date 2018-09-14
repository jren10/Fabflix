import javax.annotation.Resource;
import javax.naming.NamingException;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * A servlet that takes input from a html <form> and talks to MySQL moviedb,
 * generates output as a html <table>
 */

// Declaring a WebServlet called FormServlet sends back movie data
@WebServlet(name = "FormServlet", urlPatterns = "/api/movielist")
public class MovieListServlet extends HttpServlet {

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;


    // Use http GET
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");
        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Building page body

        // Retrieve parameter "name" from the http request
        String title = request.getParameter("title");
        String director = request.getParameter("director");
        String star = request.getParameter("star");
        String s_year = request.getParameter("year");
        String genre = request.getParameter("genre");
        String first_char = request.getParameter("first_char");
        // checks to replace null with blank for query
        title = Objects.toString(title, "");
        director = Objects.toString(director, "");
        star = Objects.toString(star, "");
        s_year = Objects.toString(s_year, "");
        
        // Create a new connection to database
        try {
            Connection dbCon = dataSource.getConnection();

            // Declare a new statement
            Statement statement = dbCon.createStatement();
            
            String query = "";

            if (StringUtils.isEmpty(genre) == false) {
                query = ("SELECT m.id, m.title, m.year, m.director, GROUP_CONCAT(DISTINCT g.name separator ',') AS genres, GROUP_CONCAT(DISTINCT s.name, ',', s.id separator ',') AS starNameID, r.rating\r\n" + 
                		"    				FROM movies m, stars_in_movies sim, stars s, genres g, genres_in_movies gim, ratings r\r\n" + 
                		"    				WHERE m.id = sim.movieid AND s.id = sim.starId AND g.id = gim.genreId AND m.id = gim.movieId AND m.id = r.movieId\r\n" + 
                		"    				AND m.title LIKE '%" + title + "%' AND m.director LIKE '%" + director + "%' AND m.year LIKE '%" + s_year + "%' \r\n" + 
                		"    				AND s.name LIKE '%" + star + "%' AND g.name LIKE '%" + genre + "%' \r\n" + 
                		"    				GROUP BY m.title; \r\n");    
            }
            else if (StringUtils.isEmpty(first_char) == false){
                query = ("SELECT m.id, m.title, m.year, m.director, GROUP_CONCAT(DISTINCT g.name separator ',') AS genres, GROUP_CONCAT(DISTINCT s.name, ',', s.id separator ',') AS starNameID, r.rating\r\n" + 
                		"    				FROM movies m, stars_in_movies sim, stars s, genres g, genres_in_movies gim, ratings r\r\n" + 
                		"    				WHERE m.id = sim.movieid AND s.id = sim.starId AND g.id = gim.genreId AND m.id = gim.movieId AND m.id = r.movieId\r\n" + 
                		"    				AND m.title LIKE '" + first_char + "%' AND m.director LIKE '%" + director + "%' AND m.year LIKE '%" + s_year + "%' \r\n" + 
                		"    				AND s.name LIKE '%" + star + "%' \r\n" + 
                		"    				GROUP BY m.title; \r\n");         
            } 
            else {
                query = ("SELECT m.id, m.title, m.year, m.director, GROUP_CONCAT(DISTINCT g.name separator ',') AS genres, GROUP_CONCAT(DISTINCT s.name, ',', s.id separator ',') AS starNameID, r.rating\r\n" + 
                		"    				FROM movies m, stars_in_movies sim, stars s, genres g, genres_in_movies gim, ratings r\r\n" + 
                		"    				WHERE m.id = sim.movieid AND s.id = sim.starId AND g.id = gim.genreId AND m.id = gim.movieId AND m.id = r.movieId\r\n" + 
                		"    				AND m.title LIKE '%" + title + "%' AND m.director LIKE '%" + director + "%' AND m.year LIKE '%" + s_year + "%' \r\n" + 
                		"    				AND s.name LIKE '%" + star + "%' \r\n" + 
                		"    				GROUP BY m.title; \r\n");         
            }
            
            // Perform the query
            ResultSet rs = statement.executeQuery(query);          
			JsonArray jsonArray = new JsonArray();
			
            while (rs.next()) {
                String m_id = rs.getString("id");
                String m_title = rs.getString("title");
                String m_year = rs.getString("year");
                String m_director = rs.getString("director");
                String genres = rs.getString("genres");
                String stars = rs.getString("starNameID");
                double rating = rs.getDouble("rating");
                
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

                DecimalFormat decimalFormat = new DecimalFormat("#.0");
                String s_rating = decimalFormat.format(rating);

				JsonObject jsonObject = new JsonObject();

				jsonObject.addProperty("movie_id", m_id);
				jsonObject.addProperty("movie_title", m_title);
				jsonObject.addProperty("movie_year", m_year);
				jsonObject.addProperty("movie_director", m_director);
				jsonObject.addProperty("movie_rating", s_rating);
				jsonObject.addProperty("genre", genres);
				jsonObject.add("star_name_array", starNameArray);
				jsonObject.add("star_id_array", starIDArray);

				jsonArray.add(jsonObject);     
            }
            // write JSON string to output
            out.write(jsonArray.toString());
            // set response status to 200 (OK)
            response.setStatus(200);
            // Close all structures
            rs.close();
            statement.close();
            dbCon.close();
        }
        catch (Exception e) {
			//out.println("An error occurred retrieving movies.");
			e.printStackTrace();
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());
			// set reponse status to 500 (Internal Server Error)
			response.setStatus(500);
		}

		out.close();

        
    }	
    
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}	

