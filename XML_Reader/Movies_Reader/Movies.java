public class Movies {
	private String id;
	private String title;
	private String year;
	private String director;
	/*
	private String genres;
	private String stars;
	private String s_rating;
	*/
	
	public Movies(){
		
	}
	
	public Movies(String id, String title, String year, String director) {
		this.id = id;
		this.title = title;
		this.year  = year;
		this.director = director;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Movie Details - ");
		sb.append("ID:" + getId());
		sb.append(", ");
		sb.append("Title:" + getTitle());
		sb.append(", ");
		sb.append("Year:" + getYear());
		sb.append(", ");
		sb.append("Director:" + getDirector());
		sb.append(".");
		
		return sb.toString();
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String string) {
		this.id = string;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getYear() {
		return year;
	}
	
	public void setYear(String year) {
		this.year = year;
	}
	
	public String getDirector() {
		return director;
	}
	
	public void setDirector(String director) {
		this.director = director;
	}
	
	/*
	public String getGenrse() {
		return genres;
	}
	
	public void setGenres(String genres) {
		this.genres = genres;
	}
	
	public String getStars() {
		return stars;
	}
	
	public void setStars(String stars ) {
		this.stars = stars;
	}
	
	public String getRating() {
		return s_rating;
	}
	
	public void setRating(String s_rating) {
		this.s_rating = s_rating;
	}
	*/
	

}
