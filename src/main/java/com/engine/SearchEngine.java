package com.engine;

public interface SearchEngine {
	void create() throws Exception;
	
	void load() throws Exception;
	
	void getQueries();
}
