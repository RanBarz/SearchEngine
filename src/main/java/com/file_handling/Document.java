package com.file_handling;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

public class Document implements Serializable {
    private final String BASIC_PATH = "https://en.wikipedia.org/wiki/";

    private String title;
    private String text;
    private String url;
    private int wordCount;
    private int id;
    private final Set<String> outgoingLinks;

    public Document() {
        this.outgoingLinks = new HashSet<>();
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; setUrl();}

    public String getUrl() { return url; }
    private void setUrl() {
        url = BASIC_PATH + title.replace(' ', '_');
    }

    public String getText() { return text; }
    public void setText(String text) {
        this.text = text;
        setWordCount();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getWordCount() { return wordCount; }

    private void setWordCount() {
        if (text == null || text.isEmpty()) {
            this.wordCount = 0;
            return;
        }
        StringTokenizer tokenizer = new StringTokenizer(text);
        this.wordCount = tokenizer.countTokens();
    }

    public Set<String> getOutgoingLinks() { return outgoingLinks; }
    public void addOutgoingLink(String link) { outgoingLinks.add(link); }
}
