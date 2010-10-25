package com.eth.ir.boolret.dictionary;

import com.eth.ir.boolret.dictionary.datastructure.PostingList;
import com.eth.ir.boolret.dictionary.datastructure.PostingListNode;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author ANSHUMAN
 */
public class Dictionary {

    // the inverted index with a dictionary of terms<String> and posting lists <PostingList>
    protected TreeMap<String, PostingList> index;
    protected String INDEX_FILE;
    // this is required for the inverted document frequency
    protected Integer NUMBER_OF_DOCUMENTS;
    protected Map<String, Integer> documentLengths;

    public Map<String, Integer> getDocumentLengths() {
        return documentLengths;
    }

    public Integer getNUMBER_OF_DOCUMENTS() {
        return NUMBER_OF_DOCUMENTS;
    }

    public void setNUMBER_OF_DOCUMENTS(Integer NUMBER_OF_DOCUMENTS) {
        this.NUMBER_OF_DOCUMENTS = NUMBER_OF_DOCUMENTS;
    }


    public TreeMap<String, PostingList> getIndex() {
        return index;
    }

    public String getINDEX_FILE() {
        return INDEX_FILE;
    }

    public Dictionary(String indexFile) {
        this.INDEX_FILE = indexFile;
        this.index = new TreeMap<String, PostingList>();
        this.documentLengths = new HashMap<String, Integer>();
    }

    public void addToDictionary(String docId, String key, Integer position) {
        // the index does not contain this term
        if (!index.containsKey(key.trim())) {
            //create a new PostingList pl
            //create a new PostingListNode pln
            // set docid to pln
            // increment frequency of pln
            // add pln to pl
            // add pl to index

            PostingList pl = new PostingList();
            PostingListNode pln = new PostingListNode(docId, position);
            pl.getPostingList().add(pln);
            pl.setNumPostings(new Integer(1));
            index.put(key.trim(), pl);

        } // the index contains this term
        else {
            // fetch the postinglist pl
            // retrieve the postinglistnode pln for given docid
            // increment frequency of pln

            PostingList pl = index.get(key);
            // this term is in the same docId. increment it for this docId only
            // for future use - storing the frequency of the word within that document
            if (docId.equals(pl.getPostingList().getLast().getDocId())) {
                PostingListNode pln = pl.getPostingList().getLast();
                pln.addPosition(position);
                index.put(key.trim(), pl);
            } // this term is for a new docId
            // create a new pln and append it
            // initiate the frequency for this docId
            else {
                PostingListNode pln = new PostingListNode(docId, position);
                pl.getPostingList().add(pln);
                pl.setNumPostings(pl.getNumPostings() + 1);
                index.put(key.trim(), pl);
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
    public void createDocumentVectorSpaceModel()
    {
        
    }

       
}
