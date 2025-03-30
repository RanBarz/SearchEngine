package com.engine;

import com.file_handling.Document;

import java.util.List;

public interface SearchEngine {
	List<Document> search(String query) throws Exception;
}
