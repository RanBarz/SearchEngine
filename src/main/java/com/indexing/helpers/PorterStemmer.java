package com.indexing.helpers;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PorterStemmer implements Stemmer, Serializable{
   private static final long serialVersionUID = 1L;
   private Map<String, String> dictionary = new HashMap<>();
   private static final Character[] vowelArr = new Character[] {'a', 'e', 'o', 'u', 'i'};
   private static final Set<Character> vowelSet = new HashSet<>(Arrays.asList(vowelArr));
   private static PorterStemmer singleton;
   private String stem;
   private int measure;
   
   private PorterStemmer() {}
   
   public static PorterStemmer getInstance() {
	   if (singleton == null) 
		   singleton = new PorterStemmer();
	   return singleton;
   }
   
   public String stem(String word) {
	   if (dictionary.containsKey(word))
    	   return dictionary.get(word);
       stem = word;
       measure = getMeasure(stem);
       if (stem.length() <= 2) {
           return stem;
       }
       step1a();
       step1b();
       step1c();
       step2();
       step3();
       step4();
       step5a();
       step5b();
       dictionary.put(word, stem);
       return stem;
   }

   private void step1a() {
	   if (stem.endsWith("sses")) {
		   stem = stem.substring(0, stem.length() - 2);
       } else if (stem.endsWith("ies")) {
    	   stem = stem.substring(0, stem.length() - 2);
       } else if (stem.endsWith("ss")) {
           return;
       } else if (stem.endsWith("s")) {
           if (containsVowel(stem.substring(0, stem.length() - 1))) {
        	   stem = stem.substring(0, stem.length() - 1);
           }
       }
   }
   
   private void step1b() {
	   boolean flag = false;
	   if (stem.endsWith("eed") && 0 < getMeasure(stem.substring(0, stem.length() - 3)))
		   stem = stem.substring(0, stem.length() - 2);
	   else if (stem.endsWith("ed") && containsVowel(stem.substring(0, stem.length() - 2))) {
		   stem = stem.substring(0, stem.length() - 2);	
		   flag = true;
	   }
	   else if (stem.endsWith("ing") && containsVowel(stem.substring(0, stem.length() - 3))) {
		   stem = stem.substring(0, stem.length() - 3);
		   flag = true;
	   }
	   if (flag) {
		   if (stem.endsWith("at") || stem.endsWith("bl") ||stem.endsWith("iz") || (getMeasure(stem) == 1 && endsWithCvc(stem)))
			   stem += "e";
		   else if (endsWithDoubleConsonant(stem) && !stem.endsWith("l") && !stem.endsWith("z") && !stem.endsWith("s"))
			   stem = stem.substring(0, stem.length() - 1);
	   }
   }
   
   private void step1c() {
	   if (stem.endsWith("y") && containsVowel(stem.substring(0, stem.length() - 1)))
		   stem = replaceEnding(stem, "y", "i");
   }
   
   private void step2() {
	   if (stem.length() < 4)
	    	return;
	    if (measure == 0)
	    	return;
	    String[] s1 = new String[]{"ational", "tional", "enci", "anci", "izer", "bli", "alli", "entli", "eli", "ousli", "ization", "ation", "ator", "alism", "iveness", "fulness", "ousness", "aliti", "iviti", "biliti", "logi"};
	    String[] s2 = new String[]{"ate", "tion", "ence", "ance", "ize", "ble", "al", "ent", "e", "ous", "ize", "ate", "ate", "al", "ive", "ful", "ous", "al", "ive", "ble", "log"};	    
	    for (int i = 0; i < s1.length; i++) {
	        if (stem.endsWith(s1[i])) {
	            stem = replaceEnding(stem, s1[i], s2[i]);
	            return;
	        }
	    }
	}
   
   private void step3() {
	   if (measure == 0)
		   return;
	   if (stem.length() < 3)
		   return;
	   String[] s1 = new String[]{"icate", "ative", "alize", "iciti", "ical", "ful", "ness"};
	   String[] s2 = new String[]{"ic", "", "al", "ic", "ic", "", ""};
	   for (int i = 0; i < s1.length; i++) {
	       if (stem.endsWith(s1[i])) {
	    	   stem = replaceEnding(stem, s1[i], s2[i]);
	       }
	   }
   }
   
   private void step4() {
	   if (measure <= 1)
		   return;
	   if (stem.length() < 2)
		   return;
	   String[] suffixes = new String[]{"al", "ance", "ence", "er", "ic", "able", "ible", "ant", "ement", "ment", "ent", "ion", "ou", "ism", "ate", "iti", "ous", "ive", "ize"};
		for (String suffix : suffixes) {
		    if (stem.endsWith(suffix)) {
		    	if (suffix.equals("ion"))
		    		if (stem.substring(0, stem.length() - 3).equals("s") || stem.substring(0, stem.length() - 3).equals("t"))
		    			 stem = replaceEnding(stem, suffix, "");
		    	else
		    		stem = replaceEnding(stem, suffix, "");	
		    }
		}
   }
   
   public void step5a() {
	   if (stem.endsWith("e")) {
           String stemWithoutE = stem.substring(0, stem.length() - 1);
           if (getMeasure(stemWithoutE) > 0 || 
               (getMeasure(stemWithoutE) == 1 && !endsWithCvc(stem))) 
        	   stem = stemWithoutE;
       }
   }
   
   public void step5b() {
	   if (measure > 1 && endsWithDoubleConsonant(stem) && stem.endsWith("l"))
	   		stem = replaceEnding(stem, "l", "");
   }
       
   private static boolean containsVowel(String word) {
	   char[] str = word.toCharArray();
	   for (int i = 0; i < str.length; i++) {
		   char previousChar = (i > 0) ? str[i-1] : ' ';
		   if (isVowel(str[i], previousChar))
			   return true;
	   }
	   return false;
   }
   
   private static boolean endsWithDoubleConsonant(String word) {
	   char[] str = word.toCharArray();
	   letterTypes(str);
	   if (str.length < 2)
		   return false;
	   if (str[str.length - 1] == 'c' && str[str.length - 2] == 'c')
		   return true;
	   return false;
   }
   
   private static boolean endsWithCvc(String word) {
	   char[] str = word.toCharArray();
	   letterTypes(str);
	   if (str.length < 3)
		   return false;
	   if (str[str.length - 1] == 'c' && str[str.length - 2] == 'v' && str[str.length - 3] == 'c' 
			   && str[str.length - 1] != 'x' && str[str.length - 1] != 'y' && str[str.length - 1] != 'w')
		   return true;
	   return false;   		   
   }
   
   private static int getMeasure(String word) {
	   int measure = 0;
	   char[] str = word.toCharArray();
	   letterTypes(str);
	   for (int i = 0; i < str.length; i++) {
		   char previousChar = (i > 0) ? str[i-1] : ' ';
		   if (previousChar == 'v' && str[i] == 'c')
			   measure++;
	   }
	   return measure;
   }
   
   
   private static void letterTypes(char[] str) {
	    for (int i = 0; i < str.length; i++) {
	        char previousChar = (i > 0) ? str[i-1] : ' ';
	        if (isVowel(str[i], previousChar))
	            str[i] = 'v';
	        else
	            str[i] = 'c';
	    }
   }      
   
   private static boolean isVowel(char c, char previousChar) {
	   if (vowelSet.contains(c))
		   return true;
	   if (!vowelSet.contains(previousChar) && c == 'y')
		   return true;
	   return false;
   }
   
   private static String replaceEnding(String word, String oldEnding, String newEnding) {
       if (word.endsWith(oldEnding)) {
           word = word.substring(0, word.length() - oldEnding.length()) + newEnding;
       }
       return word;
   }
}