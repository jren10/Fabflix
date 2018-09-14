import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.jasypt.util.password.StrongPasswordEncryptor;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Map;

@WebServlet(name = "AndroidLoginServlet2", urlPatterns = "/api/android-login2")
public class AndroidLoginServlet2 extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public AndroidLoginServlet2() {
        super();
    }

    private DataSource dataSource;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("username");
        String password = request.getParameter("password");
        String dbEmail = "";
        String dbPassword = "";
        boolean passwordVerify = false;


        Map<String, String[]> map = request.getParameterMap();
        for (String key: map.keySet()) {
            System.out.println(key);
            System.out.println(map.get(key)[0]);
        }
        
        // verify recaptcha first
        try {
    		response.setContentType("application/json"); 	

            Connection dbCon = dataSource.getConnection();

	        String query =
	        		"SELECT email, password from customers where email=? limit 20";
	        
	        java.sql.PreparedStatement preparedStatement = dbCon.prepareStatement(query);
	        preparedStatement.setString(1, email);

			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.first()) {
            	dbEmail = resultSet.getString("email");
            	dbPassword = resultSet.getString("password");
			}
			
            passwordVerify = new StrongPasswordEncryptor().checkPassword(password, dbPassword);
            
            // success
            if (passwordVerify) {
            	request.getSession().setAttribute("user", new User(email));
            	JsonObject responseJsonObject = new JsonObject();
                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success");
                response.getWriter().write(responseJsonObject.toString());
            }
            // failed to login
            else {
                JsonObject responseJsonObject = new JsonObject();
                responseJsonObject.addProperty("status", "fail");
			    responseJsonObject.addProperty("message", "Invalid username or password. ");
			    response.getWriter().write(responseJsonObject.toString());
            }
            
            resultSet.close();
            preparedStatement.close();
            dbCon.close();
			/*
			if (resultSet.first()) {
				id = Integer.parseInt(resultSet.getString("id"));
				String securePassword = resultSet.getString("password");
	            passwordVerify = new StrongPasswordEncryptor().checkPassword(password, securePassword);
	         
	            if (passwordVerify) {
					id = Integer.parseInt(resultSet.getString("id"));
				    request.getSession().setAttribute("user", new User(email));
				    request.getSession().setAttribute("user_id", id);
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
*/
            
		} catch (Exception e) {
	        		e.printStackTrace();
	                JsonObject failConnectJson = new JsonObject();
	                failConnectJson.addProperty("status", "fail");
	                response.getWriter().write(failConnectJson.toString());
	        }

	}
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
