package com.engine;

import com.file_handling.Document;

import java.util.List;

public interface SearchEngine {
	void create() throws Exception;
	
	void load() throws Exception;

	List<Document> search(String query) throws Exception;
}
