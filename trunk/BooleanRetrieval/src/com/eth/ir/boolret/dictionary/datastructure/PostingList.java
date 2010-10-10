package com.eth.ir.boolret.dictionary.datastructure;

import java.io.Serializable;
import java.util.LinkedList;

/**
 *
 * @author ANSHUMAN
 */
public class PostingList implements Serializable{

    Integer numPostings;

    LinkedList<PostingListNode> postingList;

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

    public String toString()
    {
        StringBuffer str = new StringBuffer();
        for(PostingListNode pln : getPostingList()) {
            str.append(pln);
        }
        str.append("\n");
        return str.toString();
    }
    
}
