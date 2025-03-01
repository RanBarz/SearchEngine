package com.indexing;

import com.file_handling.Document;
import com.indexing.data_types.InvertedIndexRecord;

import java.util.List;

public interface Index {
    void load() throws Exception;

    void create(List<Document> documents) throws Exception;

    List<InvertedIndexRecord> lookUp(String token);
}
