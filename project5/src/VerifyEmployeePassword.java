import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.jasypt.util.password.StrongPasswordEncryptor;

public class VerifyEmployeePassword {

	/*
	 * After you update the passwords in customers table,
	 *   you can use this program as an example to verify the password.
	 *   
	 * Verify the password is simple:
	 * success = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);
	 * 
	 * Note that you need to use the same StrongPasswordEncryptor when encrypting the passwords
	 * 
	 */

    @Resource(name = "jdbc/moviedb")
    private static DataSource dataSource;
    
	public static void main(String[] args) throws Exception {

		//System.out.println(verifyCredentials("a@email.com", "a2"));
		//System.out.println(verifyCredentials("a@email.com", "a3"));
		System.out.println(verifyCredentials("classta@email.edu", "classta"));
		System.out.println(verifyCredentials("classta@email.edu", "classta2"));
	}

	private static boolean verifyCredentials(String email, String password) throws Exception {
		
		String loginUser = "root";
		String loginPasswd = "JasonRen10";
		String loginUrl = "jdbc:mysql://localhost:3306/moviedb";

		Connection dbCon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
        //Connection dbCon = dataSource.getConnection();
		Statement statement = dbCon.createStatement();

		String query = String.format("SELECT * from employees where email='%s'", email);

		ResultSet rs = statement.executeQuery(query);

		boolean success = false;
		if (rs.next()) {
		    // get the encrypted password from the database
			String encryptedPassword = rs.getString("password");
			
			// use the same encryptor to compare the user input password with encrypted password stored in DB
			success = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);
		}

		rs.close();
		statement.close();
		dbCon.close();
		
		System.out.println("verify " + email + " - " + password);

		return success;
	}

}
