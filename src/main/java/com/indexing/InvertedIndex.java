package com.indexing;

import java.util.*;
import com.indexing.data_types.*;
import com.indexing.helpers.*;
import com.file_handling.*;
import com.utilty.Serializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class InvertedIndex implements Index <Document, InvertedIndexRecord, InvertedIndexData>{
	private final String PATH = "C:\\Software Development\\Java Projects\\Full Text Search Engine\\src\\main\\resources\\InvertedIndexData.ser.gz";
	private InvertedIndexData data;

	@Autowired
	private TextPreparer tp;

	@Autowired
	private WordNetUtil synonymFinder;

	public List<InvertedIndexRecord> lookUp(String token) {
		List<InvertedIndexRecord> result = new ArrayList<>(data.get(token));
		String synonym =  synonymFinder.findSynonym(token);

		if (synonym != null && data.get(synonym) != null)
			result.addAll(data.get(synonym));
		return result;
	}

	public Map<String, Double> getIdfMap(int totalSize) {
		return data.getIdfMap(totalSize);
	}
	
	public void create(List<Document> documents) {
		data = new InvertedIndexData();
		List<String> tokens;
		HashSet<String> handledTokens= new HashSet<>();
		float percent;

		System.out.println("Inverted Index is being created...");

		for (Document doc: documents) {
	        tokens = tp.tokenize(doc.getText());
			addToHandled(tokens, handledTokens);
			addTokens(doc, tokens);

			percent = (float) doc.getId() / documents.size() * 100;
			System.out.printf("%f done\n", percent);
	    }
	}

	private void addToHandled(List<String> tokens, HashSet<String> handledTokens) {
		for (String token : tokens) {
			if (!handledTokens.contains(token)) {
				handledTokens.add(token);
			}
		}
	}

	public void addTokens(Document doc, List<String> tokens) {
		int position = 0;

		for (String token : tokens) {
			addToken(doc, token, position++);
		}
	}

	private void addToken(Document doc, String token, int position) {
		data.add(doc, token, position);
	}

	public void save()  {
		Serializer.save(PATH, data);
	}

	@PostConstruct
	public void load() {
        try {
            data = Serializer.load(PATH);
        } catch (Exception e) {
            System.out.println("Inverted Index will be created...");
        }
    }

	public boolean isEmpty() {
		return data == null;
	}

	public InvertedIndexData getData() {
		return data;
	}
}
