package com.ranking;

import com.file_handling.Document;
import com.indexing.data_types.InvertedIndexRecord;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class OkapiRanker implements Ranker {
    private final double k1 = 1.5;
    private final double b = 0.75;

    private static double getAvgLength(List<Document> documents) {
        int totalLength = 0;
        for (Document doc : documents) {
            totalLength += doc.getWordCount();
        }
        return !documents.isEmpty() ? (double) totalLength / documents.size() : 0;
    }

    @Override
    public void sort(List<InvertedIndexRecord> records, Map<String, Double> idfMap, List<Document> documents) {
        Map<Integer, Double> scores = new HashMap<>();
        double avgDL = getAvgLength(documents);

        for (InvertedIndexRecord record : records) {
            int docId = record.getDocumentId();
            int termFrequency = record.getTokenPositions().size();
            int docLength = documents.get(docId).getWordCount();

            double score = computeBM25(termFrequency, docLength, documents.size(), avgDL);
            scores.put(docId, score);
        }

        // Sort records by BM25 score in descending order
        records.sort((a, b) -> Double.compare(scores.get(b.getDocumentId()),
                scores.get(a.getDocumentId())));
    }

    private double computeBM25(int termFrequency, int docLength, int totalDocs, double avgDL) {
        double idf = Math.log((totalDocs - 1 + 0.5) / (1 + 0.5) + 1); // Simplified IDF if precomputed is missing
        double tfComponent = (termFrequency * (k1 + 1)) / (termFrequency + k1 * (1 - b + b * (docLength / avgDL)));
        return idf * tfComponent;
    }
}
