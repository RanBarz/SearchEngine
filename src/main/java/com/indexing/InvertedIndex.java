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

@Component
public class InvertedIndex implements Index{
	private InvertedIndexData data;

	@Autowired
	private TextPreparer tp;

	public List<InvertedIndexRecord> lookUp(String token) {
		return data.get(token);
	}

	public Map<String, Double> getIdfMap() {
		return data.getIdfMap();
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

	        if (doc.getId() % 1000 == 0)
	            System.out.printf("%f done\n", percent);
			if (percent >= 5)
				break;
	    }

		data.createIdfMap(documents.size());

		save();
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

	public void save() throws Exception {
		FileOutputStream fileOut = new FileOutputStream("C:\\Software Development\\Java Projects\\Full Text Search Engine\\src\\main\\resources\\InvertedIndexData.ser.gz");
		GZIPOutputStream gzipOut = new GZIPOutputStream(fileOut);
		ObjectOutputStream out = new ObjectOutputStream(gzipOut);
		out.writeObject(data);
		out.close();
	}
	
	public void load() throws Exception{
		FileInputStream fileIn = new FileInputStream("C:\\Software Development\\Java Projects\\Full Text Search Engine\\src\\main\\resources\\InvertedIndexData.ser.gz");
		GZIPInputStream gzipIn = new GZIPInputStream(fileIn);
		ObjectInputStream in = new ObjectInputStream(gzipIn);
		data = (InvertedIndexData) in.readObject();
		in.close();
	}

	public boolean isEmpty() {
		return data == null;
	}
}
