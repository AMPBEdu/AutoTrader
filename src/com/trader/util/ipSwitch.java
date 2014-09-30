package com.trader.util;

import com.sun.jndi.toolkit.url.UrlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: PC
 * Date: 12.3.12
 * Time: 11:54
 * Converts IP from 50.60.70.80 to ec2-50-60-70-80.compute-1.amazonaws.com
 * And vice versa
 */
public class ipSwitch {

    InputStream serversFile;
    Document serversDoc = null;
    HexValueParser hexParser = new HexValueParser();


    public ipSwitch(){
        try {
            serversFile = this.getClass().getResourceAsStream("servers.xml");
            serversDoc = readXml(serversFile);
            serversDoc.getDocumentElement().normalize();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public String shorten(String longIP){
        String newString = longIP;
        newString = newString.replaceAll("ec2-","");
        int killerPos = newString.indexOf(".");
        newString = newString.substring(0, killerPos);
        newString = newString.replaceAll("-",".");
        System.out.println("SUPERINT: "+killerPos);
        System.out.println("SUPERSTRING: "+newString);
        return newString;
    }

    public String lengthen(String shortIP){
        String newShortIP = shortIP.replaceAll("\\.","-");
        String newString = "ec2-";
        newString += newShortIP;
        newString += ".compute-1.amazonaws.com";
        return newString;
    }

    public List<String> getServerNames(){
        List<String> serverList = new Vector<String>();
        NodeList nList = serversDoc.getElementsByTagName("Server");
        for (int temp = 0; temp < nList.getLength(); temp++) {

            Node nNode = nList.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                serverList.add(getTagValue("Name", eElement));
            }
        }
        return serverList;
    }

    public List<String> getServerDNSs(){
        List<String> serverList = new Vector<String>();
        NodeList nList = serversDoc.getElementsByTagName("Server");
        for (int temp = 0; temp < nList.getLength(); temp++) {

            Node nNode = nList.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                serverList.add(getTagValue("DNS", eElement));
            }
        }

        return serverList;
    }

    public String getHostName(int num){
        NodeList nList = serversDoc.getElementsByTagName("Server");
        for (int temp = 0; temp < nList.getLength(); temp++) {

            Node nNode = nList.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                if(11+temp == num)return getTagValue("Name", eElement);
            }
        }
        return null;
    }

    public String getHostDNS(int num){
        NodeList nList = serversDoc.getElementsByTagName("Server");
        for (int temp = 0; temp < nList.getLength(); temp++) {

            Node nNode = nList.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;

                if(11+temp == num)return getTagValue("DNS", eElement);
            }
        }
        return null;
    }

    public static Document readXml(InputStream is) throws SAXException, IOException,
            ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        dbf.setValidating(false);
        dbf.setIgnoringComments(false);
        dbf.setIgnoringElementContentWhitespace(true);
        dbf.setNamespaceAware(true);
        // dbf.setCoalescing(true);
        // dbf.setExpandEntityReferences(true);

        DocumentBuilder db = null;
        db = dbf.newDocumentBuilder();
        db.setEntityResolver(new NullResolver());

        // db.setErrorHandler( new MyErrorHandler());

        return db.parse(is);
    }

    public static String getTagValue(String sTag, Element eElement) {
        NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();

        Node nValue = (Node) nlList.item(0);

        return nValue.getNodeValue();
    }
}