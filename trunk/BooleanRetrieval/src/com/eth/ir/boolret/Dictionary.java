/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eth.ir.boolret;

import java.util.TreeMap;

/**
 *
 * @author ANSHUMAN
 */
public class Dictionary {

    // the inverted index with a dictionary of terms<String> and posting lists <PostingList>
    protected TreeMap<String, PostingList> index;

    public TreeMap<String, PostingList> getIndex() {
        return index;
    }
    protected String INDEX_FILE;

    public String getINDEX_FILE() {
        return INDEX_FILE;
    }
    

    Dictionary(String indexFile)
    {
        this.INDEX_FILE = indexFile;
        this.index = new TreeMap<String,PostingList>();
    }

    public void addToDictionary(String docId, String key)
    {



             // the index does not contain this term
            if (!index.containsKey(key.trim())) {
                //create a new PostingList pl
                //create a new PostingListNode pln
                // set docid to pln
                // increment frequency of pln
                // add pln to pl
                // add pl to index

                PostingList pl = new PostingList();
                PostingListNode pln = new PostingListNode(docId, new Integer(1));
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
                    pln.setFrequencyInDoc(pln.getFrequencyInDoc() + 1);
                    index.put(key.trim(), pl);
                } // this term is for a new docId
                // create a new pln and append it
                // initiate the frequency for this docId
                else {
                    PostingListNode pln = new PostingListNode(docId, new Integer(1));
                    pl.getPostingList().add(pln);
                    pl.setNumPostings(pl.getNumPostings() + 1);
                    index.put(key.trim(), pl);
                }
            }
    }
}
