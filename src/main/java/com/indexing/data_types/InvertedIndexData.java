package com.indexing.data_types;

import com.file_handling.Document;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InvertedIndexData implements Serializable {
    private final Map<String, List<InvertedIndexRecord>> index;

    public InvertedIndexData() {
        index = new HashMap<>();
    }

    public List<InvertedIndexRecord> get(String token) {
        return index.getOrDefault(token, null);
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

    public Map<String, Double> getIdfMap(int totalSize) {
        Map<String, Double> idfMap = new HashMap<>();

        for (Map.Entry<String, List<InvertedIndexRecord>> entry : index.entrySet()) {
            String term = entry.getKey();
            int docCount = entry.getValue().size();
            double idf = Math.log((totalSize - docCount + 0.5) / (docCount + 0.5) + 1);
            idfMap.put(term, idf);
        }

        return idfMap;
    }

    public Map<String, List<InvertedIndexRecord>> getIndex() {
        return index;
    }
}
