/**
 * This User class only has the username field in this example.
 * <p>
 * However, in the real project, this User class can contain many more things,
 * for example, the user's shopping cart items.
 */
public class User {

    private final String email;
    private final int id;
    
    public User(String email, int id) {
        this.email = email;
        this.id = id;
    }

	public String getEmail() {
        return this.email;
    }
	
	public int getId() {
        return this.id;
    }


}
