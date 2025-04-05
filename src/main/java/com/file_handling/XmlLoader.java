package com.file_handling;

import java.util.List;

public interface XmlLoader<T> {
	void load();

	List<T> getDocuments();

	void loadSerialized() throws Exception;
}
