package com.engine;
import java.util.*;

import com.file_handling.*;
import com.indexing.*;
import com.indexing.data_types.InvertedIndexRecord;
import com.indexing.helpers.TextPreparer;
import com.ranking.FullRanker;
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
	private FullRanker ranker;

	private List<Document> documents;

	public List<Document> search(String query) throws Exception {
		documents = xlObject.getDocuments();

		if (iiObject.isEmpty())
			iiObject.create(documents);

		Set<Document> documentResults = new HashSet<>();
		Set<InvertedIndexRecord> resultsSet = new HashSet<>();
		List<InvertedIndexRecord> allResults;

		getAllResults(query, resultsSet);

		allResults = new ArrayList<>(resultsSet);

		if (ranker.isEmpty())
			ranker.create(iiObject.getIdfMap(documents.size()));

		ranker.sort(tp.tokenize(query), allResults, documents);

		for (InvertedIndexRecord record: allResults) {
			documentResults.add(documents.get(record.getDocumentId()));
		}

		return new ArrayList<>(documentResults);
	}

	private void getAllResults(String query, Set<InvertedIndexRecord> resultsSet) {
		List<InvertedIndexRecord> result;

		for (String token: tp.tokenize(query)) {
			result = iiObject.lookUp(token);
			if (result != null) {
				resultsSet.addAll(result);
			}
		}
	}
}