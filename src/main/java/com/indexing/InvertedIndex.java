package com.indexing;

import java.io.*;
import java.util.*;
import java.util.zip.*;
import net.sf.extjwnl.JWNLException;
import com.indexing.helpers.*;
import com.file_handling.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InvertedIndex implements Serializable{
	private static final long serialVersionUID = 1L;
	private static final int PREDICTED_INDEX_SIZE = 525000;
	private static final int PREDICTED_SYNONYM_SIZE = 316000;
	private Map<String, List<Integer>> index;
	private Map<String, String> synonymMap;

	@Autowired
	private TextPreparer tp;

	public List<Integer> lookUp(String token) {
		return index.getOrDefault(this.findSynonymInMap(token), null);
	}
	
	public void create(List<Document> documents){
		index = new HashMap<>(PREDICTED_INDEX_SIZE);
		synonymMap = new HashMap<>(PREDICTED_SYNONYM_SIZE);

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
		String existingSynonym = synonymMap.entrySet()
				.parallelStream()
				.filter(entry -> {
					try {
						return SynonymChecker.areSynonyms(token, entry.getKey());
					} catch (JWNLException e) {
						System.out.println("Error checking synonyms for: " + token);
						return false;
					}
				})
				.map(Map.Entry::getKey)
				.findFirst()
				.orElse(token);

		synonymMap.put(token, existingSynonym);
	}


	public String findSynonymInMap(String token) {
		if (synonymMap.containsKey(token))
			return token;
		return synonymMap.getOrDefault(token, null);
	}
	
	
	public void save() throws Exception {
		FileOutputStream fileOut = new FileOutputStream("C:\\Software Development\\Java Projects\\Full Text Search Engine\\src\\main\\resources\\FullInvertedIndex.ser.gz");
		GZIPOutputStream gzipOut = new GZIPOutputStream(fileOut);
		ObjectOutputStream out = new ObjectOutputStream(gzipOut);
		out.writeObject(this);
		out.close();
	}
	
	public void load() throws Exception{
		FileInputStream fileIn = new FileInputStream("C:\\Software Development\\Java Projects\\Full Text Search Engine\\src\\main\\resources\\FullInvertedIndex.ser.gz");
		GZIPInputStream gzipIn = new GZIPInputStream(fileIn);
		ObjectInputStream in = new ObjectInputStream(gzipIn);
		InvertedIndex loadedObject = (InvertedIndex) in.readObject();
		in.close();
		index = loadedObject.index;
		synonymMap = loadedObject.synonymMap;
	}

	public boolean isEmpty() {
		return index == null;
	}
}
