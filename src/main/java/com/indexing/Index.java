package com.indexing;

import java.util.List;
import java.util.Map;

public interface Index <T, E, P>{
    void load() throws Exception;

    void create(List<T> documents);

    List<E> lookUp(String token);

    boolean isEmpty();

    Map<String, Double> getIdfMap(int size);

    P getData();

    void save();
}
