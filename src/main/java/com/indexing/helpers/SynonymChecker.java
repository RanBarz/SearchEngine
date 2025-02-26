package com.indexing.helpers;

import net.sf.extjwnl.dictionary.Dictionary;
import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.*;

public final class SynonymChecker {
	private static Dictionary dictionary;
	
    public static boolean areSynonyms(String word1, String word2) throws JWNLException  {
		if (dictionary == null)
			dictionary = Dictionary.getDefaultResourceInstance();
	    IndexWord indexWord1 = dictionary.lookupIndexWord(POS.ADJECTIVE, word1);
	    IndexWord indexWord2 = dictionary.lookupIndexWord(POS.ADJECTIVE, word2);

	    if (indexWord1 != null && indexWord2 != null) {
	        for (Synset synset : indexWord1.getSenses()) {
	            if (indexWord2.getSenses().contains(synset)) {
	                return true; 
	            }
	        }
	    }
	    return false; 
	}
}