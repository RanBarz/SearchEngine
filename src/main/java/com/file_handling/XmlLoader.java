package com.file_handling;

import java.util.List;

public interface XmlLoader<T> {
	public List<T> load(String path);
}
