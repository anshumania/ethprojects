/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eth.ir.boolret.dictionary;

import com.eth.ir.boolret.dictionary.datastructure.PostingList;
import com.eth.ir.boolret.dictionary.datastructure.PostingListNode;
import java.util.Map;

/**
 *
 * @author ANSHUMAN
 */
public class VectorDictionary extends Dictionary{

    private boolean stopWordMode;
    private boolean stemmedWordMode;

    public VectorDictionary(String indexFile) {
        super(indexFile);
    }

    @Override
    public void addToDictionary(String docId, String key, Integer position) {

        // normal mode
        // just add all the relevant words to the dictionary.

        super.addToDictionary(docId, key, position);


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

    }
}
