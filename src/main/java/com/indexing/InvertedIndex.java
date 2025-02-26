package com.indexing;

import java.io.*;
import java.util.*;
import java.util.zip.*;
import net.sf.extjwnl.JWNLException;
import com.indexing.helpers.*;
import com.file_handling.*;

public class InvertedIndex implements Serializable{
	private static final long serialVersionUID = 1L;
	private Map<String, List<Integer>> index;
	private Map<String, String> synonymMap;
	private TextPreparer tp;

	public InvertedIndex(String[] stopWords, int predictedIndexSize, int predictedSynonymSize) {
		index = new HashMap<>(predictedIndexSize);
		synonymMap = new HashMap<>(predictedSynonymSize);
	    tp = new TextPreparer(stopWords);
	}
	
	
	public List<Integer> lookUp(String token) {
		return index.getOrDefault(this.findSynonymInMap(token), null);
	}
	
	public void create(List<Document> documents){
		try {
			SynonymChecker.setDictionary();
		}
		catch (JWNLException e) {
			System.out.println("createInvertedIndex couldn't load up the dictionary");
		}
        List<String> tokens;
		HashSet<String> handledTokens= new HashSet<>();
		
		for (Document doc: documents) {
	        tokens = tp.tokenize(doc.getText());
	        
	        for (String token : tokens) {
	        	if (!handledTokens.contains(token)) {
	        		addToSynonymMap(token);
	        		handledTokens.add(token);
	        	}
	        }
	        
	        addTokens(doc, tokens);
	        
	        if (doc.getId() % 1000 == 0)
	            System.out.println(String.format("%.2f%% done", (float) doc.getId() / documents.size() * 100));
			if (doc.getId() / 34500 == 1)
				break;
	    }
	}
	
	public void addTokens(Document doc, List<String> tokens) {
		String synonym;
		List<Integer> ids;
		Set<String> processedTokens = new HashSet<>();
	
		for (String token : tokens) {
	        synonym = findSynonymInMap(token);
	        
	        if (processedTokens.contains(token)) 
	            continue;
	        
	        processedTokens.add(token);
	        index.computeIfAbsent(synonym, k -> new ArrayList<>());
	        ids = index.get(synonym);
	        
	        if (!ids.isEmpty() && ids.get(ids.size() - 1).equals(doc.getId())) {
	            continue;
	        }
	        
	        ids.add(doc.getId());
	    }
	}
	
	public void addToSynonymMap(String token) {	
	    for (Map.Entry<String, String> entry : synonymMap.entrySet()) {
	    	try {
	            if (SynonymChecker.areSynonyms(token, entry.getKey())) {
	                synonymMap.put(token, entry.getKey()); 
	                return; 
	            }
	    	}
	    	catch (JWNLException e){
	    		System.out.println("addToSynonymMap had a problem with token: " + token);
	    	}
	    }
	    synonymMap.put(token, token);
	}
	
	public String findSynonymInMap(String token) {
		if (synonymMap.containsKey(token))
			return token;
		return synonymMap.getOrDefault(token, null);
	}
	
	
	public void save() {
		try {
			FileOutputStream fileOut = new FileOutputStream("src/main/resources/FullInvertedIndex.ser.gz");
    		GZIPOutputStream gzipOut = new GZIPOutputStream(fileOut);
    		ObjectOutputStream out = new ObjectOutputStream(gzipOut); 
	        out.writeObject(this);
			out.close();

	    } catch (IOException e) {
	        System.out.println(e);
	    }
	}
	
	public static InvertedIndex load() {
		try { 
			FileInputStream fileIn = new FileInputStream("FullInvertedIndex.ser.gz");
    		GZIPInputStream gzipIn = new GZIPInputStream(fileIn);
    		ObjectInputStream in = new ObjectInputStream(gzipIn); 
	        InvertedIndex loadedObject = (InvertedIndex) in.readObject();
	        in.close();
	        return loadedObject;
		} catch (IOException | ClassNotFoundException e) {
	         System.out.println(e);
	    }
		return null;
	}
}
