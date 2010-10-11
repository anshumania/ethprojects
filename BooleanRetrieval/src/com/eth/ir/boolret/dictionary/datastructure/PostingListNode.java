package com.eth.ir.boolret.dictionary.datastructure;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author ANSHUMAN
 */
public class PostingListNode implements Serializable {

    private String docId;
    private TreeSet<Integer> positions;

    public PostingListNode(String docId, Integer position) {
        this.docId = docId;
        this.positions = new TreeSet<Integer>();
        this.positions.add(position);
    }

    public PostingListNode(String docId) {
        this.docId = docId;
        this.positions = new TreeSet<Integer>();
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String DocId) {
        this.docId = DocId;
    }

    public Set<Integer> getPositions() {
        return positions;
    }

    public void setPositions(Set<Integer> positions) {
        this.positions = new TreeSet<Integer>(positions);
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
