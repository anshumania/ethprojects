/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eth.ir.boolret;

import java.lang.String;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeMap;

/**
 *
 * @author ANSHUMAN
 */
public class PorterStemmerDictionary extends Dictionary{

    // this treeMap will maintain the associations of the stemmed word
    // to other words within the document 
    // so as to not 'stem' the query word during query parsing
    private TreeMap<String,Set<String>> stemmedIndex;
    private PorterStemmer porterStemmer;

    PorterStemmerDictionary(String indexFile)
    {
        super(indexFile);
        this.stemmedIndex = new TreeMap<String,Set<String>>();
        this.porterStemmer = new PorterStemmer();

    }

    @Override
   public void addToDictionary(String docId, String key)
    {
           String stemmedKey = porterStemmer.stem(key);
           super.addToDictionary(docId,stemmedKey);

           Set<String> temp;
           if(!stemmedIndex.containsKey(stemmedKey))
               temp = new LinkedHashSet<String>();
           else
               temp = stemmedIndex.get(stemmedKey);

           temp.add(key);
           stemmedIndex.put(stemmedKey, temp);

    }
}
