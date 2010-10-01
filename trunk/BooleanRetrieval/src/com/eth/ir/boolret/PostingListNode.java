/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eth.ir.boolret;

import java.io.Serializable;

/**
 *
 * @author ANSHUMAN
 */
public class PostingListNode implements Serializable {
    Integer docId;
    Integer frequencyInDoc;

    PostingListNode(Integer docId, Integer freq)
    {
    this.docId = docId;
    this.frequencyInDoc = freq;

    }

    public Integer getDocId() {
        return docId;
    }

    public void setDocId(Integer DocId) {
        this.docId = DocId;
    }

    public Integer getFrequencyInDoc() {
        return frequencyInDoc;
    }

    public void setFrequencyInDoc(Integer frequencyInDoc) {
        this.frequencyInDoc = frequencyInDoc;
    }


}
