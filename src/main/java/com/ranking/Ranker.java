package com.ranking;

import com.file_handling.Document;
import com.indexing.data_types.InvertedIndexRecord;

import java.util.List;

public interface Ranker {
    void sort(List<String> queryTerms, List<InvertedIndexRecord> records, List<Document> documents);
}