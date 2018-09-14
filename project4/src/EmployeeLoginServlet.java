import com.google.gson.JsonObject;
import com.mysql.jdbc.PreparedStatement;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.jasypt.util.password.StrongPasswordEncryptor;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

//
@WebServlet(name = "EmployeeLoginServlet", urlPatterns = "/api/employeeLogin")
public class EmployeeLoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;
    
    
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */

    	    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");  
/*
        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
        System.out.println("gRecaptchaResponse=" + gRecaptchaResponse);

        // Verify reCAPTCHA
        try {
            RecaptchaVerifyUtils.verify(gRecaptchaResponse);
        } catch (Exception e) {
		    JsonObject responseJsonObject = new JsonObject();
		    responseJsonObject.addProperty("status", "fail");
		    responseJsonObject.addProperty("message", "Invalid captcha. ");
		    response.getWriter().write(responseJsonObject.toString());
            out.close();
            return;
        }
        
    	*/
    	try {
	        Connection dbCon = dataSource.getConnection();
	
			//Statement statement = dbCon.createStatement();
			
	        String email = request.getParameter("username");
	        String password = request.getParameter("password");
	        String query = "SELECT * \n" +
	        			 "FROM employees \n" +
	        			 "WHERE email =?;";
	        
	        java.sql.PreparedStatement preparedStatement = dbCon.prepareStatement(query);
	        preparedStatement.setString(1, email);
	        //preparedStatement.setString(2, password);
	        //preparedStatement.executeUpdate();
	        
			ResultSet resultSet = preparedStatement.executeQuery();
			boolean passwordVerify = false;
			// print error message if column information is null
			if (resultSet.first()) {
				String securePassword = resultSet.getString("password");	            
	            passwordVerify = new StrongPasswordEncryptor().checkPassword(password, securePassword);

	            if (passwordVerify) {
				    request.getSession().setAttribute("user", new User(email));
				    JsonObject responseJsonObject = new JsonObject();
				    responseJsonObject.addProperty("status", "success");
				    responseJsonObject.addProperty("message", "success");
		            response.getWriter().write(responseJsonObject.toString());
	            }
	            else {
				    JsonObject responseJsonObject = new JsonObject();
				    responseJsonObject.addProperty("status", "fail");
				    responseJsonObject.addProperty("message", "Invalid username or password. ");
				    response.getWriter().write(responseJsonObject.toString());
	            }
			}	
			else {
			    JsonObject responseJsonObject = new JsonObject();
			    responseJsonObject.addProperty("status", "fail");
			    responseJsonObject.addProperty("message", "Invalid username or password. ");
			    response.getWriter().write(responseJsonObject.toString());
			}
    	} catch (Exception e) {
	        		e.printStackTrace();
	                JsonObject failConnectJson = new JsonObject();
	                failConnectJson.addProperty("status", "fail");
	        }
    	out.close();

	}
}
