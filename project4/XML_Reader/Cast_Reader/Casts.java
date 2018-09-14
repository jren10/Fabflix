import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Casts {
	private String dirID;
	private String director;
	private String title;
	private String movID;
	private List<String> stars;
	
	/*
	private String genres;
	private String stars;
	private String s_rating;
	*/
	
	public Casts(){
		
	}
	
	public Casts(String dirID, String director, String title, String movID, List<String> stars) {
		this.dirID = dirID;
		this.director = director;
		this.title = title;
		this.movID  = movID;
		this.stars = stars;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Casts Details - ");
		sb.append("dirID:" + getDirID());
		sb.append(", ");
		sb.append("Director:" + getDirector());
		sb.append(", ");
		sb.append("Title:" + getTitle());
		sb.append(", ");
		sb.append("movID:" + getMovID());
		sb.append(", ");
		sb.append("Cast:");
		for (int i = 0; i < getStarsSize(); i++) {
            sb.append(getStar(i));
            sb.append(", ");
        }
		
		return sb.toString();
	}
	
	public String getDirID() {
		return dirID;
	}
	
	public void setDirID(String string) {
		this.dirID = string;
	}
	
	public String getDirector() {
		return director;
	}
	
	public void setDirector(String director) {
		this.director = director;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getMovID() {
		return movID;
	}
	
	public void setMovID(String ID) {
		this.movID = ID;
	}
	
	public String getStar(int i) {
		return this.stars.get(i);
	}
	
	public int getStarsSize() {
		return this.stars.size();
	}
	
	public void addStar(String name) {
		this.stars.add(name);
	}
	
	public void setStarsList(List<String> list) {
		this.stars = new ArrayList<String>(list);
	}
	
	public void dumpList() {
		this.stars.clear();
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
