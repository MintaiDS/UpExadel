package by.bsu.fpmi.chat;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class XMLStorage {
    private static final String STORAGE_LOCATION = System.getProperty("user.home") +  File.separator + "history.xml"; // history.xml will be located in the home directory
    private static final String MESSAGES = "messages";
    private static final String MESSAGE = "content";
    private static final String ID = "id";
    private static final String AUTHOR = "author";
    private static final String TEXT = "text";
    private static final String DATE = "date";

    private XMLStorage() {
    }

    public static synchronized void createStorage() throws ParserConfigurationException, TransformerException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement(MESSAGES);
        doc.appendChild(rootElement);

        Transformer transformer = getTransformer();

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(STORAGE_LOCATION));
        transformer.transform(source, result);
    }

    public static synchronized void addData(Message m) throws ParserConfigurationException, SAXException, IOException, TransformerException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(STORAGE_LOCATION);
        document.getDocumentElement().normalize();

        Element root = document.getDocumentElement(); // Root <messages> element

        Element messageElement = document.createElement(MESSAGE);
        root.appendChild(messageElement);

        messageElement.setAttribute(ID, m.getId());

        Element author = document.createElement(AUTHOR);
        author.appendChild(document.createTextNode(m.getAuthor()));
        messageElement.appendChild(author);

        Element text = document.createElement(TEXT);
        text.appendChild(document.createTextNode(m.getText()));
        messageElement.appendChild(text);

        Element date = document.createElement(DATE);
        date.appendChild(document.createTextNode(m.getDate()));
        messageElement.appendChild(date);

        DOMSource source = new DOMSource(document);

        Transformer transformer = getTransformer();

        StreamResult result = new StreamResult(STORAGE_LOCATION);
        transformer.transform(source, result);
    }

    public static synchronized void updateData(Message m) throws ParserConfigurationException, SAXException, IOException, TransformerException, XPathExpressionException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(STORAGE_LOCATION);
        document.getDocumentElement().normalize();
        Node messageToUpdate = getNodeById(document, m.getId());

        if (messageToUpdate != null) {

            NodeList childNodes = messageToUpdate.getChildNodes();

            for (int i = 0; i < childNodes.getLength(); i++) {

                Node node = childNodes.item(i);

                if (AUTHOR.equals(node.getNodeName())) {
                    node.setTextContent(m.getAuthor());
                }

                if (TEXT.equals(node.getNodeName())) {
                    node.setTextContent(m.getText());
                }

                if (DATE.equals(node.getNodeName())) {
                    node.setTextContent(m.getDate());
                }

            }

            Transformer transformer = getTransformer();

            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new File(STORAGE_LOCATION));
            transformer.transform(source, result);
        } else {
            throw new NullPointerException();
        }
    }

    public static synchronized boolean doesStorageExist() {
        File file = new File(STORAGE_LOCATION);
        return file.exists();
    }

    public static synchronized List<Message> getMessages() throws SAXException, IOException, ParserConfigurationException {
        List<Message> messages = new ArrayList<>();
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(STORAGE_LOCATION);
        document.getDocumentElement().normalize();
        Element root = document.getDocumentElement(); // Root <messages> element
        NodeList messageList = root.getElementsByTagName(MESSAGE);
        for (int i = 0; i < messageList.getLength(); i++) {
            Element messageElement = (Element) messageList.item(i);
            String id = messageElement.getAttribute(ID);
            String author = messageElement.getElementsByTagName(AUTHOR).item(0).getTextContent();
            String text = messageElement.getElementsByTagName(TEXT).item(0).getTextContent();
            String date = messageElement.getElementsByTagName(DATE).item(0).getTextContent();
            messages.add(new Message(id, author, text, date));
        }
        return messages;
    }

    public static synchronized int getStorageSize() throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(STORAGE_LOCATION);
        document.getDocumentElement().normalize();
        Element root = document.getDocumentElement(); // Root <messages> element
        return root.getElementsByTagName(MESSAGE).getLength();
    }

    private static Node getNodeById(Document doc, String id) throws XPathExpressionException {
        XPath xpath = XPathFactory.newInstance().newXPath();
        XPathExpression expr = xpath.compile("//" + MESSAGE + "[@id='" + id + "']");
        return (Node) expr.evaluate(doc, XPathConstants.NODE);
    }

    private static Transformer getTransformer() throws TransformerConfigurationException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        // Formatting XML properly
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        return transformer;
    }

}