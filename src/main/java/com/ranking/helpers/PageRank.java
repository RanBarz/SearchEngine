package com.ranking.helpers;

import com.file_handling.Document;
import com.indexing.data_types.InvertedIndexRecord;
import com.utilty.Serializer;
import org.springframework.stereotype.Component;
import javax.annotation.*;
import java.io.*;
import java.util.*;
import java.util.zip.*;

@Component
public class PageRank {
    private final String PATH =
            "C:\\Software Development\\Java Projects\\Full Text Search Engine\\src\\main\\resources\\graph.ser.gz";
    private Map<String, List<Integer>> graph;
    private static final double DAMPING_FACTOR = 0.85;
    private static final double THRESHOLD = 1e-6;
    private static final int MAX_ITERATIONS = 100;
    private Map<String, Double> globalPageRanks;

    public void save() {
        Serializer.save(PATH, globalPageRanks);
    }

    @PostConstruct
    public void load() {
        try {
            globalPageRanks = Serializer.load(PATH);
        } catch (Exception e) {
            System.out.println("No page ranks, they will be created...");
        }
    }

    public void computeGlobalPageRank(List<Document> documents) {
        initialize(documents);
        int numPages = graph.size();
        Map<String, Double> pageRanks = new HashMap<>();
        Map<String, Double> newPageRanks = new HashMap<>();

        for (Document doc: documents) {
            pageRanks.put(doc.getTitle(), 1.0 / numPages);
        }

        for (int i = 0; i < MAX_ITERATIONS; i++) {
            double maxChange = 0.0;

            for (Document doc: documents) {
                String page = doc.getTitle();
                double rankSum = 0.0;
                if (graph.containsKey((page))) {
                    for (int incomingPage : graph.get(page)) {
                        int outLinks = documents.get(incomingPage).getOutgoingLinks().size();
                        if (outLinks > 0) {
                            rankSum += pageRanks.get(documents.get(incomingPage).getTitle()) / outLinks;
                        }
                    }
                }

                double newRank = (1 - DAMPING_FACTOR) / numPages + DAMPING_FACTOR * rankSum;
                newPageRanks.put(page, newRank);
                maxChange = Math.max(maxChange, Math.abs(newRank - pageRanks.get(page)));
            }

            pageRanks.putAll(newPageRanks);

            if (maxChange < THRESHOLD) {
                break;
            }
        }

        globalPageRanks = pageRanks;

        save();
    }

    public Map<String, Double> getQueryPageRanks(List<InvertedIndexRecord> relevantPages, List<Document> documents) {
        Map<String, Double> queryRanks = new HashMap<>();

        for (InvertedIndexRecord page : relevantPages) {
            queryRanks.put(documents.get(page.getDocumentId()).getTitle(),
                    globalPageRanks.getOrDefault(documents.get(page.getDocumentId()).getTitle(), 0.0));
        }

        return normalize(queryRanks);
    }

    private Map<String, Double> normalize(Map<String, Double> scores) {
        double max = Collections.max(scores.values());
        double min = Collections.min(scores.values());

        if (max == min) return scores;

        Map<String, Double> normalized = new HashMap<>();
        for (Map.Entry<String, Double> entry : scores.entrySet()) {
            double normalizedScore = (entry.getValue() - min) / (max - min);
            normalized.put(entry.getKey(), normalizedScore);
        }
        return normalized;
    }

    public void initialize(List<Document> documentList) {
        graph = new HashMap<>();
        List<Integer> incoming;
        for (Document doc: documentList) {
            for (String link: doc.getOutgoingLinks()) {
                if (!graph.containsKey(link))
                    graph.put(link, new ArrayList<>());
                incoming = graph.get(link);
                incoming.add(doc.getId());
            }
        }
    }

    public boolean isEmpty() { return globalPageRanks == null; }

    public Map<String, Double> getGlobalPageRanks() {
        return globalPageRanks;
    }
}