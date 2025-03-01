package com.file_handling;

import java.util.StringTokenizer;

public class Document {
    private String title;
    private String url;
    private String text;
    private int wordCount;
    private int id;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getWordCount() { return wordCount; }

    public void setWordCount() {
        if (text == null || text.isEmpty()) {
            this.wordCount = 0;
            return;
        }
        StringTokenizer tokenizer = new StringTokenizer(text);
        this.wordCount =  tokenizer.countTokens();
    }
}
