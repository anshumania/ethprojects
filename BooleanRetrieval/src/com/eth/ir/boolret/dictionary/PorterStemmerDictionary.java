package com.eth.ir.boolret.dictionary;

import com.eth.ir.boolret.stem.porter.Porter;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeMap;

/**
 *
 * @author ANSHUMAN
 */
public class PorterStemmerDictionary extends Dictionary {

    // this treeMap will maintain the associations of the stemmed word
    // to other words within the document 
    // so as to not 'stem' the query word during query parsing
    private TreeMap<String, Set<String>> stemmedIndex;

    public TreeMap<String, Set<String>> getStemmedIndex() {
        return stemmedIndex;
    }
//    private PorterStemmer porterStemmer;
    private Porter porterStemmer;

    public PorterStemmerDictionary(String indexFile) {
        super(indexFile);
        this.stemmedIndex = new TreeMap<String, Set<String>>();
//        this.porterStemmer = new PorterStemmer();
        this.porterStemmer = new Porter();

    }

    @Override
    public void addToDictionary(String docId, String key, Integer position) {
        //System.out.println("key="+key);
        String stemmedKey = (key.trim().length() == 1) ? key : porterStemmer.stem(key.toLowerCase());
        stemmedKey = stemmedKey.toUpperCase();
        //System.out.println("stemmedKey=" + stemmedKey);
        super.addToDictionary(docId, stemmedKey, position);

        Set<String> temp;
        if (!stemmedIndex.containsKey(stemmedKey)) {
            temp = new LinkedHashSet<String>();
        } else {
            temp = stemmedIndex.get(stemmedKey);
        }

        temp.add(key);
        stemmedIndex.put(stemmedKey, temp);
    }
}
