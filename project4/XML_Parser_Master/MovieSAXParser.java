import java.io.IOException;
import java.io.Writer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.BufferedWriter;
import java.util.HashMap;
import java.util.HashSet;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class MovieSAXParser extends DefaultHandler {

	private File movieXML;
	private String movieID;
	private String movieName;
	private String movieYear;
	private String movieDir;
	private String tempVal;
	private Statement statement;
	private int maxMId;
	private int maxGId;
	private String xmlId;
	
	private HashMap<String, String> movies;
	private HashSet<String> movieGenres;
	private HashMap<String, Integer> genres;
	private HashMap<String, String> movieIdMapping;
	private HashSet<String> genres_in_movies;
	
	private Writer writerA;
	private Writer writerB;
	private Writer writerC;
	


    public MovieSAXParser(Statement statement) {
    	movieXML = new File("stanford-movies/mains243.xml");
        movieID = "";
        movieName = "";
        movieYear = "";
        movieDir = "";
        this.statement = statement;
        movies = new HashMap<String, String>();
        movieGenres = new HashSet<String>();
        genres = new HashMap<String, Integer>();
        movieIdMapping = new HashMap<String, String>();
        genres_in_movies = new HashSet<String>();
    }

    public void runMovieParser() {       	
            parseDocument();

    }
    
    public HashMap<String,String> getMovieMapping() {
    	return movieIdMapping;
    }

    private void parseDocument() {
        createHashes();
        getMaxIds();

        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            sp.parse(movieXML, this);

        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }
    
    public void createHashes()
    {
    	try {
        	String query = "select * from movies";
    		ResultSet rs = statement.executeQuery(query);
    		while(rs.next()) {
    			String movie = "";
    			String id = rs.getString("id");
    			movie = rs.getString("title") + "|" + rs.getString("year") + "|" + rs.getString("director");
    			movies.put(movie, id);
    			movieIdMapping.put(id, id);
    		}
    		
    		query = "select * from genres";
    		rs = statement.executeQuery(query);
    		while(rs.next()) {
    			genres.put(rs.getString("name"), rs.getInt("id"));
    		}
    		
    		query = "select * from genres_in_movies";
    		rs = statement.executeQuery(query);
    		while(rs.next()) {
    			genres_in_movies.add(rs.getString("genreId") + "|" + rs.getString("movieId"));
    		}
    	} catch (SQLException e) { 
    		e.printStackTrace();
    	}
    }
    
    public void getMaxIds() {
    	try {
        	String query = "select max(id) as id from movies;";
			ResultSet rs = statement.executeQuery(query);
			rs.next();
			String max = rs.getString("id");
			max = max.substring(2);
			maxMId = Integer.parseInt(max);
			
			query = "select max(id) as id from genres;";
			rs = statement.executeQuery(query);
			rs.next();
			max = rs.getString("id");
			maxGId = Integer.parseInt(max);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }

    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    	if (qName.equalsIgnoreCase("movies")) {
            try {
    			writerA = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("movies.txt"), "ISO-8859-1"));
    			writerB = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("genres.txt"), "ISO-8859-1"));
    			writerC = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("genres_in_movies.txt"), "ISO-8859-1"));
    		} catch (UnsupportedEncodingException | FileNotFoundException e) {
    			e.printStackTrace();
    		}
    	} else if (qName.equalsIgnoreCase("film")) {
        	movieID = null;
        	movieDir = null;
			movieName = null;
        	movieYear = null;
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length).trim();
    }

    
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("film")) {
        	if (xmlId == null || movieName == null || movieYear == null || movieDir == null) {
        		System.out.println("Error: the following movie was not added id: " + xmlId + ", title: " + movieName + ", year: " + movieYear + ", director: " + movieDir);
        		return;
        	}
        	
        	maxMId += 1;
        	movieID = Integer.toString(maxMId);
        	
        	if(movieID.length() == 6)
        		movieID = "tt0".concat(movieID);
        	else
        		movieID = "tt".concat(movieID);
        	String movie = movieName + "|" + movieYear + "|" + movieDir;
    		mapClass<String, String> map = new mapClass<String, String>(movies);
    		
        	if(map.add(movie, movieID))
        	{
        		String oMovie = movieID + "|" + movie + "\n";
        		movieIdMapping.put(xmlId, movieID);
        		try {
					writerA.write(oMovie);
				} catch (IOException e) {
					e.printStackTrace();
				}
        		
        	} else {
        		movieIdMapping.put(xmlId, movieIdMapping.remove(movies.get(movie)));
        	}
        	
        	for(String temp : movieGenres) {
        		
        		if(temp.isEmpty()) {
        			System.out.println("no genre avaliable");
        			continue;
        		}
        		
        		if(temp.equalsIgnoreCase("comd") || temp.equalsIgnoreCase("comdx") || temp.equalsIgnoreCase("cond")) {
	        		temp = "Comedy";        		
        		} else if(temp.equalsIgnoreCase("myst") || temp.equalsIgnoreCase("mystp")) {
        			temp = "Mystery";
        		} else if(temp.equalsIgnoreCase("susp")) {
        			temp = "Suspense";
			    } else if(temp.equalsIgnoreCase("fant")) {
        			temp = "Fantasy";
        		} else if(temp.equalsIgnoreCase("Hist")) {
        			temp = "History"; 
        		} else if(temp.equalsIgnoreCase("romt") || temp.equalsIgnoreCase("Romtx") || temp.equalsIgnoreCase("Ront")) {
        			temp = "Romance";
        		} else if(temp.equalsIgnoreCase("docu") || temp.equalsIgnoreCase("ducu") || temp.equalsIgnoreCase("dicu") || temp.equalsIgnoreCase("duco")) {
        			temp = "Documentary";
        		} else if(temp.equalsIgnoreCase("fam") || temp.equalsIgnoreCase("faml")) {
        			temp = "Family";
        		} else if(temp.equalsIgnoreCase("avga") || temp.equalsIgnoreCase("Avant Garde")) {
        			temp = "Avant-Garde";
        		} else if(temp.equalsIgnoreCase("dram") || temp.equalsIgnoreCase("draam") || temp.equalsIgnoreCase("dramn") || temp.equalsIgnoreCase("drama") || temp.equalsIgnoreCase("dramd") || temp.equalsIgnoreCase("Dram>")) {
        			temp = "Drama";
        		} else if(temp.equalsIgnoreCase("actn") || temp.equalsIgnoreCase("act") || temp.equalsIgnoreCase("axtn")) {
        			temp = "Action";
				} else if(temp.equalsIgnoreCase("Comd Noir") || temp.equalsIgnoreCase("Noir Comd")) {
        			temp = "Comedy";
        			movieGenres.add("Noir");
        		} else if(temp.equalsIgnoreCase("Romt Fant")) {
        			temp = "Romance";
        			movieGenres.add("Fantasy");
        		} else if(temp.equalsIgnoreCase("Hor") || temp.equalsIgnoreCase("Horr")) {
        			temp = "Horror";
        		} else if(temp.equalsIgnoreCase("Expm")) {
        			temp = "Experimental";
        		} else if(temp.equalsIgnoreCase("verite")) {
        			temp = "Verite";
				} else if(temp.equalsIgnoreCase("epic")) {
        			temp = "Epic";
        		} else if(temp.equalsIgnoreCase("s.f.") || temp.equalsIgnoreCase("Scfi")  || temp.equalsIgnoreCase("Sxfi")|| temp.equalsIgnoreCase("Scif")) {
        			temp = "Sci-Fi";
        		} else if(temp.equalsIgnoreCase("stage musical") || temp.equalsIgnoreCase("Muscl")) {
        			temp = "Musical";
        		} else if (temp.equalsIgnoreCase("Musc")|| temp.equalsIgnoreCase("Muusc")) {
        			temp = "Music";
        		} else if(temp.equalsIgnoreCase("Advt") || temp.equalsIgnoreCase("Adctx") || temp.equalsIgnoreCase("Adct")) {
        			temp = "Adventure";
        		} else if(temp.equalsIgnoreCase("Psyc")) {
        			temp = "Psychological";
        		} else if(temp.equalsIgnoreCase("porn") || temp.equalsIgnoreCase("porb") || temp.equalsIgnoreCase("kinky")) {
        			temp = "Adult";
        		} else if(temp.equalsIgnoreCase("Cult")) {
        			temp = "Cult";
				} else if(temp.equalsIgnoreCase("crim")) {
        			temp = "Crime";
        		} else if(temp.equalsIgnoreCase("Dram.Actn")) {
        			temp = "Drama";
        			movieGenres.add("Action");
        		} else if(temp.equalsIgnoreCase("Psych Dram")) {
        			temp = "Psychological";
        			movieGenres.add("Drama");
        		} else if(temp.equalsIgnoreCase("Romt Dram")) {
        			temp = "Romance";
        			movieGenres.add("Drama");
        		} else if(temp.equalsIgnoreCase("Romt. Comd") || temp.equalsIgnoreCase("Romt Comd")) {
        			temp = "Romance";
        			movieGenres.add("Comedy");
        		} else if(temp.equalsIgnoreCase("Docu Dram") || temp.equalsIgnoreCase("Dram Docu")) {
        			temp = "Documentary";
        			movieGenres.add("Drama");
        		} else if(temp.equalsIgnoreCase("West1") || temp.equalsIgnoreCase("West")) {
        			temp = "Western";
        		} else if(temp.equalsIgnoreCase("sport") || temp.equalsIgnoreCase("sports")) {
        			temp = "Sport";
        		} else if(temp.equalsIgnoreCase("cart")) {
        			temp = "Animation";
        		} else if(temp.equalsIgnoreCase("biop") || temp.equalsIgnoreCase("biopp") || temp.equalsIgnoreCase("biog") || temp.equalsIgnoreCase("biob") || temp.equalsIgnoreCase("BioPx")|| temp.equalsIgnoreCase("bio") ) {
        			temp = "Biography";
        		} else if(temp.equalsIgnoreCase("noir")) {
        			temp = "Noir";
        		} else if(temp.equalsIgnoreCase("col TV") || temp.equalsIgnoreCase("bnw TV") || temp.equalsIgnoreCase("TV") || temp.equalsIgnoreCase("TVmini")) {
        			temp = "TV";
        		} else if(temp.equalsIgnoreCase("Surl") || temp.equalsIgnoreCase("Surreal") || temp.equalsIgnoreCase("Surr")) {
        			temp = "Surreal";
        		} else if(temp.equalsIgnoreCase("Romt Actn") || temp.equalsIgnoreCase("RomtAdvt")) {
        			temp = "Romance";
        			movieGenres.add("Action");
        		} else if(temp.equalsIgnoreCase("Comd West")) {
        			temp = "Comedy";
        			movieGenres.add("Western");
        		} else if(temp.equalsIgnoreCase("Noir Comd Romt")) {
        			temp = "Noir";
        			movieGenres.add("Comedy");
        			movieGenres.add("Romance");
        		} else {
        			System.out.println("The following genre is not accepted: " + temp);
        		}
        		
        		mapClass<String, Integer> map2 = new mapClass<String, Integer>(genres);
        		if(map2.add(temp, maxGId+1))
        		{
        			++maxGId;
        			String newGenre = Integer.toString(genres.get(temp)) + "|" + temp + "\n";
        			try {
						writerB.write(newGenre);
					} catch (IOException e) {
						e.printStackTrace();
					}
        		}
        		
        		String gim = genres.get(temp) + "|" + movies.get(movie);
        		if(genres_in_movies.add(gim)) {
        			gim += "\n";
        			try {
						writerC.write(gim);
					} catch (IOException e) {
						e.printStackTrace();
					}
        		}
        	}
        	
        	movieGenres.clear();
        } else if (qName.equalsIgnoreCase("fid")) {
        	xmlId = (tempVal.length() > 0) ? tempVal : null;
        } else if (qName.equalsIgnoreCase("t")) {
            movieName = (tempVal.length() > 0) ? tempVal : null;
        } else if (qName.equalsIgnoreCase("year")) {
            movieYear = (tempVal.length() > 0) ? tempVal : null;
        } else if (qName.equalsIgnoreCase("dirn") && movieDir == null) {
        	movieDir = (tempVal.length() > 0) ? tempVal : null;
        } else if (qName.equalsIgnoreCase("cat")) {
        	if (tempVal.length() == 0) {
        		System.out.println("one of genres was empty for " + xmlId + ", " + movieName + ", " + movieYear + ", " + movieDir);
        	}
        	movieGenres.add(tempVal);
        } else if (qName.equalsIgnoreCase("movies")) {
        	try {
				writerA.close();
				writerB.close();
				writerC.close();

				
				String query = "load data local infile 'movies.txt' into table movies "
					  + "columns terminated by '|' "
					  + "lines terminated by '\\n';";
				statement.execute(query);
				
				query = "load data local infile 'genres.txt' into table genres "
						+ "columns terminated by '|' "
						+ "lines terminated by '\\n';";
				statement.execute(query);
				
				query = "load data local infile 'genres_in_movies.txt' into table genres_in_movies "
						+ "columns terminated by '|' "
						+ "lines terminated by '\\n';";
				statement.execute(query);
				
			} catch (IOException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
        }
    }

}