package com;

import com.engine.FullTextSearchEngine;
import com.engine.SearchEngine;

public class Program {
	private static final String PATH = "src/main/resources/Database.xml.gz";
	private static final int PREDICTED_XML_SIZE = 690000;
	private static final int PREDICTED_INDEX_SIZE = 525000;
	private static final int PREDICTED_SYNONYM_SIZE = 316000;

	public static void main(String[] args) {
		SearchEngine se = new FullTextSearchEngine(PREDICTED_XML_SIZE, PREDICTED_INDEX_SIZE, PREDICTED_SYNONYM_SIZE, PATH);
		try {
			se.load();
		}
		catch (Exception e) {
			System.out.println("an error occured");
		}
		se.getQueries();
	}

}
