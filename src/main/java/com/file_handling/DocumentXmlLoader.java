package com.file_handling;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.*;
import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.zip.GZIPInputStream;

public final class DocumentXmlLoader implements XmlLoader<Document>{
	private final int predictedSize;
	
	public DocumentXmlLoader(int predictedSize) {
		this.predictedSize = predictedSize;
	}

    public List<Document> load(String path) throws Exception {
        List<Document> documents = new ArrayList<>(predictedSize);
        InputStream fileStream = openGzipStream(path);
        parseXml(fileStream, documents);
        return documents;
    }

    private InputStream openGzipStream(String path) throws IOException {
        return new GZIPInputStream(Files.newInputStream(new File(path).toPath()));
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
                if ("doc".equalsIgnoreCase(qName)) {
                    currentDocument = new Document();
                    currentDocument.setId(counter++);
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
                        case "url":
                            currentDocument.setUrl(currentValue.toString());
                            break;
                        case "abstract":
                            currentDocument.setText(currentValue.toString());
                            break;
                        case "doc":
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
}
