package com.ranking;

import com.file_handling.Document;
import com.indexing.data_types.InvertedIndexData;
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

    private static final double ALPHA = 0.7;

    public Map<String, Double>
    combineScores(List<String> queryTerms, List<InvertedIndexRecord> records, Map<String, Double> idfMap, List<Document> documents) {
        Map<String, Double> finalScores = new HashMap<>();

        oRanker.rank(queryTerms, records, idfMap, documents);
        if (pRanker.isEmpty())
            pRanker.computeGlobalPageRank(documents);
        Map<String, Double> pRanks = pRanker.getQueryPageRanks(records, documents);

        for (InvertedIndexRecord rec : records) {
            Document doc = documents.get(rec.getDocumentId());
            double bm25 = oRanker.get(doc.getTitle());
            double pageRank = pRanks.get(doc.getTitle());
            double finalScore = ALPHA * bm25 + (1 - ALPHA) * pageRank;
            finalScores.put(doc.getTitle(), finalScore);
        }

        return finalScores;
    }

    @Override
    public void
    sort(List<String> queryTerms, List<InvertedIndexRecord> records, Map<String, Double> idfMap, List<Document> documents) {
        Map<String, Double> finalScores = combineScores(queryTerms, records, idfMap, documents);
        records.sort(Comparator.comparingDouble(
                (InvertedIndexRecord r) ->
                        finalScores.getOrDefault(documents.get(r.getDocumentId()).getTitle(), 0.0)).
                reversed());
    }
}
