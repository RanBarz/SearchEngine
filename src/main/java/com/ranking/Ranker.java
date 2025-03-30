package com.ranking;

import com.file_handling.Document;
import com.indexing.data_types.InvertedIndexRecord;

import java.util.List;
import java.util.Map;

public interface Ranker {
    void sort(List<String> queryTerms, List<InvertedIndexRecord> records, Map<String, Double> idfMap, List<Document> documents);
}