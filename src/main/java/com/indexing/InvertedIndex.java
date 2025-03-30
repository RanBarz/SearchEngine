package com.indexing;

import java.io.*;
import java.util.*;
import java.util.zip.*;

import com.indexing.data_types.InvertedIndexData;
import com.indexing.data_types.InvertedIndexRecord;
import com.indexing.helpers.*;
import com.file_handling.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class InvertedIndex implements Index{
	private final String PATH = "C:\\Software Development\\Java Projects\\Full Text Search Engine\\src\\main\\resources\\InvertedIndexData.ser.gz";
	private InvertedIndexData data;

	@Autowired
	private TextPreparer tp;

	public List<InvertedIndexRecord> lookUp(String token) {
		return data.get(token);
	}

	public Map<String, Double> getIdfMap(int totalSize) {
		return data.getIdfMap(totalSize);
	}
	
	public void create(List<Document> documents) throws Exception {
		data = new InvertedIndexData();
		List<String> tokens;
		HashSet<String> handledTokens= new HashSet<>();
		float percent;

		System.out.printf("Inverted Index is being created...\n");

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
				data.addToSynonymMap(token);
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
		String synonym = data.findSynonymInMap(token);

		data.add(doc, synonym, position);
	}

	@PreDestroy
	public void save() throws Exception {
		FileOutputStream fileOut = new FileOutputStream(PATH);
		GZIPOutputStream gzipOut = new GZIPOutputStream(fileOut);
		ObjectOutputStream out = new ObjectOutputStream(gzipOut);
		out.writeObject(data);
		out.close();
	}

	@PostConstruct
	public void load() throws Exception{
		try {
			FileInputStream fileIn = new FileInputStream(PATH);
			GZIPInputStream gzipIn = new GZIPInputStream(fileIn);
			ObjectInputStream in = new ObjectInputStream(gzipIn);
			data = (InvertedIndexData) in.readObject();
			in.close();
		}
		catch (Exception e) {
			System.out.println("No Inverted Index, one will be created.");
		}
	}

	public boolean isEmpty() {
		return data == null;
	}

	public InvertedIndexData getData() {
		return data;
	}
}
