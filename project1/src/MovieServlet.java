

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class MovieServlet
 */
@WebServlet("/MovieServlet")
public class MovieServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MovieServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // change this to your own mysql username and password
        String loginUser = "root";
        String loginPasswd = "JasonRen10";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
		
        // set response mime type
        response.setContentType("text/html"); 

        // get the printwriter for writing response
        PrintWriter out = response.getWriter();

        out.println("<html>");
        out.println("<head><link rel='stylesheet' href='style.css'>\r\n" + 
        			"<title>Fabflix</title></head>");
        
        
	        try {
	    		Class.forName("com.mysql.jdbc.Driver").newInstance();
	    		// create database connection
	    		Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
	    		// declare statement
	    		Statement statement = connection.createStatement();
	    		// prepare query
	    		String query = "SELECT DISTINCT top20.title, top20.year, top20.director, top20.rating, GROUP_CONCAT(DISTINCT g.name ORDER BY g.name separator ', ') AS genres_list, GROUP_CONCAT(DISTINCT s.name ORDER BY s.name separator ', ' ) AS stars_list\r\n" + 
	    					   "FROM (SELECT DISTINCT m.id, m.title, m.year, m.director, r.rating\r\n" + 
	    					   "	  FROM movies m, ratings r\r\n" + 
	    					   "	  WHERE m.id = r.movieId \r\n" + 
	    					   "	  GROUP BY rating desc\r\n" + 
	    					   "	  LIMIT 20) AS top20, genres g, stars s, genres_in_movies gim, stars_in_movies sim\r\n" + 
	    					   "WHERE top20.id = sim.movieid AND s.id = sim.starId AND g.id = gim.genreId AND top20.id = gim.movieId\r\n" + 
	    					   "GROUP BY top20.rating DESC;\r\n";
	    		// execute query
	    		ResultSet resultSet = statement.executeQuery(query);

	    		out.println("<body>");
	    		out.println("<h1>Top 20 Movies Sorted By Rating</h1>");
	    		
	    		out.println("<table border>");
	    		
	    		// add table header row
	    		out.println("<tr>");
	    		out.println("<td>Movie Title</td>");
	    		out.println("<td>Year</td>");
	    		out.println("<td>Director</td>");
	    		out.println("<td>Ratings</td>");
	    		out.println("<td>Stars</td>");
	    		out.println("<td>Genres</td>");
	    		out.println("</tr>");
        		
        		// add a row for every star result
        		while (resultSet.next()) {
        			// get data
        			String movieTitle = resultSet.getString("title");
        			String movieYear = resultSet.getString("year");
        			String director = resultSet.getString("director");
        			String rating = resultSet.getString("rating");
        			String stars = resultSet.getString("stars_list");
        			String genres = resultSet.getString("genres_list");

        			
        			out.println("<tr>");
        			out.println("<td>" + movieTitle + "</td>");
        			out.println("<td>" + movieYear + "</td>");
        			out.println("<td>" + director + "</td>");
        			out.println("<td>" + rating + "</td>");
        			out.println("<td>" + stars + "</td>");
        			out.println("<td>" + genres + "</td>");
        			out.println("</tr>");
        		}
        		
        		out.println("</table>");
        		
        		out.println("</body>");
        		
        		resultSet.close();
        		statement.close();
        		connection.close();
        		
        } catch (Exception e) {
        		/*
        		 * After you deploy the WAR file through tomcat manager webpage,
        		 *   there's no console to see the print messages.
        		 * Tomcat append all the print messages to the file: tomcat_directory/logs/catalina.out
        		 * 
        		 * To view the last n lines (for example, 100 lines) of messages you can use:
        		 *   tail -100 catalina.out
        		 * This can help you debug your program after deploying it on AWS.
        		 */
        		e.printStackTrace();
        		
        		out.println("<body>");
        		out.println("<p>");
        		out.println("Exception in doGet: " + e.getMessage());
        		out.println("</p>");
        		out.print("</body>");
        }
        
        out.println("</html>");
        out.close();
        
	}
	
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}
	 */
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
