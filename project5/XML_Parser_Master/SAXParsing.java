import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SAXParsing {

    public static void main(String[] args) {

    	Connection dbCon;
    	Statement statement = null;
    	
        try {
        	String mySQLUrl = "jdbc:mysql://localhost:3306/moviedb";
        	String username = "root";
        	String password = "JasonRen10";
        	dbCon = DriverManager.getConnection(mySQLUrl, username, password);

			statement = dbCon.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	
        MovieSAXParser msp = new MovieSAXParser(statement);
        msp.runMovieParser();
        StarsSAXParser ssp = new StarsSAXParser(statement);
        ssp.runStarsParser();
        CastSAXParser csp = new CastSAXParser(statement, ssp.getStarsMapping(), msp.getMovieMapping());
        csp.runCastsParser();
    }

}
