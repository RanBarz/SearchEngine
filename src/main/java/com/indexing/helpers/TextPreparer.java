package com.indexing.helpers;

import java.util.List;
import java.io.Serializable;
import java.util.ArrayList;

public class TextPreparer implements Serializable{
	private static final long serialVersionUID = 1L;
	private final String[] stopWords;
	private final Stemmer stemmer;
	
	public TextPreparer(String[] stopWords) {
		this.stopWords = stopWords;
		stemmer = PorterStemmer.getInstance();
	}
	
	public List<String> tokenize(String text) {
		String[] tokens = text.split("[^a-zA-Z0-9]+");
		List<String> filteredTokens = new ArrayList<>();
        for (String token : tokens) {
            token = token.toLowerCase();
            if (!isStopWord(token)) {
				token = stemmer.stem(token);
				filteredTokens.add(token);
			}
        }
        return filteredTokens;
	}
        
      public boolean isStopWord(String token) {
            for (String stopWord: stopWords) 
            	if (token.equals(stopWord))
            		return true;
            return false;
      }
}