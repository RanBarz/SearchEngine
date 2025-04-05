package com.indexing.helpers;

import com.indexing.data_types.WordNetData;
import com.utilty.Serializer;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.*;

@Component
public class WordNetUtil
{
    private final String WORDNET_PATH = "C:\\Software Development\\Java Projects\\Full Text Search Engine\\src\\main\\resources\\WordNetData.ser.gz";
    WordNetData data;

    public boolean isVerb(String word) {
        return data.isVerb(word);
    }

    public String findSynonym(String word) {
        return data.findSynonym(word);
    }

    @PostConstruct
    public void load() {
        try {
           data =  Serializer.load(WORDNET_PATH);
        }
        catch (Exception e) {
            data = new WordNetData();
            data.loadDatabase();
            data.loadVerbData();
            save();
        }
    }

    public void save() {
        Serializer.save(WORDNET_PATH, data);
    }


}
