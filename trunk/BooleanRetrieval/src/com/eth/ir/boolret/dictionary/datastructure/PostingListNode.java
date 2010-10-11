package com.eth.ir.boolret.dictionary.datastructure;

import java.io.Serializable;
import java.util.HashSet;

/**
 *
 * @author ANSHUMAN
 */
public class PostingListNode implements Serializable {

    private String docId;
    private HashSet<Integer> positions;

    public PostingListNode(String docId, Integer position) {
        this.docId = docId;
        this.positions = new HashSet<Integer>();
        this.positions.add(position);
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String DocId) {
        this.docId = DocId;
    }

    public HashSet<Integer> getPositions() {
        return positions;
    }

    public void setPositions(HashSet<Integer> positions) {
        this.positions = positions;
    }

    public void addPosition(Integer position) {
        this.positions.add(position);
    }

    public Integer getFrequencyInDoc() {
        return this.positions.size();
    }

    public Boolean equals(PostingListNode node) {
        return (this.docId).equals(node.getDocId());
    }

    @Override
    public String toString() {
        return "[docId=" + getDocId() + ", positions=" + getPositions() + "]";
    }
}
