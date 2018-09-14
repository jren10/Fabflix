
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

public class ActorsParser extends DefaultHandler {

    List<Actors> myActors;
    

    private String tempVal;

    //to maintain context
    private Actors tempAct;

    public ActorsParser() {
        myActors = new ArrayList<Actors>();
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
            sp.parse("actors63.xml", this);

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

        System.out.println("No of Actors '" + myActors.size() + "'.");

        Iterator<Actors> it = myActors.iterator();
        while (it.hasNext()) {
            System.out.println(it.next().toString());
        }
    }

    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("actor")) {
            //create a new instance of actors
            tempAct = new Actors();
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (qName.equalsIgnoreCase("actor")) {
            //add it to the list
            myActors.add(tempAct);

        } else if (qName.equalsIgnoreCase("stagename")) {
            tempAct.setName(tempVal);
        } else if (qName.equalsIgnoreCase("dob")) {
        	if (tempVal.equals("")){
        		tempAct.setDob("NA");
        	}
        	else {
        		tempAct.setDob(tempVal);
        	}
        }

    }

    public static void main(String[] args) {
        ActorsParser spe = new ActorsParser();
        spe.runExample();
    }

}
