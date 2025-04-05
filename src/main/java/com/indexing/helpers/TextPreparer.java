package com.indexing.helpers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.ArrayList;

@Component
public class TextPreparer{
	private static final String[] STOP_WORDS = {"a", "and", "be", "have", "i", "in", "of", "that", "the", "to"};

	@Autowired
	private Stemmer stemmer;

	@Autowired
	private WordNetUtil wordnetObj;
	
	public List<String> tokenize(String text) {
		String[] tokens = text.split("[^a-zA-Z0-9]+");
		List<String> filteredTokens = new ArrayList<>();
        for (String token : tokens) {
            token = token.toLowerCase();
            if (!isStopWord(token)) {
				if (wordnetObj.isVerb(stemmer.stem(token)))
					token = stemmer.stem(token);
				filteredTokens.add(token);
			}
        }
        return filteredTokens;
	}
        
      public boolean isStopWord(String token) {
            for (String stopWord: STOP_WORDS)
            	if (token.equals(stopWord))
            		return true;
            return false;
      }
}