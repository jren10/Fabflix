import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
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
import com.mysql.jdbc.PreparedStatement;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A servlet that takes input from a html <form> and talks to MySQL moviedb,
 * generates output as a html <table>
 */

// Declaring a WebServlet called CartServlet, which maps to url "/form"
@WebServlet(name = "ConfirmationServlet", urlPatterns = "/api/confirmation")
public class ConfirmationServlet extends HttpServlet {
	private static final long serialVersionUID = 2L;

    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;
    
    // Use http POST
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
    	response.setContentType("application/json");    // Response mime type

        PrintWriter out = response.getWriter();

        JsonArray jsonArray = new JsonArray();
        HttpSession cartSession = request.getSession();
        HttpSession userSession = request.getSession();
        HashMap<String, Integer> cart = (HashMap<String, Integer>) cartSession.getAttribute("previousItems");
        int u_id = (int) userSession.getAttribute("user_id");
        Date date = new Date();
        String modifiedDate= new SimpleDateFormat("yyyy-MM-dd").format(date);


        try {        	
            // Obtain our environment naming context

            Context initCtx = new InitialContext();

            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            if (envCtx == null)
                out.println("envCtx is NULL");

            // Look up our data source
            DataSource ds = (DataSource) envCtx.lookup("jdbc/TestDB");
            
            if (ds == null)
                out.println("ds is null.");

            Connection dbCon = ds.getConnection();
            if (dbCon == null)
                out.println("dbcon is null.");
            Statement statement = dbCon.createStatement();

        	 synchronized (cart) {
        		 	Iterator<Entry<String, Integer>> iterator = cart.entrySet().iterator();
        		 	while (iterator.hasNext()) {
        		 		Entry<String, Integer> e = iterator.next();
        		 		JsonObject responseJsonObject = new JsonObject();
        		 		responseJsonObject.addProperty("movie_id", e.getKey());
        		 		responseJsonObject.addProperty("movie_quantity", e.getValue());
                  		jsonArray.add(responseJsonObject);
        		 		String query = "INSERT INTO sales (id ,customerId, movieId, saleDate) VALUES (null," + u_id + ",'" + e.getKey() + "','" + modifiedDate + "');";
        		 		responseJsonObject.addProperty("query", query);
		               	statement.executeUpdate(query);          
		               	iterator.remove(); 
        		 	}
    	             statement.close();
    	             dbCon.close();

        		 }

        	 out.write(jsonArray.toString());
        } catch (Exception ex) {
            // Output Error Massage to html
        	System.out.println("failure");
        	JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("status", "exception");
            jsonArray.add(responseJsonObject);
            out.write(jsonArray.toString());
            return;
        }
        out.close();
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
    	doPost(request, response);
    }
}
