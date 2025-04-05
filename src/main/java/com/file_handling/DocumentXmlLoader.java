package com.file_handling;

import com.utilty.Serializer;
import org.springframework.stereotype.Component;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import javax.annotation.*;
import javax.xml.parsers.*;
import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.*;
import java.util.zip.*;

@Component
public final class DocumentXmlLoader implements XmlLoader<Document> {
    private final String XML_PATH =
            "C:\\Software Development\\Java Projects\\Full Text Search Engine\\src\\main\\resources\\fullTextWikipediaData.xml.gz";
    private final String SER_PATH =
            "C:\\Software Development\\Java Projects\\Full Text Search Engine\\src\\main\\resources\\fullTextWikipediaData.ser.gz";
    private final int PREDICTED_SIZE = 1000000;
    private List<Document> documents;

    public List<Document> getDocuments() {
        return documents;
    }

    @PostConstruct
    public void load() {
        try {
            loadSerialized();
        } catch (Exception e) {
            getFromXmlFile();
        }
    }

    private void getFromXmlFile() {
        try {
            documents = new ArrayList<>(PREDICTED_SIZE);
            InputStream fileStream = openGzipStream();
            parseXml(fileStream, documents);
            save();
        }
        catch (Exception e) {
            System.out.println("Encountered a problem loading the xml file of pages...");
        }
    }

    @Override
    public void loadSerialized() throws Exception{
        documents = Serializer.load(SER_PATH);
    }

    public void save() {
        Serializer.save(SER_PATH, documents);
    }

    private InputStream openGzipStream() throws IOException {
        return new GZIPInputStream(Files.newInputStream(new File(XML_PATH).toPath()));
    }

    private void parseXml(InputStream fileStream, List<Document> documents) throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        saxParser.parse(fileStream, createHandler(documents));
    }

    private DefaultHandler createHandler(List<Document> documents) {
        return new DefaultHandler() {
            private Document currentDocument;
            private StringBuilder currentValue;
            private int counter;

            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes) {
                if ("page".equalsIgnoreCase(qName)) {
                    currentDocument = new Document();
                }
                currentValue = new StringBuilder();
            }

            @Override
            public void endElement(String uri, String localName, String qName) {
                if (currentDocument != null) {
                    switch (qName.toLowerCase()) {
                        case "title":
                            currentDocument.setTitle(currentValue.toString());
                            break;
                        case "text": // Full Wikipedia page text
                            String text = currentValue.toString();
                            currentDocument.setText(text);
                            extractLinks(text, currentDocument);
                            break;
                        case "page":
                            currentDocument.setId(counter++);
                            documents.add(currentDocument);
                            break;
                    }
                }
            }

            @Override
            public void characters(char[] ch, int start, int length) {
                currentValue.append(ch, start, length);
            }
        };
    }

    private void extractLinks(String text, Document document) {
        Pattern linkPattern = Pattern.compile("\\[\\[([^\\]|#]+)");
        Matcher matcher = linkPattern.matcher(text);
        while (matcher.find()) {
            document.addOutgoingLink(matcher.group(1).trim()); // Store link without anchor text
        }
    }
}
