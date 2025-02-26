package com.indexing;

import java.io.*;
import java.util.*;
import java.util.zip.*;
import net.sf.extjwnl.JWNLException;
import com.indexing.helpers.*;
import com.file_handling.*;

public class InvertedIndex implements Serializable{
	private static final long serialVersionUID = 1L;
	private final Map<String, List<Integer>> index;
	private final Map<String, String> synonymMap;
	private final TextPreparer tp;

	public InvertedIndex(String[] stopWords, int predictedIndexSize, int predictedSynonymSize) {
		index = new HashMap<>(predictedIndexSize);
		synonymMap = new HashMap<>(predictedSynonymSize);
	    tp = new TextPreparer(stopWords);
	}
	
	
	public List<Integer> lookUp(String token) {
		return index.getOrDefault(this.findSynonymInMap(token), null);
	}
	
	public void create(List<Document> documents){
        List<String> tokens;
		HashSet<String> handledTokens= new HashSet<>();
		
		for (Document doc: documents) {
	        tokens = tp.tokenize(doc.getText());

			addToHandled(tokens, handledTokens);
			addTokens(doc, tokens);
	        
	        if (doc.getId() % 1000 == 0)
	            System.out.printf("%.2f%% done\n", (float) doc.getId() / documents.size() * 100);
	    }
	}

	private void addToHandled(List<String> tokens, HashSet<String> handledTokens) {
		for (String token : tokens) {
			if (!handledTokens.contains(token)) {
				addToSynonymMap(token);
				handledTokens.add(token);
			}
		}
	}

	public void addTokens(Document doc, List<String> tokens) {
		Set<String> processedTokens = new HashSet<>();
	
		for (String token : tokens) {
			addToken(doc, token, processedTokens);
		}
	}

	private void addToken(Document doc, String token, Set<String> processedTokens) {
		if (processedTokens.contains(token))
			return;

		String synonym;
		List<Integer> idList;

		synonym = findSynonymInMap(token);
		index.computeIfAbsent(synonym, k -> new ArrayList<>());
		idList = index.get(synonym);

		if (!idList.isEmpty() && idList.get(idList.size() - 1).equals(doc.getId())) {
			return;
		}

		idList.add(doc.getId());
		processedTokens.add(token);
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
	
	
	public void save() throws Exception {
		FileOutputStream fileOut = new FileOutputStream("src/main/resources/FullInvertedIndex.ser.gz");
		GZIPOutputStream gzipOut = new GZIPOutputStream(fileOut);
		ObjectOutputStream out = new ObjectOutputStream(gzipOut);
		out.writeObject(this);
		out.close();
	}
	
	public static InvertedIndex load() throws Exception{
		FileInputStream fileIn = new FileInputStream("src/main/resources/FullInvertedIndex.ser.gz");
		GZIPInputStream gzipIn = new GZIPInputStream(fileIn);
		ObjectInputStream in = new ObjectInputStream(gzipIn);
		InvertedIndex loadedObject = (InvertedIndex) in.readObject();
		in.close();
		return loadedObject;
	}
}
