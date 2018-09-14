import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.naming.NamingException;
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
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.StringTokenizer;


// Declaring a WebServlet called StarsServlet, which maps to url "/api/stars"
@WebServlet(name = "MovieAutoCompleteServlet", urlPatterns = "/api/MovieAutoComplete")
public class MovieAutoComplete extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	PrintWriter out = response.getWriter();
    	String term = request.getParameter("term");
    	System.out.println(term);
    	if (term.length() < 3) {
    		out.println(new JsonArray().toString());
    		out.close();
    		return;
    	}
    	JsonArray jarray = new JsonArray();
    	try {
    		// System.out.println("Going here");
    		jarray = getSuggestions(term);
    	}
    	catch (SQLException s) {
    		s.printStackTrace();
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
    	out.write(jarray.toString());
    	out.close();
    	
    }
    
    private String returnQueryString (String term)
    /*
    {
    	String queryString = "";
    	String[] keywordArray = term.split(" ");
    	//if keyword has more than one word
    	if (keywordArray.length != 1)
    	{
    		for (String s: keywordArray) {
    			String newString = "";
    			if (!s.substring(0).equals("-") && !s.substring(0).equals("+")) {
    				newString += "+" + s;
    			} else {
    				newString += s;
    			}
    			if (!s.substring(s.length()-1).equals("*")) {
    				newString += "*";
    			}
    			queryString += newString + " ";	
    		}
    	} else {
    		queryString += term.trim() + "*";
    	}
    	return queryString;
    }*/
    {
		String[] search_terms = term.split("\\s+");
		String search_expression = "+" + search_terms[0] + "*";
		for (int i = 1 ; i < search_terms.length; ++i)
		{
			search_expression = search_expression + " +" + search_terms[i] + "*";
		}
		return search_expression;
    }
    
    private JsonArray getSuggestions(String term) throws SQLException, NamingException
    {
    	Connection dbcon = dataSource.getConnection();
    	JsonArray suggestions = new JsonArray();
//    	String query = "SELECT title, id FROM movies WHERE (title LIKE ? AND title LIKE ?) OR title = ? ORDER BY title LIMIT 10";
//        //replace whitespace
//         StringTokenizer tokenizer = new StringTokenizer(term);
//         String firstToken = tokenizer.nextToken();
//         term.replaceAll("\\s","%");
//         System.out.println("term=" + term);
//
//         PreparedStatement xprepStmnt = dbcon.prepareStatement(query);
//         prepStmnt.setString(1, firstToken+'%');
//         prepStmnt.setString(2, term+'%');
//         prepStmnt.setString(3, term);
    	String queryString = returnQueryString(term);
    	term = "%" + term + "%";
//    	String query = "Select * from movies where match (title) against (? in boolean mode) limit 10;";
    	String query = "SELECT distinct movies.id as id, title\r\n" + 
    			"FROM movies\r\n" + 
    			"WHERE MATCH(title) AGAINST (? in boolean mode)\r\n" + 
    			"LIMIT 10;";
    //	String query = "select id, title from movies where (match (title) against (? in boolean mode));";
    	
		PreparedStatement statement = dbcon.prepareStatement(query);
		statement.setString(1, queryString);
		//statement.setString(2, term);
    	ResultSet rs = statement.executeQuery();

    	while(rs.next())
    	{
    		String id = rs.getString("id");
    		String title = rs.getString("title");
			JsonObject jsonObject = new JsonObject();
    		suggestions.add(generateJsonObject(id, title, "single-movie"));
    	}
    	
    	rs.close();
    	statement.close();
    	dbcon.close();
    	return suggestions;

    }
    /*
	 * Generate the JSON Object from hero and category to be like this format:
	 * {
	 *   "value": "Iron Man",
	 *   "data": { "category": "movies", "movie_id": nm000011 }
	 * }
	 * 
	 */	
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
