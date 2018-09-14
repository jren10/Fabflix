public class Movie {
	private String id;
	private String title;
	private int year;
	private String director;
	private String genres;
	private String stars;
	private String s_rating;
	
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
	
	public int getYear() {
		return year;
	}
	
	public void setYear(int year) {
		this.year = year;
	}
	
	public String getDirector() {
		return director;
	}
	
	public void setDirector(String director) {
		this.director = director;
	}
	
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
	

}
