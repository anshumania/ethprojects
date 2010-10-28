/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

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
public class VectorDictionary extends Dictionary{

    private boolean stopWordMode;
    private boolean stemmedWordMode;

    private Porter porterStemmer;


    public VectorDictionary(String indexFile) {
        super(indexFile);
        this.porterStemmer = new Porter();
    }

    public VectorDictionary(String indexFile, boolean stopWord, boolean stemmedWord) {
        super(indexFile);
        this.stemmedWordMode = stemmedWord;
        this.stopWordMode = stopWord;
        this.porterStemmer = new Porter();
    }

    @Override
    public void addToDictionary(String docId, String key, Integer position) {



        // normal mode
        // just add all the relevant words to the dictionary.


        if(stopWordMode)
        {
            if (!StopWords.isStopWord(key.trim()))
            {
                if(stemmedWordMode) // STOPWORD AND STEMMING
                {

                 String stemmedKey = (key.trim().length() == 1) ? key : porterStemmer.stem(key.toLowerCase());
                 stemmedKey = stemmedKey.toUpperCase();
                 
                 super.addToDictionary(docId, stemmedKey, position);
                }
                else
                  super.addToDictionary(docId, key, position);  // STOPWORDS
            }
        }
        else
        {
            if (stemmedWordMode) //STEMMING
            {
                 String stemmedKey = (key.trim().length() == 1) ? key : porterStemmer.stem(key.toLowerCase());
                 stemmedKey = stemmedKey.toUpperCase();
                 super.addToDictionary(docId, stemmedKey, position);
            }
            else
                super.addToDictionary(docId, key, position); // NORMAL MODE
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
        
        for(Map.Entry<String, PostingList> iterator : index.entrySet())
        {
            PostingList pl = iterator.getValue();

            // the document frequency = dft
            Integer docFrequency = pl.getPostingList().size();
            // damp the document frequency = log<10>(N/dft)
            Double docFrequencyDamped = Math.log10((double)getNUMBER_OF_DOCUMENTS()/docFrequency);
            pl.setInverseDocumentFrequency(docFrequencyDamped);

            for(PostingListNode pln : pl.getPostingList())
            {
                // the term frequency
                Integer termFrequency = pln.getTermFrequencyInDoc();
                // damp the frequency
                Double termFrequencyDamped = (1 + Math.log10(termFrequency));

                //calculate the weighted tf-idf weight for the term
                Double tfIdfWeight = docFrequencyDamped * termFrequencyDamped;
                pln.setTf_idf_weight(tfIdfWeight);
           }
        }

       // protected TreeMap<String, PostingList> index;
//        System.out.println("index-"+index);

    }
}
