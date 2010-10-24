package com.eth.ir.boolret.dictionary.datastructure;

import java.io.Serializable;
import java.util.LinkedList;

/**
 *
 * @author ANSHUMAN
 */
public class PostingList implements Serializable{

    private Integer numPostings;
    private LinkedList<PostingListNode> postingList;

    // the number of documents in the collection ( a constant N )
    // divided by the number termFrequency ( the number of documents
    // in which the term occurs)
    private Integer inverseDocumentFrequency;


    public LinkedList<PostingListNode> getPostingList() {
        return postingList;
    }

    public PostingList() {
        postingList = new LinkedList<PostingListNode>();
        numPostings = 0;
    }

    public Integer getNumPostings() {
        return numPostings;
    }

    public void setNumPostings(Integer numPostings) {
        this.numPostings = numPostings;
    }

    public Integer getInverseDocumentFrequency() {
        return inverseDocumentFrequency;
    }

    public void setInverseDocumentFrequency(Integer inverseDocumentFrequency) {
        this.inverseDocumentFrequency = inverseDocumentFrequency;
    }

    

    @Override
    public String toString()
    {
        StringBuilder str = new StringBuilder();
        for(PostingListNode pln : getPostingList()) {
            str.append(pln);
        }
        str.append("\n");
        return str.toString();
    }
    
}
