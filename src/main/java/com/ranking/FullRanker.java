package com.ranking;

import com.file_handling.Document;
import com.indexing.data_types.InvertedIndexRecord;
import com.ranking.helpers.OkapiRanker;
import com.ranking.helpers.PageRank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class FullRanker implements Ranker{
    @Autowired
    private OkapiRanker oRanker;

    @Autowired
    private PageRank pRanker;

    private final double ALPHA = 0.7;
    private double avgDl;

    public Map<String, Double>
    combineScores(List<String> queryTerms, List<InvertedIndexRecord> records, List<Document> documents) {
        if (avgDl == 0)
            avgDl = documents.stream().mapToInt(Document::getWordCount).average().orElse(0);

        Map<String, Double> oRanks = oRanker.rank(avgDl, queryTerms, records, documents);
        if (pRanker.isEmpty())
            pRanker.computeGlobalPageRank(documents);
        Map<String, Double> pRanks = pRanker.getQueryPageRanks(records, documents);

        return makeFinalScores(records, documents, pRanks, oRanks);
    }

    private Map<String, Double>
    makeFinalScores(List<InvertedIndexRecord> records, List<Document> documents, Map<String, Double> pRanks, Map<String, Double> oRanks) {
        Map<String, Double> finalScores = new HashMap<>();

        for (InvertedIndexRecord rec : records) {
            Document doc = documents.get(rec.getDocumentId());
            double bm25 = oRanks.get(doc.getTitle());
            double pageRank = pRanks.get(doc.getTitle());
            double finalScore = ALPHA * bm25 + (1 - ALPHA) * pageRank;
            finalScores.put(doc.getTitle(), finalScore);
        }

        return finalScores;
    }

    public boolean isEmpty() {
        return oRanker.isEmpty();
    }

    public void create(Map<String, Double> idfMap) {
        oRanker.create(idfMap);
    }

    @Override
    public void
    sort(List<String> queryTerms, List<InvertedIndexRecord> records, List<Document> documents) {
        Map<String, Double> finalScores = combineScores(queryTerms, records, documents);
        records.sort(Comparator.comparingDouble(
                (InvertedIndexRecord r) ->
                        finalScores.getOrDefault(documents.get(r.getDocumentId()).getTitle(), 0.0)).
                reversed());
    }
}
