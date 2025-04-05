package com.indexing.data_types;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class InvertedIndexRecord implements Serializable {
    private int documentId;
    private List<Integer> tokenPositions;

    public InvertedIndexRecord(int documentId, int position) {
        this.documentId = documentId;
        tokenPositions = new ArrayList<>();
        tokenPositions.add(position);
    }

    public List<Integer> getTokenPositions() {
        return tokenPositions;
    }

    public void setTokenPositions(List<Integer> tokenPositions) {
        this.tokenPositions = tokenPositions;
    }

    public int getDocumentId() {
        return documentId;
    }

    public void setDocumentId(int documentId) {
        this.documentId = documentId;
    }

    public void addPosition(int position) {
        tokenPositions.add(position);
    }
}
