
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

public class CastsParser extends DefaultHandler {

    List<Casts> myCasts;
    
    List<String> TempCastList;
    private String tempVal;
    private String dirName;
    private String dirID;
    private String title;
    private String movID;
    

    //to maintain context
    private Casts tempCst;

    public CastsParser() {
    	TempCastList = new ArrayList<String>();
        myCasts = new ArrayList<Casts>();
    }

    public void runExample() {
        parseDocument();
        printData();
    }

    private void parseDocument() {

        //get a factory
        SAXParserFactory spf =SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse("casts124.xml", this);

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

        System.out.println("No of Casts '" + myCasts.size() + "'.");

        Iterator<Casts> it = myCasts.iterator();
        while (it.hasNext()) {
            System.out.println(it.next().toString());
        }
    }

    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("filmc")) {
            //create a new instance of directorfilm
            tempCst = new Casts();
            //tempEmp.setType(attributes.getValue("type"));
            
        } else if (qName.equalsIgnoreCase("is")) {
        	dirName = "";
        } else if (qName.equalsIgnoreCase("dirid")) {
        	dirID = "";
        } else if (qName.equalsIgnoreCase("f")) {
        	movID = "";
        } else if (qName.equalsIgnoreCase("t")) {
        	title = "";
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
    	if (qName.equalsIgnoreCase("is")) {
        	dirName = tempVal;
        } else if (qName.equalsIgnoreCase("dirid")) {
            dirID = tempVal;
        } else if (qName.equalsIgnoreCase("filmc")) {
            //add it to the list
        	tempCst.setDirector(dirName);
        	tempCst.setDirID(dirID);
        	tempCst.setMovID(movID);
        	tempCst.setTitle(title);
        	tempCst.setStarsList(TempCastList);
        	
            myCasts.add(tempCst);
            
            TempCastList.clear();

        } else if (qName.equalsIgnoreCase("f")) {
            movID = tempVal;
        } else if (qName.equalsIgnoreCase("t")) {
            title = tempVal;
        } else if (qName.equalsIgnoreCase("a")) {
            TempCastList.add(tempVal);
        }

    }

    public static void main(String[] args) {
        CastsParser spe = new CastsParser();
        spe.runExample();
    }

}
