import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.annotation.Resource;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
//import com.mysql.jdbc.PreparedStatement;

import java.sql.PreparedStatement;

//import java.sql.Statement;
//import java.util.ArrayList;


// server endpoint URL
@WebServlet(name = "MovieAutoCompleteServlet", urlPatterns = "/api/MovieAutoComplete")
public class MovieAutoComplete extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@Resource(name = "jdbc/moviedb")
	private DataSource dataSource;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	PrintWriter out = response.getWriter();
    	String term = request.getParameter("term");
    	JsonArray jarray = null;
    	try {
    		// System.out.println("Going here");
    		jarray = getSuggestions(term);
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    		
    	}
    	out.println(jarray.toString());
    	out.close();
    	
    }
	
	/*
	 * Generate the JSON Object from hero and category to be like this format:
	 * {
	 *   "value": "Iron Man",
	 *   "data": { "category": "marvel", "heroID": 11 }
	 * }
	 * 
	 */
	private JsonArray getSuggestions(String term) throws SQLException, NamingException
    {
    	Connection dbcon = dataSource.getConnection();
    	JsonArray suggestions = new JsonArray();
    	String query = "SELECT title, id FROM movies WHERE (title LIKE ? AND title LIKE ?) OR title = ? ORDER BY title LIMIT 10";
        //replace whitespace
         StringTokenizer tokenizer = new StringTokenizer(term);
         String firstToken = tokenizer.nextToken();
         term.replaceAll("\\s","%");
         System.out.println("term=" + term);

         PreparedStatement prepStmnt = dbcon.prepareStatement(query);
         prepStmnt.setString(1, firstToken+'%');
         prepStmnt.setString(2, term+'%');
         prepStmnt.setString(3, term);

         ResultSet rs = prepStmnt.executeQuery();

         while(rs.next())
         {
             String id = rs.getString("id");
             String title = rs.getString("title");
             JsonObject jsonObject = new JsonObject();
             suggestions.add(generateJsonObject(id, title, "single-movie"));
         }
         rs.close();
         prepStmnt.close();
         dbcon.close();
         return suggestions;
    	
    }
	
	private static JsonObject generateJsonObject(String id, String title, String categoryName) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("value", title);
		JsonObject additionalDataJsonObject = new JsonObject();
		additionalDataJsonObject.addProperty("category", categoryName);
		additionalDataJsonObject.addProperty("movie_id", id);
		jsonObject.add("data", additionalDataJsonObject);
		return jsonObject;
	}


}