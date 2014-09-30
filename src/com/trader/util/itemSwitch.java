package com.trader.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: Cookie Taker
 * Date: 12/30/12
 * Time: 2:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class itemSwitch {
    InputStream itemsFile;
    Document itemsDoc = null;
    HexValueParser hexParser = new HexValueParser();

    public itemSwitch(){
        try {
            itemsFile = this.getClass().getResourceAsStream("items.xml");
            itemsDoc = readXml(itemsFile);
            itemsDoc.getDocumentElement().normalize();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public String idToName(int num){
        NodeList nList = itemsDoc.getElementsByTagName("Object");
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
            Element elem = (Element)nNode;
            int typeNum = hexParser.parseInt(elem.getAttribute("type"));
            if(num == typeNum)return elem.getAttribute("id");
        }
        return null;
    }

    public int nameToId(String name){
        NodeList nList = itemsDoc.getElementsByTagName("Object");
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
            Element elem = (Element)nNode;
            int typeNum = hexParser.parseInt(elem.getAttribute("type"));
            if(elem.getAttribute("id").contains(name))return typeNum;
        }
        return -1;
    }

    public List<String> getItemStrings(){
        List<String> itemList = new Vector<String>();

        NodeList nList = itemsDoc.getElementsByTagName("Object");
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
            Element elem = (Element)nNode;
            itemList.add(elem.getAttribute("id"));
        }

        return itemList;
    }

    public List<Integer> getItemIds(){
        List<Integer> itemList = new Vector<Integer>();
        NodeList nList = itemsDoc.getElementsByTagName("Object");
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
            Element elem = (Element)nNode;
            itemList.add(hexParser.parseInt(elem.getAttribute("type")));
        }

        return itemList;
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
