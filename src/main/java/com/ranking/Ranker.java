package com.ranking;

import com.file_handling.Document;
import com.indexing.data_types.InvertedIndexData;

import java.util.List;
import java.util.Map;

public interface Ranker <T, E, P> {
    void sort(List<String> queryTerms, List<T> records, List<E> documents, int maxSort);

    boolean isEmpty();

    void create(Map<String, Double> idfMap);

    void preSort(P data, List<E> documents);
}