package com.engine;
import java.util.*;

import com.file_handling.*;
import com.indexing.*;
import com.indexing.helpers.TextPreparer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FullTextSearchEngine implements SearchEngine {
	private final String PATH = "C:\\Software Development\\Java Projects\\Full Text Search Engine\\src\\main\\resources\\Database.xml.gz";

	@Autowired
	private  XmlLoader<Document> xlObject;

	@Autowired
	private InvertedIndex iiObject;

	@Autowired
	private TextPreparer tp;

	private List<Document> documents;

	public void create() throws Exception{
		documents = xlObject.load(PATH);
		iiObject.create(documents);
		iiObject.save();
	}
	
	public void load() throws Exception{
		documents = xlObject.load(PATH);
        iiObject.load();
    }

	public void getInvertedIndex() throws Exception {
		try {
			load();
		}
		catch(Exception e) {
			create();
		}
	}

	public List<Document> search(String query) throws Exception {
		if (iiObject.isEmpty())
			getInvertedIndex();

		List<Document> documentResults = new ArrayList<>();
		Set<Integer> idSet= new HashSet<>();
		List<Integer> result;

		for (String token: tp.tokenize(query)) {
			result = iiObject.lookUp(token);
			if (result != null)
                idSet.addAll(result);
		}
		for (int id: idSet) {
			documentResults.add(documents.get(id));
		}
		return documentResults;
	}	
}