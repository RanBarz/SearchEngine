package com.ranking.helpers;

import com.file_handling.Document;
import com.indexing.data_types.InvertedIndexRecord;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class OkapiRanker {
    private final double k1 = 1.2;
    private final double b = 0.75;

    Map<String, Double> scores;


    private static double getAvgLength(List<Document> documents) {
        return documents.stream().mapToInt(Document::getWordCount).average().orElse(0);
    }

    public void
    rank(List<String> queryTerms, List<InvertedIndexRecord> records, Map<String, Double> idfMap, List<Document> documents) {
        double avgDL = getAvgLength(documents);
        scores = new HashMap<>();

        for (String term : queryTerms) {
            double idf = idfMap.getOrDefault(term, 1.0);

            for (InvertedIndexRecord record : records) {
                int docId = record.getDocumentId();
                int termFrequency = record.getTokenPositions().size();

                Document doc = documents.get(docId);
                if (doc == null) continue;

                double termScore = computeBM25(idf, termFrequency, doc.getWordCount(), avgDL);
                scores.put(doc.getTitle(), scores.getOrDefault(doc.getTitle(), 0.0) + termScore);
            }
        }
    }

    private double computeBM25(double idf, int termFrequency, int docLength, double avgDL) {
        double tfComponent = (termFrequency * (k1 + 1)) / (termFrequency + k1 * (1 - b + b * (docLength / avgDL)));
        return idf * tfComponent;
    }

    public double get(String title) {
        return scores.get(title);
    }
}
