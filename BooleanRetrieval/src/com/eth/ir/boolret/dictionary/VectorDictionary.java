package com.eth.ir.boolret.dictionary;

import com.eth.ir.boolret.StopWords;
import com.eth.ir.boolret.dictionary.datastructure.PostingList;
import com.eth.ir.boolret.dictionary.datastructure.PostingListNode;
import com.eth.ir.boolret.stem.porter.Porter;
import java.util.Map;

/**
 *
 * @author ANSHUMAN
 */
public class VectorDictionary extends Dictionary {

    private boolean stopWordMode;
    private boolean stemmedWordMode;
    private Porter porterStemmer;

    public VectorDictionary(String indexFile) {
        super(indexFile);
        this.stopWordMode = false;
        this.stemmedWordMode = false;
        this.porterStemmer = null;
    }

    public VectorDictionary(String indexFile, boolean stopWord, boolean stemmedWord) {
        super(indexFile);
        this.stemmedWordMode = stemmedWord;
        this.stopWordMode = stopWord;
        this.porterStemmer = new Porter();
    }

    @Override
    public void addToDictionary(String docId, String key, Integer position) {

        if (stopWordMode) {
            if (!StopWords.isStopWord(key.trim())) {
                if (stemmedWordMode) {
                    String stemmedKey = (key.trim().length() == 1) ? key : porterStemmer.stem(key.toLowerCase());
                    stemmedKey = stemmedKey.toUpperCase();
                    super.addToDictionary(docId, stemmedKey, position); // STOPWORD AND STEMMING
                } else {
                    super.addToDictionary(docId, key, position);  // STOPWORDS ONLY
                }
            }
        } else {
            if (stemmedWordMode) {
                String stemmedKey = (key.trim().length() == 1) ? key : porterStemmer.stem(key.toLowerCase());
                stemmedKey = stemmedKey.toUpperCase();
                super.addToDictionary(docId, stemmedKey, position); // STEMMING ONLY
            } else {
                super.addToDictionary(docId, key, position); // NORMAL MODE
            }
        }
    }

    /**
     * The purpose of this function to create the
     * vector space model for the documents
     * @param docId
     * @param key
     * @param position
     */
    @Override
    public void createDocumentVectorSpaceModel() {

        // for each term in the dictionary calculate its termFrequency
        for (Map.Entry<String, PostingList> iterator : index.entrySet()) {
            PostingList pl = iterator.getValue();

            // the document frequency = dft
            Integer docFrequency = pl.getPostingList().size();
            // damp the document frequency = log<10>(N/dft)
            Double docFrequencyDamped = Math.log10((double) getNUMBER_OF_DOCUMENTS() / docFrequency);
            //Double docFrequencyDamped = Math.log10(getNUMBER_OF_DOCUMENTS() / docFrequency);
            pl.setInverseDocumentFrequency(docFrequencyDamped);

            for (PostingListNode pln : pl.getPostingList()) {
                Integer termFrequency = pln.getTermFrequencyInDoc();
                Double termFrequencyDamped = (1 + Math.log10(termFrequency));
                //Double termFrequencyDamped2 = (1 + Math.log10(new Double(termFrequency)));
                //Double termFrequencyDamped3 = (1 + Math.log10(new Double((double) termFrequency)));
                //Double termFrequencyDamped4 = (1 + Math.log10(new Double(termFrequency.doubleValue())));

                //calculate the weighted tf-idf weight for the term
                Double tfIdfWeight = docFrequencyDamped * termFrequencyDamped;
                pln.setTf_idf_weight(tfIdfWeight);
            }
        }
    }
}
