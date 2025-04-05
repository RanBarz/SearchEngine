package com.engine;
import java.util.*;

import com.file_handling.*;
import com.indexing.*;
import com.indexing.data_types.InvertedIndexData;
import com.indexing.data_types.InvertedIndexRecord;
import com.indexing.helpers.TextPreparer;
import com.ranking.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FullTextSearchEngine implements SearchEngine {
	private final String PATH = "C:\\Software Development\\Java Projects\\Full Text Search Engine\\src\\main\\resources\\Database.xml.gz";
	private static final int NUM_TO_SORT = 1000;


	@Autowired
	private  XmlLoader<Document> xlObject;

	@Autowired
	private Index<Document, InvertedIndexRecord, InvertedIndexData> iiObject;

	@Autowired
	private TextPreparer tp;

	@Autowired
	private Ranker<InvertedIndexRecord, Document, InvertedIndexData> ranker;

	private List<Document> documents;

	public List<Document> search(String query) throws Exception {
		if (documents == null)
			documents = xlObject.getDocuments();

		if (iiObject.isEmpty()) {
			iiObject.create(documents);
			ranker.preSort(iiObject.getData(), documents);
			iiObject.save();
		}
		Set<Document> documentSet = new HashSet<>();
		List<Document> documentResults = new ArrayList<>();
		Set<InvertedIndexRecord> resultsSet = new HashSet<>();
		List<InvertedIndexRecord> allResults;

		getAllResults(query, resultsSet);

		allResults = new ArrayList<>(resultsSet);

		if (ranker.isEmpty())
			ranker.create(iiObject.getIdfMap(documents.size()));

		ranker.sort(tp.tokenize(query), allResults, documents, NUM_TO_SORT);

		for (InvertedIndexRecord record: allResults) {
			if (!documentSet.contains(documents.get(record.getDocumentId())))
				documentResults.add(documents.get(record.getDocumentId()));
			documentSet.add(documents.get(record.getDocumentId()));
		}

		return documentResults;
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