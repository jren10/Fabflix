

public class Actors {

	private String name;

	private String dob;
	
	public Actors(){
		
	}
	
	public Actors(String name, String dob) {
		this.name = name;
		this.dob = dob;
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}	
	
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Actor Details - ");
		sb.append("Name:" + getName());
		sb.append(", ");
		sb.append("dob:" + getDob());
		sb.append(".");
		
		return sb.toString();
	}
}
