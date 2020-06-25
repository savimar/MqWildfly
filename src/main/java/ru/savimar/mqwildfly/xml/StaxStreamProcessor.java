package ru.savimar.mqwildfly.xml;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;

public class StaxStreamProcessor implements AutoCloseable {

    private final XMLStreamReader reader;

    public StaxStreamProcessor(String xml) throws XMLStreamException, UnsupportedEncodingException {

        byte[] byteArray = xml.getBytes("UTF-8");
        ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArray);
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        reader = inputFactory.createXMLStreamReader(inputStream);
    }
    public XMLStreamReader getReader() {
        return reader;
    }

    @Override
    public void close() throws Exception {
        if (reader != null) {
            try {
                reader.close();
            } catch (XMLStreamException e) {
            }
        }
    }
}
