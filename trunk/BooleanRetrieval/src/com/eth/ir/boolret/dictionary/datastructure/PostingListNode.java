package com.eth.ir.boolret.dictionary.datastructure;

import java.io.Serializable;

/**
 *
 * @author ANSHUMAN
 */
public class PostingListNode implements Serializable {

    String docId;
    Integer frequencyInDoc;

    public PostingListNode(String docId, Integer freq) {
        this.docId = docId;
        this.frequencyInDoc = freq;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String DocId) {
        this.docId = DocId;
    }

    public Integer getFrequencyInDoc() {
        return frequencyInDoc;
    }

    public void setFrequencyInDoc(Integer frequencyInDoc) {
        this.frequencyInDoc = frequencyInDoc;
    }

    public Boolean equals(PostingListNode node) {
        return (this.docId).equals(node.getDocId());
    }

    @Override
    public String toString() {
        return "[docId=" + getDocId() + ", freq=" + getFrequencyInDoc() + "]";
    }
}
