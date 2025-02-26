package com.engine;
import java.util.*;

import com.file_handling.*;
import com.indexing.*;
import com.indexing.helpers.TextPreparer;


public class FullTextSearchEngine implements SearchEngine {
	private String path;
	private static final String[] STOP_WORDS = {"a", "and", "be", "have", "i", "in", "of", "that", "the", "to"};
	private XmlLoader<Document> xlObject;
	private InvertedIndex iiObject;
	private List<Document> documents;
	
	public FullTextSearchEngine(int predictedSize, int predictedIndexSize, int predictedSynonymSize, String path) {
		xlObject = new DocumentXmlLoader(predictedSize);
		iiObject = new InvertedIndex(STOP_WORDS, predictedIndexSize, predictedSynonymSize);
		this.path = path;
	}
	
	public void create() {
		documents = xlObject.load(path);
		iiObject.create(documents);
		iiObject.save();
	}
	
	public void load() {
		documents = xlObject.load(path);
        iiObject = InvertedIndex.load();
    }
	
	public void getQueries () {
		Scanner scanner = new Scanner(System.in);
        System.out.printf("\nEnter you query: ");
        String query = scanner.nextLine();
        while (!query.equals("end")) {
	        for (int result: search(query, iiObject)) 
	        	System.out.printf("%s\n\n", documents.get(result).getText());
	        System.out.printf("\nEnter you query: ");
	        query = scanner.nextLine();     
        }   
        scanner.close();
	}

	public Set<Integer> search(String query, InvertedIndex iiObject) {
		TextPreparer tp = new TextPreparer(STOP_WORDS);
		Set<Integer> idSet= new HashSet<>();
		List<Integer> result;
		for (String token: tp.tokenize(query)) {
			result = iiObject.lookUp(token);
			if (result != null)
                idSet.addAll(result);
		}
		return idSet;
	}	
}