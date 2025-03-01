package com.engine;
import java.util.*;

import com.file_handling.*;
import com.indexing.*;
import com.indexing.data_types.InvertedIndexRecord;
import com.indexing.helpers.TextPreparer;
import com.ranking.OkapiRanker;
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

	@Autowired
	private OkapiRanker ranker;

	private List<Document> documents;

	public void create() throws Exception{
		documents = xlObject.load(PATH);
		iiObject.create(documents);
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
		List<InvertedIndexRecord> allResults = new ArrayList<>();
		List<InvertedIndexRecord> result;

		for (String token: tp.tokenize(query)) {
			result = iiObject.lookUp(token);
			if (result != null)
                allResults.addAll(result);
		}

		ranker.sort(allResults, iiObject.getIdfMap(), documents);

		for (InvertedIndexRecord record: allResults) {
			documentResults.add(documents.get(record.getDocumentId()));
		}

		return documentResults;
	}	
}