package com.ranking.helpers;

import com.file_handling.Document;
import com.indexing.data_types.InvertedIndexRecord;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@Component
public class PageRank {
    private final String PATH =
            "C:\\Software Development\\Java Projects\\Full Text Search Engine\\src\\main\\resources\\graph.ser.gz";
    Map<String, List<Integer>> graph;
    private static final double DAMPING_FACTOR = 0.85;
    private static final double THRESHOLD = 1e-6;
    private static final int MAX_ITERATIONS = 100;
    private Map<String, Double> globalPageRanks;

    @PreDestroy
    public void save() throws Exception {
        FileOutputStream fileOut = new FileOutputStream(PATH);
        GZIPOutputStream gzipOut = new GZIPOutputStream(fileOut);
        ObjectOutputStream out = new ObjectOutputStream(gzipOut);
        out.writeObject(globalPageRanks);
        out.close();
    }

    @PostConstruct
    public void load() {
        try {
            FileInputStream fileIn = new FileInputStream(PATH);
            GZIPInputStream gzipIn = new GZIPInputStream(fileIn);
            ObjectInputStream in = new ObjectInputStream(gzipIn);
            globalPageRanks = (Map<String, Double>) in.readObject();
            in.close();
        } catch (Exception e) {
            System.out.println("No pages graph, one will be created.");
        }
    }

    public void computeGlobalPageRank(List<Document> documents) {
        intialize(documents);
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
    }

    public Map<String, Double> getQueryPageRanks(List<InvertedIndexRecord> relevantPages, List<Document> documents) {
        Map<String, Double> queryRanks = new HashMap<>();

        for (InvertedIndexRecord page : relevantPages) {
            queryRanks.put(documents.get(page.getDocumentId()).getTitle(),
                    globalPageRanks.getOrDefault(documents.get(page.getDocumentId()).getTitle(), 0.0));
        }

        return normalize(queryRanks);
    }

    // Normalize values to a 0-1 scale for consistency
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

    public void intialize(List<Document> documentList) {
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
}