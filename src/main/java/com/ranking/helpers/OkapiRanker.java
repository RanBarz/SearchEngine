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
public class OkapiRanker {
    private final String PATH =
            "C:\\Software Development\\Java Projects\\Full Text Search Engine\\src\\main\\resources\\idfMap.ser.gz";

    private final double k1 = 1.2;
    private final double b = 0.75;
    private Map<String, Double> idfMap;

    @PreDestroy
    public void save() {
        try {
            FileOutputStream fileOut = new FileOutputStream(PATH);
            GZIPOutputStream gzipOut = new GZIPOutputStream(fileOut);
            ObjectOutputStream out = new ObjectOutputStream(gzipOut);
            out.writeObject(idfMap);
            out.close();
        }
        catch (Exception e) {
            System.out.println("Failed to save idf map...");
        }
    }

    @PostConstruct
    public void load() {
        try {
            FileInputStream fileIn = new FileInputStream(PATH);
            GZIPInputStream gzipIn = new GZIPInputStream(fileIn);
            ObjectInputStream in = new ObjectInputStream(gzipIn);
            idfMap = (Map<String, Double>) in.readObject();
            in.close();
        } catch (Exception e) {
            System.out.println("No idf map, one will be created.");
        }
    }

    public void create(Map<String, Double> idfMap) {
        this.idfMap = idfMap;
        save();
    }

    public Map<String, Double>
    rank(double avgDl, List<String> queryTerms, List<InvertedIndexRecord> records, List<Document> documents) {
        Map<String, Double> scores = new HashMap<>();

        for (String term : queryTerms) {
            double idf = idfMap.getOrDefault(term, 1.0);

            addToScores(scores, records, documents, idf, avgDl);
        }
        return scores;
    }

    private void addToScores(Map<String, Double> scores, List<InvertedIndexRecord> records, List<Document> documents, double idf, double avgDl) {
        for (InvertedIndexRecord record : records) {
            int docId = record.getDocumentId();
            int termFrequency = record.getTokenPositions().size();

            Document doc = documents.get(docId);
            if (doc == null) continue;

            double termScore = computeBM25(idf, termFrequency, doc.getWordCount(), avgDl);
            scores.put(doc.getTitle(), scores.getOrDefault(doc.getTitle(), 0.0) + termScore);
        }
    }

    private double computeBM25(double idf, int termFrequency, int docLength, double avgDL) {
        double tfComponent = (termFrequency * (k1 + 1)) / (termFrequency + k1 * (1 - b + b * (docLength / avgDL)));
        return idf * tfComponent;
    }

    public boolean isEmpty() {
        return idfMap == null;
    }
}
