/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eth.ir.boolret;

import java.io.Serializable;
import java.util.LinkedList;

/**
 *
 * @author ANSHUMAN
 */
public class PostingList implements Serializable{

    Integer frequency;

    LinkedList<PostingListNode> postingList;

    public LinkedList<PostingListNode> getPostingList() {
        return postingList;
    }

    PostingList()
    {
        postingList = new LinkedList<PostingListNode>();
    }

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
        
    }

    public String toString()
    {
        StringBuffer str = new StringBuffer();
        for(PostingListNode pln : getPostingList())
        {
            str.append(pln);
        }
        str.append("\n");
        return str.toString();
    }


}
