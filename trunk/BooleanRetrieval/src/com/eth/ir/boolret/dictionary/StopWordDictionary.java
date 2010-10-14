package com.eth.ir.boolret.dictionary;

import com.eth.ir.boolret.StopWords;

/**
 *
 * @author ANSHUMAN
 */
public class StopWordDictionary extends Dictionary {

    public StopWordDictionary(String indexFile) {
        super(indexFile);
    }

    @Override
    public void addToDictionary(String docId, String key, Integer position) {
        if (!StopWords.isStopWord(key.trim())) {
            super.addToDictionary(docId, key, position);
        }
    }
}