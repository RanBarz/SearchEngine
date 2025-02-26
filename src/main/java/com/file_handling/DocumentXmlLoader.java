package com.file_handling;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

public final class DocumentXmlLoader implements XmlLoader<Document>{
	private int predictedSize;
	
	public DocumentXmlLoader(int predictedSize) {
		this.predictedSize = predictedSize;
	}
	
    public List<Document> load(String path) {
        List<Document> documents = new ArrayList<>(predictedSize);
        try (InputStream fileStream = new GZIPInputStream(new FileInputStream(new File(path)))) {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            DefaultHandler handler = new DefaultHandler() {
                private Document currentDocument;
                private StringBuilder currentValue;
                private int counter;

                @Override
                public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                    if (qName.equalsIgnoreCase("doc")) {
                        currentDocument = new Document();
                        currentDocument.setId(counter);
                        counter++;
                    }
                    currentValue = new StringBuilder();
                }

                @Override
                public void endElement(String uri, String localName, String qName) throws SAXException {
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
                public void characters(char[] ch, int start, int length) throws SAXException {
                    currentValue.append(ch, start, length);
                }
            };

            saxParser.parse(fileStream, handler);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return documents;
    }
}
