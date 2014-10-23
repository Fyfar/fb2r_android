/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fb2parser;

import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.text.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 *
 * @author Александр
 */
public class DataAccess {

    public static Collection<String> OpenFile(String path) throws Exception {
        Collection list = new ArrayList<String>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                //System.out.println(sCurrentLine);
                list.add(sCurrentLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void SaveFileLikeTXT(Collection<String> collection) {
        Iterator iter = collection.iterator();
        try (PrintWriter writer = new PrintWriter("the-file-name.txt", "UTF-8")) {
            while (iter.hasNext()) {
                String nextElement = (String) iter.next();
                writer.println(nextElement);
            }
            writer.close();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public static void tryToParseXML(String path) throws ParserConfigurationException, SAXException, IOException {
        // упрощенный пример 
        File f = new File(path);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        org.w3c.dom.Document document = builder.parse(f);
        Element root = document.getDocumentElement();
        // для простоты сразу берем message
        Element message = (Element) root.getElementsByTagName("author").item(0);
        String textContent = message.getTextContent(); // тоже для упрощения
        System.out.println(textContent);
        message = (Element) root.getElementsByTagName("title-info").item(0);
        textContent = message.getTextContent(); // тоже для упрощения
        System.out.println(textContent);
        message = (Element) root.getElementsByTagName("document-info").item(0);
        textContent = message.getTextContent(); // тоже для упрощения
        System.out.println(textContent);
        message = (Element) root.getElementsByTagName("description").item(0);
        textContent = message.getTextContent(); // тоже для упрощения
        System.out.println(textContent);
        message = (Element) root.getElementsByTagName("publish-info").item(0);
        textContent = message.getTextContent(); // тоже для упрощения
        System.out.println(textContent);
    }
}
