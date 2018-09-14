import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;

public class MoviesParser extends DefaultHandler {

    List<Movies> myMovies;

    private String tempVal;
    private String dirName;

    //to maintain context
    private Movies tempMov;

    public MoviesParser() {
        myMovies = new ArrayList<Movies>();
    }

    public void runExample() {
        parseDocument();
        printData();
    }

    private void parseDocument() {

        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse("mains243.xml", this);

        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    /**
     * Iterate through the list and print
     * the contents
     */
    private void printData() {

        System.out.println("No of Movies '" + myMovies.size() + "'.");

        Iterator<Movies> it = myMovies.iterator();
        while (it.hasNext()) {
            System.out.println(it.next().toString());
        }
    }

    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("film")) {
            //create a new instance of directorfilm
            tempMov = new Movies();
            //tempEmp.setType(attributes.getValue("type"));
            
        } else if (qName.equalsIgnoreCase("dirname")) {
        	dirName = "";
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {

    	/*
        if (qName.equalsIgnoreCase("Employee")) {
            //add it to the list
            myEmpls.add(tempEmp);

        } else if (qName.equalsIgnoreCase("Name")) {
            tempEmp.setName(tempVal);
        } else if (qName.equalsIgnoreCase("Id")) {
            tempEmp.setId(Integer.parseInt(tempVal));
        } else if (qName.equalsIgnoreCase("Age")) {
            tempEmp.setAge(Integer.parseInt(tempVal));
        }
        */
    	if (qName.equalsIgnoreCase("dirname")) {
        	dirName = tempVal;
        } else if (qName.equalsIgnoreCase("film")) {
            //add it to the list
        	tempMov.setDirector(dirName);
            myMovies.add(tempMov);

        } else if (qName.equalsIgnoreCase("fid")) {
            tempMov.setId(tempVal);
        } else if (qName.equalsIgnoreCase("t")) {
            tempMov.setTitle(tempVal);
        } else if (qName.equalsIgnoreCase("year")) {
            //tempMov.setYear(Integer.parseInt(tempVal));
            tempMov.setYear(tempVal);
        }

    }

    public static void main(String[] args) {
        MoviesParser spe = new MoviesParser();
        spe.runExample();
    }

}
