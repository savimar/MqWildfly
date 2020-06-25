package ru.savimar.mqwildfly.service;


import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import ru.savimar.mqwildfly.Entity.Person;
import ru.savimar.mqwildfly.Entity.PersonDocument;
import ru.savimar.mqwildfly.Repository.PersonRepository;
import ru.savimar.mqwildfly.xml.SAXHandler;
import ru.savimar.mqwildfly.xml.StaxStreamProcessor;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.xml.XMLConstants;
import javax.xml.parsers.*;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDateTime;
import org.joda.time.LocalDate;

@Stateless
public class XMLService {

    @EJB
    PersonRepository repository;
    private static final Logger LOGGER = LoggerFactory.getLogger(XMLService.class);


    public List<String> parseXPath(String xml) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException {
        List<String> lastNames = new ArrayList<>();
        Document doc = getDomElement(xml);
        doc.getDocumentElement().normalize();

        XPath xPath = XPathFactory.newInstance().newXPath();
        String expression = "//*[local-name()='LastName']/text()";
        NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);

        for (int i = 0; i < nodeList.getLength(); i++) {
            String lastName = nodeList.item(i).getTextContent();
            lastNames.add(lastName);
            System.out.println(String.format("Фамилия: %s", lastName));
        }

        LOGGER.info(String.format("message parseXPath= %s", lastNames));
        return lastNames;
    }

    private Document getDomElement(String xml) {
        Document doc;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        try {

            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));
            doc = db.parse(is);

        } catch (ParserConfigurationException e) {
            LOGGER.error("Error: " + e.getMessage());
            return null;
        } catch (SAXException e) {
            LOGGER.error("Error: " + e.getMessage());
            return null;
        } catch (IOException e) {
            LOGGER.error("Error: " + e.getMessage());
            return null;
        }
        return doc;
    }


    public List<String> parseStax(String xml) {
        List<String> firstNames = new ArrayList<>();
        try (StaxStreamProcessor processor = new StaxStreamProcessor(xml)) {
            XMLStreamReader reader = processor.getReader();
            while (reader.hasNext()) {
                int event = reader.next();
                if (event == XMLEvent.START_ELEMENT &&
                        "FirstName".equals(reader.getLocalName())) {
                    firstNames.add(reader.getElementText());
                }
            }
            for (String str : firstNames) {
                System.out.println(String.format("Имя: %s", str));
            }
        } catch (XMLStreamException e) {
            LOGGER.error("Error: " + e.getMessage());
            return null;
        } catch (Exception e) {
            LOGGER.error("Error: " + e.getMessage());
            return null;
        }
        LOGGER.info(String.format("message parseXPath= %s", firstNames));
        return firstNames;
    }

    public List<String> parseSax(String xml) throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        SAXParser parser = factory.newSAXParser();
        List<String> middleNames = new ArrayList<>();

        try {
            SAXHandler handler = new SAXHandler();
            parser.parse(new InputSource(new StringReader(xml)), handler);
            middleNames = handler.getMiddleNames();

            for (String str : middleNames) {
                System.out.println(String.format("Отчество: %s", str));
            }


        } catch (SAXException e) {
            LOGGER.error("Error: " + e.getMessage());
        } catch (IOException e) {
            LOGGER.error("Error: " + e.getMessage());
        }
        LOGGER.info(String.format("message parseXPath= %s", middleNames));
        return middleNames;
    }

    public boolean checkXMLforXSD(String pathXml, String pathXsd)
            throws Exception {

        try {
            File xml = new File(pathXml);
            File xsd = new File(pathXsd);

            if (!xml.exists()) {
                System.out.println("Не найден XML " + pathXml);
            }

            if (!xsd.exists()) {
                System.out.println("Не найден XSD " + pathXsd);
            }

            if (!xml.exists() || !xsd.exists()) {
                return false;
            }

            SchemaFactory factory = SchemaFactory
                    .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new StreamSource(pathXsd));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(pathXml));
            return true;
        } catch (SAXException e) {
            e.printStackTrace();
            LOGGER.error("Error: " + e.getMessage());
            return false;
        }

    }

    public void parseToDb(String xml) throws XPathExpressionException {
        Document doc = getDomElement(xml);
        doc.getDocumentElement().normalize();
        XPath xPath = XPathFactory.newInstance().newXPath();
        String expression = "//*[local-name()='BaseDeclarant']/node()";
        NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);
        PersonDocument document = null;
        Person person = null;

        for (int temp = 0; temp < nodeList.getLength(); temp++) {

            Node node = nodeList.item(temp);
            if (node.getNodeType() == Node.ELEMENT_NODE) {

                switch (node.getLocalName()) {
                    case "Type":
                        if (person == null) {
                            person = new Person();
                        } else {
                            LOGGER.info("parseToDb person {}, document  {}", person, document);
                            repository.save(person);
                            person = new Person();
                        }
                        break;
                    case "Documents":
                        document = new PersonDocument();
                        String expressionDoc = "//*[local-name()='ServiceDocument']/node()";
                        NodeList nodeListDoc = (NodeList) xPath.compile(expressionDoc).evaluate(node, XPathConstants.NODESET);
                        for (int i = 0; i < nodeListDoc.getLength(); i++) {
                            Node nodeDoc = nodeListDoc.item(i);
                            if (nodeDoc.getNodeType() == Node.ELEMENT_NODE) {
                                switch (nodeDoc.getLocalName()) {
                                    case "DocSubType":
                                        document.setDocSubType(Integer.parseInt(nodeDoc.getTextContent()));
                                        break;
                                    case "DocSerie":
                                        document.setDocSerie(nodeDoc.getTextContent());
                                        break;
                                    case "DocNumber":
                                        document.setDocNumber(nodeDoc.getTextContent());
                                        break;
                                    case "DocDate":
                                        document.setDocDate(LocalDateTime.parse(nodeDoc.getTextContent()));
                                        break;
                                    case "WhoSign":
                                        document.setWhoSign(nodeDoc.getTextContent());
                                        break;
                                    case "DivisionCode":
                                        document.setDivisionCode(nodeDoc.getTextContent());
                                        break;
                                    case "DocKind":
                                        String expressionDocKind = "//*[local-name()='DocKind']/node()";
                                        NodeList nodeListDocKind = (NodeList) xPath.compile(expressionDocKind).evaluate(nodeDoc, XPathConstants.NODESET);
                                        for (int j = 0; j < nodeListDocKind.getLength(); j++) {
                                            if (nodeListDocKind.item(j).getNodeType() == Node.ELEMENT_NODE && nodeListDocKind.item(j).getLocalName().equals("Name")) {
                                                document.setDocKindName(nodeListDocKind.item(j).getTextContent());
                                            }
                                        }
                                        break;
                                    case "DocFiles":
                                        String expressionDocFiles = "//*[local-name()='CoordinateFileReference']/node()";
                                        NodeList nodeListDocFiles = (NodeList) xPath.compile(expressionDocFiles).evaluate(nodeDoc, XPathConstants.NODESET);
                                        for (int k = 0; k < nodeListDocFiles.getLength(); k++) {
                                            if (nodeListDocFiles.item(k).getNodeType() == Node.ELEMENT_NODE && nodeListDocFiles.item(k).getLocalName().equals("Id")) {
                                                document.setDocFileId(nodeListDocFiles.item(k).getTextContent());
                                            }
                                        }
                                        break;
                                    default:
                                        break;
                                }
                            }

                        }
                        List<PersonDocument> documents = new ArrayList<>();
                        if (document.getDocKindName() != null) {
                            documents.add(document);
                            person.setDocuments(documents);
                        }
                        break;
                    case "LastName":
                        person.setLastName(node.getTextContent());
                        break;
                    case "FirstName":
                        person.setFirstName(node.getTextContent());
                        break;
                    case "MiddleName":
                        person.setMiddleName(node.getTextContent());
                        break;
                    case "BirthDate":
                        person.setBirthDate(LocalDate.parse(node.getTextContent()));
                        break;
                    case "Snils":
                        person.setSnils(node.getTextContent());
                        break;
                    case "MobilePhone":
                        person.setMobilePhone(node.getTextContent());
                        break;
                    case "SsoId":
                        person.setSsoId(node.getTextContent());
                        break;
                    default:
                        break;
                }
            }
        }
        LOGGER.info("parseToDb person {}, document  {}", person, document);
        repository.save(person);
    }

}
