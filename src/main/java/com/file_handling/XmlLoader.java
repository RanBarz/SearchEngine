package com.file_handling;

import java.util.List;

public interface XmlLoader<T> {
	List<T> load(String path) throws Exception;
}
