package com.indexing.data_types;

import java.io.*;
import java.util.*;

public class WordNetData implements Serializable {
    private Set<String> verbSet = new HashSet<>();
    private Map<String, List<String>> wordToSynsets;
    private Map<String, List<String>> synsetToWords;
    private final String WORDNET_DIR = "C:\\Software Development\\Java Projects\\Full Text Search Engine\\src\\main\\resources";
    private final String VERB_DATA_PATH = "C:\\Software Development\\Java Projects\\Full Text Search Engine\\src\\main\\resources\\dict\\verb.exc";
    private static final Map<String, String> POS_ABBR = new HashMap<>();
    static {
        POS_ABBR.put("noun", "n");
        POS_ABBR.put("verb", "v");
        POS_ABBR.put("adj", "a");
        POS_ABBR.put("adv", "r");
    }

    public WordNetData() {
        this.wordToSynsets = new HashMap<>();
        this.synsetToWords = new HashMap<>();
    }

    public void loadDatabase() {
        for (String pos : POS_ABBR.keySet()) {
            String filePath = WORDNET_DIR + "/dict/data." + pos;
            String posAbbr = POS_ABBR.get(pos);

            try {
                BufferedReader reader = new BufferedReader(new FileReader(filePath));
                String line;

                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("  ")) continue;

                    String[] parts = line.split("\\s+");
                    String offset = parts[0];

                    String synsetId = posAbbr + "." + offset;

                    int wordCount = Integer.parseInt(parts[3], 16);

                    List<String> words = new ArrayList<>();
                    for (int i = 0; i < wordCount; i++) {
                        String word = parts[4 + i * 2].toLowerCase().replace("_", " ");
                        words.add(word);

                        wordToSynsets.computeIfAbsent(word, k -> new ArrayList<>()).add(synsetId);
                    }

                    synsetToWords.put(synsetId, words);
                }
            } catch (Exception e) {
                System.out.println("Problem loading the synonyms...");
            }
        }
    }

    public void loadVerbData(){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(VERB_DATA_PATH));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split("\\s+");
                String baseForm = tokens[tokens.length - 1];
                verbSet.add(baseForm);
            }
        } catch (Exception e) {
            System.out.println("A problem occurred loading the verb file.");
        }
    }

    public boolean isVerb(String word) {
        return verbSet.contains(word.toLowerCase());
    }

    public String findSynonym(String word) {
        if (!wordToSynsets.containsKey(word)) {
            return null;
        }

        List<String> synsets = wordToSynsets.get(word);

        for (String synsetId : synsets) {
            for (String synonym : synsetToWords.get(synsetId)) {
                if (!synonym.equals(word)) {
                    return synonym;
                }
            }
        }

        return null;
    }
}
