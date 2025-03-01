package com.indexing.data_types;

import com.file_handling.Document;
import com.indexing.helpers.SynonymChecker;
import net.sf.extjwnl.JWNLException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InvertedIndexData implements Serializable {
    private Map<String, List<InvertedIndexRecord>> index;
    private Map<String, String> synonymMap;

    public Map<String, Double> getIdfMap() {
        return idfMap;
    }

    private Map<String, Double> idfMap;

    public InvertedIndexData() {
        index = new HashMap<>();
        synonymMap = new HashMap<>();
    }

    public List<InvertedIndexRecord> get(String token) {
        return index.getOrDefault(findSynonymInMap(token), null);
    }

    public String findSynonymInMap(String token) {
        if (synonymMap.containsKey(token))
            return token;
        return synonymMap.getOrDefault(token, null);
    }

    public void addToSynonymMap(String token) {
        String existingSynonym = synonymMap.entrySet()
                .parallelStream()
                .filter(entry -> {
                    try {
                        return SynonymChecker.areSynonyms(token, entry.getKey());
                    } catch (JWNLException e) {
                        System.out.println("Error checking synonyms for: " + token);
                        return false;
                    }
                })
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(token);

        synonymMap.put(token, existingSynonym);
    }

    public void add(Document doc, String synonym, int position) {
        List<InvertedIndexRecord> recordList;
        index.computeIfAbsent(synonym, k -> new ArrayList<>());
        recordList = index.get(synonym);

        if (!recordList.isEmpty() && recordList.get(recordList.size() - 1).getDocumentId() == doc.getId())
            recordList.get(recordList.size() - 1).addPosition(position);
        else
            recordList.add(new InvertedIndexRecord(doc.getId(), position));
    }

    public void createIdfMap(int totalSize) {
        idfMap = new HashMap<>();

        for (Map.Entry<String, List<InvertedIndexRecord>> entry : index.entrySet()) {
            String term = entry.getKey();
            int docCount = entry.getValue().size();
            double idf = Math.log((totalSize - docCount + 0.5) / (docCount + 0.5) + 1);
            idfMap.put(term, idf);
        }
    }
}
